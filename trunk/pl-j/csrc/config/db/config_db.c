
#include "module_config.h"

char* plj_get_configvalue_string(const char* paramName){
	
	return "dummy :)";
}

int plj_get_configvalue_int(const char* paramName){
	return atoi(plj_get_configvalue_string(paramName));
}

int plj_get_configvalue_boolean(const char* paramName){
	return (strcmp("true",plj_get_configvalue_string(paramName) == 0));
}

