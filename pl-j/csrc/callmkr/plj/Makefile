
include ../../Makefile.in

CFLAGS=-I../../include/ -I$(POSTGRES_SRC_HOME)/include/

all: callmkr.a

callmkr.a: plj-callmkr.o
	ar r $@ $^

clean:
	rm *.o *.a -f
