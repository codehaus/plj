
/**
 * file name:			plpgj_hook.c
 * description:			PL/pgJ call hook.
 * author:				Laszlo Hornyak
 */

#include "postgres.h"
#include "fmgr.h"
#include "plpgj_messages.h"
#include "plpgj_channel.h"
#include "plpgj_message_fns.h"
#include "plpgj_core.h"

#include "utils/portal.h"
#include "lib/stringinfo.h"
#include "nodes/makefuncs.h"
#include "parser/parse_type.h"
#include "module_config.h"
#include "commands/trigger.h"
#include "executor/spi_priv.h"
#include "msghandler.h"

#include "utils/palloc.h"
#include "utils/memutils.h"
#include "access/xact.h"
#include "pljelog.h"
#include "plantable.h"
#include "envstack.h"
#include <string.h>

#include "plpgj_hook.h"

#include "memdebug.h"

/*	*/

/* proto */

/*	*/

/**
 * Called for handling an exception from the java backend.
 */
void		plpgj_exception_do(error_message);

/**
 * Called for handling sql calls from the java backend.
 */
void		plpgj_sql_do(sql_msg msg);

/**
 * Called for handling results from the java backend.
 */
Datum		plpgj_result_do(plpgj_result);

void		plpgj_log_do(log_message);



void		plpgj_EOXactCallBack(bool isCommit, void *arg);

#if (PG_MAJOR_VERSION < 8)
void		plpgj_ErrorContextCallback(void *arg);
#endif

int			callbacks_init = 0;

/* 
 This is how it WILL work

short		plpgj_tx_externalize = 1;
short		plpgj_tx_externalize_nested = 0;
*/

// This is how it works right now

#define plpgj_tx_externalize		plj_get_configvalue_boolean("plpgj.tx.externalize")
#define plpgj_tx_externalize_nested	plj_get_configvalue_boolean("plpgj.tx.externalize.nested")

#if (PG_MAJOR_VERSION < 8)
#define plpgj_return_cleanup		if(envstack_isempty()) { unreg_error_callback(); UnregisterEOXactCallback(plpgj_EOXactCallBack, NULL); elog(DEBUG1, "plpgj_return_cleanup: done"); }
#else
#define plpgj_return_cleanup		
#endif


#if (PG_MAJOR_VERSION < 8)

void reg_error_callback() {
	if (!callbacks_init)
	{
	ErrorContextCallback *mycallback;
		mycallback = palloc(sizeof(ErrorContextCallback));
		mycallback->previous = error_context_stack;
		mycallback->callback = plpgj_ErrorContextCallback;
		mycallback->arg = NULL;

		error_context_stack = mycallback;

	callbacks_init = 1;
	}

}

#endif


#if (PG_MAJOR_VERSION > 7)

void handle_exception(void){
	ErrorData  *edata;
	MemoryContext oldCtx;
	elog(DEBUG1, "exception handler.");

	//oldCtx = CurrentMemoryContext;
	//MemoryContextSwitchTo(ErrorContext);
	
	edata = CopyErrorData();
	plpgj_utl_senderror(edata -> message);

	elog(DEBUG1, "flushing error");
	FlushErrorState();

	//MemoryContextSwitchTo(oldCtx);

//	RollbackAndReleaseCurrentSubTransaction();
	SPI_restore_connection();
}

#else
#endif


#if (PG_MAJOR_VERSION < 8)
void unreg_error_callback() {
	if (callbacks_init) {
		elog(DEBUG1, "unreg_error_callback: 1");
		//XXX this will fuck up the env if someone else registers a callback in a reentrant sp.
		
		if(error_context_stack != NULL && error_context_stack -> callback == plpgj_ErrorContextCallback){
			elog(DEBUG1, "unreg_error_callback: 2");
			error_context_stack = error_context_stack -> previous;
			elog(DEBUG1, "unreg_error_callback: 3");
		}
		elog(DEBUG1, "unreg_error_callback: 4");
	}
	callbacks_init = 0;
}
#endif

