
OMNIORB_HOME=/home/bitfakk/binary/omniorb/
IDL_DIR=../../../../etc/corba/

CXXFLAGS=-I$(OMNIORB_HOME)/include -I.

%SK.cc %.hh: $(IDL_DIR)/%.idl
	omniidl -bcxx -I$(IDL_DIR) $<

all: callserverSK.o commonsSK.o resultSK.o errorSK.o callreqSK.o sqlSK.o resultSK.o

clean:
	rm *o *cc *hh -f

