/**
 * file name:			plpgj_chanell_work.c
 * description:			ORBIT chanell implementation, central functions.
 * author:				Laszlo Hornyak
 * TODO:				Ezt az egesz hanyast szepen gatyaba razni!
 */

#include "plpgj_chanell.h"
#include "plpgj_messages.h"
#include "plpgj_message_fns.h"

#include "callserver.h"
#include "callreq.h"
#include "result.h"
#include "error.h"

#include <stdio.h>
#include <unistd.h>

#include <executor/spi.h>
#include <regex/regex.h>

#include <orbit/orbit.h>
//#include <orbit/orb-core/corba-any.h>
//#include <orbit/orb-core/corba-any-type.h>

CORBA_ORB corba_orb;
CORBA_Environment corba_env;
CORBA_Object corba_server;
char session_id[100];

int plpgj_chanell_send(message msg){
	CORBA_any corbamsg;
	
	if(msg == NULL){
		elog(FATAL, "cannot send NULL message");
		return -1;
	}
	
	switch(msg->msgtype){
		case MT_CALLREQ:{
			org_pgj_corba_callreq corba_callreq;
			elog(DEBUG1,"send callreq\n");
			corba_callreq.classname = ((callreq)msg)->classname;
			corba_callreq.methodname = ((callreq)msg)->methodname;
			corba_callreq.values._length = 0;
			corba_callreq.sid = session_id;

			corbamsg._type = &TC_org_pgj_corba_callreq_struct;
			corbamsg._value = &corba_callreq;
			elog(DEBUG1,"test");
			org_pgj_corba_CallServer_sendMessage(corba_server, &corbamsg, &corba_env);
			
			elog(DEBUG1,"test 2");
			if(corba_env._major != CORBA_NO_EXCEPTION){
				elog(ERROR, "message send error %s",corba_env._id);
				return -1;
			}
		}
		break;
		case MT_EXCEPTION:{
			org_pgj_corba_errorstruct error;
			error.sid = session_id;
			error.classname = ((error_message)msg)->classname;
			error.message = ((error_message)msg)->message;
			error.stacktrace = ((error_message)msg)->stacktrace;
			
			}
			elog(DEBUG1,"send exception\n");
		break;
		case MT_SQL:{
				switch( ((sql_msg)msg)->sqltype ){
					case SQL_TYPE_STATEMENT:{
					}
					break;
					case SQL_TYPE_CURSOR_CLOSE:{
					}
					break;
					
				}
			}
			elog(DEBUG1,"send sql?\n");
		break;
		case MT_RESULT:{
			org_pgj_corba_result corba_result;
			elog(DEBUG1,"send result\n");
			
			corba_result.sid = session_id;
			
			corbamsg._type = &TC_org_pgj_corba_result_struct;
			corbamsg._value = &corba_result;
			
			org_pgj_corba_CallServer_sendMessage(corba_server, &corbamsg, &corba_env);
			}
		break;
		default:
		elog(FATAL, "unknown message %d\n", msg->msgtype);
		return -1;
	}
	return 1;
}

message plpgj_chanell_receive(void){
	message msg = NULL;
	CORBA_any* ansver;
	elog(WARNING,"UNDER CONSTRUCTION");
	
	ansver = org_pgj_corba_CallServer_receiveAnsver( corba_server, session_id, &corba_env);
	elog(DEBUG1, ansver->_type->name);
	elog(DEBUG1, "message received");
	if(strcmp(ansver->_type->name, "errorstruct") == 0){
		
		org_pgj_corba_errorstruct* msg_error = (org_pgj_corba_errorstruct*)(ansver->_value);
		
		elog(DEBUG1,"filling in error struct");
		
		msg = malloc(sizeof(str_error_message));
		((error_message)msg)->classname = msg_error->classname;
		((error_message)msg)->message = msg_error->message;
		((error_message)msg)->stacktrace = msg_error->stacktrace;
		((error_message)msg)->msgtype = MT_EXCEPTION;
		
		
	} else if (strcmp(ansver->_type->name, "result") == 0){
		
		org_pgj_corba_result* msg_result = NULL;
		int i,j;

		elog(DEBUG1, "filling in result struct.");
		msg_result = (org_pgj_corba_result*)(ansver->_value);
		
		msg = malloc(sizeof(str_plpgj_result));
		
		((plpgj_result)msg)->rows = msg_result->result_tab._length;
		if(((plpgj_result)msg)->rows == 0){
			((plpgj_result)msg)->cols = 0;
			((plpgj_result)msg)->data = NULL;
		} else {
			((plpgj_result)msg)->cols = msg_result->result_tab._buffer[0]._length;
			((plpgj_result)msg)->rows = msg_result->result_tab._length;
			
			((plpgj_result)msg)->data = malloc( (((plpgj_result)msg)->cols) * (((plpgj_result)msg)->rows) * sizeof(void*) );
		}
		
		//checkings first.
		if(((plpgj_result)msg)->rows != msg_result->result_tab._length){
			elog(FATAL, "ups, somebody must be crazy on the other side...");
			return NULL;
		}
		if((((plpgj_result)msg)->rows >= 1) && (msg_result->result_tab._buffer[0]._length == 0)){
			elog(FATAL, "how the fuck is this logical...");
			return NULL;
		}
		for(i = 0; i<msg_result->result_tab._length; i++){
			if(msg_result->result_tab._buffer[i]._length != msg_result->result_tab._buffer[0]._length){
				elog(FATAL, "Hey, the table must contain equal length rows, babe!");
				return NULL;
			}
		}
		
		//mk type array, or at least it should. // TODO
		((plpgj_result)msg)->types = msg_result->types._buffer;
		
		((plpgj_result)msg)->rows = msg_result->result_tab._length;
		((plpgj_result)msg)->cols = ((plpgj_result)msg)->rows == 0 ? 0 : msg_result->result_tab._buffer[0]._length;
		((plpgj_result)msg)->data = ((plpgj_result)msg)->rows == 0 ? NULL : malloc(((plpgj_result)msg)->rows * sizeof(raw*));
		//for each row
		for(i=0; i<((plpgj_result)msg)->rows; i++){
			
			((plpgj_result)msg)->data[i] = malloc(((plpgj_result)msg)->cols * sizeof(raw));
			((plpgj_result)msg)->data[i] = malloc( ((plpgj_result)msg)->cols * sizeof(raw));
			//for each field: copy
			for(j=0; j<((plpgj_result)msg)->cols; j++){
				int dlen = msg_result->result_tab._buffer[i]._buffer[j]._length; //data length
				((plpgj_result)msg)->data[i][j].data = dlen == 0 ? NULL : malloc(dlen);
				((plpgj_result)msg)->data[i][j].length = dlen;
				if(dlen)
					memcpy(((plpgj_result)msg)->data[i][j].data, msg_result->result_tab._buffer[i]._buffer[j]._buffer, dlen);
				
				((plpgj_result)msg)->data[i][j].length = dlen;
			}
		}
		
		((plpgj_result)msg)->msgtype = MT_RESULT;
	} else if (strcmp(ansver->_type->name, "") == 0){
		
	}
	
	
	return msg;
}

