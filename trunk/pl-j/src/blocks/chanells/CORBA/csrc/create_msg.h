#ifndef CREATE_MSG_H
#define CREATE_MSG_H

#include <jni.h>
#include <orbit/orbit.h>
#include "error.h"
#include "result.h"
#include "callreq.h"

jobject create_msg_result(JNIEnv* env, org_pgj_corba_result* res);

jobject create_msg_call(JNIEnv* env, org_pgj_corba_callreq* callreq, jobject worker_thread);

jobject create_msg_error(JNIEnv* env, org_pgj_corba_errorstruct* error);

#endif
