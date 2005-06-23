
include Makefile.in

INCDIR=include $(POSTGRES_SRC_HOME)/include

CFLAGS=$(addprefix -I, $(INCDIR)) -Wall -DPOSTGRES_VERSION=$(POSTGRES_VERSION) -g

LIBS=

all: plpgj.so

modules/$(COMM_MODULE)/module.a:
	make -C modules/$(COMM_MODULE)

conf/$(CONF_MODULE)/conf.a:
	make -C conf/$(CONF_MODULE)

callmkr/$(CALLMKR_MODULE)/callmkr.a:
	make -C callmkr/$(CALLMKR_MODULE)

plpgj.so: msghandler.o plpgj_message_fns.o plantable.o pljloging.o plpgj_hook.o plpgj_core.o modules/$(COMM_MODULE)/module.a conf/$(CONF_MODULE)/conf.a callmkr/$(CALLMKR_MODULE)/callmkr.a
ifeq ($(PORTNAME), linux)
	cc -shared $^ -o plpgj.so $(LIBS) -Wall
endif
ifeq ($(PORTNAME), darwin)
	gcc -dynamiclib $^ -flat_namespace -undefined suppress -o plpgj.dynlib
endif
#	strip plpgj.so

clean:
	rm -f plpgj.so *.o
	make -C modules/$(COMM_MODULE) clean
	make -C conf/$(CONF_MODULE) clean
	make -C callmkr/$(CALLMKR_MODULE) clean

