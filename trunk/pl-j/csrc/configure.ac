# ----------
# configure.ac
#
#	Autoconf configuration file for PL-J 
#	(PostgreSQL call handler module)
#
#	It was rewritten from the Slony
#
#	Copyright (c) 2003-2004, PostgreSQL Global Development Group
#	Author: Jan Wieck, Afilias USA INC.
#
# $Id: configure.ac,v 1.18 2004/03/31 13:46:09 wieck Exp $
#
# Process this file with autoconf to produce a configure script.
# ----------

AC_INIT(pl-pgj, 20040330)
AC_CONFIG_HEADERS(config.h)
AC_CONFIG_AUX_DIR(config)
AC_CONFIG_SRCDIR([src])

AC_CANONICAL_HOST

template=
AC_MSG_CHECKING([which template to use])

case $host_os in
     aix*) template=aix ;;
    beos*) template=beos ;;
    bsdi*) template=bsdi ;;
  cygwin*) template=win ;;
  darwin*) template=darwin ;;
    dgux*) template=dgux ;;
 freebsd*) template=freebsd ;;
    hpux*) template=hpux ;;
    irix*) template=irix5 ;;
   linux*) template=linux ;;
  netbsd*) template=netbsd ;;
nextstep*) template=nextstep ;;
 openbsd*) template=openbsd ;;
     osf*) template=osf ;;
     qnx*) template=qnx4 ;;
     sco*) template=sco ;;
 solaris*) template=solaris ;;
   sunos*) template=sunos4 ;;
 sysv4.2*)
        case $host_vendor in
          univel) template=univel ;;
        esac ;;
   sysv4*) template=svr4 ;;
   sysv5*) template=unixware ;;
  ultrix*) template=ultrix4 ;;
esac

AC_MSG_RESULT([$template])
AC_SUBST(PORTNAME, $template)

# Checks for programs.
AC_PROG_CC
AC_PROG_LD
AC_SUBST(LD)
AC_SUBST(with_gnu_ld)
ACX_PTHREAD()

# Locate the PostgreSQL source tree

AC_ARG_WITH(pgsourcetree, [  --with-pgsourcetree=<dir> Location of PostgreSQL source tree], AC_SUBST(PGSOURCETREE, $with_pgsourcetree))
AC_ARG_WITH(pgversion, [  --with-pgversion=<version> Version number of the PG source (74 or 80)], AC_SUBST(POSTGRES_VERSION, $with_pgversion))

AC_ARG_WITH(confmod, [  --with-confmod=<module> configuration module], AC_SUBST(CONFMOD, $with_confmod))
AC_ARG_WITH(channel, [  --with-channel=<module> channel module], AC_SUBST(CHANMOD, $with_channel))
AC_ARG_WITH(callmaker, [  --with-callmaker=<module> call maker module], AC_SUBST(CALLMKRMOD, $with_callmaker))

if test "x$with_pgsourcetree" = "x" ; then
	AC_MSG_ERROR([
    ********************
    * You need PostgreSQL source installed to build.
    * See howto at http://docs.codehaus.org/display/PLJ/Build+and+install+howto
    ********************
	])
fi

if test "x$with_confmod" = "x" ; then
	AC_MSG_WARN([
    using db conf module
	])
    with_confmod=db
    CONFMOD=db
fi

if test "x$with_channel" = "x" ; then
	AC_MSG_WARN([
    using febe channel module
	])
    with_chanmod=febe
    CHANMOD=febe
fi

if test "x$with_callmaker" = "x" ; then
	AC_MSG_WARN([
    using plj callmaker
	])
    with_callmaker=plj
    CALLMKRMOD=plj
fi

if test "x$with_pgversion" = "x" ; then
	AC_MSG_WARN([
    assuming version 7.4
	])
    with_pgversion=74
    POSTGRES_VERSION=74
fi



# ----
# Check if our libpq contains several functions
# ----
save_CFLAGS="$CFLAGS"
save_LIBS="$LIBS"
# CFLAGS="-I${with_pgsourcetree}/src/interfaces/libpq -I${with_pgsourcetree}/src/include $CFLAGS"
# LIBS="-L${with_pgsourcetree}/src/interfaces/libpq -lpq $LIBS"

# PQfreemem()
#AC_MSG_CHECKING([for PQfreemem() in libpq])
#have_pqfreemem=no
#AC_TRY_LINK([#include <libpq-fe.h>],
#	   [PQfreemem(NULL);],
#	   [have_pqfreemem=yes])
#AC_MSG_RESULT($have_pqfreemem)
#if test $have_pqfreemem = yes ; then
#	AC_DEFINE(HAVE_PQFREEMEM,1,)
#fi

# PQputCopyData()
AC_MSG_CHECKING([for PQputCopyData() in libpq])
have_pqputcopydata=no
AC_TRY_LINK([#include <libpq-fe.h>],
	   [PQputCopyData(NULL,NULL,0);],
	   [have_pqputcopydata=yes])
AC_MSG_RESULT($have_pqputcopydata)
if test $have_pqputcopydata = yes ; then
	AC_DEFINE(HAVE_PQPUTCOPYDATA,1,)
fi

# PQsetNoticeReceiver()
AC_MSG_CHECKING([for PQsetNoticeReceiver() in libpq])
have_pqsetnoticereceiver=no
AC_TRY_LINK([#include <libpq-fe.h>],
	   [PQsetNoticeReceiver(NULL,NULL,NULL);],
	   [have_pqsetnoticereceiver=yes])
AC_MSG_RESULT($have_pqsetnoticereceiver)
if test $have_pqsetnoticereceiver = yes ; then
	AC_DEFINE(HAVE_PQSETNOTICERECEIVER,1,)
fi

AC_MSG_CHECKING([for typcache.h])
if test -f "${with_pgsourcetree}/src/include/utils/typcache.h"; then
	have_typcache=yes
	AC_DEFINE(HAVE_TYPCACHE,1,)
else
	have_typcache=no
fi
AC_MSG_RESULT($have_typcache)

CFLAGS="$save_CFLAGS"
LIBS="$save_LIBS"

AC_CONFIG_FILES([
	Makefile.in 
])

AC_OUTPUT()

dnl AC_OUTPUT([
dnl    postgresql-slony1-engine.spec
dnl ])
