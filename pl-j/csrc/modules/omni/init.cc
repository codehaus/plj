
#include "omniORB4/CORBA.h"
#include "data.hh"

int omni_init(){
	int argv = 0;
	
	omni_orb = CORBA::ORB_init( argv, NULL, "omniORB4");
	CORBA::Object_var nameserv_var = omni_orb->resolve_initial_references("NameService");
	CosNaming::NamingContext_var rootContext = 
		CosNaming::NamingContext::_narrow(nameserv_var);
	
	CosNaming::Name callServerName;
	callServerName.length(1);
	callServerName[0].id = "callserver";
	CORBA::Object_var callserver_obj = rootContext->resolve(callServerName);
	
	callserver = org::pgj::corba::CallServer::_narrow(callserver_obj);
	
	return 0;
}

const char* channel_impl_name = "omni4";

extern "C" {
	#include "postgres.h"
	#include "plpgj_chanell.h"

	int plpgj_chanell_initialized(void){
		return (initialized == 1);
	}
	
	int plpgj_chanell_initialize(void){
		if(initialized == 1)
			return 1;
	}
	
	const char* plpgj_chanell_getimplname(void){
		return channel_impl_name;
	}
	
}

