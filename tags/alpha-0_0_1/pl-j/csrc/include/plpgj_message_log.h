#ifndef PLPGJ_LOG_MESSAGE
#define PLPGJ_LOG_MESSAGE

#include "plpgj_message_base.h"

typedef struct str_log_message {
	base_message_content
	int level;
	char* category;
	char* message;
} str_log_message, * log_message;


#endif
