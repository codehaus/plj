
#ifndef MEMDEBUG_H
#define MEMDEBUG_H

#include "postgres.h"
#include "nodes/memnodes.h"

#ifdef MEMORY_CONTEXT_CHECKING

#define MEMDEBUG(msg)		elog(DEBUG1, msg); ((CurrentMemoryContext->methods)->check(CurrentMemoryContext)); elog(DEBUG1, msg);

#else

#define MEMDEBUG(MSG)		

#endif

#endif

