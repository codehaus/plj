
include ../../Makefile.in
include Makefile.in

CFLAGS=-I../../include/ -I$(POSTGRES_SRC_HOME)/include/ -Iskeletons $(shell orbit2-config --cflags)

all: module.a

#.PHONY: skeletons

mkskeletons:
	make -C skeletons

.PHONY:mkskeletons

init.o: mkskeletons

module.a: plpgj_chanell_init.o plpgj_chanell_work.o
	ar r $@ $^ skeletons/*.o

clean:
	rm *.o *.a -f
	make -C skeletons clean
