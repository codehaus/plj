#ifndef LIBPQ_MIN_MISC_H
#define LIBPQ_MIN_MISC_H

#include "libpq-mini.h"
#include "pqexpbuffer.h"

int
pqPutMsgEnd(PGconn_min *conn);

int
pqReadData(PGconn_min *conn);

static int
pqSendSome(PGconn_min *conn, int len);


int
pqFlush(PGconn_min *conn);

int
pqGetc(char *result, PGconn_min *conn);

int
pqPutc(char c, PGconn_min *conn);

int
pqPuts(const char *s, PGconn_min *conn);

int
pqGets(PQExpBuffer buf, PGconn_min *conn);

int
pqGetnchar(char *s, size_t len, PGconn_min *conn);

int
pqPutnchar(const char *s, size_t len, PGconn_min *conn);

int
pqGetInt(int *result, size_t bytes, PGconn_min *conn);

int
pqPutInt(int value, size_t bytes, PGconn_min *conn);



#endif
