
include ../../Makefile.in
include Makefile.in

CFLAGS=-I../../include/ -I$(POSTGRES_SRC_HOME)/include/  -Wall -g

all: module.a

.PHONY:clean

init.o: mkskeletons

module.a: febe_channel_init.o febe_channel_work.o libpq-mini.o libpq-mini-misc.o libpq-mini-secure.o pqexpbuffer.o
	ar r $@ $^ 

clean:
	rm *.o *.a -f
