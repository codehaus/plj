
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

#include "utils/palloc.h"
#include "utils/memutils.h"
#include "access/xact.h"
#include "pljelog.h"
#include "plantable.h"
#include "envstack.h"
#include <string.h>

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


void plpgj_utl_sendint(int);
void plpgj_utl_sendstr(const char*);
void plpgj_utl_senderror(const char*);

void		plpgj_EOXactCallBack(bool isCommit, void *arg);

void		plpgj_ErrorContextCallback(void *arg);

int			callbacks_init = 0;

/* 
 This is how it WILL work

short		plpgj_tx_externalize = 1;
short		plpgj_tx_externalize_nested = 0;
*/

// This is how it works right now

#define plpgj_tx_externalize		plj_get_configvalue_boolean("plpgj.tx.externalize")
#define plpgj_tx_externalize_nested	plj_get_configvalue_boolean("plpgj.tx.externalize.nested")

#define plpgj_return_cleanup		if(envstack_isempty()) { unreg_error_callback(); UnregisterEOXactCallback(plpgj_EOXactCallBack, NULL); elog(DEBUG1, "plpgj_return_cleanup: done"); }


/*	*/

/* impl */

/*	*/

void reg_error_callback() {
	if (!callbacks_init)
	{
	ErrorContextCallback *mycallback;
		mycallback = SPI_palloc(sizeof(ErrorContextCallback));
		mycallback->previous = error_context_stack;
		pljelog(DEBUG1, "1");
		mycallback->callback = plpgj_ErrorContextCallback;
		pljelog(DEBUG1, "2");
		mycallback->arg = "hello world!";
		pljelog(DEBUG1, "3");

		error_context_stack = mycallback;
		pljelog(DEBUG1, "4");

	callbacks_init = 1;
	}

}

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

