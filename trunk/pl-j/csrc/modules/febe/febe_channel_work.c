
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
#include "pljelog.h"

#include "libpq-mini-misc.h"
#include "libpq-mini.h"

extern PGconn_min *min_conn;

int
febe_receive_integer_4(void)
{
	unsigned char c[4];
	int			i;

	elog(DEBUG1, "cursor: %d, end: %d", min_conn -> inCursor, min_conn -> inEnd);
	if( min_conn -> inCursor == min_conn -> inEnd ){
		pqReadData(min_conn);
		elog(DEBUG1, "cursor: %d, end: %d", min_conn -> inCursor, min_conn -> inEnd);
		
	}
	for (i = 0; i < 4; i++) {
		pqGetc(c + i, min_conn);
	}
	i = c[3] + (c[2] * 256) + (c[1] * 256 * 256) +
		(c[0] * 256 * 256 * 256);
	elog(DEBUG1, "");
	return i;
}

char *
febe_receive_string(void)
{
	/*
	 * this is a hackaround, should work with pqGets
	 */
	int			cnt = 0;
	char	   *tmp_chr;

	/*
	 * pqGetInt(&cnt, 4, min_conn);
	 */
	cnt = febe_receive_integer_4();
//	elog(DEBUG1,"string len: %d", (unsigned int)cnt);
	tmp_chr = SPI_palloc(sizeof(char) * (cnt + 2));
	pqGetnchar(tmp_chr, cnt, min_conn);
	tmp_chr[cnt] = 0;
	return tmp_chr;
}

void
trigger_send_tuple(pparam * tuple, int colcount)
{
	int			i;

	for (i = 0; i < colcount; i++)
	{
		pqPutInt(tuple[i]->data.isnull != 0, 4, min_conn);
		if (tuple[i]->data.isnull)
		{

		}
		else
		{
			pqPutInt(tuple[i]->data.length, 4, min_conn);
			pqPutnchar(tuple[i]->data.data, tuple[i]->data.length,
					   min_conn);
			pqPuts(tuple[i]->type, min_conn);
		}
	}
}

int
febe_send_trigger(trigger_callreq call)
{
	int			i;

	pqPutMsgStart(0, 0, min_conn);
	pqPutc('T', min_conn);
	pqPutInt(call->row, 4, min_conn);
	pqPutInt(call->reason, 4, min_conn);
	pqPutInt(call->actionorder, 4, min_conn);
	pqPutInt(call->row, 4, min_conn);
	pqPuts(call->tablename, min_conn);
	pqPuts(call->classname, min_conn);
	pqPuts(call->methodname, min_conn);

	pqPutInt(call->colcount, 4, min_conn);

	for (i = 0; i < call->colcount; i++)
	{
		pqPuts(call->colnames[i], min_conn);
		pqPuts(call->coltypes[i], min_conn);
	}

	switch (call->reason)
	{
		case PLPGJ_TRIGGER_REASON_INSERT:
			trigger_send_tuple(call->_new, call->colcount);
			break;
		case PLPGJ_TRIGGER_REASON_UPDATE:
			trigger_send_tuple(call->_old, call->colcount);
			trigger_send_tuple(call->_new, call->colcount);
			break;
		case PLPGJ_TRIGGER_REASON_DELETE:
			trigger_send_tuple(call->_old, call->colcount);
			break;
	}

	return 0;
}

int
febe_send_call(callreq call)
{
	int			i;

	pqPutMsgStart(0, 0, min_conn);
	pqPutc('C', min_conn);
	pqPuts((const char *) call->classname, min_conn);
	pqPuts(call->methodname, min_conn);
	pqPuts(call->expect, min_conn);
	/*
	 * TODO: hmm?
	 */
	pqPutInt(call->nrOfParams, 4, min_conn);
	for (i = 0; i < call->nrOfParams; i++)
	{
		pqPuts(call->parameters[i].type, min_conn);
		if (call->parameters[i].data.isnull)
			pqPutc('N', min_conn);
		else
		{
			pqPutc('D', min_conn);
			pqPutInt(call->parameters[i].data.length, 4, min_conn);
			pqPutnchar(call->parameters[i].data.data,
					   call->parameters[i].data.length, min_conn);
		}

	}
	return 0;
}

