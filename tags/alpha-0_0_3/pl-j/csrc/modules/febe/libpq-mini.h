/**
 * file:			libpq-min.h
 * description:			very minimal functionality from the original 
 * 				libpq implementation. Structures and 
 * 				functionalities are extremely simplified.
 * author:			PostgreSQL developement group.
 * author:			Laszlo Hornyak
 */

#ifndef LIBPQ_MIN_H
#define LIBPQ_MIN_H

#include <sys/types.h>
#include <sys/socket.h>
#include <arpa/inet.h>
#include <netinet/in.h>
#include <netinet/tcp.h>

#include <stdio.h>

//no support for non blocking functions
#define pqIsnonblocking(conn)     1

struct pg_conn_min
{
	char	*pghost;
	char	*pghostaddr;
	int	pgport;
	char	*pgunixsocket;
	char	*pgtty;
	char	*connect_timeout;
	// Connection data
	int	sock;
	struct sockaddr*	laddr;	/** Local address */
	struct sockaddr_in	raddr;  /** Remote address */
	int	db_encoding;	/** The encoding of the DB (orig.: client_encoding)*/
	/** Buffer */
	char	*inBuffer;
	int	inBufSize;
	int	inCursor;
	int	inStart;
	int	inEnd;
	
	char	*outBuffer;
	int	outMsgStart;
	int	outMsgEnd;
	int	outBufSize;
	int	outCount;

	int status;

	FILE*	Pfdebug;

};

typedef struct pg_conn_min PGconn_min;

/** from PQconnectStart */
extern PGconn_min* pq_min_connect();

/** from PQfinish */
extern void pq_min_finish(PGconn_min*);

extern void pq_min_set_trace(PGconn_min*, FILE*);

typedef enum
{
        /*
         * Although it is okay to add to this list, values which become unused
         * should never be removed, nor should constants be redefined - that
         * would break compatibility with existing code.
         */
        CONNECTION_OK,
        CONNECTION_BAD,
        /* Non-blocking mode only below here */

        /*
         * The existence of these should never be relied upon - they should
         * only be used for user feedback or similar purposes.
         */
        CONNECTION_STARTED,                     /* Waiting for connection to be made.  */
        CONNECTION_MADE,                        /* Connection OK; waiting to send.         */
        CONNECTION_AWAITING_RESPONSE,           /* Waiting for a response from the
                                                                                 * postmaster.            */
        CONNECTION_AUTH_OK,                     /* Received authentication; waiting for
                                                                 * backend startup. */
        CONNECTION_SETENV,                      /* Negotiating environment. */
        CONNECTION_SSL_STARTUP,         /* Negotiating SSL. */
        CONNECTION_NEEDED                       /* Internal state: connect() needed */
} ConnStatusType;

typedef enum
{
        PGRES_POLLING_FAILED = 0,
        PGRES_POLLING_READING,          /* These two indicate that one may        */
        PGRES_POLLING_WRITING,          /* use select before polling again.   */
        PGRES_POLLING_OK,
        PGRES_POLLING_ACTIVE            /* unused; keep for awhile for backwards
                                                                 * compatibility */
} PostgresPollingStatusType;

#endif
