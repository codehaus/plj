/**
 * file name:			febe_channel_work.c
 * description:			FEBE chanell implementation, central functions.
 * author:			Laszlo Hornyak
 */

#include "plpgj_channel.h"
#include "plpgj_messages.h"
#include "plpgj_message_fns.h"

#include <stdio.h>
#include <unistd.h>

#include <executor/spi.h>
#include <regex/regex.h>

#include "executor/spi.h"
#include "utils/elog.h"

#include "libpq-mini-misc.h"

extern PGconn_min* min_conn;

int febe_receive_integer_4(void){
	unsigned char c[4];
	int i;
	for(i=0; i<4; i++){
		pqGetc(c+i, min_conn);
	}
	i = c[3] + (c[2]*256) + (c[1]*256*256) + (c[0]*256*256*256);
	return i;
}

char* febe_receive_string(void){
	//this is a hackaround, should work with pqGets
	int cnt = 0;
	char* tmp_chr;
	//pqGetInt(&cnt, 4, min_conn);
	cnt = febe_receive_integer_4();
	tmp_chr = SPI_palloc(sizeof(char) * (cnt+2));
	pqGetnchar(tmp_chr, cnt, min_conn);
	tmp_chr[cnt] = 0;
	return tmp_chr;
}

int febe_send_trigger(trigger_callreq call){
	elog(DEBUG1, "sending trigger");
	pqPutMsgStart(0,0,min_conn);
	elog(DEBUG1, "tracepoint 1");
	pqPutc('T',min_conn);
	elog(DEBUG1, "tracepoint 2");
	pqPutInt(call -> row, 4, min_conn);
	elog(DEBUG1, "tracepoint 3");
	pqPutInt(call -> reason, 4, min_conn);
	elog(DEBUG1, "tracepoint 4");
	pqPuts(call -> tablename, min_conn);
	elog(DEBUG1, "tracepoint 5");
	pqPuts(call -> classname, min_conn);
	elog(DEBUG1, "tracepoint 6");
	pqPuts(call -> methodname, min_conn);
	elog(DEBUG1,"trace point (before pqPutMsgEnd)");
	pqPutMsgEnd(min_conn);
	elog(DEBUG1,"trace point (before pqFlush)");
	pqFlush(min_conn);
	elog(DEBUG1,"trace point (last)");
	return 0;
}

int febe_send_call(callreq call){
	int i;
	elog(DEBUG1, "sending call req. (test phase)");
	pqPutMsgStart(0,0,min_conn);
	elog(DEBUG1, "pqPutMsgStart");
	pqPutc('C',min_conn);
	elog(DEBUG1, "putc");
	pqPuts((const char*)call -> classname, min_conn);
	elog(DEBUG1, "puts 1");
	pqPuts(call -> methodname, min_conn);
	elog(DEBUG1, "puts 2");
	pqPuts(call -> expect, min_conn);
	elog(DEBUG1, "puts 3");
	//TODO: hmm?
	pqPutInt(call -> nrOfParams,  4, min_conn);
	elog(DEBUG1, "number of params: %d", call -> nrOfParams);
	for(i = 0; i < call -> nrOfParams; i++){
		elog(DEBUG1, "sending param %d", i);
		pqPuts(call -> parameters[i].type, min_conn);
		elog(DEBUG1, "tp 1");
		if(call -> parameters[i].data.isnull){
			pqPutc('N', min_conn);
			elog(DEBUG1, "tp 2");
		} else {
			pqPutc('D', min_conn);
			elog(DEBUG1, "tp 3");
			pqPutInt(call -> parameters[i].data.length, 4, min_conn);
			elog(DEBUG1, "tp 4");
			pqPutnchar(call -> parameters[i].data.data, call -> parameters[i].data.length, min_conn);
			elog(DEBUG1, "tp 5");
		}
		elog(DEBUG1, "sending param %d done", i);

		
	}
	elog(DEBUG1, "putint 1");
	pqPutMsgEnd(min_conn);
	elog(DEBUG1, "message end");
	pqFlush(min_conn);
	elog(DEBUG1, "flush, done");
	return 0;
}

int febe_send_result(plpgj_result res){
	elog(DEBUG1, "sending result. (not impl)");
	return 0;
}

int febe_send_exception(error_message err){
	elog(DEBUG1, "sending exception. (not impl)");
	return 0;
}

int plpgj_channel_send(message msg){
	switch(msg->msgtype){
		case MT_TRIGREQ:
			return febe_send_trigger((callreq)msg);
		case MT_CALLREQ:
			return febe_send_call((trigger_callreq)msg);
		case MT_RESULT:
			return febe_send_result((plpgj_result)msg);
		case MT_EXCEPTION:
			return febe_send_exception((error_message)msg);
		default:
		break;
	}
}