Datum
plpgj_call_hook(PG_FUNCTION_ARGS)
{

#if (PG_MAJOR_VERSION < 8)
	if(envstack_isempty())
		reg_error_callback();
#endif

	message		req;
	int			message_type;

	if (!plpgj_channel_initialized())
	{
		pljelog(DEBUG1, "initing channel");
		plpgj_channel_initialize();
	}

#if (PG_MAJOR_VERSION < 8)
	if(plpgj_tx_externalize && envstack_isempty())
		RegisterEOXactCallback(plpgj_EOXactCallBack, NULL);
#endif


	if (CALLED_AS_TRIGGER(fcinfo)
		&& plj_get_configvalue_boolean("plpgj.core.usetriggers"))
	{
		req = (message) plpgj_create_trigger_call(fcinfo);
	}
	else
	{
		pljelog(DEBUG1, "creating call structure");
		req = (message) plpgj_create_call(fcinfo);
	}

	plpgj_channel_send((message) req);
	free_message(req);

	do
	{
		void	   *ansver = NULL;
		MemoryContext messageContext;
		MemoryContext oldContext;

		messageContext = AllocSetContextCreate(CurrentMemoryContext, "PL-J message context", 128000, 32, 65536);
		oldContext = CurrentMemoryContext;

		MemoryContextSwitchTo(messageContext);
		
		pljelog(DEBUG1, "waiting ansver.");
		ansver = plpgj_channel_receive();
		message_type = plpgj_message_type(ansver);
		pljelog(DEBUG1, "ansver of type: %d", message_type);
		switch (message_type)
		{
			case MT_RESULT:
				//handle elsewhere
				break;
			case MT_EXCEPTION:
				plpgj_exception_do((error_message) ansver);
				plpgj_return_cleanup;
				PG_RETURN_NULL();
				break;
			case MT_SQL:
				{
					plpgj_sql_do(ansver);
				}
				break;
			case MT_LOG:
				plpgj_log_do((log_message) ansver);
				break;
			case MT_TUPLRES:
				pljelog(DEBUG1, "received: tupleresult");

				break;
			default:
				//lets first switch back to the old context and handle the error.
				MemoryContextSwitchTo(oldContext);
				MemoryContextDelete(messageContext);
				pljelog(FATAL, "[plj core] received: unhandled message with type id %d", message_type);
				//this wont run.
				PG_RETURN_NULL();
		}

		//we do not try to free messages anymore, we switch context and delete the old one
		//elog(DEBUG1, "[plj core] free message");
		//free_message(ansver);
		//elog(DEBUG1, "[plj core] free message done");

		/*
		 * here is how to escape from the loop
		 */
		pljelog(DEBUG1, "begin return procedure");

		if (message_type == MT_RESULT)
		{

			pljelog(DEBUG1, "result");
			plpgj_result res = (plpgj_result) ansver;

			pljelog(DEBUG1, "result 1");
			if (res->rows == 1 && res->cols == 1)
			{
				Datum		ret;
				HeapTuple	typetup;
				Form_pg_type type;
				TypeName   *typeName;
				Oid			typeOid;
				Datum		rawDatum;
				StringInfo	rawString;
				MemoryContext oldctx;

				if (res->data[0][0].isnull == 1){
					MemoryContextSwitchTo(oldContext);
					MemoryContextDelete(messageContext);
					plpgj_return_cleanup;
					PG_RETURN_NULL();
				}

				pljelog(DEBUG1,"1");
				typeName = makeTypeName(res->types[0]);
				typeOid = typenameTypeId(typeName);
				typetup = SearchSysCache(TYPEOID, typeOid, 0, 0, 0);
				if (!HeapTupleIsValid(typetup)){
					MemoryContextSwitchTo(oldContext);
					MemoryContextDelete(messageContext);
					elog(FATAL, "[plj - core] Invalid heaptuple at result return");
					//This won`t run
					PG_RETURN_NULL();
				}

				type = (Form_pg_type) GETSTRUCT(typetup);
				ReleaseSysCache(typetup);

				oldctx = CurrentMemoryContext;
				MemoryContextSwitchTo(QueryContext);

				rawString = SPI_palloc(sizeof(StringInfoData));
				initStringInfo(rawString);
				appendBinaryStringInfo(rawString, res->data[0][0].data,
									   res->data[0][0].length);
				rawDatum = PointerGetDatum(rawString);

				ret = OidFunctionCall1(type->typreceive, rawDatum);


				MemoryContextSwitchTo(oldctx);

				plpgj_return_cleanup;
				return ret;
			}
			else if (res->rows == 0)
			{
				MemoryContextSwitchTo(oldContext);
				MemoryContextDelete(messageContext);
				pljelog(ERROR,"Resultset return not implemented.");
				PG_RETURN_VOID();
			}

			/*
			 * continue here!!
			 */


/*			PG_RETURN_NULL(); */
			/*
			 * break;
			 */
		}

		elog(DEBUG1, "deleting message ctx");
		MemoryContextSwitchTo(oldContext);
		MemoryContextDelete(messageContext);
		elog(DEBUG1, "deleted message ctx");

		if (message_type == MT_EXCEPTION) {
			elog(ERROR, ((error_message)ansver) -> message);
			plpgj_return_cleanup;
			PG_RETURN_NULL();
		}
		if (message_type == MT_TUPLRES)
		{
			HeapTuple	rettup;
			trigger_tupleres res = (trigger_tupleres) ansver;

			/*
			 * if(!CALLED_AS_TRIGGER(fcinfo))
			 */
			TriggerData *tdata = (TriggerData *) fcinfo->context;

			if (TRIGGER_FIRED_FOR_STATEMENT(tdata->tg_event)) {
				rettup = NULL;
			}
			else
			{
				Datum	   *datums;
				int		   *columns;
				char	   *nulls;
				int			i;

				/*
				 * copy tuple until i find out how to alloc one...
				 */
				rettup = SPI_copytuple(tdata->tg_trigtuple);
				/*
				 * rettup = SPI_palloc(HEAPTUPLESIZE);
				 */

				if (res->colcount > 0)
				{
					datums = SPI_palloc(res->colcount * sizeof(Datum));
					columns = SPI_palloc(res->colcount * sizeof(int));
					nulls = SPI_palloc(res->colcount * sizeof(char));
				}
				else
				{
					datums = NULL;
					columns = NULL;
					nulls = NULL;
				}
				for (i = 0; i < res->colcount; i++)
				{
					HeapTuple	typtup;
					Oid			typoid;
					Form_pg_type typ;
					TypeName   *typnam;
					StringInfoData *rawString;

					rawString = SPI_palloc(sizeof(StringInfoData));
					initStringInfo(rawString);
					appendBinaryStringInfo(rawString,
										   res->_tuple[i]->data.data,
										   res->_tuple[i]->data.length);
					{
					int j;
					for(j=0; j < res->_tuple[i]->data.length; j++)
						pljelog(DEBUG1, "res->_tuple[%d]->data.data[%d] = %d", i, j, ((char*)(res->_tuple[i]->data.data))[j]);
					}

					typnam = makeTypeName(res->_tuple[i]->type);
					typoid = LookupTypeName(typnam);
					typtup = SearchSysCache(TYPEOID, typoid, 0, 0, 0);
					typ = (Form_pg_type) GETSTRUCT(typtup);
					ReleaseSysCache(typtup);


					datums[i] = OidFunctionCall1(typ->typreceive,
												 PointerGetDatum
												 (rawString));
					/*
					 * wrong
					 */
					columns[i] = SPI_fnumber
						(tdata->tg_relation->rd_att, res->colnames[i]);
					nulls[i] = ' ';
				}
				rettup = SPI_modifytuple(tdata->tg_relation,
										 rettup,
										 res->colcount,
										 columns, datums, nulls);
			}
			pljelog(DEBUG1, "returning tuple");
			plpgj_return_cleanup;
			return PointerGetDatum(rettup);
		}
		elog(DEBUG1, "debug");
		
//		pljelog(ERROR, "no handler for message type: %d", message_type);

	}
	while (1);

	pljelog(DEBUG1, "return null");
	plpgj_return_cleanup;
	PG_RETURN_NULL();

}

