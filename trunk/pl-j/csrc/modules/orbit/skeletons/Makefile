
CFLAGS=$(shell orbit2-config --cflags)

IDLDIR=../../../../etc/corba/
#IDLFILES=callreq.idl callserver.idl result.idl sql.idl error.idl commons.idl
IDLFILES=workaround.idl
IDLS=$(addprefix $(IDLDIR), $(IDLFILES))

GEN_SRC=workaround-common \
	workaround-stubs \
	workaround-skels

SRC=$(addsuffix .c, $(GEN_SRC))
OBJECTS=$(addsuffix .o, $(GEN_SRC))

skeleton.so: $(SRC)
	$(CC) $(SRC) $(CFLAGS) --shared -o skeleton.so

skeleton.a: $(OBJECTS)
	ar $(OBJECTS) skeleton.a

all: skeleton.so skeleton.a $(OBJECTS)

.PHONY: idls

idls:
	orbit-idl-2 $(IDLS)

$(OBJECTS):idls

$(OBJETCS): $(SRC)

clean:
	rm skeleton.so workaround* -f

*.c: idls

