#include <stdlib.h>
#include "create_any.h"
#include "error.h"
#include "result.h"
#include "log.h"

extern jclass callreq_class;
extern jclass error_class;
extern jmethodID error_get_message;
extern jmethodID error_get_stacktrace;
extern jmethodID error_get_classname;
extern jmethodID message_get_sid;

extern jclass result_class;
extern jmethodID result_setsize;
extern jmethodID result_set;
extern jmethodID result_get;
extern jmethodID result_getrows;
extern jmethodID result_getcolumns;


extern jclass message_sql;
extern jclass message_sql_cursor;
extern jclass message_sql_close;
extern jclass message_sql_open_wia_sql;
extern jclass message_sql_fetch;

extern jclass field_class;
	extern jmethodID field_get;
	extern jmethodID field_rdbmstype;


/* ... */

CORBA_any* create_any(jobject obj, jobject threadobj, JNIEnv* env){
	CORBA_any * ret;
	jboolean copy = 0;
	
	ret = CORBA_any_alloc();
	
	//printf(" cp 1\n\n");
	worker_nlog(env, threadobj, WNL_DEBUG, "creating any struct");
	
	if((*env)->IsInstanceOf(env, obj, error_class)){
		//local tmp vars.
		org_pgj_corba_errorstruct* es;
		jstring str_sid;
		jstring str_cn;
		jstring str_msg;
		jstring str_st;
		
		es = malloc(sizeof(org_pgj_corba_errorstruct));
		
		//set sid
		str_sid = (*env)->CallObjectMethod(env, obj, message_get_sid);
		es->sid = (*env)->GetStringUTFChars(env, str_sid, &copy);
		//set class name
		str_cn = (*env)->CallObjectMethod(env, obj, error_get_classname);
		es->classname = (*env)->GetStringUTFChars(env, str_cn, &copy);
		//set message txt
		str_msg = (*env)->CallObjectMethod(env, obj, error_get_message);
		es->message = (*env)->GetStringUTFChars(env, str_msg, &copy);
		//set stack trace
		str_st = (*env)->CallObjectMethod(env, obj, message_get_sid);
		es->stacktrace = (*env)->GetStringUTFChars(env, str_st, &copy);
		
		ret->_type = TC_org_pgj_corba_errorstruct;
		ret->_value = es;
		//ret->_release = 1;
	} else if( (*env)->IsInstanceOf(env, obj, result_class) ){
		
		org_pgj_corba_result* rs;
		jstring str_sid;				//the sid
		jint rows;						//the nr of rows
		jint columns;					//the nr of columns
		int i,j;						//iterators

		
		rs = malloc(sizeof(org_pgj_corba_result));
		
		//printf("creating result\n\n");
		worker_nlog(env, threadobj, WNL_DEBUG, "creating result");
		
		rows = (*env)->CallIntMethod(env, obj, result_getrows);
		columns = (*env)->CallIntMethod(env, obj, result_getcolumns);
		
		worker_nlog(env, threadobj, WNL_DEBUG, "ok");
		
		//set sid
		str_sid = (*env)->CallObjectMethod(env, obj, message_get_sid);
		rs->sid = (*env)->GetStringUTFChars(env, str_sid, &copy);
		//printf("sid was set\n\n");
		worker_nlog(env, threadobj, WNL_DEBUG, "sid was set");
		
		//set result array
		rs->result_tab._maximum = rows;
		rs->result_tab._length = rows;
		rs->result_tab._release = 1;
		rs->result_tab._buffer = CORBA_sequence_CORBA_sequence_org_pgj_corba_raw_allocbuf(rows);
		worker_nlog(env, threadobj, WNL_DEBUG, "result array was set");
		
		for(i=0; i<rows; i++){
			
			//org_pgj_corba_raw* thisrow = rs->result_tab._buffer + i * sizeof(org_pgj_corba_raw);
			CORBA_sequence_org_pgj_corba_raw* thisrow = &(rs->result_tab._buffer[i]);

			worker_nlog(env, threadobj, WNL_DEBUG, "row");
			
			thisrow->_maximum = columns;
			thisrow->_length = columns;
			thisrow->_release = 1;
			thisrow->_buffer = org_pgj_corba_raw_allocbuf(columns);
			
			for(j=0; j<columns; j++){
				jbyteArray buff;					//temporary place for raw data
				jobject fld;
				
				jint _i = i, _j = j;
				
				CORBA_sequence_CORBA_char* thisfield = &(thisrow->_buffer[j]);

				worker_nlog(env, threadobj, WNL_DEBUG, "column");
				
				fld = (*env)->CallObjectMethod(env,obj,result_get,i,j);
				worker_nlog(env, threadobj, WNL_DEBUG, "1");
				buff = (*env)->CallObjectMethod(env,fld,field_get);
				worker_nlog(env, threadobj, WNL_DEBUG, "2");
				
				thisfield->_length = (*env)->GetArrayLength(env, buff);
				thisfield->_buffer = (*env)->GetByteArrayElements(env, buff, 0);
				thisfield->_maximum = thisfield->_length;
				thisfield->_release = 0;
				worker_nlog(env, threadobj, WNL_DEBUG, "3");
			}


		}
		
		//set types array
		rs->types._maximum = 0;
		rs->types._length = 0;
		rs->types._release = 1;
		worker_nlog(env, threadobj, WNL_DEBUG, "result types array set");
		
		ret->_type = TC_org_pgj_corba_result;
		ret->_value = rs;
	} else if( (*env)->IsInstanceOf(env, obj, message_sql) ){
		//check the kind of that SQL message
		worker_nlog(env, threadobj, WNL_DEBUG, "it is an SQL structure");
		if( (*env)->IsInstanceOf(env, obj, message_sql_cursor) ){
			worker_nlog(env, threadobj, WNL_DEBUG, "is is cursor operation");
			if( (*env)->IsInstanceOf(env, obj, message_sql_close) ){
				worker_nlog(env, threadobj, WNL_DEBUG, "it is cursor close");
			} else if( (*env)->IsInstanceOf(env, obj, message_sql_open_wia_sql) ){
				worker_nlog(env, threadobj, WNL_DEBUG, "it is cursor close with an sql");
			} else if( (*env)->IsInstanceOf(env, obj, message_sql_fetch) ){
				worker_nlog(env, threadobj, WNL_DEBUG, "it is cursor fetch");
			} else {
				worker_nlog(env, threadobj, WNL_FATAL, "unknown or unhandled cursor operation");
			}
		} else {
			//...
		}
		
	} else {
		worker_nlog(env, threadobj, WNL_DEBUG, "ehhh, upsz.");
	}
	printf("all done\n\n");
	return ret;
}



