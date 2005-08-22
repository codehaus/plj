/**
 * Message handler implementation.
 * 
 */

#include "msghandler.h"
#include "plpgj_hook.h"
#include "pljelog.h"
#include "plpgj_channel.h"
#include "postgres.h"
#include "c.h"
#include "nodes/makefuncs.h"
#include "parser/parse_type.h"
#include "plantable.h"
#include "plpgj_messages.h"
#include "lib/stringinfo.h"
#include "executor/spi_priv.h"

plpgj_handler handlertab = NULL;
int msg_handler_init = 0;

message handle_invalid_message(sql_msg msg) {
	elog(ERROR, "[plj core] invalid message type: %d", msg -> sqltype);
	return NULL;
}

message handle_statement_message(sql_msg msg) {
	elog(DEBUG1, "[plj core] runing statement: %s", ((sql_msg_statement) msg)->statement);
	PG_TRY();
		SPI_exec(((sql_msg_statement) msg)->statement, 0);
		plpgj_utl_sendint(SPI_processed);
	PG_CATCH();
		handle_exception();
	PG_END_TRY();
	return NULL;
}

message handle_cursor_close_message(sql_msg msg) {
	Portal          portal;
	sql_msg_cursor_close sql_c_c = (sql_msg_cursor_close) msg;

	portal = GetPortalByName(sql_c_c->cursorname);
	if (!PortalIsValid(portal))
	{
		plpgj_utl_senderror("Cursor close error");
	}
	PG_TRY();
		PortalDrop(portal, 0);
		plpgj_utl_sendint(1);
	PG_CATCH();
		//      plpgj_utl_senderror("Portal drop problem");
		handle_exception();
	PG_END_TRY();
	return NULL;
}

