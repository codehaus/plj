
include Makefile.in

INCDIR=include $(POSTGRES_SRC_HOME)/include

CFLAGS=$(addprefix -I, $(INCDIR)) -Wall
LIBS=$(shell orbit2-config --libs)

all: plpgj.so

modules/$(COMM_MODULE)/module.a:
	make -C modules/$(COMM_MODULE)

plpgj.so: plpgj_message_fns.o plpgj_hook.o plpgj_create_call.o plpgj_core.o modules/$(COMM_MODULE)/module.a
	cc -shared $^ -o plpgj.so $(LIBS) -Wall

clean:
	rm plpgj.so *.o -rf
	make -C modules/$(COMM_MODULE) clean