Datum
plpgj_call_hook(PG_FUNCTION_ARGS)
{

	if(envstack_isempty())
		reg_error_callback();

	message		req;
	int			message_type;

	if (!plpgj_channel_initialized())
	{
		pljelog(DEBUG1, "initing channel");
		plpgj_channel_initialize();
	}

	if(plpgj_tx_externalize && envstack_isempty())
		RegisterEOXactCallback(plpgj_EOXactCallBack, NULL);
		pljelog(DEBUG1, "5");


	/*
	 * register event handlers
	 */

	/*
	 * now do the real job
	 */

	pljelog(DEBUG1, "7");

	if (CALLED_AS_TRIGGER(fcinfo)
		&& plj_get_configvalue_boolean("plpgj.core.usetriggers"))
	{
		req = (message) plpgj_create_trigger_call(fcinfo);
		pljelog(DEBUG1, "8");
	}
	else
	{
		pljelog(DEBUG1, "9");
		req = (message) plpgj_create_call(fcinfo);
		pljelog(DEBUG1, "10");
	}

	plpgj_channel_send((message) req);
	free_message(req);

	do
	{
		void	   *ansver = NULL;

		pljelog(DEBUG1, "waiting ansver.");
		ansver = plpgj_channel_receive();
		pljelog(DEBUG1, "got %s", ansver == NULL ? "null" : "not null");
		message_type = plpgj_message_type(ansver);
		pljelog(DEBUG1, "ansver of type: %d", message_type);
		switch (message_type)
		{
			case MT_RESULT:
				pljelog(DEBUG1, "received: result");

				break;
			case MT_EXCEPTION:
				pljelog(DEBUG1, "received: exception");
				plpgj_exception_do((error_message) ansver);
				plpgj_return_cleanup;
				PG_RETURN_NULL();
				break;
			case MT_SQL:
				{
					pljelog(DEBUG1, "received: sql");
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
				pljelog(FATAL, "received: unknown message.");
				/*
				 * TODO perhaps there is a more elegant way to escape from here.
				 */
				PG_RETURN_NULL();
		}
		free_message(ansver);

		/*
		 * here is how to escape from the loop
		 */
		pljelog(DEBUG1, "begin return procedure");

		if (message_type == MT_RESULT)
		{
			/*
			 * TODO what now? we should return a value HERE.
			 */
			/*
			 * PG_RETURN...
			 */

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

				pljelog(DEBUG1, "result 2");
				if (res->data[0][0].isnull == 1){
					pljelog(DEBUG1, "result 2.1");
					pljelog(DEBUG1, "result 3");
					plpgj_return_cleanup;
					PG_RETURN_NULL();
				}

				pljelog(DEBUG1,"1");
				typeName = makeTypeName(res->types[0]);
				typeOid = typenameTypeId(typeName);
				typetup = SearchSysCache(TYPEOID, typeOid, 0, 0, 0);
				if (!HeapTupleIsValid(typetup)){
					//TODO how to handle this situation the best?
					pljelog(ERROR, "invalid heaptuple at result return");
				}

					type = (Form_pg_type) GETSTRUCT(typetup);

				pljelog(DEBUG1,"2");
				/*
				 */
				/*
				 * (see http://jira.codehaus.org/secure/ViewIssue.jspa?key=PLJ-1)
				 */
				/*
				 */

				pljelog(DEBUG1,"3");
				oldctx = CurrentMemoryContext;
				MemoryContextSwitchTo(QueryContext);

				rawString = SPI_palloc(sizeof(StringInfoData));
				initStringInfo(rawString);
				pljelog(DEBUG1,"4");
				/*
				 * {
				 * int i = 0;
				 * for(i = 0; i<res->data[0][0].length; i++){
				 * pljelog(DEBUG1,">%d",(char)((char*)res->data[0][0].data)[i]);
				 * }
				 *
				 * }
				 */
				appendBinaryStringInfo(rawString, res->data[0][0].data,
									   res->data[0][0].length);
				pljelog(DEBUG1,"4.1");
				rawDatum = PointerGetDatum(rawString);

				pljelog(DEBUG1,"4.2");
				ret = OidFunctionCall1(type->typreceive, rawDatum);

				pljelog(DEBUG1,"5");
				/*
				 * SPI_pfree(rawString);
				 */
				ReleaseSysCache(typetup);

				MemoryContextSwitchTo(oldctx);

				pljelog(DEBUG1,"return ret;");
				plpgj_return_cleanup;
				return ret;
			}
			else if (res->rows == 0)
			{
				pljelog(DEBUG1,"multirow not implemented.");
				//error_context_stack = error_context_stack->previous;
				plpgj_return_cleanup;
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
		if (message_type == MT_EXCEPTION) {
			elog(ERROR, ((error_message)ansver) -> message);
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


					datums[i] = OidFunctionCall1(typ->typreceive,
												 PointerGetDatum
												 (rawString));
					/*
					 * wrong
					 */
					columns[i] = SPI_fnumber
						(tdata->tg_relation->rd_att, res->colnames[i]);
					nulls[i] = ' ';
					ReleaseSysCache(typtup);
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
		pljelog(DEBUG1, "debug");
		
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
	switch (msg->sqltype)
	{
		case SQL_TYPE_STATEMENT:
			elog(DEBUG1, "entering nolog area");
			pljlogging_error = 1;
			SPI_exec(((sql_msg_statement) msg)->statement, 0);
			pljlogging_error = 0;
			elog(DEBUG1, "leaving nolog area");
			break;
		case SQL_TYPE_PREPARE:
			//do trixx
			{
				Oid *argtypes;
				sql_msg_prepapre prep;
				int i;
				TypeName *typnam;
				void *plan;
				int planid;

				prep = (sql_msg_prepapre) msg;
				argtypes = prep -> ntypes == 0 ? NULL : SPI_palloc(prep -> ntypes * sizeof(Oid) );
				for(i = 0; i < prep -> ntypes; i++) {
					typnam = makeTypeName( prep -> types[i] );
					argtypes[i] = LookupTypeName(typnam);
				}

				elog(DEBUG1, "SQL_TYPE_PREPARE, nolog area");
				pljlogging_error = 1;
				//THIS WILL FUCK YOUR SYSTEM IF YOU HAVE prepare
				
				PG_TRY();
					
					elog(DEBUG1,"success");
					plan = SPI_prepare( prep -> statement, prep -> ntypes, argtypes);
				PG_CATCH();
					elog(DEBUG1,"failure");
				PG_END_TRY();
				
				pljlogging_error = 0;
				elog(DEBUG1, "SQL_TYPE_PREPARE done, leaving nolog area");
				planid = store_plantable(plan);

				plpgj_utl_sendint(planid);
				pljloging = 1;
			}
			break;
		case SQL_TYPE_PEXECUTE:
			pljelog(DEBUG1,"pexec");
			{
				char* nulls;
				Datum* values;
				sql_pexecute sql;
				int i;

				sql = (sql_pexecute)msg;
				nulls = SPI_palloc( sql -> nparams * sizeof(char)) + 1;
				pljelog(DEBUG1, "nparams: %d", sql -> nparams);
				if(sql -> nparams == 0){
					values = NULL;
				} else {
					values = SPI_palloc( sql -> nparams * sizeof(Datum));
				}
				for(i = 0; i < sql -> nparams; i++) {
					if(sql -> params[i].data.isnull){
						nulls[i] = 'n';
					} else {
						TypeName* typnam;
						HeapTuple typtup;
						Form_pg_type typstr;
						Oid typoid;
						StringInfo rawString;
						
						pljelog(DEBUG1, "[%d]creating Datum: %s", i, sql -> params[i].type);
						pljelog(DEBUG1, "[%d] type: %s", i, sql -> params[i].type);

						nulls[i] = ' ';
						pljelog(DEBUG1, "1");
						typnam = makeTypeName(sql -> params[i].type );
						pljelog(DEBUG1, "2");
						typoid = LookupTypeName(typnam);
						pljelog(DEBUG1, "3");
						typtup = SearchSysCache(TYPEOID, typoid, 0, 0, 0);
						pljelog(DEBUG1, "4");
						typstr = (Form_pg_type)GETSTRUCT(typtup);
						pljelog(DEBUG1, "5");
						ReleaseSysCache(typtup);
						
						rawString = SPI_palloc(sizeof(StringInfoData));
						initStringInfo(rawString);
						pljelog(DEBUG1, "6: %d", sql -> params[i].data.length);
						appendBinaryStringInfo(rawString, sql -> params[i].data.data, sql -> params[i].data.length);
						pljelog(DEBUG1, "7");
						values[i] = OidFunctionCall1(typstr -> typreceive, PointerGetDatum(rawString));
						pljelog(DEBUG1, "8");
						
						
					}
				}
				nulls[sql -> nparams + 1] = 0;
				pljelog(DEBUG1,"executing.");
				{
				int ret;
				Portal pret;
				pljlogging_error = 1;
				elog(DEBUG1, "action:%d", sql -> action);
				PG_TRY();
					switch(sql -> action){
						case SQL_PEXEC_ACTION_OPENCURSOR:
							pljelog(DEBUG1,"SQL_PEXEC_ACTION_OPENCURSOR");
							pret = SPI_cursor_open(NULL, plantable[sql -> planid], values, nulls);
							break;
						case SQL_PEXEC_ACTION_EXECUTE:
							pljelog(DEBUG1,"SQL_PEXEC_ACTION_EXECUTE");
							ret = SPI_execp(plantable[sql -> planid], values, nulls, 0);
							break;
						case SQL_PEXEC_ACTION_UPDATE:
							pljelog(DEBUG1,"SQL_PEXEC_ACTION_UPDATE");
							ret = SPI_execp(plantable[sql -> planid], values, nulls, 0);
							break;
					}
				PG_CATCH();
					//send back error
				PG_END_TRY();
				pljlogging_error = 0;
				pljelog(DEBUG1,"executed.");
				//TODO: add logic here!!
				switch (sql -> action){
				case SQL_PEXEC_ACTION_EXECUTE:
					switch (ret) {
					case SPI_ERROR_ARGUMENT:
						pljelog(DEBUG1, "SPI_ERROR_ARGUMENT");
						plpgj_utl_senderror("SPI_ERROR_ARGUMENT");
						break;
					case SPI_ERROR_PARAM:
						pljelog(DEBUG1, "SPI_ERROR_PARAM");
						plpgj_utl_senderror("SPI_ERROR_PARAM");
						break;
					default:
						pljelog(DEBUG1,"success?");
						plpgj_utl_sendint(1);
						break;
					}
					break;
				case SQL_PEXEC_ACTION_UPDATE:
					plpgj_utl_sendint(ret);
					break;
				case SQL_PEXEC_ACTION_OPENCURSOR:
					if(pret == NULL) {
						elog(DEBUG1, "OPOENC 1");
						plpgj_utl_senderror("Cursor open error");
					} else {
						elog(DEBUG1, "OPOENC 2");
						plpgj_utl_sendstr(pret -> name);
						elog(DEBUG1, "OPOENC 3");
					}
					break;
				}
			}

			}
			break;
		case SQL_TYPE_CURSOR_CLOSE:
			{
				Portal		portal;
				sql_msg_cursor_close sql_c_c = (sql_msg_cursor_close) msg;

				elog(DEBUG1, "closing cursot %s", sql_c_c -> cursorname);
				portal = GetPortalByName(sql_c_c->cursorname);
				if (!PortalIsValid(portal))
				{
					pljlogging_error = 1;
					pljelog(ERROR, "the portal %s does not exist!",
							sql_c_c->cursorname);

					plpgj_utl_senderror("Cursor close error");
				}
				PG_TRY();
					PortalDrop(portal, 0);
					plpgj_utl_sendint(1);
				PG_CATCH();
					plpgj_utl_senderror("Portal drop problem");
				PG_END_TRY();

			}
			break;
		case SQL_TYPE_CURSOR_OPEN:
			{
				Portal		portal;
				sql_msg_cursor_open sql_c_o = (sql_msg_cursor_open) msg;

				/*
				 * TODO: creates constantly bidirectional cursors :(
				 */
				portal = CreatePortal(sql_c_o->cursorname, 1, 1);


			}
			break;
		case SQL_TYPE_FETCH:
			{
				
			Portal portal;
			plpgj_result result;
			int i, j;
			
			sql_msg_cursor_fetch sql_c_f = (sql_msg_cursor_fetch)msg;
			pljelog(DEBUG1, "fetching from %s", sql_c_f -> cursorname);
			
			//portal = GetPortalByName(sql_c_f -> cursorname);
			portal = SPI_cursor_find(sql_c_f -> cursorname);
			if(!PortalIsValid(portal)) {
				plpgj_utl_senderror("Cursor invalid");
				break;
			}
			elog(DEBUG1, "f 1");
			SPI_cursor_fetch(portal, /*((sql_msg_cursor_fetch)msg) -> direction ==*/ 1 , ((sql_msg_cursor_fetch)msg) -> count);
			if(SPI_processed < 0){
				plpgj_utl_senderror("Not processed");
				break;
			}
			elog(DEBUG1, "f 2");

			result = SPI_palloc(sizeof(str_plpgj_result));
			result -> msgtype = MT_RESULT;
			result -> length = sizeof(str_plpgj_result);
			result -> cols = SPI_tuptable -> tupdesc -> natts;
			result -> rows = SPI_processed;
			elog(DEBUG1, "rows: %d, cols: %d", result -> rows, result -> cols);
			result -> types = SPI_palloc(result -> cols * sizeof(char*));
			elog(DEBUG1, "f 3");
			for(j = 0; j < result -> cols; j++){
				HeapTuple typtup;
				Form_pg_type typstr;
				typtup = SearchSysCache(TYPEOID,SPI_tuptable -> tupdesc -> attrs[j] -> atttypid, 0,0,0);
				typstr = (Form_pg_type)GETSTRUCT(typtup);
				ReleaseSysCache(typtup);
				result -> types[j] = NameStr(typstr -> typname);
				elog(DEBUG1, "COLUMN [%d]: %s", j, result -> types[j]);
			}
			elog(DEBUG1, "f 4");
			if(result -> rows > 0){
				result -> data = SPI_palloc(sizeof(raw) * result -> rows);
			} else {
				result -> data = NULL;
			}
			elog(DEBUG1, "f 5");
			for(i = 0; i < result -> rows; i++){
				
				result -> data[i] = SPI_palloc(result -> cols * sizeof(struct str_raw));
				for(j = 0; j < result -> cols; j++){
					HeapTuple typtup;
					Form_pg_type typstr;
					bool isnull;
					Datum binDat;
					typtup = SearchSysCache(TYPEOID, SPI_tuptable -> tupdesc -> attrs[j] -> atttypid , 0,0,0);
					typstr = (Form_pg_type)GETSTRUCT(typtup);
					binDat = OidFunctionCall1(typstr -> typsend , SPI_getbinval(SPI_tuptable -> vals[i], SPI_tuptable -> tupdesc, j, &isnull ));
					if(isnull){
						result -> data[i][j].isnull = 1;
					} else {
						result -> data[i][j].isnull = 0;
						result -> data[i][j].length = datumGetSize(binDat, false, typstr -> typlen)
			                                + (typstr -> typbyval ? 4 : 0);;
						result -> data[i][j].data = DatumGetPointer(binDat);
					}
				}
			}
			elog(DEBUG1, "sending ansver");
			plpgj_channel_send(result);
			}
			
			break;
		default:
			pljelog(FATAL, "Unhandled SQL message.");
	}
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

#if (POSTGRES_VERSION == 74)

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

#elif (POSTGRES_VERSION == 80)



#endif

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
	error_message msg = SPI_palloc(sizeof(str_error_message));

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
	/*
	 * error_context_stack = error_context_stack -> previous;
	 */

	/*
	 * re-enable loging
	 */
	if(reenable_loging)
		pljloging = 1;

}

void plpgj_utl_sendstr(const char* str) {
				plpgj_result res;
				res = SPI_palloc(sizeof(str_plpgj_result));
				res -> msgtype = MT_RESULT;
				res -> length = sizeof(str_plpgj_result);
				res -> rows = 1;
				res -> cols = 1;
				res -> types = SPI_palloc(sizeof(char*));
				res -> types[0] = "varchar";
				res -> data = SPI_palloc(sizeof(raw));
				res -> data[0] = SPI_palloc(sizeof(struct str_raw));
				res -> data[0] -> length = 4 + strlen(str);
				res -> data[0] -> isnull = 0;
				elog(DEBUG1, "plpgj_utl_sendstr, 1");
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
				elog(DEBUG1, "plpgj_utl_sendstr, 2");
					int4typ = (Form_pg_type)GETSTRUCT(int4htp);
					ReleaseSysCache(int4htp);
				elog(DEBUG1, "plpgj_utl_sendstr, 2.1");

					rawString = SPI_palloc(sizeof(StringInfoData));
				elog(DEBUG1, "plpgj_utl_sendstr, 2.1.1");
					initStringInfo(rawString);
				elog(DEBUG1, "plpgj_utl_sendstr, 2.1.2: %d, %s", strlen(str), str);
					appendBinaryStringInfo(rawString, str, strlen(str));
				elog(DEBUG1, "plpgj_utl_sendstr, 2.1.3");
					d = OidFunctionCall1(int4typ -> typsend, OidFunctionCall1(int4typ -> typinput, PointerGetDatum(str)));
				elog(DEBUG1, "plpgj_utl_sendstr, 2.2");
					res -> data[0] -> data = DatumGetPointer(d);
				elog(DEBUG1, "plpgj_utl_sendstr, 3");
				}
				pljelog(DEBUG1,"1");
				pljloging = 0;
				plpgj_channel_send((message)res);
				elog(DEBUG1, "plpgj_utl_sendstr, 4");
				pljloging = 1;


}

void plpgj_utl_sendint(int i) {
				plpgj_result res;
				res = SPI_palloc(sizeof(str_plpgj_result));
				res -> msgtype = MT_RESULT;
				res -> length = sizeof(str_plpgj_result);
				res -> rows = 1;
				res -> cols = 1;
				res -> types = SPI_palloc(sizeof(char*));
				res -> types[0] = "int4";
				res -> data = SPI_palloc(sizeof(raw));
				res -> data[0] = SPI_palloc(sizeof(struct str_raw));
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

void plpgj_utl_senderror(const char* errorstr){
	
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