message handle_fetch_message(sql_msg msg) {
	Portal portal;
	plpgj_result result;
	int i, j;
	SPITupleTable* res_tuptable;
	sql_msg_cursor_fetch sql_c_f = (sql_msg_cursor_fetch)msg;
	pljelog(DEBUG1, "fetching from %s", sql_c_f -> cursorname);
	portal = SPI_cursor_find(sql_c_f -> cursorname);
	if(!PortalIsValid(portal)) {
		plpgj_utl_senderror("Cursor invalid");
		return NULL;
	}
	SPI_cursor_fetch(portal, !(((sql_msg_cursor_fetch)msg) -> direction) , ((sql_msg_cursor_fetch)msg) -> count);
	res_tuptable = SPI_tuptable;
	SPI_tuptable = NULL;

	if(SPI_processed < 0){
		plpgj_utl_senderror("Not processed");
		return NULL;
	}

	result = palloc(sizeof(str_plpgj_result));
	result -> msgtype = MT_RESULT;
	result -> length = sizeof(str_plpgj_result);
	result -> cols = res_tuptable -> tupdesc -> natts;
	result -> rows = SPI_processed;
	result -> types = palloc(result -> cols * sizeof(char*));
	for(j = 0; j < result -> cols; j++){
		HeapTuple typtup;
		Form_pg_type typstr;
		typtup = SearchSysCache(TYPEOID,res_tuptable -> tupdesc -> attrs[j] -> atttypid, 0,0,0);
		typstr = (Form_pg_type)GETSTRUCT(typtup);
		ReleaseSysCache(typtup);
		result -> types[j] = NameStr(typstr -> typname);
	}

	if(result -> rows > 0){
		result -> data = palloc(sizeof(raw) * result -> rows);
	} else {
		result -> data = NULL;
	}

	for(i = 0; i < result -> rows; i++){
		result -> data[i] = palloc(result -> cols * sizeof(struct str_raw));
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
	plpgj_channel_send((message)result);
	return NULL;

}

message handle_cursor_open(sql_msg msg) {
	Portal          portal;
	sql_msg_cursor_open sql_c_o = (sql_msg_cursor_open) msg;
	char* cname = sql_c_o -> cursorname;
	void* plan;

	elog(DEBUG1, "[plj core - cursor open] ");

	if(strlen(cname) == 0)
		cname = NULL;
	
	elog(DEBUG1, " -> %s", cname);

	/*
	 * TODO: creates constantly bidirectional cursors :(
	 */

	PG_TRY();
	{
		elog(DEBUG1,"[plj core - cursor open] -> %s", sql_c_o -> query);
		plan = SPI_prepare(sql_c_o -> query, 0, NULL);
		elog(DEBUG1,"[plj core - cursor open] -> prepared");
#if (PG_MAJOR_VERSION < 8)
		portal = //CreatePortal(sql_c_o->cursorname, 1, 1);
		SPI_cursor_open(cname, plan, NULL, NULL);
#else
		portal =
			SPI_cursor_open(cname, plan, NULL, NULL, true);
#endif
		elog(DEBUG1,"[plj core - cursor open] portal opened");
	}
	PG_CATCH();
	{
		elog(DEBUG1,"[plj core - cursor open] error caught at cursor opening");
		handle_exception();
		return NULL;
	}
	PG_END_TRY();

	plpgj_utl_sendstr(portal -> name);
	return NULL;
	
}

message handle_prepare_message(sql_msg msg) {
	Oid *argtypes;
	sql_msg_prepare prep;
	int i;
	TypeName *typnam;
	void *plan;
	int planid;

	prep = (sql_msg_prepare) msg;
	argtypes = prep -> ntypes == 0 ? NULL : palloc(prep -> ntypes * sizeof(Oid) );
	for(i = 0; i < prep -> ntypes; i++) {
		typnam = makeTypeName( prep -> types[i] );
		argtypes[i] = LookupTypeName(typnam);
	}

	elog(DEBUG1, "[plj core] SQL_TYPE_PREPARE");
           
	PG_TRY();
                     
		plan = SPI_prepare( prep -> statement, prep -> ntypes, argtypes);
		plan = SPI_saveplan(plan);
		elog(DEBUG1,"success");
		planid = store_plantable(plan);
		plpgj_utl_sendint(planid);

	PG_CATCH();
		elog(DEBUG1,"failure");
		handle_exception();
	PG_END_TRY();

	return NULL;

}

message handle_pexecute_message(sql_msg msg){
	char* nulls;
	Datum* values;
	int i;
	sql_pexecute sql = (sql_pexecute)msg;

	elog(DEBUG1, "handle_pexecute_message");

	if(sql -> nparams == 0){
		values = NULL;
		nulls = NULL;
	} else {
		values = palloc( (sql -> nparams+1) * sizeof(Datum) );
		nulls = palloc( (sql -> nparams+1) * sizeof(char) );
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

			nulls[i] = ' ';
			typnam = makeTypeName(sql -> params[i].type );
			typoid = LookupTypeName(typnam);
			typtup = SearchSysCache(TYPEOID, typoid, 0, 0, 0);
			typstr = (Form_pg_type)GETSTRUCT(typtup);
			ReleaseSysCache(typtup);
			
			rawString = palloc(sizeof(StringInfoData));
			initStringInfo(rawString);
			appendBinaryStringInfo(rawString, sql -> params[i].data.data, sql -> params[i].data.length);
			values[i] = OidFunctionCall1(typstr -> typreceive, PointerGetDatum(rawString));
		} 
	}

	if(!plantable_entry_valid(sql -> planid)) {
		elog(WARNING,"Invalidated plan id: %d", sql -> planid);
		plpgj_utl_senderror("Invalidated plan id");
		return NULL;
	}

	if(nulls != NULL)
		nulls[sql -> nparams + 1] = 0;

	{
		Portal pret;
		PG_TRY();
			_SPI_plan* pln;
			pln = (_SPI_plan*) plantable[sql -> planid];
			if (sql -> action == SQL_PEXEC_ACTION_OPENCURSOR) {
					elog(DEBUG1, "[plj - sql] opening cursor.");
					#if PG_MAJOR_VERSION >= 8
					pret = SPI_cursor_open(NULL, 
						plantable[sql -> planid], values, nulls == NULL ? "" : nulls, true);
					#else
					pret = SPI_cursor_open(NULL, 
						plantable[sql -> planid], values, nulls == NULL ? "" : nulls);
					#endif
					if(pret == NULL) {
						plpgj_utl_senderror("Cursor open error");
					} else {
						plpgj_utl_sendstr(pret -> name);
					}
			} else if (sql -> action == SQL_PEXEC_ACTION_UPDATE) {
				elog(DEBUG1, "[plj - sql] runing update");
				int ret = SPI_execute_plan(plantable[sql -> planid], values, nulls == NULL ? "" : nulls, false, sql -> nparams);
				plpgj_utl_sendint(ret);
			} else if (sql -> action == SQL_PEXEC_ACTION_EXECUTE) {
				elog(DEBUG1, "[plj - sql] runing statement");
				int ret = SPI_execute_plan(plantable[sql -> planid], values, nulls == NULL ? "" : nulls, false, sql -> nparams);
				elog(DEBUG1, "[plj - sql] statement result: %d", ret);
				if(ret < 0) {
					if(ret == SPI_ERROR_ARGUMENT)
						plpgj_utl_senderror("Argument error");
					else if(ret == SPI_ERROR_COPY)
						plpgj_utl_senderror("COPY FROM/TO stdout requested");
					else if(ret == SPI_ERROR_CURSOR)
						plpgj_utl_senderror("DECLARE, CLOSE or FETCH requested");
					else if(ret == SPI_ERROR_TRANSACTION)
						plpgj_utl_senderror("Transaction operations are not available as prepared statements");
					else if(ret == SPI_ERROR_OPUNKNOWN)
						plpgj_utl_senderror(
							"Command type unknown. Please report this error to http://jira.codehaus.org/browse/PLJ");
					else if(ret == SPI_ERROR_UNCONNECTED)
						plpgj_utl_senderror("Disconnected from database");
					else
						plpgj_utl_senderror("Unknown error type");
						
				} else
					plpgj_utl_sendint(ret);
			} else {
				elog(WARNING, "[plj - sql] Wrong type of sql action: %d", sql -> action);
				plpgj_utl_senderror("Wrong type of sql action");
			}
		PG_CATCH();
			_SPI_plan* pln;
			elog(DEBUG1, "[plj core - plan exec] error at execution.");
			pln = (_SPI_plan*) plantable[sql -> planid];
			elog(DEBUG1, "%s",  pln -> query );
			handle_exception();
		PG_END_TRY();
	}
	return NULL;
}

