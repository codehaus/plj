
include ../../Makefile.in

CFLAGS=-I../../include/ -I$(POSTGRES_SRC_HOME)/include/ -Wall -g

all: conf.a

conf.a: config_db.o
	ar r $@ $^

clean:
	rm -f *.o *.a
