
#ifndef CALLSERVER_IMPL_H
#define CALLSERVER_IMPL_H
#include "callserver.h"
#include <jni.h>


org_pgj_corba_CallServer
 impl_org_pgj_corba_CallServer__create(PortableServer_POA poa,
 CORBA_Environment * ev, JNIEnv * env, jobject worker);

void proxy_impl_org_pgj_corba_CallServer__destroy(org_pgj_corba_CallServer callserver, 
	CORBA_Environment * ev, 
	JNIEnv* env, 
	jobject threadobj, 
	PortableServer_POA poa);

#endif