message handle_unprepare_message(sql_msg msg) {
	sql_msg_unprepare unprep = (sql_msg_unprepare) msg;
	elog(DEBUG1, "[plj core] unpreparing plan %d", unprep -> planid);
	remove_plantable_entry(unprep -> planid);
	return NULL;
}

void message_handler_init(){
	int i;

	elog(DEBUG1, "[pl-j - sql] initializing handler table");

	handlertab = malloc(SQL_TYPE_MAX * sizeof(plpgj_handler_rec));

	for(i=0; i<SQL_TYPE_MAX; i++) {
		handlertab[i].desc = "Invalid or not handled type";
		handlertab[i].handler = handle_invalid_message;
	}

	handlertab[SQL_TYPE_STATEMENT].desc = "statement message handler";
	handlertab[SQL_TYPE_STATEMENT].handler = handle_statement_message;

	handlertab[SQL_TYPE_CURSOR_CLOSE].desc = "cursor close handler";
	handlertab[SQL_TYPE_CURSOR_CLOSE].handler = handle_cursor_close_message;

	handlertab[SQL_TYPE_FETCH].desc = "fetch handler";
	handlertab[SQL_TYPE_FETCH].handler = handle_fetch_message;

	handlertab[SQL_TYPE_CURSOR_OPEN].desc = "cursor open handler";
	handlertab[SQL_TYPE_CURSOR_OPEN].handler = handle_cursor_open;

	handlertab[SQL_TYPE_PREPARE].desc = "prepare call";
	handlertab[SQL_TYPE_PREPARE].handler = handle_prepare_message;

	handlertab[SQL_TYPE_PEXECUTE].desc = "execute prepared statement";
	handlertab[SQL_TYPE_PEXECUTE].handler = handle_pexecute_message;

	handlertab[SQL_TYPE_UNPREPARE].desc = "close prepared statement";
	handlertab[SQL_TYPE_UNPREPARE].handler = handle_unprepare_message;

	elog(DEBUG1, "[pl-j - sql] init done");
	msg_handler_init = 1;
}

message handle_message(sql_msg msg) {
	int msg_type = msg -> sqltype;
	
	if(!msg_handler_init)
		message_handler_init();

	if(msg_type > SQL_TYPE_MAX)
		return handle_invalid_message(msg);

	elog(DEBUG1,"calling handler: %d", msg_type);
	elog(DEBUG1,"desc: %s", handlertab[msg_type].desc);
	return handlertab[msg_type].handler(msg);
}