int
febe_send_result(plpgj_result res)
{
	int i, j;
	pqPutMsgStart(0, 0, min_conn);
	pqPutc('R', min_conn);
	pqPutInt(res -> rows, 4, min_conn);
	pqPutInt(res -> cols, 4, min_conn);
	for(i = 0; i < res -> rows; i++) {
		for(j = 0; j < res -> cols; j++) {
			if(res-> data[i][j]. isnull) {
				pqPutc('N', min_conn);
			} else {
				pqPutc('D', min_conn);
				pqPutInt(res -> data[i][j].length, 4, min_conn);
				pqPutnchar(res -> data[i][j].data, res -> data[i][j].length, min_conn);
				
				pqPuts(res -> types[j], min_conn);
				
			}
		}
	}
	return 0;
}

int
febe_send_exception(error_message err)
{
	pqPutMsgStart(0, 0, min_conn);
	pqPutc('E', min_conn);
	pqPuts(err->classname, min_conn);
	pqPuts(err->message, min_conn);
	pqPuts(err->stacktrace, min_conn);
	return 0;
}

int
plpgj_channel_send(message msg)
{
	int			ret;

	switch (msg->msgtype)
	{
		case MT_TRIGREQ:
			ret = febe_send_trigger((trigger_callreq) msg);
			break;
		case MT_CALLREQ:
			ret = febe_send_call((callreq) msg);
			break;
		case MT_RESULT:
			ret = febe_send_result((plpgj_result) msg);
			break;
		case MT_EXCEPTION:
			ret = febe_send_exception((error_message) msg);
			break;
		default:
			//pljlogging_error = 1;
//			elog(ERROR, "UNHANDLED MESSAGE");
			return -1;
			break;
	}
	pqPutMsgEnd(min_conn);
	pqFlush(min_conn);
	return ret;
}

void *
febe_receive_exception()
{
	PQExpBuffer name,
				mesg;
	error_message ret;

	name = createPQExpBuffer();
	mesg = createPQExpBuffer();

	ret = SPI_palloc(sizeof(str_error_message));

	ret->classname = febe_receive_string();
	ret->message = febe_receive_string();
	ret->stacktrace = NULL;


	ret->msgtype = MT_EXCEPTION;
	ret->length = sizeof(error_message);

	if (min_conn->Pfdebug)
		fflush(min_conn->Pfdebug);
	return ret;
}

void *
febe_receive_result()
{
	plpgj_result ret;
	int			i,
				j;				//iterators

	ret = SPI_palloc(sizeof(str_plpgj_result));
	ret->msgtype = MT_RESULT;
	ret->length = sizeof(str_plpgj_result);

	ret->rows = febe_receive_integer_4();
	ret->cols = febe_receive_integer_4();

	if (ret->rows > 0)
	{
		ret->data = SPI_palloc((ret->rows) * sizeof(raw));
		ret->types = SPI_palloc(ret->rows * (sizeof(char *)));
	}
	else
	{
		ret->data = NULL;
		ret->types = NULL;
	}

	for (i = 0; i < ret->rows; i++)
		ret->types[i] = NULL;


	for (i = 0; i < ret->rows; i++)
	{
		if (ret->cols > 0)
			ret->data[i] = SPI_palloc((ret->cols) * sizeof(raw));
		else
			ret->data[i] = NULL;
		for (j = 0; j < ret->cols; j++)
		{
			char		isn;

			pqGetc(&isn, min_conn);
			if (isn == 'N')
			{
				ret->data[i][j].data = NULL;
				ret->data[i][j].length = 0;
				ret->data[i][j].isnull = 1;
			}
			else
			{
				int			len;

				ret->data[i][j].isnull = 0;
				len = febe_receive_integer_4();
				ret->data[i][j].length = len;
				ret->data[i][j].data = SPI_palloc(len);
				pqGetnchar(ret->data[i][j].data, len, min_conn);
				{
					/*
					 * evil!
					 */
					char		buff[100];
					int			l;

					l = febe_receive_integer_4();
					pqGetnchar(buff, l, min_conn);
					buff[l] = 0;
					if (ret->types[j] == NULL)
					{
						ret->types[j] = SPI_palloc(strlen(buff));
						strcpy(ret->types[j], buff);
					}
				}

			}
		}
	}

	if (min_conn->Pfdebug)
		fflush(min_conn->Pfdebug);
	return ret;
}

message
febe_receive_log()
{
	log_message ret;

	ret = SPI_palloc(sizeof(str_log_message));
	ret->msgtype = MT_LOG;
	ret->length = sizeof(str_log_message);

	ret->level = febe_receive_integer_4();
	ret->category = febe_receive_string();
	ret->message = febe_receive_string();

	if (min_conn->Pfdebug)
		fflush(min_conn->Pfdebug);

	return (message) ret;
}


