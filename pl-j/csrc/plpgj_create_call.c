/**
 * file name:			plpgj_create_call.c
 * description:			PL/pgJ call message creator routine.
 * author:				Laszlo Hornyak
 */

#include "postgres.h"
#include "plpgj_messages.h"
#include "plpgj_message_fns.h"

#include "utils/palloc.h"
#include "catalog/pg_type.h"
#include "utils/elog.h"
#include "utils/datum.h"
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

callreq plpgj_create_call(PG_FUNCTION_ARGS){
	callreq ret;
	Oid funcoid;
	HeapTuple proctup;
	Form_pg_proc procstruct;
	char *func_src;
	int i, fret, func_src_len;
	regmatch_t matches[MAX_NO_OPTS];
	i = 0;
	
	plpgj_create_call_regex_init();
	
	ret = palloc(sizeof(str_msg_callreq));
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
	
	//need to get EXPECT field!!
	
	//elog(DEBUG1, func_src);
	return ret;
}

