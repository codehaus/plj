
include Makefile.in

CFLAGS= -I $(JAVA_HOME)/include -I$(JAVA_HOME)/include/linux/ -I $(JAVA_HOME)/../include/linux -Icorba $(shell orbit2-config --cflags) -Wall -g3 -pg 
LIBS=-L $(JAVA_HOME)/jre/lib/i386/ -ljava $(shell orbit2-config --libs)

all: CORBAChanellWorker.so

$(OBJECTS): gensrc

.PHONY: gensrc

corba/skeleton.so: gensrc

CORBAChanellWorker.so: CORBAChanellWorker.o callserverImpl.o create_msg.o create_any.o corba/skeleton.so
	cc -shared  -o $@ $^ $(LIBS)

clean:
	rm *so *o -f

distclean: clean
	rm *skelimpl.c *skels.c *common.c *stubs.c callserver.h commons.h sql.h result.h callreq.h -f

gensrc:
	make -C corba
