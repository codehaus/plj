#include "callserver.h"
#include <stdlib.h>
#include <jni.h>
#include "result.h"
#include "callreq.h"
#include "create_msg.h"
#include "create_any.h"
#include <string.h>
#include "callserverImpl.h"

#include "log.h"

extern jmethodID get_message;
extern jmethodID get_client;

typedef struct
{
		POA_org_pgj_corba_CallServer servant;
		PortableServer_POA poa;
		JNIEnv* java_env;
		jobject worker_obj;
}
impl_POA_org_pgj_corba_CallServer;


/*** Implementation stub prototypes ***/

static void
impl_org_pgj_corba_CallServer__destroy(impl_POA_org_pgj_corba_CallServer *
				       servant, CORBA_Environment * ev);
static void
impl_org_pgj_corba_CallServer_sendMessage(impl_POA_org_pgj_corba_CallServer *
					  servant, const CORBA_any * message,
					  CORBA_Environment * ev);

static CORBA_any
   *impl_org_pgj_corba_CallServer_receiveAnsver
   (impl_POA_org_pgj_corba_CallServer * servant, const CORBA_char * sid,
    CORBA_Environment * ev);

static CORBA_any
   *impl_org_pgj_corba_CallServer_SendReceive
   (impl_POA_org_pgj_corba_CallServer * servant, const CORBA_any * message,
    CORBA_Environment * ev);

/*** epv structures ***/

static PortableServer_ServantBase__epv impl_org_pgj_corba_CallServer_base_epv
   = {
   NULL,			/* _private data */
   NULL,			/* finalize routine */
   NULL,			/* default_POA routine */
};
static POA_org_pgj_corba_CallServer__epv impl_org_pgj_corba_CallServer_epv = {
   NULL,			/* _private */
   (gpointer) & impl_org_pgj_corba_CallServer_sendMessage,

   (gpointer) & impl_org_pgj_corba_CallServer_receiveAnsver,

   (gpointer) & impl_org_pgj_corba_CallServer_SendReceive,

};

/*** vepv structures ***/

static POA_org_pgj_corba_CallServer__vepv impl_org_pgj_corba_CallServer_vepv = {
   &impl_org_pgj_corba_CallServer_base_epv,
   &impl_org_pgj_corba_CallServer_epv,
};

/*** Stub implementations ***/

org_pgj_corba_CallServer
impl_org_pgj_corba_CallServer__create(PortableServer_POA poa,
				      CORBA_Environment * ev, JNIEnv* env, jobject threadobj)
{
   org_pgj_corba_CallServer retval;
   impl_POA_org_pgj_corba_CallServer *newservant;
   PortableServer_ObjectId *objid;

   newservant = g_new0(impl_POA_org_pgj_corba_CallServer, 1);
   newservant->servant.vepv = &impl_org_pgj_corba_CallServer_vepv;
   newservant->poa = poa;
   newservant->java_env = env;
   newservant->worker_obj = threadobj;
   POA_org_pgj_corba_CallServer__init((PortableServer_Servant) newservant,
				      ev);
   objid = PortableServer_POA_activate_object(poa, newservant, ev);
   CORBA_free(objid);
   retval = PortableServer_POA_servant_to_reference(poa, newservant, ev);
   worker_nlog(env, threadobj, WNL_DEBUG, 
		   "call server implementation created");

   return retval;
}

void proxy_impl_org_pgj_corba_CallServer__destroy(org_pgj_corba_CallServer callserver, 
	CORBA_Environment * ev, 
	JNIEnv* env, 
	jobject threadobj, PortableServer_POA poa)
{
   PortableServer_Servant poa_servant;
   
   poa_servant = PortableServer_POA_reference_to_servant(poa, callserver, ev);
   
   worker_nlog(
      ((impl_POA_org_pgj_corba_CallServer *)poa_servant)->java_env, 
      threadobj,
      WNL_DEBUG, 
      "proxy: destroying call server implementation");
   
   impl_org_pgj_corba_CallServer__destroy(
   (impl_POA_org_pgj_corba_CallServer*)poa_servant, ev);
   
}


