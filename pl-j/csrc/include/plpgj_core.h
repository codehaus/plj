
/**
 * file name:			plpgj_core.h
 * description:			Chanell API core.
 * author:				Laszlo Hornyak
 */
#ifndef PLPGJ_CORE_H
#define PLPGJ_CORE_H

#include "postgres.h"
#include "fmgr.h"

Datum		plpgj_call_hook(PG_FUNCTION_ARGS);
Datum		plpgj_call_handler(PG_FUNCTION_ARGS);

#endif
