#ifndef PLPGJ_MESSAGES_BASE
#define PLPGJ_MESSAGES_BASE

#define base_message_content \
	unsigned short msgtype;\
	unsigned int length;

typedef struct str_message
{
base_message_content} str_message, *message;

typedef struct str_raw
{
	int			length;
	unsigned short isnull;
	void	   *data;
}	*raw;

typedef struct fnc_param
{
	char	   *type;
	struct str_raw data;
}	*pparam;

#endif
