/**
 * file name:			plpgj_channel_work.c
 * description:			ORBIT channel implementation, initialization
 * author:			Laszlo Hornyak
 */

#include "plpgj_messages.h"
#include "plpgj_channel.h"
#include "module_config.h"
#include <unistd.h>
#include <stdlib.h>
#include <stdio.h>

int	PLJ_FEBE_INITED		= 0;
char*	PLJ_JAVA_HOST		= NULL;
char*	PLJ_UNIX_SOCKET		= NULL;
char*	PLJ_CONNECT_TIMEOUT	= NULL;
int	PLJ_PORT		= -1;

#define CONFIG_COPY(a,b) tmp=plj_get_configvalue_string(a);\
	b = malloc(strlen(tmp));\
	strcpy(b,tmp);

struct PGconn_min* min_conn = NULL;

int plpgj_channel_initialize(){
	char* tmp;
	char* tracefile;

	CONFIG_COPY("febe-mini.host",PLJ_JAVA_HOST)

	PLJ_PORT = plj_get_configvalue_int("febe-mini.port");

	CONFIG_COPY("febe-mini.unix-socket",PLJ_UNIX_SOCKET)
	CONFIG_COPY("febe-mini.connect-timeout",PLJ_CONNECT_TIMEOUT)


	elog(DEBUG1, "configured");
	min_conn = (struct PGconn_min*)pq_min_connect();
	elog(DEBUG1, "connected");

	tracefile = plj_get_configvalue_string("febe-mini.proto-trace");
	if(strcmp(tracefile,"") != 0){
		elog(DEBUG2,"using tracefile: %s", tracefile);
		pq_min_set_trace(min_conn, fopen(tracefile,"a"));
	}

	if(min_conn != NULL)
		PLJ_FEBE_INITED = 1;

}

int plpgj_channel_initialized(){
	return PLJ_FEBE_INITED;
}

