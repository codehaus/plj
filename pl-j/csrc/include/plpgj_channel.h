
/**
 * file name:			plpgj_channel.h
 * description:			Channel API.
 * author:			Laszlo Hornyak Kocka
 */

#ifndef PLPGJ_CHANNEL_H
#define PLPGJ_CHANNEL_H

#include "plpgj_messages.h"

#define PLPGJ_CHANNEL_OK			0
#define PLPGJ_CHANNEL_INIT_ERROR		1
#define PLPGJ_CHANNEL_SEND_ERROR		2

/**
 * Is the chanell initialized?
 * Returns 0 if not, anything else otherwise.
 */
int			plpgj_channel_initialized(void);

/**
 * Initialize the chanell.
 * Returns 0 if not, anything else otherwise.
 */
int			plpgj_channel_initialize(void);

/**
 * Send message across the channel.
 * Parameters:
 *	-message: a message to send
 * Returns
 */
int			plpgj_channel_send(message);
message		plpgj_channel_receive(void);
const char *plpgj_channel_getimplname(void);

#endif
