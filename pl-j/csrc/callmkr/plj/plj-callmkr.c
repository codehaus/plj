
/**
 * file name:		plj-callmkr.c
 * description:		PL/pgJ call message creator routine. This file
 *			was renamed from plpgj_call_maker.c
 *			It is replaceable with pljava-way of declaring java
 *			method calls. (read the readme!)
 * author:		Laszlo Hornyak
 */

#include "postgres.h"
#include "plpgj_messages.h"
#include "plpgj_message_fns.h"

#include "utils/palloc.h"
#include "catalog/pg_type.h"

/* #include "utils/elog.h" */
#include "pljelog.h"
#include "utils/datum.h"
#include "commands/trigger.h"
#include "fmgr.h"
#include "executor/spi.h"
#include "regex.h"
#include "pg_config.h"

#ifndef MAX_NO_OPTS
#define MAX_NO_OPTS		10
#warning MAX_NO_OPTS not defined, using default 10
#endif

#ifndef POSTGRES_VERSION
#warning POSTGRES_VERSION is the default 74
#define POSTGRES_VERSION 74
#endif

const char *__func_opt_regexp = "\\(\\w*\\)=\\(.*\\)$";
regex_t		func_opt_regexp;
int			plpgj_re_init = 0;

void
plpgj_create_call_regex_init()
{
	int			ret;

	if (plpgj_re_init)
		return;

	/*
	 * elog(DEBUG1,"initializing regexp.");
	 */

	ret = regcomp(&func_opt_regexp, __func_opt_regexp, REG_NEWLINE);

	plpgj_re_init = 1;
}

Form_pg_proc
glpgj_getproc(PG_FUNCTION_ARGS)
{
	HeapTuple	proctup;
	Form_pg_proc procstruct;
	Oid			funcoid;

	funcoid = fcinfo->flinfo->fn_oid;
	proctup = SearchSysCache(PROCOID, ObjectIdGetDatum(funcoid), 0, 0, 0);
	procstruct = (Form_pg_proc) GETSTRUCT(proctup);
	ReleaseSysCache(proctup);
	return procstruct;
}

void
plpgj_fill_callstruct(Form_pg_proc procStruct,
					  char *classname, char *methodname)
{

	char	   *func_src;
	int			func_src_len;
	int			fret,
				i;
	regmatch_t	matches[MAX_NO_OPTS];

	plpgj_create_call_regex_init();

#if POSTGRES_VERSION == 74

	func_src =
		DatumGetCString(DirectFunctionCall1
						(textout, PointerGetDatum(&procStruct->prosrc)));

#elif POSTGRES_VERSION == 75

	func_src = DatumGetCString(DirectFunctionCall1(textout,
												   SysCacheGetAttr(PROCOID,
																   proctup,
																   Anum_pg_proc_prosrc,
																   &isnull)));


#else

#error UNSUPPORTED PG VERSION, MUST BE 7.4 OR 7.5DEV
#endif

	/*
	 * elog(DEBUG1,func_src);
	 */
	func_src_len = strlen(func_src);

	i = 0;
	while (1)
	{
		int			namestart;
		int			nameend;
		int			start;
		int			end;
		char		tmp[100];

		fret =
			regexec(&func_opt_regexp, func_src + i, MAX_NO_OPTS, matches,
					0);
		if (fret)
			break;

		start = matches[2].rm_so;
		end = matches[2].rm_eo;
		namestart = matches[1].rm_so;
		nameend = matches[1].rm_eo;

		strncpy(tmp, func_src + i + namestart, nameend - namestart);
		tmp[nameend - namestart] = 0;

		strncpy(tmp, func_src + i + start, end - start);
		tmp[end - start] = 0;
		if ((start == -1) || (end == -1))
			break;

		if (strncmp(func_src + i + namestart, "class", 5) == 0)
		{
			if (end - start > 50)
			{
				pljlogging_error = 1;
				elog(ERROR, "Too long class name");
				return;
			}
			strncpy(classname, func_src + i + start, end - start);
		}
		else if (strncmp(func_src + i + namestart, "method", 6) == 0)
		{
			if (end - start > 50)
			{
				pljlogging_error = 1;
				elog(ERROR, "Too long method name");
				return;
			}
			strncpy(methodname, func_src + i + start, end - start);
		}
		else
		{
			/*
			 * elog(DEBUG1,"unhandled directive");
			 */
		}

		i += end + 1;
		/*
		 * elog(DEBUG1,"moving to: %d",i);
		 */
		if (i >= func_src_len)
			break;
	}

}

/**
 * Creates a prtocol-implementation-friendly representation of a tuple.
 */
