#ifndef PLPGJ_MESSAGE_TRIGREQ_H
#define PLPGJ_MESSAGE_TRIGREQ_H

#ifdef __cplusplus
extern "C" {
#endif

#include "plpgj_message_base.h"

#define PLPGJ_TRIGGER_REASON_INSERT 1
#define PLPGJ_TRIGGER_REASON_UPDATE 2
#define PLPGJ_TRIGGER_REASON_DELETE 3

#define PLPGJ_TRIGGER_ACTIONORDER_BEFORE 1
#define PLPGJ_TRIGGER_ACTIONORDER_AFTER 2

#define PLPGJ_TRIGGER_STARTED_FOR_ROW 1
#define PLPGJ_TRIGGER_STARTED_FOR_STATEMENT 2

typedef struct {
	base_message_content;
	//class name
	char classname[50];
	//method name
	char methodname[50];
	char* tablename;
	short reason;
	short actionorder;
	short row;
	int colcount;
	char** colnames;
	char** coltypes;
	pparam* _new;
	pparam* _old;
}str_msg_trigger_callreq;

typedef str_msg_trigger_callreq* trigger_callreq;

#ifdef __cplusplus
}
#endif

#endif
