/**
 * file name:			plpgj_channel_work.c
 * description:			ORBIT channel implementation, initialization
 * author:			Laszlo Hornyak
 */

#include "plpgj_messages.h"
#include "plpgj_channel.h"
#include <unistd.h>
#include <stdlib.h>

int initialized = 0;

int plpgj_channel_initialize(){
	initialized = 1;
}

int plpgj_channel_initialized(){
	return initialized;
}

