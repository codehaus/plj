#ifndef PLJELOG_H
#define PLJELOG_H

#include "postgres.h"
#include <setjmp.h>
#include "c.h"
#include "utils/elog.h"

/**
 * Guys I feel so sorry for this file. This is a workaround on
 * the PostgreSQL 7.4 and 7.3 logging. They simply don't match
 * our needs, with PostgreSQL 8.0 we can drop this file.
 *
 * Kocka
 */

#if (PG_MAJOR_VERSION < 8)
#define DEBUG5 DEBUG
#define DEBUG4 DEBUG
#define DEBUG3 DEBUG
#define DEBUG2 DEBUG
#define DEBUG1 DEBUG
#endif

extern short pljloging;
extern short pljlogging_error;


extern sigjmp_buf* PG_exception_stack;

/**
 * copy-paste from pg 8.0 (some kind of backport...)
 */

#if (PG_MAJOR_VERSION == 7)

// 
// ultra minimal exception handling imitation for version 7.4
// Errors wont be recovered, stored procedure execution will fail.
// 

#define PG_TRY() 	
#define PG_CATCH() 	if(false) {
#define PG_END_TRY()	}

#endif

/*
#define PG_TRY()  \
        do { \
                sigjmp_buf *save_exception_stack = PG_exception_stack; \
                ErrorContextCallback *save_context_stack = error_context_stack; \
                sigjmp_buf local_sigjmp_buf; \
                if (sigsetjmp(local_sigjmp_buf, 0) == 0) \
                { \
                        PG_exception_stack = &local_sigjmp_buf


#define PG_CATCH()      \
                } \
                else \
                { \
                        PG_exception_stack = save_exception_stack; \
                        error_context_stack = save_context_stack

#define PG_END_TRY()  \
                } \
                PG_exception_stack = save_exception_stack; \
                error_context_stack = save_context_stack; \
        } while (0)

#define PG_RE_THROW()  \
        siglongjmp(*PG_exception_stack, 1)

*/





#define pljelog		if(pljloging) elog

#if (POSTGRES_VERSION == 80)

//char* plj_exceptionreason(void);
#define plj_exceptionreason()		"RDBMS exception"

#else

#define plj_exceptionreason()		"RDBMS exception"

#endif

#endif
