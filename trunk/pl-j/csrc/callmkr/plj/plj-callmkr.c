/**
 * file name:		plj-callmkr.c
 * description:		PL/pgJ call message creator routine. This file
 *			was renamed from plpgj_call_maker.c
 *			It is replaceable with pljava-way of declaring java
 *			method calls. (read the readme!)
 * author:		Laszlo Hornyak
 */

#include "postgres.h"
#include "plpgj_messages.h"
#include "plpgj_message_fns.h"

#include "utils/palloc.h"
#include "catalog/pg_type.h"
#include "utils/elog.h"
#include "utils/datum.h"
#include "commands/trigger.h"
#include "fmgr.h"
#include "executor/spi.h"
#include "regex.h"

#ifndef MAX_NO_OPTS
#define MAX_NO_OPTS		10
#warning MAX_NO_OPTS not defined, using default 10
#endif

const char* __func_opt_regexp = "\\(\\w*\\)=\\(.*\\)$";
regex_t func_opt_regexp;
int plpgj_re_init = 0;

void plpgj_create_call_regex_init(){
	int ret;
	if(plpgj_re_init)
		return;
	
	elog(DEBUG1,"initializing regexp.");
	
	ret = regcomp(&func_opt_regexp, __func_opt_regexp, REG_NEWLINE);
	
	elog(DEBUG1,"pg95_regcomp: %d (0 = success)",ret);
	
	plpgj_re_init = 1;
}

Form_pg_proc glpgj_getproc(PG_FUNCTION_ARGS){
	HeapTuple proctup;
	Form_pg_proc procstruct;
	Oid funcoid;

	funcoid = fcinfo->flinfo->fn_oid;
	proctup = SearchSysCache(PROCOID, ObjectIdGetDatum(funcoid), 0,0,0 );
	procstruct = (Form_pg_proc) GETSTRUCT(proctup);
	ReleaseSysCache(proctup);
	return procstruct;
}

void plpgj_fill_callstruct(
	Form_pg_proc procStruct,
	char* classname, 
	char* methodname){

	char *func_src;
	int func_src_len;
	int fret, i;
	regmatch_t matches[MAX_NO_OPTS];

	plpgj_create_call_regex_init();

	func_src = DatumGetCString( DirectFunctionCall1(textout, PointerGetDatum( &procStruct->prosrc ) ) );
	elog(DEBUG1,func_src);
	func_src_len = strlen(func_src);

	i = 0;
	while(1){
		int namestart;
		int nameend;
		int start;
		int end;
		char tmp[100];

		elog(DEBUG1,"trace!");

		fret = regexec(&func_opt_regexp, func_src + i, MAX_NO_OPTS, matches, 0);
		if(fret)
			break;
		
		start = matches[2].rm_so; 
		end = matches[2].rm_eo;
		namestart = matches[1].rm_so; 
		nameend = matches[1].rm_eo;
		
		elog(DEBUG1,"trace 1");
		strncpy(tmp, func_src+i+namestart, nameend-namestart);
		elog(DEBUG1,"trace 2");
		tmp[nameend-namestart] = 0;
		elog(DEBUG1,"name:%s", tmp);
		//elog(DEBUG1,"namestart: %d, nameend: %d", namestart, nameend);
		//elog(DEBUG1,"start: %d, end: %d", start, end);
		strncpy(tmp, func_src+i+start, end-start);
		tmp[end-start] = 0;
		//elog(DEBUG1, "data:%s", tmp);
		if((start == -1)||(end == -1))
			break;
		
		if(strncmp(func_src+i+namestart, "class", 5) == 0){
			if(end - start > 50){
				elog(ERROR, "Too long class name");
				return;
			}
			strncpy(classname, func_src+i+start, end-start);
		}else
		if(strncmp(func_src+i+namestart, "method", 6) == 0){
			if(end - start > 50){
				elog(ERROR, "Too long method name");
				return;
			}
			strncpy(methodname, func_src+i+start, end-start);
		}else {
			elog(DEBUG1,"");
		}
		
		i+=end+1;
		//elog(DEBUG1,"moving to: %d",i);
		if(i >= func_src_len)
			break;
	}

}

