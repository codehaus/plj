#ifndef PLPGJ_HOOK_H
#define PLPGJ_HOOK_H

#if(POSTGRES_VERSION == 74)
	void reg_error_callback();
#else
	void handle_exception(void);
#endif

void plpgj_utl_sendint(int);
void plpgj_utl_sendstr(const char*);
void plpgj_utl_senderror(char*);


#endif