pparam *
plpgj_create_trigger_tuple(HeapTuple tuple, TupleDesc desc)
{
	int			i;

	/*
	 * I wonder if this protection is neccessary.
	 */
	if (desc->natts <= 0)
	{
		/*
		 * elog(INFO, "tuple desc with zero attributes");
		 */
		return NULL;
	}

	pparam	   *ret;

	ret = SPI_palloc(desc->natts * sizeof(pparam));
	for (i = 0; i < desc->natts; i++)
	{
		Datum		binval;
		Oid			typeid;
		bool		isnull;
		int2		typlen = 0;
		bool		typbyval = FALSE;
		regproc		typsnd;
		HeapTuple	typtup;
		Form_pg_type typ;


		/*
		 * alloc each param
		 */
		ret[i] = palloc(sizeof(struct fnc_param));
		typeid = SPI_gettypeid(desc, i + 1);

		typtup =
			SearchSysCache(TYPEOID, ObjectIdGetDatum(typeid), 0, 0, 0);
		typ = (Form_pg_type) GETSTRUCT(typtup);
		typbyval = typ->typbyval;
		typlen = typ->typlen;
		typsnd = typ->typsend;
		ReleaseSysCache(typtup);

		if (tuple != NULL)
		{
			binval = OidFunctionCall1(typsnd,
									  SPI_getbinval(tuple, desc, i + 1,
													&isnull));

			ret[i]->data.data = DatumGetPointer(binval);
			ret[i]->data.isnull = isnull;
			ret[i]->data.length = datumGetSize(binval, typbyval, typlen);

			ret[i]->type = SPI_gettype(desc, i + 1);
		}
		else
			elog(WARNING, "tupl is null");
		/*
		 * if(isnull){
		 * elog(DEBUG1,"null");
		 * } else {
		 * elog(DEBUG1,"not null");
		 * }
		 */

	}
	return ret;
}

/**
 * Fills in the colnames and the coltypes for the trigger call request.
 */
int
plpgj_create_trigger_tuplekind(trigger_callreq call, TriggerData *tdata)
{

	int			i;

	/*
	 * TODO is this here still needed?
	 */

/*	if(tdata == NULL)
		elog(ERROR, "tdata is null");
	if(tdata -> tg_relation == NULL)
		elog(ERROR, "tg_relation is null");
	if(tdata -> tg_relation -> rd_att == NULL)
		elog(ERROR, "rd_att is null");
*/
	call->colcount = tdata->tg_relation->rd_att->natts;
	call->colnames = palloc(call->colcount * sizeof(char *));
	call->coltypes = palloc(call->colcount * sizeof(char *));
	for (i = 0; i < call->colcount; i++)
	{
		TupleDesc	desc;
		Form_pg_attribute attr;

		desc = tdata->tg_relation->rd_att;
		attr = desc->attrs[i];
		call->colnames[i] = NameStr(attr->attname);
		call->coltypes[i] = SPI_gettype(desc, i + 1);
	}
	return 1;
}

/**
 * Creates trigger call. It has some in common with plpgj_create_call
 * so a merge will be neccesary here.
 */
trigger_callreq
plpgj_create_trigger_call(PG_FUNCTION_ARGS)
{
	TriggerData *tdata;
	trigger_callreq ret;
	Form_pg_proc procStruct;

	ret = SPI_palloc(sizeof(str_msg_trigger_callreq));
	ret->msgtype = MT_TRIGREQ;
	ret->length = sizeof(str_msg_trigger_callreq);

	tdata = (TriggerData *) fcinfo->context;
	if (TRIGGER_FIRED_AFTER(tdata->tg_event))
		ret->actionorder = PLPGJ_TRIGGER_ACTIONORDER_AFTER;
	else
		ret->actionorder = PLPGJ_TRIGGER_ACTIONORDER_BEFORE;

	if (TRIGGER_FIRED_FOR_ROW(tdata->tg_event))
		ret->row = PLPGJ_TRIGGER_STARTED_FOR_ROW;
	else
		ret->row = PLPGJ_TRIGGER_STARTED_FOR_STATEMENT;

	/*
	 * set relation name
	 */
	ret->tablename = SPI_getrelname(tdata->tg_relation);
	/*
	 * elog(DEBUG1, "relation: %s", ret -> tablename);
	 */

/* TODO this is wrong here!! */
	if (tdata->tg_trigtuple != NULL)
		plpgj_create_trigger_tuplekind(ret, tdata /* -> tg_trigtuple */ );
	else if (tdata->tg_newtuple != NULL)
		plpgj_create_trigger_tuplekind(ret, tdata /* -> tg_newtuple */ );


	if (TRIGGER_FIRED_BY_INSERT(tdata->tg_event))
	{
		/*
		 * elog(DEBUG1, "insert trigger");
		 */
		if (tdata->tg_trigtuple == NULL)
		{
			/*
			 * elog(DEBUG1, "this is where trigtuple should not be null.");
			 */
		}
		else
		{
			/*
			 * elog(DEBUG1, "tg_trigtuple is not null!!");
			 */
		}
		ret->reason = PLPGJ_TRIGGER_REASON_INSERT;
		ret->_new = plpgj_create_trigger_tuple(tdata->tg_trigtuple,
											   tdata->tg_relation->rd_att);
	}
	else if (TRIGGER_FIRED_BY_UPDATE(tdata->tg_event))
	{
		ret->reason = PLPGJ_TRIGGER_REASON_UPDATE;
		ret->_old = plpgj_create_trigger_tuple(tdata->tg_trigtuple,
											   tdata->tg_relation->rd_att);
		ret->_new = plpgj_create_trigger_tuple(tdata->tg_newtuple,
											   tdata->tg_relation->rd_att);

	}
	else
	{
		ret->reason = PLPGJ_TRIGGER_REASON_DELETE;
		ret->_old = plpgj_create_trigger_tuple(tdata->tg_trigtuple,
											   tdata->tg_relation->rd_att);
	}


	procStruct = glpgj_getproc(fcinfo);
	plpgj_fill_callstruct(procStruct, ret->classname, ret->methodname);
	return ret;
}

