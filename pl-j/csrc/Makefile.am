
POSTGRES_SRC_HOME=@PGSOURCETREE@/src/

COMM_MODULE=@CHANMOD@
CONF_MODULE=@CONFMOD@
CALLMKR_MODULE=@CALLMKRMOD@
PORTNAME=@PORTNAME@
PG_MAJOR_VERSION=@PG_MAJOR_VERSION@
PG_MINOR_VERSION=@PG_MINOR_VERSION@
PG_RELEASE_VERSION=@PG_RELEASE_VERSION@

plpgjdir=@SRCDIR@
plpgj_LTLIBRARIES = libplpgj.la
libplpgj_la_SOURCES = msghandler.c pljloging.c plpgj_hook.c plantable.c plpgj_core.c plpgj_message_fns.c conf/db/config_db.c callmkr/plj/plj-callmkr.c modules/febe/febe_channel_init.c modules/febe/libpq-mini-misc.c modules/febe/libpq-mini.c modules/febe/febe_channel_work.c modules/febe/libpq-mini-secure.c modules/febe/pqexpbuffer.c
AM_CFLAGS = -Iinclude -I@PGSOURCETREE@/src/include -DPG_MAJOR_VERSION=@PG_MAJOR_VERSION@ -DPG_MINOR_VERSION=@PG_MINOR_VERSION@ -DPG_RELEASE_VERSION=@PG_RELEASE_VERSION@

#this is wrong hardcoding, remove it
#SUBDIRS=conf modules callmkr

