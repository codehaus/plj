#ifndef PLPGJ_MESSAGE_CALLREQ_H
#define PLPGJ_MESSAGE_CALLREQ_H

#ifdef __cplusplus
extern "C" {
#endif

#include "plpgj_message_base.h"

typedef struct {
	base_message_content;
	//class name
	char classname[50];
	//method name
	char methodname[50];
	// expected return type
	char* expect;
	//parameters
	int nrOfParams;
	pparam parameters;
}str_msg_callreq;

typedef str_msg_callreq* callreq;

#ifdef __cplusplus
}
#endif

#endif
