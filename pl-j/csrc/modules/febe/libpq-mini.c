#include "libpq-mini.h"

PGconn_min* pq_min_connect(){
	PGconn_min* conn;
	
	conn = malloc(sizeof(PGconn_min));
	conn -> pghost = PLJ_HOST_ADDR;
	conn -> pgport = PLJ_PORT_NUM;
	conn -> pgunixsocket = PLJ_UNIX_SOCKET;
	conn -> connect_timeout = PLJ_CONNECT_TIMEOUT;

	//buffers are empty	
	conn->inStart = conn->inCursor = conn->inEnd = 0;

	conn->outCount = 0;

	
}

void pq_min_finish(PGconn_min*){
	
}


