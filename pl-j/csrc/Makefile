
include Makefile.in

INCDIR=include $(POSTGRES_SRC_HOME)/include

CFLAGS=$(addprefix -I, $(INCDIR)) -Wall
LIBS=

all: plpgj.so

modules/$(COMM_MODULE)/module.a:
	make -C modules/$(COMM_MODULE)

config/$(CONF_MODULE)/conf.a:
	make -C config/$(CONF_MODULE)

plpgj.so: plpgj_message_fns.o plpgj_hook.o plpgj_create_call.o plpgj_core.o modules/$(COMM_MODULE)/module.a config/$(CONF_MODULE)/conf.a
	cc -shared $^ -o plpgj.so $(LIBS) -Wall

clean:
	rm plpgj.so *.o -rf
	make -C modules/$(COMM_MODULE) clean
	make -C config/$(CONF_MODULE) clean