Datum
plpgj_result_do(plpgj_result res)
{
	return 0;
}

void
plpgj_sql_do(sql_msg msg)
{
	message res;
	res = handle_message(msg);
	if(res != NULL)
		plpgj_channel_send((message)res);
}

void
plpgj_exception_do(error_message msg)
{
	elog(ERROR,"Java side exception occured: \n %s : %s \n ", msg->classname, msg->message ); 
}

void
plpgj_log_do(log_message log)
{
	int			level = DEBUG1;

	if (log == NULL)
		return;
	switch (log->level)
	{
		case 1:
			level = DEBUG5;
			break;
		case 2:
			level = INFO;
			break;
		default:
			level = INFO;
	}

	pljelog(level, "[%s] -  %s ", log->category, log->message);

}

#if (PG_MAJOR_VERSION < 8)

/**
 * This may be the starting point of transaction externalisation.
 */
void
plpgj_EOXactCallBack(bool isCommit, void *arg)
{

	/*
	 * Not externalizing if configured so.
	 */
	if(!plpgj_tx_externalize)
		return;

	if (isCommit) {
		pljelog(DEBUG1, "Transaction commit - plpgj");
		
	}
	else
	{
		pljelog(DEBUG1, "Transaction rollback - plpgj");
	};
}

/**
 * ErrorContextCallback function
 */


