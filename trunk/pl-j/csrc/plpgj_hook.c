
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
void plpgj_utl_senderror(char*);

void		plpgj_EOXactCallBack(bool isCommit, void *arg);

#if (POSTGRES_VERSION == 74)
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

#if (POSTGRES_VERSION == 74)
#define plpgj_return_cleanup		if(envstack_isempty()) { unreg_error_callback(); UnregisterEOXactCallback(plpgj_EOXactCallBack, NULL); elog(DEBUG1, "plpgj_return_cleanup: done"); }
#else
#define plpgj_return_cleanup		
#endif


#if (POSTGRES_VERSION == 74)

void reg_error_callback() {
	if (!callbacks_init)
	{
	ErrorContextCallback *mycallback;
		mycallback = SPI_palloc(sizeof(ErrorContextCallback));
		mycallback->previous = error_context_stack;
		mycallback->callback = plpgj_ErrorContextCallback;
		mycallback->arg = NULL;

		error_context_stack = mycallback;

	callbacks_init = 1;
	}

}

#endif


#if (POSTGRES_VERSION != 74)

void handle_exception(void){
	ErrorData  *edata;
	edata = CopyErrorData();
	plpgj_utl_senderror(edata -> message);

	FlushErrorState();

//	RollbackAndReleaseCurrentSubTransaction();
	SPI_restore_connection();
}

#else
#endif

void sql_cursor_open(message msg){
				Portal		portal;
				sql_msg_cursor_open sql_c_o = (sql_msg_cursor_open) msg;
				char* cname = sql_c_o -> cursorname;
				void* plan;
				char success = 0;

				elog(DEBUG1, "[plj core - cursor open] ");

				if(strlen(cname) == 0){
					cname = NULL;
				}
				elog(DEBUG1, " -> %s", cname);

				/*
				 * TODO: creates constantly bidirectional cursors :(
				 */

				PG_TRY();
				{
					elog(DEBUG1,"[plj core - cursor open] -> %s", sql_c_o -> query);
					plan = SPI_prepare(sql_c_o -> query, 0, NULL);
					elog(DEBUG1,"[plj core - cursor open] -> prepared");
#if (POSTGRES_VERSION == 74)
					portal = //CreatePortal(sql_c_o->cursorname, 1, 1);
						SPI_cursor_open(cname, plan, NULL, NULL);
#else
					portal =
						SPI_cursor_open(cname, plan, NULL, NULL, true);
#endif
					elog(DEBUG1,"[plj core - cursor open] portal opened");
					success = 1;
				}
				PG_CATCH();
				{
					elog(DEBUG1,"[plj core - cursor open] error caught at cursor opening");
					//plpgj_utl_senderror("");
					handle_exception();
				}
				PG_END_TRY();
				
				if(success){
					plpgj_utl_sendstr(portal -> name);
					elog(DEBUG1," -> ansvered");
				}


}


#if (POSTGRES_VERSION == 74)
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

#if (POSTGRES_VERSION == 74)
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

#if (POSTGRES_VERSION == 74)
	if(plpgj_tx_externalize && envstack_isempty())
		RegisterEOXactCallback(plpgj_EOXactCallBack, NULL);
