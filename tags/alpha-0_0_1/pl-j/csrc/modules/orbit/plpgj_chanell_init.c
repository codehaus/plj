/**
 * file name:			plpgj_chanell_work.c
 * description:			ORBIT chanell implementation, central functions.
 * author:				Laszlo Hornyak
 */

//#include "postgres.h"
//#include "fmgr.h"
#include "plpgj_messages.h"
#include "plpgj_chanell.h"
#include <unistd.h>
#include <stdlib.h>
#include <orbit/orbit.h>

extern CORBA_ORB corba_orb;
extern CORBA_Environment corba_env;
extern CORBA_Object corba_server;
extern char session_id[];

int corba_argc = 3;
const char** corba_argv = {"","--ORBIIOPUSock=0","",""};

int initialized = 0;

void get_server_ior();

int plpgj_chanell_initialize(){
	char* server_ior;
	CORBA_exception_init(&corba_env);
	
	corba_orb = CORBA_ORB_init(&corba_argc, corba_argv, "orbit-local-orb", &corba_env);
	
	if(corba_env._major != CORBA_NO_EXCEPTION){
//		elog(ERROR, "chanell init error: ORB initialization error");
		return -1;
	}
	
	server_ior = malloc(2048);
	
	get_server_ior(&server_ior);
	
	corba_server = CORBA_ORB_string_to_object(corba_orb, server_ior, &corba_env);
	if(corba_env._major != CORBA_NO_EXCEPTION){
//		elog(ERROR, "chanell init error: couldn`t get reference.");
		return -1;
	}
	
	free(server_ior);
	
	sprintf(session_id, "%d", getpid());
	
	initialized = 1;
	
	return 0;
}

#ifndef CORBA_IOR_METHOD
#warning no CORBA_IOR_METHOD defined using ior-file
#define CORBA_IOR_METHOD ior-file
#endif

#if CORBA_IOR_METHOD == ior-file

#ifndef IOR_FILE
#warning IOR_FILE not defined using /tmp/plpgj.ior
#define IOR_FILE "/tmp/plpgj.ior"
#endif

void get_server_ior(char** ior){
	FILE* iorfile;
	int iorsize;
	
//	elog(DEBUG1,"get_server_ior");
//	elog(DEBUG1,"ior file is:%s", IOR_FILE);
	
	iorfile = fopen(IOR_FILE,"r");
	
	if(iorfile == NULL){
//		elog(DEBUG1, "ior file problem.");
		return;
	}
	
//	elog(DEBUG1, "get_server_ior");
	
	iorsize = fread((*ior), 1, 2048, iorfile);
	(*ior)[iorsize + 1] = 0;
	
//	elog(DEBUG1,"ior is %s\n", (*ior));
	
	fclose(iorfile);
	
	return;
}

#elif CORBA_IOR_METHOD == nameserver

void get_server_ior(char** ior){
	
}

#endif

int plpgj_chanell_initialized(){
	return initialized;
}

