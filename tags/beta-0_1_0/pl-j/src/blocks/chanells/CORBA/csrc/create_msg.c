#ifndef CREATE_MSG_H
#define CREATE_MSG_H

#include <jni.h>
#include <orbit/orbit.h>
#include "result.h"
#include "error.h"
#include "callreq.h"
#include "create_msg.h"
#include <stdlib.h>

extern jclass chanell_worker_class;
extern jmethodID chanell_create_result;
extern jmethodID chanell_create_callreq;
extern jmethodID chanell_create_exception;
extern jmethodID chanell_set_client;

extern jobject typemapper_block;
extern jclass typemapper_interface;
extern jmethodID typemapper_map_method;

extern jclass result_class;
//extern jmethodID result_class_default_constructor;

extern jclass message_class;
extern jmethodID message_set_sid;

extern jclass callreq_class;
//extern jmethodID callreq_class_default_constructor;
extern jmethodID callreq_set_method_name;
extern jmethodID callreq_set_class_name;
extern jmethodID callreq_add_param;


extern jclass error_class;
//extern jmethodID error_class_default_constructor;


jobject create_msg_result(JNIEnv* env ,org_pgj_corba_result* res){
	jobject ret;
	
//	ret = (*env)->NewObject(env, result_class, result_class_default_constructor);
	
	return ret;
}

jobject create_msg_call(JNIEnv* env, org_pgj_corba_callreq* callreq, jobject worker_thread){
	jobject ret;
	jstring methodname;
	jstring classname;
	jstring sid;
	int i;					//iterator on fields
	
	
	ret = (*env)->CallStaticObjectMethod(env, chanell_worker_class,
				chanell_create_callreq);
	
	sid = (*env)->NewStringUTF(env, callreq->sid);
	(*env)->CallVoidMethod(env, ret, message_set_sid, sid);
	
	//set methodname
	methodname = (*env)->NewStringUTF(env, callreq->methodname );
	(*env)->CallObjectMethod(env, ret, callreq_set_method_name, methodname );
	
	//set class name
	classname = (*env)->NewStringUTF(env, callreq->classname );
	(*env)->CallObjectMethod(env, ret, callreq_set_class_name, classname );
	
	//create fields
	for(i=0; i<callreq->values._length; i++){
		jobject field;
		jstring typename;
		struct org_pgj_corba_pair_type* fld;
/*		fld = &(callreq->values._buffer[i]);
		
		typename = (*env)->NewStringUTF(env, fld->name );
		field = (*env)->CallObjectMethod(env, typemapper_block, typemapper_map_method, 
					fld->value);
		
		
*/		
		//add parameter to the callreq.
		(*env)->CallObjectMethod(env, ret, callreq_add_param, field);
		
	}
	
	//set client
	
	(*env)->CallObjectMethod(env, worker_thread, chanell_set_client, atoi(callreq->sid), ret );
	
	return ret;
}

jobject create_msg_error(JNIEnv* env, org_pgj_corba_errorstruct* error){
	jobject ret;
	
//	ret = (*env)->NewObject(env, error_class, error_class_default_constructor);
	
	return ret;
}

#endif