void
plpgj_ErrorContextCallback(void *arg)
{
	bool reenable_loging;

//	if (!pljlogging_error)
		return;
	pljlogging_error = 0;
	
	reenable_loging = pljloging;

	/*
	 * disable loging
	 */
	pljloging = 0;

	/*
	 *send the error to the java process.
	 */
	error_message msg = palloc(sizeof(str_error_message));

	msg->msgtype = MT_EXCEPTION;
	msg->length = sizeof(str_error_message);
	msg->classname = "PostgreSQL statement";
	msg->message = "No information (see your statement)";
	msg->stacktrace = "";
//	pljelog(DEBUG1, "sending exception");
	plpgj_channel_send((message) msg);


	/*
	 * Unregister ErrorContextCallback
	 */
	unreg_error_callback();

	/*
	 * error_context_stack = error_context_stack -> previous;
	 */

	/*
	 * re-enable loging
	 */
	if(reenable_loging)
		pljloging = 1;

	

}

#endif

void plpgj_utl_sendstr(const char* str) {
				plpgj_result res;
				res = palloc(sizeof(str_plpgj_result));
				res -> msgtype = MT_RESULT;
				res -> length = sizeof(str_plpgj_result);
				res -> rows = 1;
				res -> cols = 1;
				res -> types = palloc(sizeof(char*));
				res -> types[0] = "varchar";
				res -> data = palloc(sizeof(raw));
				res -> data[0] = palloc(sizeof(struct str_raw));
				res -> data[0] -> length = 4 + strlen(str);
				res -> data[0] -> isnull = 0;
				{
					Form_pg_type int4typ;
					HeapTuple int4htp;
					Oid int4oid;
					TypeName* int4nam;
					Datum d;
					StringInfo      rawString;
					
					int4nam = makeTypeName("varchar");
					int4oid = LookupTypeName(int4nam);
					int4htp = SearchSysCache(TYPEOID, int4oid, 0, 0, 0);
					int4typ = (Form_pg_type)GETSTRUCT(int4htp);
					ReleaseSysCache(int4htp);

					rawString = palloc(sizeof(StringInfoData));
					initStringInfo(rawString);
					appendBinaryStringInfo(rawString, str, strlen(str));
					d = OidFunctionCall1(int4typ -> typsend, OidFunctionCall1(int4typ -> typinput, PointerGetDatum(str)));
					res -> data[0] -> data = DatumGetPointer(d);
				}
				plpgj_channel_send((message)res);
}

void plpgj_utl_sendint(int i) {
				plpgj_result res;
				res = palloc(sizeof(str_plpgj_result));
				res -> msgtype = MT_RESULT;
				res -> length = sizeof(str_plpgj_result);
				res -> rows = 1;
				res -> cols = 1;
				res -> types = palloc(sizeof(char*));
				res -> types[0] = "int4";
				res -> data = palloc(sizeof(raw));
				res -> data[0] = palloc(sizeof(struct str_raw));
				res -> data[0] -> length = 8;
				res -> data[0] -> isnull = 0;
				//res -> data[0] -> data = SPI_palloc(sizeof(12));
				{
					Form_pg_type int4typ;
					HeapTuple int4htp;
					Oid int4oid;
					TypeName* int4nam;
					Datum d;
					
					int4nam = makeTypeName("int4");
					int4oid = LookupTypeName(int4nam);
					int4htp = SearchSysCache(TYPEOID, int4oid, 0, 0, 0);
					int4typ = (Form_pg_type)GETSTRUCT(int4htp);
					ReleaseSysCache(int4htp);

					d = OidFunctionCall1(int4typ -> typsend, UInt32GetDatum(i));
					res -> data[0] -> data = DatumGetPointer(d);
				}
				pljelog(DEBUG1,"1");
				pljloging = 0;
				plpgj_channel_send((message)res);
				pljloging = 1;

}

void
plpgj_utl_senderror(char* errmsg){
	str_error_message msg;

	msg.msgtype = MT_EXCEPTION;
	msg.classname = "rdbms";
	msg.stacktrace = "";
	msg.message = errmsg;
	plpgj_channel_send((message) &msg);
}

typedef struct plj_envstack_struct {
	short logging;
	short logging_error;
	void	*next;
} *plj_envstack;

plj_envstack envstack = NULL;
sigjmp_buf *PG_exception_stack = NULL;


void envstack_push(void) {
	plj_envstack nenv;
	nenv = SPI_palloc(sizeof( struct plj_envstack_struct));
	nenv -> logging = pljloging;
	nenv -> logging_error = pljlogging_error;
	nenv -> next = envstack;
}

void envstack_pop(void) {
	if(envstack == NULL)
		return;
	pljloging = envstack -> logging;
	pljlogging_error = envstack -> logging_error;
	envstack = envstack -> next;
}

int envstack_isempty(void) {
	return (envstack == NULL);
}

void envstack_clean(void) {
	envstack = NULL;
}
