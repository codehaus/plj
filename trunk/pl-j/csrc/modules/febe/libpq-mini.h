/**
 * file:			libpq-min.h
 * description:			very minimal functionality from the original 
 * 				libpq implementation. Structures and 
 * 				functionalities are extremely simplified.
 * author:			Laszlo Hornyak
 */

struct pg_conn_min
{
	char	*pghost;
	char	*pghostaddr;
	char	*pgport;
	char	*pgunixsocket;
	char	*pgtty;
	char	*connect_timeout;
	/** Connection data */
	int	sock;
	SockAddr	laddr;	/** Local address */
	SockAddr	raddr;  /** Remote address */
	int	db_encoding;	/** The encoding of the DB (orig.: client_encoding)*/
	/** Buffer */
	char	*inBuffer;
	int	inBuffSize;
	int	inCursor;
	int	inEnd;
	
	char	*outBuffer;
	int	outBuffSize;
	int	outCount;
}

typedef struct pg_conn_min PGconn_min;

/** from PQconnectStart */
extern PGconn_min* pq_min_connect();

/** from PQfinish */
extern void pq_min_finish(PGconn_min*);

