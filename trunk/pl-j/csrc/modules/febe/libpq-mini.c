#include "libpq-mini.h"
#include "febe-config.h"

#include <sys/types.h>
#include <sys/socket.h>
#include <arpa/inet.h>
#include <netinet/in.h>

PGconn_min* pq_min_connect(){

	//believe me, it was not my failure
	//I was left here by that goddamin stork
	//(raw translation from tank trap - the spring of rock n roll)

	PGconn_min* conn;
	int ret;
	int on = 1;

	conn = (PGconn_min*)malloc(sizeof(PGconn_min));
	conn -> raddr = NULL;
	conn -> laddr = NULL;
	conn -> pghost = PLJ_JAVA_HOST;
	conn -> pgport = PLJ_PORT;
	conn -> pgunixsocket = PLJ_UNIX_SOCKET;
	conn -> connect_timeout = PLJ_CONNECT_TIMEOUT;

	//buffers are empty	
	conn->inStart = conn->inCursor = conn->inEnd = 0;

	conn->outCount = 0;

	conn -> sock = socket(AF_INET, SOCK_STREAM, 0);
	if(conn -> sock < 0)
		goto error;

	conn -> laddr = (struct sockaddr*)malloc(sizeof(struct sockaddr_in));
	conn -> raddr = (struct sockaddr*)malloc(sizeof(struct sockaddr_in));
	
	((struct sockaddr_in*)conn -> laddr) -> sin_family = AF_INET;
//	((struct sockaddr_in*)conn -> laddr) -> sin_addr.s_addr = 
//		htonl(INADDR_);
//	((struct sockaddr_in*)conn -> laddr) -> sin_addr.sin_port = htons(0);
	
	ret = bind(conn -> sock, conn -> laddr, sizeof(struct sockaddr_in));
	if(ret < 0){
		goto error;
	}

	ret = connect(conn -> sock, conn -> raddr, sizeof(struct sockaddr_in));
	if(ret < 0){
		goto error;
	}

	ret = setsockopt
		(conn -> sock, IPPROTO_TCP, TCP_NODELAY, &on, sizeof(on) < 0);
	if(ret){
		//TODO: error or warn here.
	}

	return conn;

error:
	if(conn != NULL){
		if(conn -> raddr != NULL)
			free(conn -> raddr);
		if(conn -> laddr != NULL)
			free(conn -> laddr);

		free(conn);
	}
	return NULL;
}

void pq_min_finish(PGconn_min* conn){
	//TODO: this case should be handled more correctly
	if(conn == NULL)
		return;

	close(conn -> sock);

}