#endif


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
	}
	else
	{
		pljelog(DEBUG1, "creating call structure");
		req = (message) plpgj_create_call(fcinfo);
	}

	pljelog(DEBUG1, "7");
	plpgj_channel_send((message) req);
	free_message(req);

	pljelog(DEBUG1, "7");
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
				//this wont run.
				PG_RETURN_NULL();
		}
		elog(DEBUG1, "[plj core] free message");
		free_message(ansver);
		elog(DEBUG1, "[plj core] free message done");

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
				
				PG_TRY();
					
					plan = SPI_prepare( prep -> statement, prep -> ntypes, argtypes);
					plan = SPI_saveplan(plan);
					elog(DEBUG1,"success");
				PG_CATCH();
					elog(DEBUG1,"failure");
					handle_exception();
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
				if(sql -> nparams == 0){
					values = NULL;
					nulls = NULL;
				} else {
					values = SPI_palloc( sql -> nparams * sizeof(Datum));
					nulls = SPI_palloc( sql -> nparams * sizeof(char)) + 1;
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
						typnam = makeTypeName(sql -> params[i].type );
						typoid = LookupTypeName(typnam);
						typtup = SearchSysCache(TYPEOID, typoid, 0, 0, 0);
						typstr = (Form_pg_type)GETSTRUCT(typtup);
						ReleaseSysCache(typtup);
						
						rawString = SPI_palloc(sizeof(StringInfoData));
						initStringInfo(rawString);
						appendBinaryStringInfo(rawString, sql -> params[i].data.data, sql -> params[i].data.length);
						values[i] = OidFunctionCall1(typstr -> typreceive, PointerGetDatum(rawString));
						
						
					}
				}
				elog(DEBUG1,"sql->nparams=%d", sql -> nparams);
				elog(DEBUG1,"sql->planid=%d", sql -> planid);
				if(!plantable_entry_valid(sql -> planid)){
					elog(WARNING,"Invalidated plan id: %d", sql -> planid);
					plpgj_utl_senderror("Invalidated plan id");
					break;
				}
				if(nulls != NULL)
					nulls[sql -> nparams + 1] = 0;

				pljelog(DEBUG1,"executing.");
				{
				int ret;
				Portal pret;
				pljlogging_error = 1;
				PG_TRY();
					_SPI_plan* pln;
					pln = (_SPI_plan*) plantable[sql -> planid];
					elog(DEBUG1, "plan: (%d) %s", sql -> planid, pln -> query );
					switch(sql -> action){
						case SQL_PEXEC_ACTION_OPENCURSOR:
							pljelog(DEBUG1,"SQL_PEXEC_ACTION_OPENCURSOR");
							
							if(plantable[sql -> planid] == NULL){
								elog(WARNING, "hoppa");
							} else {
								elog(WARNING, "oksa");
							}
							if(values == NULL){
								elog(WARNING, "values is null");
							} else {
								elog(WARNING, "values not null");
							}

							#if POSTGRES_VERSION >= 80
							pret = SPI_cursor_open(NULL, plantable[sql -> planid], values, nulls == NULL ? "" : nulls, true);
							#else
							pret = SPI_cursor_open(NULL, plantable[sql -> planid], values, nulls == NULL ? "" : nulls);
							#endif
							elog(DEBUG1, "SPI_processed: %d", SPI_processed);
							if(pret == NULL) {
								plpgj_utl_senderror("Cursor open error");
							} else {
								plpgj_utl_sendstr(pret -> name);
							}
							break;
						case SQL_PEXEC_ACTION_EXECUTE:
							pljelog(DEBUG1,"SQL_PEXEC_ACTION_EXECUTE: %d, >%s<", sql -> planid, nulls == NULL ? "null" : nulls);
							ret = SPI_execp(plantable[sql -> planid], values, nulls, 0);
							pljelog(DEBUG1,"SQL_PEXEC_ACTION_EXECUTE: %d, %s", sql -> planid, nulls == NULL ? "null" : nulls);
							switch (ret) {
							case SPI_ERROR_ARGUMENT:
								elog(DEBUG1, "SPI_ERROR_ARGUMENT");
								plpgj_utl_senderror("SPI_ERROR_ARGUMENT");
							break;
							case SPI_ERROR_PARAM:
								elog(DEBUG1, "SPI_ERROR_PARAM");
								plpgj_utl_senderror("SPI_ERROR_PARAM");
							break;
							default:
								elog(DEBUG1,"success?");
								plpgj_utl_sendint(1);
							break;
							}
						break;
						case SQL_PEXEC_ACTION_UPDATE:
							pljelog(DEBUG1,"SQL_PEXEC_ACTION_UPDATE");
							ret = SPI_execp(plantable[sql -> planid], values, nulls, 0);
							plpgj_utl_sendint(ret);
							break;
					}
				PG_CATCH();
					_SPI_plan* pln;
					//plpgj_utl_senderror(plj_exceptionreason());
					elog(WARNING, "[plj core - plan exec] error at execution.");
					pln = (_SPI_plan*) plantable[sql -> planid];
					elog(DEBUG1, "%s",  pln -> query );
					//FlushErrorState();
					handle_exception();
				PG_END_TRY();
				pljlogging_error = 0;
				pljelog(DEBUG1,"executed.");
			}

			}
			break;
		case SQL_TYPE_CURSOR_CLOSE:
			{
				Portal		portal;
				sql_msg_cursor_close sql_c_c = (sql_msg_cursor_close) msg;

				portal = GetPortalByName(sql_c_c->cursorname);
				if (!PortalIsValid(portal))
				{
					pljlogging_error = 1;

					plpgj_utl_senderror("Cursor close error");
				}
				PG_TRY();
					PortalDrop(portal, 0);
					plpgj_utl_sendint(1);
				PG_CATCH();
				//	plpgj_utl_senderror("Portal drop problem");
					handle_exception();
				PG_END_TRY();

			}
			break;
		case SQL_TYPE_CURSOR_OPEN:
			{
				sql_cursor_open(msg);
			}
			break;
		case SQL_TYPE_FETCH:
			{
			
			Portal portal;
			plpgj_result result;
			int i, j;
			SPITupleTable* res_tuptable;
			
			sql_msg_cursor_fetch sql_c_f = (sql_msg_cursor_fetch)msg;
			pljelog(DEBUG1, "fetching from %s", sql_c_f -> cursorname);
			
			//portal = GetPortalByName(sql_c_f -> cursorname);
			portal = SPI_cursor_find(sql_c_f -> cursorname);
			if(!PortalIsValid(portal)) {
				plpgj_utl_senderror("Cursor invalid");
				break;
			}
			elog(DEBUG1, "f 1: %d", ((sql_msg_cursor_fetch)msg) -> count);
			SPI_cursor_fetch(portal, !(((sql_msg_cursor_fetch)msg) -> direction) , ((sql_msg_cursor_fetch)msg) -> count);
			res_tuptable = SPI_tuptable;
			SPI_tuptable = NULL;

			if(SPI_processed < 0){
				plpgj_utl_senderror("Not processed");
				break;
			}

			result = SPI_palloc(sizeof(str_plpgj_result));
			result -> msgtype = MT_RESULT;
			result -> length = sizeof(str_plpgj_result);
			result -> cols = res_tuptable -> tupdesc -> natts;
			result -> rows = SPI_processed;
			result -> types = SPI_palloc(result -> cols * sizeof(char*));
			for(j = 0; j < result -> cols; j++){
				HeapTuple typtup;
				Form_pg_type typstr;
				typtup = SearchSysCache(TYPEOID,res_tuptable -> tupdesc -> attrs[j] -> atttypid, 0,0,0);
				typstr = (Form_pg_type)GETSTRUCT(typtup);
				ReleaseSysCache(typtup);
				result -> types[j] = NameStr(typstr -> typname);
			}
			if(result -> rows > 0){
				result -> data = SPI_palloc(sizeof(raw) * result -> rows);
			} else {
				result -> data = NULL;
			}
			for(i = 0; i < result -> rows; i++){
				
				result -> data[i] = SPI_palloc(result -> cols * sizeof(struct str_raw));
				for(j = 0; j < result -> cols; j++){
					HeapTuple typtup;
					Form_pg_type typstr;
					bool isnull;
					Datum binDat;
					typtup = SearchSysCache(TYPEOID, res_tuptable -> tupdesc -> attrs[j] -> atttypid , 0,0,0);
					typstr = (Form_pg_type)GETSTRUCT(typtup);
					ReleaseSysCache(typtup);
					binDat = SPI_getbinval(res_tuptable -> vals[i], res_tuptable -> tupdesc, j +1, &isnull );
					binDat = OidFunctionCall1(typstr -> typsend, binDat);
					if(isnull){
						result -> data[i][j].isnull = 1;
					} else {
						result -> data[i][j].isnull = 0;
						result -> data[i][j].length = datumGetSize(binDat, false, typstr -> typlen)
			                                + (typstr -> typbyval ? 4 : 0);
						result -> data[i][j].data = DatumGetPointer(binDat);
					}
				}
			}
			elog(DEBUG1, "sending ansver");
			plpgj_channel_send(result);
			}
			
			break;
		case SQL_TYPE_UNPREPARE: {
			sql_msg_unprepare unprep = (sql_msg_unprepare) msg;
			remove_plantable_entry(unprep -> planid);
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

#if (POSTGRES_VERSION == 74)
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

