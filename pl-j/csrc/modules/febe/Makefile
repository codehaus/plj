
include ../../Makefile.in
include Makefile.in

CFLAGS=-I../../include/ -I$(POSTGRES_SRC_HOME)/include/ 

all: module.a

.PHONY:clean

init.o: mkskeletons

module.a: febe_channel_init.o febe_channel_work.o libpq-mini.o
	ar r $@ $^ 

clean:
	rm *.o *.a -f
