
/**
 * file name:			plpgj_message_result.h
 * description:			Result message structure definition.
 * author:				Laszlo Hornyak
 * TODO:				Ezt az egesz hanyast szepen gatyaba razni!
 */

#ifndef PLPGJ_MESSAGE_RESULT_H
#define PLPGJ_MESSAGE_RESULT_H
#include "plpgj_message_base.h"

#define PLPGJ_MESSAGE_RESULT	2

typedef struct
{
	base_message_content 
	int			rows;
	int			cols;
	char	  		**types;
	raw			*data;
} str_plpgj_result;

typedef str_plpgj_result *plpgj_result;

#endif
