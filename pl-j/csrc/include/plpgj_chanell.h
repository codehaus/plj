/**
 * file name:			plpgj_chanell.h
 * description:			Chanell API.
 * author:			Laszlo Hornyak Kocka
 */

#ifndef PLPGJ_CHANELL_H
#define PLPGJ_CHANELL_H

#ifdef __cplusplus
extern "C" {
#endif
	
#include "plpgj_messages.h"

#define PLPGJ_CHANELL_OK			0
#define PLPGJ_CHANELL_INIT_ERROR		1
#define PLPGJ_CHANELL_SEND_ERROR		2

/**
 * Is the chanell initialized?
 * Returns 0 if not, anything else otherwise.
 */
int plpgj_chanell_initialized(void);

/**
 * Initialize the chanell.
 * Returns 0 if not, anything else otherwise.
 */
int plpgj_chanell_initialize(void);

/**
 * Send message across the chanell.
 * Parameters:
 *  -message: a message to send
 * Returns 
 */
int plpgj_chanell_send(message);
message plpgj_chanell_receive(void);
const char* plpgj_chanell_getimplname(void);

#ifdef __cplusplus
}
#endif

#endif
