
include ../../Makefile.in

CFLAGS=-I../../include/ -I$(POSTGRES_SRC_HOME)/include/ -Wall -g -DPOSTGRES_VERSION=$(POSTGRES_VERSION)

all: callmkr.a

callmkr.a: plj-callmkr.o
	ar r $@ $^

clean:
	rm -f *.o *.a
