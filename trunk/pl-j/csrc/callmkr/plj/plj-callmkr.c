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

/*void plpgj_fill_callstruct(
	ProcStruct struct,
	char** classname, 
	char** methodname){

	char *func_src;
	regmatch_t matches[MAX_NO_OPTS];
	
}*/

trigger_callreq plpgj_create_trigger_call(PG_FUNCTION_ARGS){
	TriggerData* tdata;
	trigger_callreq ret;

	ret = SPI_palloc(sizeof(str_msg_trigger_callreq));
	tdata = (TriggerData*)fcinfo -> context;
	if(TRIGGER_FIRED_AFTER(tdata)){
		ret -> actionorder = PLPGJ_TRIGGER_ACTIONORDER_AFTER;
	} else {
		ret -> actionorder = PLPGJ_TRIGGER_ACTIONORDER_BEFORE;
	}

	if(TRIGGER_FIRED_FOR_ROW(tdata)){
		ret -> row = PLPGJ_TRIGGER_STARTED_FOR_ROW;
	} else {
		ret -> row = PLPGJ_TRIGGER_STARTED_FOR_STATEMENT;
	}

	if(TRIGGER_FIRED_BY_INSERT(tdata)){
		ret -> reason = PLPGJ_TRIGGER_REASON_INSERT;
	} else if (TRIGGER_FIRED_BY_UPDATE(tdata)){
		
	}

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
	
	plpgj_create_call_regex_init();
	
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
	
	while(1){
		int namestart;
		int nameend;
		int start;
		int end;
		char tmp[100];
		
		fret = regexec(&func_opt_regexp, func_src + i, MAX_NO_OPTS, matches, 0);
		if(fret)
			break;
		
		start = matches[2].rm_so; 
		end = matches[2].rm_eo;
		namestart = matches[1].rm_so; 
		nameend = matches[1].rm_eo;
		
		strncpy(tmp, func_src+i+namestart, nameend-namestart);
		tmp[nameend-namestart] = 0;
		//elog(DEBUG1,"name:%s", tmp);
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
				return NULL;
			}
			strncpy(ret->classname, func_src+i+start, end-start);
			//elog(DEBUG1,"class <- %s", ret->classname);
		}else
		if(strncmp(func_src+i+namestart, "method", 6) == 0){
			if(end - start > 50){
				elog(ERROR, "Too long method name");
				return NULL;
			}
			strncpy(ret->methodname, func_src+i+start, end-start);
			//elog(DEBUG1,"method <- %s", ret->methodname);
		}else
		if(strncmp(func_src+i+namestart, "oneway", 6) == 0){
			//elog(DEBUG1, "nothing to do with oneway calls");
		}else{
			elog(DEBUG1,"didn`t match any");
		}
		
		i+=end+1;
		//elog(DEBUG1,"moving to: %d",i);
		if(i >= func_src_len)
			break;
	}
	
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

