
extern "C" {
#include "plpgj_chanell.h"
}

int channel_send(message msg){
	switch(msg->msgtype){
		case MT_CALLREQ:
			break;
		case MT_RESULT:
			break;
		case MT_EXCEPTION:
			break;
		case MT_SQL:
			break;
		default:
			return -1;
	}
}

extern "C" {
	int plpgj_chanell_send(message msg){
		return channel_sed(msg);
	}
}

