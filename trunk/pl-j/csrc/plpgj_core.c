
/**
 * file name:			plpgj_core.c
 * description:			PL-J language handler module.
 * author:			Laszlo Hornyak
 */

#include "plpgj_channel.h"
#include "plpgj_core.h"

#include "postgres.h"
#include "fmgr.h"
#include "executor/spi.h"
#include "pljelog.h"

sigjmp_buf* PG_exception_stack;

PG_FUNCTION_INFO_V1(plpgj_call_handler);

Datum
plpgj_call_handler(PG_FUNCTION_ARGS)
{
	Datum		datumreturn;
	int ret;

	ret = SPI_connect();
	if (ret != SPI_OK_CONNECT)
#if (POSTGRES_VERSION == 74)
		elog(ERROR, "[pl-j core] SPI connect error: %d", ret);
#else
		elog(ERROR, "[pl-j core] SPI connect error: %d (%s)", ret, SPI_result_code_string(ret));
#endif


	pljelog(DEBUG1, "[pl-j core] plpgj_call_handler");

	datumreturn = plpgj_call_hook(fcinfo);

	pljelog(DEBUG1, "[pl-j core] call done, disconnect and return");
	ret = SPI_finish();
	if (ret != SPI_OK_FINISH)
#if (POSTGRES_VERSION == 74)
		elog(ERROR, "SPI finsh error: %d", ret);
#else
		elog(ERROR, "SPI finis error: %d (%s)", ret, SPI_result_code_string(ret));
#endif
	elog(DEBUG1, "SPI_finish called");
	return datumreturn;
}	//plpgj_call_handler