void* febe_receive_exception(){
	PQExpBuffer name, mesg;
	int res;
	error_message ret;

	elog(DEBUG1, "febe_receive_exception");

	name = createPQExpBuffer();
	mesg = createPQExpBuffer();

//	pqReadData(min_conn);

	elog(DEBUG1, "febe_receive_exception tp 1");
	//res = pqGets(name, min_conn);

	//res = pqGets(mesg, min_conn);

	elog(DEBUG1, "febe_receive_exception tp 2");

	ret = SPI_palloc(sizeof(str_error_message));
	if(ret == NULL){
		elog(DEBUG1, "gebasz.");
	}

	//ret -> classname = name -> data;
	//ret -> message = mesg -> data;
	ret -> classname = febe_receive_string();
	elog(DEBUG1, "febe_receive_exception tp 2.1");
	ret -> message = febe_receive_string();
	ret -> stacktrace = NULL;

	elog(DEBUG1, "febe_receive_exception tp 3");

	ret -> msgtype = MT_EXCEPTION;
	ret -> length = sizeof(error_message);

	if(min_conn -> Pfdebug)
		fflush(min_conn -> Pfdebug);
	return ret;
}

void* febe_receive_result() {
	plpgj_result ret;
	int i,j;		//iterators

	ret = SPI_palloc(sizeof(str_plpgj_result));
	ret -> msgtype = MT_RESULT;
	ret -> length = sizeof(str_plpgj_result);

	ret -> rows = febe_receive_integer_4();
	ret -> cols = febe_receive_integer_4();

	if(ret -> rows > 0){
		ret -> data = SPI_palloc( (ret -> rows) * sizeof(raw) );
		ret -> types = SPI_palloc( ret -> rows * (sizeof(char*)) );
	} else {
		ret -> data = NULL;
		ret -> types = NULL;
	}

	for(i = 0; i < ret -> rows; i++) {
		ret -> types[i] = NULL;
	}


	for(i = 0; i < ret -> rows; i++) {
		if(ret -> cols > 0){
			ret -> data[i] = SPI_palloc( (ret -> cols) * sizeof(raw) );
		} else {
			ret -> data[i] = NULL;
		}
		for(j = 0; j < ret -> cols; j++) {
			char isn;
			pqGetc(&isn, min_conn);
			if(isn == 'N'){
				ret -> data[i][j].data = NULL;
				ret -> data[i][j].length;
				ret -> data[i][j].isnull = 1;
			} else {
				int len;
				ret -> data[i][j].isnull = 0;
				len = febe_receive_integer_4();
				ret -> data[i][j].length = len;
				ret -> data[i][j].data = 
					SPI_palloc(len);
				pqGetnchar(ret -> data[i][j].data, len, min_conn);
				{
					//evil!
					char buff[100];
					int l;
					l = febe_receive_integer_4();
					pqGetnchar(buff, l, min_conn);
					buff[l] = 0;
					if(ret -> types[j] == NULL){
						ret -> types[j] = SPI_palloc(strlen(buff));
						strcpy(ret -> types[j], buff);
					}
				}
					
			}
		}
	}

	if(min_conn -> Pfdebug)
		fflush(min_conn -> Pfdebug);
	return ret;
}

message febe_receive_log() {
	log_message ret;

	ret = SPI_palloc(sizeof(str_log_message));
	ret -> msgtype = MT_LOG;
	ret -> length = sizeof(str_log_message);

	ret -> level = febe_receive_integer_4();
	ret -> category = febe_receive_string();
	ret -> message = febe_receive_string();

	if(min_conn -> Pfdebug)
		fflush(min_conn -> Pfdebug);

	return ret;
}

message plpgj_channel_receive(void){
	int header;
	char type;
	int ret;
	
	//ret = pqGetInt(&header, 4, min_conn);

	//elog(DEBUG1, "header: %d", header);

	if(min_conn -> inCursor == min_conn -> inEnd){
		elog(DEBUG1,"fetching data...");
		ret = pqReadData(min_conn);
	switch(ret) {
		case 1: elog(DEBUG1, "got data from the server"); 
		break;
		case 0: elog(DEBUG1, "no data, but still okay (who knows?)"); 
		break;
		case -1: elog(WARNING, "ERROR"); 
		break;
		default: elog(ERROR, "something is realy _very_ wrong");
	}
	}

	ret = pqGetc(&type, min_conn);

	if(ret == EOF){
		elog(ERROR, "pqGetc returned EOF");
	}

	switch(type){
		case 'R':
			return (message) febe_receive_result();
		case 'E':
			return (message) febe_receive_exception();
		case 'L':
			return (message) febe_receive_log();
	default:
			elog(ERROR, "message type unknown :%d", type);
			return NULL;
	}

	return NULL;
}