/**
 * Creates call message structure from call data. (if not trigger call)
 */
callreq
plpgj_create_call(PG_FUNCTION_ARGS)
{
	callreq		ret;
	Oid			funcoid;
	HeapTuple	proctup;
	Form_pg_proc procstruct;
	HeapTuple	retTypetup;
	Form_pg_type rettype;
	char	   *func_src;
	int			i,
				func_src_len;

	i = 0;

	ret = SPI_palloc(sizeof(str_msg_callreq));
	memset(ret, 0, sizeof(str_msg_callreq));
	ret->msgtype = MT_CALLREQ;
	ret->length = sizeof(str_msg_callreq);

	funcoid = fcinfo->flinfo->fn_oid;
	proctup = SearchSysCache(PROCOID, ObjectIdGetDatum(funcoid), 0, 0, 0);
	procstruct = (Form_pg_proc) GETSTRUCT(proctup);
	ReleaseSysCache(proctup);

#if POSTGRES_VERSION == 74

	func_src =
		DatumGetCString(DirectFunctionCall1
						(textout, PointerGetDatum(&procstruct->prosrc)));

#elif POSTGRES_VERSION == 75

	func_src =
		DatumGetCString(DirectFunctionCall1
						(textout,
						 SysCacheGetAttr(PROCOID, proctup,
										 Anum_pg_proc_prosrc, &isnull)));

#else

#error NOT SUPPORTED POSTGRESQL VERSION (but i guess i told about it)
#endif

	func_src_len = strlen(func_src);

	plpgj_fill_callstruct(procstruct, ret->classname, ret->methodname);

	ret->nrOfParams = fcinfo->nargs;
	if (ret->nrOfParams > 0)
		ret->parameters = SPI_palloc((ret->nrOfParams) *
									 sizeof(struct fnc_param));

	/*
	 * fill in parameter type structures
	 */
	for (i = 0; i < ret->nrOfParams; i++)
	{
		Form_pg_type paramtype;
		HeapTuple	typeTup;

		typeTup =
			SearchSysCache(TYPEOID,
						   ObjectIdGetDatum(procstruct->proargtypes[i]), 0,
						   0, 0);
		if (!HeapTupleIsValid(typeTup))
			pljlogging_error = 1;
		elog(ERROR, "INVALID TYPE OID?");

		paramtype = (Form_pg_type) GETSTRUCT(typeTup);
		if (fcinfo->argnull[i])
		{
			ret->parameters[i].data.isnull = 1;
			ret->parameters[i].data.data = NULL;
			ret->parameters[i].data.length = 0;
		}
		else
		{
			Datum		sendDatum;

			sendDatum =
				OidFunctionCall1(paramtype->typsend, fcinfo->arg[i]);
			ret->parameters[i].data.isnull = 0;
			ret->parameters[i].data.data = DatumGetPointer(sendDatum);
			/*
			 * elog(DEBUG1,"alive");
			 */
			ret->parameters[i].data.length =
				datumGetSize(fcinfo->arg[i], paramtype->typbyval,
							 paramtype->typlen);
			/*
			 * elog(DEBUG1,"alive");
			 */
			/*
			 * if param is not null, get it from
			 */
		}

		ret->parameters[i].type = paramtype->typname.data;
		ReleaseSysCache(typeTup);
	}

	/*
	 * fill in expected return type
	 */
	retTypetup =
		SearchSysCache(TYPEOID, ObjectIdGetDatum(procstruct->prorettype),
					   0, 0, 0);
	if (!HeapTupleIsValid(retTypetup)){
		pljlogging_error = 1;
		elog(ERROR, "return type is invalid?");
	}
	rettype = (Form_pg_type) GETSTRUCT(retTypetup);
	ret->expect = (char *) rettype->typname.data;
	ReleaseSysCache(retTypetup);

	/*
	 * elog(DEBUG1, func_src);
	 */
	return ret;
}
