/**
 * file:			libpq-min.h
 * description:			very minimal functionality from the original 
 * 				libpq implementation. Structures and 
 * 				functionalities are extremely simplified.
 * author:			Laszlo Hornyak
 */

#include <sys/types.h>
#include <sys/socket.h>

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
	struct sockaddr*	raddr;  /** Remote address */
	int	db_encoding;	/** The encoding of the DB (orig.: client_encoding)*/
	/** Buffer */
	char	*inBuffer;
	int	inBuffSize;
	int	inCursor;
	int	inStart;
	int	inEnd;
	
	char	*outBuffer;
	int	outBuffSize;
	int	outCount;
};

typedef struct pg_conn_min PGconn_min;

/** from PQconnectStart */
extern PGconn_min* pq_min_connect();

/** from PQfinish */
extern void pq_min_finish(PGconn_min*);

