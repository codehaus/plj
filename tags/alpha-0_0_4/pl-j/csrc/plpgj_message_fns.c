//#include "plpgj_messages.h"
#include "plpgj_message_fns.h"

#include <stdlib.h>

int MESSAGE_ERROR_CODE;

int free_message(void* msg){
	
	message mmsg = (message)msg;
	
	if(msg == NULL){
		MESSAGE_ERROR_CODE = MEC_NULL_MESSAGE;
		return -1;
	}
	
	switch(mmsg->msgtype){
		default:
			MESSAGE_ERROR_CODE = MEC_UNKNOW_MESSAGE_TYPE;
			return -1;
	}
	
}

int free_msg_callreq(callreq msg){
	free(msg);
	return 0;
}

