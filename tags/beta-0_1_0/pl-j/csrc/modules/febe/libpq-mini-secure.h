#ifndef LIBPQ_SECURE_H
#define LIBPQ_SECURE_H
#include "libpq-mini.h"

ssize_t
pqsecure_read(PGconn_min * conn, void *ptr, size_t len);

void
pqsecure_close(PGconn_min * conn);

ssize_t
pqsecure_read(PGconn_min * conn, void *ptr, size_t len);

ssize_t
pqsecure_write(PGconn_min * conn, const void *ptr, size_t len);

#endif

