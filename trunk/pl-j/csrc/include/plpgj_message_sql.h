#ifndef PLPGJ_MESSAGE_SQL_H
#define PLPGJ_MESSAGE_SQL_H

#define sql_message_content	\
		int		sqltype

#define SQL_TYPE_STATEMENT				1
#define SQL_TYPE_CURSOR_CLOSE			2
#define SQL_TYPE_FETCH					3
#define SQL_TYPE_CURSOR_OPEN			4

typedef struct str_sql_msg{
	base_message_content;
	sql_message_content;
}*sql_msg;

typedef struct str_sql_statement{
	base_message_content;
	sql_message_content;
	char* statement;
}*sql_msg_statement;

/**
 * For opening and closing cursors.
 */
typedef struct str_sql_msg_cursor_close{
	base_message_content;
	sql_message_content;
	char* cursorname;
}*sql_msg_cursor_close;

typedef struct str_sql_msg_cursor_open{
	base_message_content;
	sql_message_content;
	char* cursorname;
	char* query;
}*sql_msg_cursor_open;

/**
 * For fetching from cursors.
 */
typedef struct str_sql_msg_cursor_fetch{
	base_message_content;
	sql_message_content;
	char* cursorname;
	/** direction: 0 if forward, 1 backward */
	unsigned short direction;
	
}sql_msg_cursor_fetch;

#endif
