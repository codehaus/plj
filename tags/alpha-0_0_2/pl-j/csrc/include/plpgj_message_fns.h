#ifndef PLPGJ_MESSAGE_FNS
#define PLPGJ_MESSAGE_FNS

#include "postgres.h"
#include "plpgj_messages.h"
#include "fmgr.h"
#include "executor/spi.h"

#define MEC_NULL_MESSAGE			10
#define MEC_UNKNOW_MESSAGE_TYPE		11

#define MT_CALLREQ					0
#define MT_RESULT					1
#define MT_EXCEPTION					2
#define MT_SQL						3
#define MT_LOG						4
#define MT_TRIGREQ					5
#define MT_TUPLRES					6
#define MT_TRANSEVENT				7

/**
 * Crete a call structure.
 */

/* callreq plpgj_create_call(PG_FUNCTION_ARGS); */

/**
 * Free a message structure. (any kind)
 * @param msg	the message
 * @return		-1 on error something else otherwise.
 */
int			free_message(void *msg);

int			data_free(void *data);

callreq		plpgj_create_call(PG_FUNCTION_ARGS);

trigger_callreq plpgj_create_trigger_call(PG_FUNCTION_ARGS);

#define plpgj_message_type(msg) ((message)(msg))->msgtype

#endif
