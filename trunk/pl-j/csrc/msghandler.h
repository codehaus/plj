#ifndef MSGHANDLER_H
#define MSGHANDLER_H

#include <plpgj_messages.h>

typedef struct plpgj_handler_rec {
	int event_type;
	char* desc;
	message (*handler) (sql_msg);
} plpgj_handler_rec, *plpgj_handler;

message handle_message(sql_msg);

#endif

 //MSGHANDLER_H