message
febe_receive_tupres()
{
	trigger_tupleres res;
	int			i;

	res = SPI_palloc(sizeof(str_msg_trigger_tupleresult));
	res->length = sizeof(str_msg_trigger_tupleresult);
	res->msgtype = MT_TUPLRES;

	res->tablename = febe_receive_string();
	res->colcount = febe_receive_integer_4();
	res->colnames =
		res->colcount >
		0 ? SPI_palloc(res->colcount * sizeof(char *)) : NULL;
	if (res->colcount > 0)
		res->_tuple = SPI_palloc(sizeof(pparam) * res->colcount);
	else
		res->_tuple = NULL;
	for (i = 0; i < res->colcount; i++)
	{
		char	   *name;
		char		isnull;

		name = febe_receive_string();
		res->colnames[i] = name;
		res->_tuple[i] = SPI_palloc(sizeof(struct fnc_param));
		pqGetc(&isnull, min_conn);
		if (isnull == 'n')
		{
			/*
			 * NULL
			 */
			res->_tuple[i]->type = NULL;
			res->_tuple[i]->data.isnull = true;
		}
		else
		{
			/*
			 * not null
			 */
			res->_tuple[i]->type = febe_receive_string();
			res->_tuple[i]->data.isnull = false;
			res->_tuple[i]->data.length = febe_receive_integer_4();
			if (res->_tuple[i]->data.length > 0)
			{
				res->_tuple[i]->data.data =
					SPI_palloc(res->_tuple[i]->data.length);
			}
			else
				res->_tuple[i]->data.data = NULL;
			pqGetnchar(res->_tuple[i]->data.data,
					   res->_tuple[i]->data.length, min_conn);
		}
	}

	return (message) res;
}

sql_msg_cursor_fetch
febe_receive_sql_fetch(void)
{
	sql_msg_cursor_fetch ret;

	ret = SPI_palloc(sizeof(struct str_sql_msg_cursor_fetch));
	ret -> length = sizeof(struct str_sql_msg_cursor_fetch);
	ret -> msgtype = MT_SQL;
	ret -> sqltype = SQL_TYPE_FETCH;
	ret -> cursorname = febe_receive_string();
	ret -> count = febe_receive_integer_4();
	ret -> direction = febe_receive_integer_4();

	return ret;
}

sql_msg_statement
febe_receive_sql_statement(void)
{
	sql_msg_statement ret;

	ret = (sql_msg_statement) SPI_palloc(sizeof(struct str_sql_statement));
	ret->msgtype = MT_SQL;
	ret->length = sizeof(struct str_sql_statement);
	ret->sqltype = SQL_TYPE_STATEMENT;
	ret->statement = febe_receive_string();
	return ret;
}

sql_msg_prepapre 
febe_receive_sql_prepare(void)
{
	sql_msg_prepapre ret;
	int i;

	ret = (sql_msg_prepapre) SPI_palloc(sizeof(struct str_sql_prepare));
	ret -> length = sizeof(struct str_sql_prepare);
	ret -> msgtype = MT_SQL;
	ret -> sqltype = SQL_TYPE_PREPARE;
	ret -> statement = febe_receive_string();
	ret -> ntypes = febe_receive_integer_4();
	ret -> types = ret -> ntypes == 0 ? NULL : SPI_palloc(ret -> ntypes * sizeof(char*));

//	ret -> ntypes = 0;
//	ret -> types = NULL;

	for (i = 0; i < ret -> ntypes; i++) {
		ret -> types[i] = febe_receive_string();
//		pljelog(DEBUG1, "ret -> types[%d] = %s", i, ret -> types[i]);
	}

//	pljelog(DEBUG1,"febe_receive_sql_prepare");
	return ret;
}