pparam* plpgj_create_trigger_tuple(HeapTuple tuple, TupleDesc desc){
	int i;

	//I wonder if this protection is neccessary.
	if(desc -> natts <= 0){
		elog(WARNING, "tuple desc with zero attributes");
		return NULL;
	}

	pparam* ret;
	ret = SPI_palloc(desc -> natts * sizeof(pparam));
	for(i = 0; i < desc -> natts; i++){
		Datum binval;
		Oid typeid;
		bool isnull;
		ret[i] = palloc( sizeof(struct fnc_param) );
		elog(DEBUG1, "hello world :)");
		elog(DEBUG1, "fname: %s", SPI_fname(desc, i+1) );
		if(tuple != NULL){
			binval = SPI_getbinval(tuple, desc, i+1, &isnull);
			
			ret[i] -> data.data = binval;
			ret[i] -> data.isnull = isnull;
			ret[i] -> data.length = datumGetSize(binval, 1, 0);
			
			ret[i] -> type = SPI_gettype(desc, i + 1);
			elog(DEBUG1, "type: %s", ret[i] -> type);
		} else {
			elog(WARNING, "tupl is null");
		}
		elog(DEBUG1,"trace");
		typeid = SPI_gettypeid(desc, i+1);
		if(isnull){
			elog(DEBUG1,"null");
		} else {
			elog(DEBUG1,"not null");
		}
		
	}
	return ret;
}

int plpgj_create_trigger_tuplekind(
		trigger_callreq call,
		TriggerData* tdata){

	int i;
	elog(DEBUG1, "plpgj_create_trigger_tuplekind");
	if(tdata == NULL)
		elog(ERROR, "tdata is null");
	if(tdata -> tg_relation == NULL)
		elog(ERROR, "tg_relation is null");
	if(tdata -> tg_relation -> rd_att == NULL)
		elog(ERROR, "rd_att is null");

	call -> colcount = tdata -> tg_relation -> rd_att -> natts;
	elog(DEBUG1, "call -> colcount = %d", call -> colcount);
	call -> colnames = palloc(call -> colcount * sizeof(char*));
	call -> coltypes = palloc(call -> colcount * sizeof(char*));
	for(i = 0; i< call -> colcount; i++){
		TupleDesc desc;
		Form_pg_attribute attr;

		desc = tdata -> tg_relation -> rd_att;
		attr = desc -> attrs[i];
		call -> colnames[i] = NameStr(attr -> attname);
		call -> coltypes[i] = SPI_gettype(desc, i+1);
	}
	return 1;
}

trigger_callreq plpgj_create_trigger_call(PG_FUNCTION_ARGS){
	TriggerData* tdata;
	trigger_callreq ret;
	Form_pg_proc procStruct;
	Oid relid;

	ret = SPI_palloc(sizeof(str_msg_trigger_callreq));
	ret -> msgtype = MT_TRIGREQ;
	ret -> length = sizeof(str_msg_trigger_callreq);

	tdata = (TriggerData*)fcinfo -> context;
	if(TRIGGER_FIRED_AFTER(tdata -> tg_event)){
		ret -> actionorder = PLPGJ_TRIGGER_ACTIONORDER_AFTER;
	} else {
		ret -> actionorder = PLPGJ_TRIGGER_ACTIONORDER_BEFORE;
	}

	if(TRIGGER_FIRED_FOR_ROW(tdata -> tg_event)){
		ret -> row = PLPGJ_TRIGGER_STARTED_FOR_ROW;
	} else {
		ret -> row = PLPGJ_TRIGGER_STARTED_FOR_STATEMENT;
	}

	//set relation name
	ret -> tablename = SPI_getrelname ( tdata -> tg_relation );
	elog(DEBUG1, "relation: %s", ret -> tablename);

	if(tdata -> tg_trigtuple != NULL) {
		plpgj_create_trigger_tuplekind(ret, tdata/* -> tg_trigtuple*/);
	} else if (tdata -> tg_newtuple != NULL) {
		plpgj_create_trigger_tuplekind(ret, tdata/* -> tg_newtuple*/);
	}
	
	if(TRIGGER_FIRED_BY_INSERT(tdata -> tg_event)){
		elog(DEBUG1, "insert trigger");
		if(tdata -> tg_trigtuple == NULL){
			elog(DEBUG1, "this is where trigtuple should not be null.");
		} else {
			elog(DEBUG1, "tg_trigtuple is not null!!");
		}
		ret -> reason = PLPGJ_TRIGGER_REASON_INSERT;
		ret -> _new = plpgj_create_trigger_tuple(
			tdata -> tg_trigtuple,
			tdata -> tg_relation -> rd_att);
	} else if (TRIGGER_FIRED_BY_UPDATE(tdata -> tg_event)){
		ret -> reason = PLPGJ_TRIGGER_REASON_UPDATE;
		ret -> _old = plpgj_create_trigger_tuple(
			tdata -> tg_trigtuple,
			tdata -> tg_relation -> rd_att);
		ret -> _new = plpgj_create_trigger_tuple(
			tdata -> tg_newtuple,
			tdata -> tg_relation -> rd_att);

	} else {
		ret -> reason = PLPGJ_TRIGGER_REASON_DELETE;
		ret -> _old = plpgj_create_trigger_tuple(
			tdata -> tg_trigtuple,
			tdata -> tg_relation -> rd_att);
	}


	procStruct = glpgj_getproc(fcinfo);
	plpgj_fill_callstruct(procStruct, ret -> classname, ret -> methodname);
	return ret;
}

