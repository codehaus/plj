#ifndef PLPGJ_MESSAGES_BASE
#define PLPGJ_MESSAGES_BASE

#define base_message_content \
	unsigned short msgtype;\
	unsigned int length;

typedef struct str_message{
	base_message_content
}str_message, *message;

#endif
