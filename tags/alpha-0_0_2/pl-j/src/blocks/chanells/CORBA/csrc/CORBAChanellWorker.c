
//needed to convert strings to unicode
#include <glib/gunicode.h>

//orbit includes
#include <orbit/orbit.h>

//unix includes
#include <stdio.h>

//plpgj specific includes
#include "CORBAChanellWorker.h"
#include "callserver.h"
#include "callserverImpl.h"

#include "create_msg.h"
#include "callserverImpl.h"
#include "log.h"

int argc = 0;
char * argv[] = {""};

static const char* STR_CLASS_CALLREQ = "org/pgj/messages/CallRequest";
static const char* STR_CLASS_RESULT = "org/pgj/messages/Result";
static const char* STR_CLASS_SQL = "org/pgj/messages/SQL";
static const char* STR_CLASS_EXCEPTION = "org/pgj/messages/Exception";
static const char* STR_CLASS_MESSAGE = "org/pgj/messages/Message";

static const char* STR_CLASS_FIELD = "org/pgj/typemapping/Field";

static const char* STR_CLASS_SQL_CURSOR = 
	"org/pgj/messages/SQLCursor";
static const char* STR_CLASS_SQL_OPEN_SQL = 
	"org/pgj/messages/SQLCursorOpenWithSQL";
static const char* STR_CLASS_SQL_CLOSE = 
	"org/pgj/messages/SQLCursorClose";
static const char* STR_CLASS_SQL_FETCH = 
	"org/pgj/messages/SQLFetch";
	
// class and method signatures for class Channel
jclass chanell_worker_class;
jmethodID chanell_create_result;
jmethodID chanell_create_callreq;
jmethodID chanell_create_exception;
jmethodID chanell_set_client;
jmethodID chanell_log;
jmethodID get_message;
jmethodID get_client;

jmethodID chanell_msg_arrival;

jobject typemapper_block;
jclass typemapper_interface;
jmethodID typemapper_map_method;

jclass result_class;
//jmethodID result_class_default_constructor;
	jmethodID result_setsize;
	jmethodID result_set;
	jmethodID result_get;
	jmethodID result_getrows;
	jmethodID result_getcolumns;


jclass message_class;
	jmethodID message_set_sid;
	jmethodID message_get_sid;

jclass callreq_class;
//jmethodID callreq_class_default_constructor;
	jmethodID callreq_set_method_name;
	jmethodID callreq_set_class_name;
	jmethodID callreq_add_param;

jclass error_class;
	jmethodID error_get_message;
	jmethodID error_set_message;
	jmethodID error_get_stacktrace;
	jmethodID error_set_stacktrace;
	jmethodID error_get_classname;
	jmethodID error_set_classname;

jclass message_sql;
jclass message_sql_cursor;
	jmethodID sql_fetch_get_cursorname;
	jmethodID sql_fetch_set_cursorname;
jclass message_sql_open_wia_sql;
jclass message_sql_close;
jclass message_sql_fetch;
	jmethodID sql_fetch_get_direction;
	jmethodID sql_fetch_set_direction;
	jmethodID sql_fetch_get_count;
	jmethodID sql_fetch_set_count;

jclass field_class;
	jmethodID field_get;
	jmethodID field_rdbmstype;


//jmethodID error_class_default_constructor;

//org_pgj_corba_CallServer 
//impl_org_pgj_corba_CallServer__create(PortableServer_POA poa, CORBA_Environment * ev);

CORBA_Environment env;
CORBA_ORB orb;
org_pgj_corba_CallServer server;
PortableServer_POA poa;
PortableServer_POAManager poa_manager;
JNIEnv* global_javaenv;

