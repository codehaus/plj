#ifndef PLPGJ_MESSAGE_TRANSEVENT_H
#define PLPGJ_MESSAGE_TRANSEVENT_H

/*
 *	Transaction events 
 */
#define PLPGJ_TRANSACTION_EVENT_COMMIT		1
#define PLPGJ_TRANSACTION_EVENT_ROLLBACK	2
#define PLPGJ_TRANSACTION_EVENT_BEGIN		3
#define PLPGJ_TRANSACTION_EVENT_PREPARE		4

typedef struct str_message_transevent
{
	base_message_content;
	unsigned int tevent_type;
}	*message_transevent;

#endif
