
#include "module_config.h"
#include "executor/spi.h"
#include "utils/elog.h"

const char *get_sql =
	"SELECT config_value FROM SQLJ.plj_config where config_key='%s'";

char *
plj_get_configvalue_string(const char *paramName)
{

	char	   *sql;
	int			proc,
				ret;

	/*
	 * no SPI_connect, we are already connected.
	 */

	sql = SPI_palloc(strlen(paramName) + strlen(get_sql));

	sprintf(sql, get_sql, paramName);

	ret = SPI_exec(sql, 1);
	proc = SPI_processed;
	if (ret == SPI_OK_SELECT && proc > 0)
	{
		TupleDesc	tupdesc = SPI_tuptable->tupdesc;
		SPITupleTable *tuptable = SPI_tuptable;

		return SPI_getvalue(tuptable->vals[0], tupdesc, 1);
	}

	elog(WARNING, "config value not set");
	return "";
}

int
plj_get_configvalue_int(const char *paramName)
{
	return atoi(plj_get_configvalue_string(paramName));
}

int
plj_get_configvalue_boolean(const char *paramName)
{
	return (strcmp("true", plj_get_configvalue_string(paramName)) == 0);
}