JNIEXPORT void JNICALL Java_org_pgj_chanells_corba_CORBAChanellWorker_run
(JNIEnv * javaenv, jobject threadobj){
	
	JNIEnv _javaenv = *javaenv;
	global_javaenv = javaenv;
	
	//---------------------------------------
	// initialize class and method references
	//---------------------------------------
	
	//chanell worker class and method ID`s
	chanell_worker_class = (*javaenv)->FindClass(javaenv, 
					"org/pgj/chanells/corba/CORBAChanellWorker");
	chanell_create_result = (*javaenv)->GetStaticMethodID(javaenv, chanell_worker_class, 
					"createResultBean", "()Lorg/pgj/messages/Result;");
	chanell_create_callreq = (*javaenv)->GetStaticMethodID(javaenv, chanell_worker_class,
					"createCallRequestBean", "()Lorg/pgj/messages/CallRequest;");
	chanell_create_exception = (*javaenv)->GetStaticMethodID(javaenv, chanell_worker_class,
					"createExceptionBean", "()Lorg/pgj/messages/Exception;");
	chanell_set_client = (*javaenv)->GetMethodID(javaenv, chanell_worker_class,
					"setClient","(ILorg/pgj/messages/Message;)V");
	chanell_msg_arrival = (*javaenv)->GetMethodID(javaenv, chanell_worker_class,
					"msgArrival", "(Lorg/pgj/messages/Message;)V");
	chanell_log = (*javaenv)->GetMethodID(javaenv, 
			chanell_worker_class, 
			"nativeLog", "(ILjava/lang/String;)V");
	
	get_message = (*javaenv)->GetMethodID(javaenv,
			chanell_worker_class,
			"getMessage", 
			"(Lorg/pgj/chanells/corba/CORBAClient;)Lorg/pgj/messages/Message;");
	get_client = (*javaenv)->GetMethodID(javaenv,
			chanell_worker_class,
			"getClient",
			"(I)Lorg/pgj/chanells/corba/CORBAClient;");
	
	//type mapper ID`s
	typemapper_interface = (*javaenv)->FindClass(javaenv, 
					"org/pgj/typemapping/TypeMapper");
	typemapper_map_method = (*javaenv)->GetMethodID(javaenv, typemapper_interface, 
					"map", "([BLjava/lang/String;)Lorg/pgj/typemapping/Field;");
	
	//message class
	message_class = (*javaenv)->FindClass(javaenv, STR_CLASS_MESSAGE);
	message_set_sid = (*javaenv)->GetMethodID(javaenv, message_class,
					"setSid", "(Ljava/lang/Object;)V");
	message_get_sid = (*javaenv)->GetMethodID(javaenv, message_class,
					"getSid", "()Ljava/lang/Object;");
	
	//result bean ID`s
	result_class = (*javaenv)->FindClass(javaenv, STR_CLASS_RESULT);
 	result_setsize = (*javaenv)->GetMethodID(javaenv, result_class, "setSize","(II)V");
	result_set =(*javaenv)->GetMethodID(javaenv, result_class, "set","(IILorg/pgj/typemapping/Field;)V");
	result_get = (*javaenv)->GetMethodID(javaenv, result_class, "get","(II)Lorg/pgj/typemapping/Field;");
	result_getrows = (*javaenv)->GetMethodID(javaenv, result_class, "getRows","()I");
	result_getcolumns = (*javaenv)->GetMethodID(javaenv, result_class, "getColumns","()I");
	
	//call request bean ID`s
	callreq_class = (*javaenv)->FindClass(javaenv, STR_CLASS_CALLREQ);
	callreq_set_method_name = (*javaenv)->GetMethodID(javaenv, callreq_class, 
					"setMethodname", "(Ljava/lang/String;)V");
	callreq_set_class_name = (*javaenv)->GetMethodID(javaenv, callreq_class,
					"setClassname", "(Ljava/lang/String;)V");
	callreq_add_param = (*javaenv)->GetMethodID(javaenv, callreq_class,
					"addParam", "(Lorg/pgj/typemapping/Field;)V");
	
	//exception bean ID`s
	error_class = (*javaenv)->FindClass(javaenv, STR_CLASS_EXCEPTION);
	error_get_message = (*javaenv)->GetMethodID(javaenv, error_class, "getMessage","()Ljava/lang/String;");
	error_set_message = (*javaenv)->GetMethodID(javaenv, error_class, "getMessage","()Ljava/lang/String;");
	error_get_stacktrace = (*javaenv)->GetMethodID(javaenv, error_class, "getStackTrace","()Ljava/lang/String;");
	error_set_stacktrace = (*javaenv)->GetMethodID(javaenv, error_class, "setStackTrace","(Ljava/lang/String;)V");
	error_get_classname = (*javaenv)->GetMethodID(javaenv, error_class, "getExceptionClassName","()Ljava/lang/String;");
	error_set_classname = (*javaenv)->GetMethodID(javaenv, error_class, "setExceptionClassName","(Ljava/lang/String;)V");
	
	message_sql = (*javaenv)->FindClass(javaenv, STR_CLASS_SQL);

	message_sql_cursor = (*javaenv)->FindClass(javaenv, STR_CLASS_SQL_CURSOR);
		sql_fetch_get_cursorname = (*javaenv)->GetMethodID(javaenv, message_sql_cursor, "getCursorName","()Ljava/lang/String;");
		sql_fetch_set_cursorname = (*javaenv)->GetMethodID(javaenv, message_sql_cursor, "setCursorName","(Ljava/lang/String;)V");

	message_sql_open_wia_sql = (*javaenv)->FindClass(javaenv, STR_CLASS_SQL_OPEN_SQL);
	message_sql_close = (*javaenv)->FindClass(javaenv, STR_CLASS_SQL_CLOSE);
	message_sql_fetch = (*javaenv)->FindClass(javaenv, STR_CLASS_SQL_FETCH);
	
	field_class = (*javaenv)->FindClass(javaenv, STR_CLASS_FIELD);
		field_get = (*javaenv)->GetMethodID(javaenv, field_class, "get","()[B");
		field_rdbmstype = (*javaenv)->GetMethodID(javaenv, field_class, "rdbmsType","()Ljava/lang/String;");
	
	if(_javaenv->ExceptionOccurred(javaenv)){
		return;
	}
	
	worker_nlog(javaenv, threadobj, WNL_DEBUG,"-----corba-----\n");
	
	//------------
	// Start CORBA
	//------------
	
	CORBA_exception_init(&env);
			
	orb = CORBA_ORB_init(&argc, argv, "orbit-local-orb", &env);
	
	poa = (PortableServer_POA) CORBA_ORB_resolve_initial_references(orb, "RootPOA", &env);
	poa_manager = PortableServer_POA__get_the_POAManager(poa, &env);
	PortableServer_POAManager_activate(poa_manager, &env);
	
	server = impl_org_pgj_corba_CallServer__create(poa, &env, javaenv, threadobj);
	
	worker_nlog(javaenv, threadobj, WNL_DEBUG,"starting orb\n");
	
	//publish IOR
	{
		char* ior;
		jmethodID putIor;
		jclass workerclass;
		jbyteArray bior;
		
		gunichar ior_utf;
		
		worker_nlog(javaenv, threadobj, WNL_DEBUG, "critical area\n");
		
		ior = CORBA_ORB_object_to_string(orb, server, &env);
		ior_utf = g_utf8_get_char(ior);
		
		bior = _javaenv->NewByteArray(javaenv, strlen(ior));
		
		if(_javaenv->ExceptionCheck(javaenv)){
			worker_nlog(javaenv, threadobj, WNL_FATAL,
					"problems");
			return;
		}
		
		_javaenv->SetByteArrayRegion(javaenv, bior, 0, strlen(ior), ior);
		
		if(_javaenv->ExceptionCheck(javaenv)){
			worker_nlog(javaenv, threadobj, WNL_ERROR, "error occured");
			return;
		}
		
		
		workerclass = _javaenv->GetObjectClass(javaenv, threadobj);
		
		if(_javaenv->ExceptionCheck(javaenv)){
			worker_nlog(javaenv, threadobj, WNL_ERROR, "problems");
			return;
		}
		
		putIor = _javaenv->GetMethodID(javaenv, workerclass, "putIor", "([B)V");
		
		if(_javaenv->ExceptionCheck(javaenv)){
			worker_nlog(javaenv, threadobj, WNL_FATAL,
					"problems");
			return;
		}
		
		worker_nlog(javaenv, threadobj, WNL_DEBUG, "publishing IOR");
		
		_javaenv->CallObjectMethod(javaenv, threadobj, putIor, bior);
		
		if(_javaenv->ExceptionCheck(javaenv)){
			worker_nlog(javaenv, threadobj, WNL_FATAL,
					"Error publishing IOR! Stoping!");
			return;
		}
		worker_nlog(javaenv, threadobj, WNL_INFO, 
				"IOR published successfuly");
		
	}
	//publish IOR
	
	CORBA_ORB_run(orb, &env);
	
	worker_nlog(javaenv, threadobj, WNL_FATAL, 
		"ORB run returned. There must be something problem here.");
	
}

JNIEXPORT void JNICALL Java_org_pgj_chanells_corba_CORBAChanellWorker_terminate
	(JNIEnv * javaenv, jobject threadobj){
	//--
	worker_nlog(javaenv, threadobj, WNL_INFO,
		"worker was asked to terminate\n");
	
	proxy_impl_org_pgj_corba_CallServer__destroy(server, &env, javaenv, threadobj, poa);
	
}

void worker_nlog(JNIEnv * javaenv, jobject threadobj, int level, const char* log){
	
	jstring strlog;
	jint jlevel = level;
	
	if(log == NULL)
		return;
	
	if(javaenv == NULL)
		return;
	
	strlog = (*javaenv)->NewStringUTF(javaenv, log);
	
	(*javaenv)->CallObjectMethod(javaenv, threadobj, chanell_log, 
				     jlevel, strlog);

}

