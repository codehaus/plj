
include ../../Makefile.in
include Makefile.in

CXXFLAGS=-I../../include/ -I$(POSTGRES_SRC_HOME)/include/  -I/home/bitfakk/binary/omniorb/include -Iskeletons

all: module.a

#.PHONY: skeletons

mkskeletons:
	make -C skeletons

.PHONY:mkskeletons

init.o: mkskeletons

module.a: init.o send.o receive.o data.o
	ar r $@ $^ skeletons/*.o

clean:
	rm *.o *.a -f
	make -C skeletons clean
