
include ../../Makefile.in
include Makefile.in

CFLAGS=-I../../include/ -I$(POSTGRES_SRC_HOME)/include/ -Iskeletons $(shell orbit2-config --cflags)

all: module.a

.PHONY:clean

init.o: mkskeletons

module.a: plpgj_chanell_init.o plpgj_chanell_work.o
	ar r $@ $^ 

clean:
	rm *.o *.a -f