callreq plpgj_create_call(PG_FUNCTION_ARGS){
	callreq ret;
	Oid funcoid;
	HeapTuple proctup;
	Form_pg_proc procstruct;
	HeapTuple retTypetup;
	Form_pg_type rettype;
	char *func_src;
	int i, fret, func_src_len;
	regmatch_t matches[MAX_NO_OPTS];
	i = 0;
	
	ret = SPI_palloc(sizeof(str_msg_callreq));
	memset(ret, 0, sizeof(str_msg_callreq));
	ret->msgtype = MT_CALLREQ;
	ret->length = sizeof(str_msg_callreq);
	
	funcoid = fcinfo->flinfo->fn_oid;
	proctup = SearchSysCache(PROCOID, ObjectIdGetDatum(funcoid), 0,0,0 );
	procstruct = (Form_pg_proc) GETSTRUCT(proctup);
	ReleaseSysCache(proctup);
	func_src = DatumGetCString( DirectFunctionCall1(textout, PointerGetDatum( &procstruct->prosrc ) ) );
	
	func_src_len = strlen(func_src);
	
	plpgj_fill_callstruct(procstruct, ret -> classname, ret -> methodname);
	
	ret -> nrOfParams = fcinfo -> nargs;
	if(ret -> nrOfParams > 0)
		ret -> parameters = SPI_palloc( 
			(ret -> nrOfParams) * sizeof(struct fnc_param) );
	
	// fill in parameter type structures
	for(i = 0; i < ret -> nrOfParams; i++) {
		Form_pg_type paramtype;
		HeapTuple typeTup;
		typeTup = SearchSysCache(TYPEOID, ObjectIdGetDatum(procstruct -> proargtypes[i] ), 0, 0, 0 );
		if (!HeapTupleIsValid(typeTup))
			elog(ERROR, "INVALID TYPE OID?");

		paramtype = (Form_pg_type) GETSTRUCT(typeTup);
		if(fcinfo -> argnull[i]) {
			ret -> parameters[i].data.isnull = 1;
			ret -> parameters[i].data.data = NULL;
			ret -> parameters[i].data.length = 0;
		} else {
			Datum sendDatum;
			sendDatum = OidFunctionCall1(paramtype -> typsend, fcinfo -> arg[i]);
			ret -> parameters[i].data.isnull = 0;
			ret -> parameters[i].data.data = 
				DatumGetPointer(sendDatum);
			elog(DEBUG1,"alive");
                        ret -> parameters[i].data.length = 
				datumGetSize(fcinfo -> arg[i], paramtype -> typbyval, paramtype -> typlen);
			elog(DEBUG1,"alive");
			//if param is not null, get it from
		}

		ret -> parameters[i].type = paramtype -> typname.data;
		ReleaseSysCache(typeTup);
	}

	// fill in expected return type
	retTypetup = SearchSysCache(TYPEOID, ObjectIdGetDatum(procstruct -> prorettype), 0, 0, 0);
	if (!HeapTupleIsValid(retTypetup))
		elog(ERROR, "return type is invalid?");
	rettype = (Form_pg_type) GETSTRUCT(retTypetup);
	ret -> expect = (char*)rettype -> typname.data;
	ReleaseSysCache(retTypetup);

	//elog(DEBUG1, func_src);
	return ret;
}