sql_pexecute febe_receive_sql_pexec(void){
	sql_pexecute ret;
	int i;

//	pljelog(DEBUG1,"febe_receive_sql_pexec");
	ret = SPI_palloc(sizeof(struct str_sql_pexecute));
	ret -> length = sizeof(struct str_sql_pexecute);
	ret -> msgtype = MT_SQL;
	ret -> sqltype = SQL_TYPE_PEXECUTE;
	ret -> planid = febe_receive_integer_4();
	ret -> action = febe_receive_integer_4();
	ret -> nparams = febe_receive_integer_4();

	if(ret -> nparams == 0){
		ret -> params = NULL;
	} else {
		ret -> params = SPI_palloc( ret -> nparams * sizeof(struct fnc_param));
	}
	
	for(i = 0; i < ret -> nparams; i++) {
		char isnull;
		isnull = 0;
		pqGetc(&isnull, min_conn);
		if(isnull == 'N') {
			ret -> params[i].data.isnull = 1;
			ret -> params[i].data.length = 0;
			ret -> params[i].data.data = NULL;
		} else {
			if(isnull != 'D')
				elog(WARNING, "Should be N or D: %d", (unsigned char)isnull);
			ret -> params[i].data.isnull = 0;
			ret -> params[i].type = febe_receive_string();
			ret -> params[i].data.length = febe_receive_integer_4();
			ret -> params[i].data.data = 
				ret -> params[i].data.length == 0 ?
					NULL : SPI_palloc(ret -> params[i].data.length);
			pqGetnchar(ret -> params[i].data.data, ret -> params[i].data.length, min_conn);
		}
	}
//	pljelog(DEBUG1,"febe_receive_sql_pexec end");
//	for(i = 0; i < ret -> nparams; i++) {
//		pljelog(DEBUG1, "[%d] type: %d", i, ret -> params[i].type);
//	}
	return ret;
}

sql_msg_cursor_close 
febe_receive_sql_cursorclose(){
	sql_msg_cursor_close ret;

	ret = SPI_palloc(sizeof(struct str_sql_msg_cursor_close));
	ret -> msgtype = MT_SQL;
	ret -> sqltype = SQL_TYPE_CURSOR_CLOSE;
	ret -> length = sizeof(struct str_sql_msg_cursor_close);
	ret -> cursorname = febe_receive_string();

	return ret;
}

sql_msg_unprepare
febe_receive_sql_unprepare(){
	sql_msg_unprepare ret;

	ret = SPI_palloc(sizeof(struct str_sql_unprepare));
	ret -> msgtype = MT_SQL;
	ret -> sqltype = SQL_TYPE_UNPREPARE;
	ret -> length = sizeof(struct str_sql_unprepare);
	ret -> planid = febe_receive_integer_4();

	return ret;
}

sql_msg
febe_receive_sql(void)
{
	int			typ;

	typ = febe_receive_integer_4();
	switch (typ)
	{
		case SQL_TYPE_STATEMENT:
			return (sql_msg) febe_receive_sql_statement();
		case SQL_TYPE_PREPARE:
			return (sql_msg) febe_receive_sql_prepare();
		case SQL_TYPE_PEXECUTE:
			return (sql_msg) febe_receive_sql_pexec();
		case SQL_TYPE_FETCH:
			return (sql_msg) febe_receive_sql_fetch();
		case SQL_TYPE_CURSOR_CLOSE:
			return (sql_msg)febe_receive_sql_cursorclose();
		case SQL_TYPE_UNPREPARE:
			return (sql_msg)febe_receive_sql_unprepare();
		default:
			//pljlogging_error = 1;
		elog(ERROR, "UNHANDLED SQL TYPE: %d", typ);

	}
	return NULL;				//which never happens, but syntax failure otherwise.
}

message
plpgj_channel_receive(void)
{
	char		type;
	int			ret;
	message		msgret;

	if (min_conn->inCursor == min_conn->inEnd)
	{
		ret = pqReadData(min_conn);
/*
		switch (ret)
		{
			case 1:				
				break;
			case 0:	
				elog(WARNING, "This should not ever happen.");
				break;
			case -1:
			default:
				//pljlogging_error = 1;
				elog(ERROR, "something is realy _very_ wrong");
		}
*/
	}

	ret = pqGetc(&type, min_conn);

	if (ret == EOF){
		//pljlogging_error = 0;
		elog(ERROR, "Unexpected EOF from socket. PL-J server is gone?");
	}

	msgret = NULL;
	switch (type)
	{
		case 'R':
			msgret = (message) febe_receive_result();
			break;
		case 'E':
			msgret = (message) febe_receive_exception();
			break;
		case 'L':
			msgret = (message) febe_receive_log();
			break;
		case 'U':
			msgret = (message) febe_receive_tupres();
			break;
		case 'S':{
			msgret = (message) febe_receive_sql();
			break;
			}
		default:
			//pljlogging_error = 1;
			elog(ERROR, "message type unknown :%d", type);
			break;
			return NULL;
	}

	if(msgret != NULL)
		pqMessageRecvd(min_conn);

	return msgret;
}

