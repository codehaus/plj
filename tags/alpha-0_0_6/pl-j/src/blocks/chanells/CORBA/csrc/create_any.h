#ifndef CREATE_ANY_H
#define CREATE_ANY_H

#include "jni.h"
#include "orbit/orbit.h"
#include "orbit/orb-core/corba-any.h"

CORBA_any* create_any(jobject obj, jobject threadobj, JNIEnv* env);

#endif
