#include "libpq-mini.h"
#include "febe-config.h"

#include "executor/spi.h"

/* #include "utils/elog.h" */
#include "pljelog.h"

#include <sys/types.h>
#include <sys/socket.h>
#include <arpa/inet.h>
#include <netinet/in.h>
#include <netinet/tcp.h>

#include "executor/spi.h"

PGconn_min *
pq_min_connect()
{
	/*
	 * believe me, it was not my failure
	 */
	/*
	 * I was left here by that goddamin stork
	 */
	/*
	 * (raw translation from tank trap - the spring of rock n roll)
	 */

	PGconn_min *conn;
	struct hostent *server;

	conn = (PGconn_min *) malloc(sizeof(PGconn_min));
	bzero(conn, sizeof(sizeof(PGconn_min)));
	conn->laddr = NULL;
	conn->pghost = PLJ_JAVA_HOST;
	conn->pgport = PLJ_PORT;
	conn->pgunixsocket = PLJ_UNIX_SOCKET;
	conn->connect_timeout = PLJ_CONNECT_TIMEOUT;

	/*
	 * in buff
	 */
	conn->inBuffer = malloc(128);
	conn->inBufSize = 128;
	conn->inCursor = 0;
	conn->inStart = 0;
	conn->inEnd = 0;

	/*
	 * out buff
	 */
	conn->outBuffer = malloc(256);
	conn->outMsgStart = 0;
	conn->outMsgEnd = 0;
	conn->outBufSize = 256;
	conn->outCount = 0;

	conn->status = CONNECTION_NEEDED;

	conn->Pfdebug = 0;

	/*
	 * elog(DEBUG1,"socket");
	 */
	conn->sock = socket(AF_INET, SOCK_STREAM, 0);
	if (conn->sock < 0)
	{
		perror("socket");
		goto error;
	}
	/*
	 * elog(DEBUG1,"socket done");
	 */

	server = gethostbyname(conn->pghost);
	if (server == NULL)
	{
		perror("gethostbyname");
		goto error;
	}

	elog(DEBUG1, "host done: %d ", server->h_length);

	conn->raddr.sin_family = AF_INET;
	bcopy((char *) server->h_addr,
		  &((conn->raddr).sin_addr.s_addr), server->h_length);
	/*
	 * elog(DEBUG1,"addr set");
	 */
	conn->raddr.sin_port = htons(conn->pgport);
	elog(DEBUG1, "connect");
	if (connect(conn->sock, &(conn->raddr), sizeof(struct sockaddr_in)) <
		0)
	{
		perror("connect");
		goto error;
	}
	/*
	 * elog(DEBUG1,"connect done");
	 */

	return conn;

  error:
	pljlogging_error = 1;
	elog(ERROR, "Connection error: %s", sys_errlist[errno]);
	if (conn != NULL)
		free(conn);
	return NULL;
}

void
pq_min_finish(PGconn_min * conn)
{
	/*
	 * TODO: this case should be handled more correctly
	 */
	if (conn == NULL){
		return;
		pljelog(WARNING, "Connection is null, was it initialized at all?");
	}
	close(conn->sock);

}

void
pq_min_set_trace(PGconn_min * conn, FILE *tf)
{
	conn->Pfdebug = tf;
}
