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
		elog(DEBUG1, "febe_receive_integer_4 [%d]: %d", i, c[i]);
	}
	i = c[3] + (c[2]*256) + (c[1]*256*256) + (c[0]*256*256*256);
	elog(DEBUG1, "febe_receive_integer_4: %d", i);
	return i;
}

char* febe_receive_string(void){
	//this is a hackaround, should work with pqGets
	int cnt = 0;
	char* tmp_chr;
	//pqGetInt(&cnt, 4, min_conn);
	cnt = febe_receive_integer_4();
	elog(DEBUG1, "febe_receive_string: geting %d bytes as string", cnt);
	tmp_chr = SPI_palloc(sizeof(char) * (cnt+2));
	elog(DEBUG1, "febe_receive_string: 1");
	pqGetnchar(tmp_chr, cnt, min_conn);
	elog(DEBUG1, "febe_receive_string: 2");
	tmp_chr[cnt] = 0;
}



int febe_send_call(callreq call){
	
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
	pqPutInt(0,  4, min_conn);
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
		case MT_CALLREQ:
			return febe_send_call((callreq)msg);
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

	return ret;
}

void* febe_receive_result(){
	return NULL;
}

message plpgj_channel_receive(void){
	int header;
	char type;
	int ret;
	
	//ret = pqGetInt(&header, 4, min_conn);

	//elog(DEBUG1, "header: %d", header);

	ret = pqReadData(min_conn);
	switch(ret){
		case 1: elog(DEBUG1, "got data from the server"); 
		break;
		case 0: elog(DEBUG1, "no data, but still okay (who knows?)"); 
		break;
		case -1: elog(DEBUG1, "got data from the server"); 
		break;
		default: elog(ERROR, "something is realy _very_ wrong");
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
		default:
			elog(ERROR, "message type unknown :%d", type);
			return NULL;
	}

	return NULL;
}

