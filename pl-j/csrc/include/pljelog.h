#ifndef PLJELOG_H
#define PLJELOG_H

#include "postgres.h"
#include "c.h"
#include "utils/elog.h"

/**
 * Guys I feel so sorry for this file. This is a workaround on
 * the PostgreSQL 7.4 and 7.3 logging. They simply don't match
 * our needs, with PostgreSQL 8.0 we can drop this file.
 *
 * Kocka
 */

#ifndef DEBUG1
#define DEBUG5 DEBUG
#define DEBUG4 DEBUG
#define DEBUG3 DEBUG
#define DEBUG2 DEBUG
#define DEBUG1 DEBUG
#endif

extern short pljloging;
extern short pljlogging_error;


#define pljelog		if(pljloging) elog

#endif
