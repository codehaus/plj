#ifndef NATIVE_LOG_H
#define NATIVE_LOG_H

#define WNL_DEBUG	0
#define WNL_INFO	1
#define WNL_WARN	2
#define WNL_ERROR	3
#define WNL_FATAL	4

/*
 * logger function for the native functions.
 */
void worker_nlog(JNIEnv * javaenv, jobject threadobj, int level, const char* log);

#endif