static void
impl_org_pgj_corba_CallServer__destroy(impl_POA_org_pgj_corba_CallServer *
				       servant, CORBA_Environment * ev)
{
   PortableServer_ObjectId *objid;
   worker_nlog(servant->java_env, servant->worker_obj, WNL_DEBUG, 
		   "destroying call server implementation");

   objid = PortableServer_POA_servant_to_id(servant->poa, servant, ev);
   PortableServer_POA_deactivate_object(servant->poa, objid, ev);
   CORBA_free(objid);

   POA_org_pgj_corba_CallServer__fini((PortableServer_Servant) servant, ev);
   g_free(servant);
}

extern jmethodID chanell_msg_arrival;

/**
 * Interface function for clients to send message to this java server.
 */
static void
impl_org_pgj_corba_CallServer_sendMessage(impl_POA_org_pgj_corba_CallServer *
					  servant, const CORBA_any * message,
					  CORBA_Environment * ev)
{
	jobject msg;
	
	worker_nlog(servant->java_env, servant->worker_obj, WNL_DEBUG, 
			"sendMessage");
	
	if(strcmp(TC_org_pgj_corba_result_struct.repo_id, message->_type->repo_id) == 0){
		worker_nlog(servant->java_env, servant->worker_obj, WNL_DEBUG, 
				"result\n");
		
		return;
	}
	
	if(strcmp(TC_org_pgj_corba_callreq_struct.repo_id, message->_type->repo_id) == 0){
		worker_nlog(servant->java_env, servant->worker_obj, WNL_DEBUG, 
				"callreq\n");
		
		msg = create_msg_call(servant->java_env, 
						((org_pgj_corba_callreq*)message->_value),
						servant->worker_obj);
		
		worker_nlog(servant->java_env, servant->worker_obj, WNL_DEBUG,
				"and now...\n");
		
		(*(servant->java_env))->CallVoidMethod(servant->java_env, servant->worker_obj, chanell_msg_arrival, msg);
		
		/*
		if( (*(servant->java_env))->ExceptionCheck(servant->java_env) )
				(*(servant->java_env))
		*/
		
		worker_nlog(servant->java_env, servant->worker_obj, 
				WNL_DEBUG, "Queued!!\n");
		
		return;
	}
}

static CORBA_any *
impl_org_pgj_corba_CallServer_receiveAnsver(impl_POA_org_pgj_corba_CallServer
					* servant, const CORBA_char * sid,
					CORBA_Environment * ev)
{
   CORBA_any *retval;
   jobject msg;
   jobject clnt;
   
   worker_nlog(servant->java_env, servant->worker_obj, WNL_DEBUG, "receiveAnsver ");
   
   clnt =  (*(servant->java_env))->CallObjectMethod(servant->java_env, servant->worker_obj, get_client, atoi(sid));
   
   worker_nlog(servant->java_env, servant->worker_obj, WNL_DEBUG, "have client");
   
   msg = (*(servant->java_env))->CallObjectMethod(servant->java_env, servant->worker_obj, get_message, clnt );
   
   worker_nlog(servant->java_env, servant->worker_obj, WNL_DEBUG, "have message");
   
   retval = create_any(msg, servant->worker_obj, servant->java_env);
   
   worker_nlog(servant->java_env, servant->worker_obj, WNL_DEBUG, "created any ansver");
   
   return retval;
}

static CORBA_any *
impl_org_pgj_corba_CallServer_SendReceive(impl_POA_org_pgj_corba_CallServer *
					  servant, const CORBA_any * message,
					  CORBA_Environment * ev)
{

   // soo simple
   
   CORBA_char * sid;
   
   impl_org_pgj_corba_CallServer_sendMessage(servant, message, ev);
   
   if(strcmp(TC_org_pgj_corba_callreq_struct.repo_id, message->_type->repo_id) == 0){
   	sid = ((org_pgj_corba_callreq*)message->_value)->sid;
   } else if (strcmp(TC_org_pgj_corba_result_struct.repo_id, message->_type->repo_id) == 0){
   	sid = ((org_pgj_corba_result*)message->_value)->sid;
   } else {
   	sid = NULL;
   }
   
   return impl_org_pgj_corba_CallServer_receiveAnsver(servant, sid, ev);
   
}
