/**
 * file name:			plpgj_core.c
 * description:			PL/pgJ language handler module.
 * author:				Laszlo Hornyak
 */

#include "plpgj_channel.h"
#include "plpgj_core.h"

#include "postgres.h"
#include "fmgr.h"
#include "executor/spi.h"


PG_FUNCTION_INFO_V1(plpgj_call_handler);

Datum plpgj_call_handler(PG_FUNCTION_ARGS){
	Datum datumreturn;
	
	if( SPI_connect() != SPI_OK_CONNECT ){
		elog(FATAL, "SPI connect error");
	}
	
	elog(DEBUG1,"plpgj_call_handler");
	
	datumreturn = plpgj_call_hook(fcinfo);
	
	elog(DEBUG1,"call done, disconnect and return");
	
	if( SPI_finish() != SPI_OK_FINISH ){
		elog(ERROR,"SPI finsh error");
	}
	
	return datumreturn;
}//plpgj_call_handler

