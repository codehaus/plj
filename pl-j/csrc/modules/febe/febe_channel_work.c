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

	pqReadData(min_conn);

	elog(DEBUG1, "febe_receive_exception tp 1");
	res = pqGets(name, min_conn);

	res = pqGets(mesg, min_conn);

	elog(DEBUG1, "febe_receive_exception tp 2");	

	ret = malloc(sizeof(str_error_message));

	ret -> classname = name -> data;
	ret -> message = mesg -> data;
	ret -> stacktrace = NULL;

	elog(DEBUG1, "febe_receive_exception tp 3");

	return ret;
}

void* febe_receive_result(){
	return NULL;
}

message plpgj_channel_receive(void){
	int header;
	char type;
	int ret;
	sleep(10);
	
	ret = pqGetInt(&header, 4, min_conn);

	elog(DEBUG1, "header: %d", header);

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

