
CFLAGS=$(shell orbit2-config --cflags)

IDLDIR=../../../../../../etc/corba/
#IDLFILES=callreq.idl callserver.idl result.idl sql.idl error.idl commons.idl
IDLFILES=workaround.idl
IDLS=$(addprefix $(IDLDIR), $(IDLFILES))

GEN_SRC=workaround-common \
	workaround-skels

SRC=$(addsuffix .c, $(GEN_SRC))
OBJECTS=$(addsuffix .o, $(GEN_SRC))

skeleton.so: $(SRC)
	$(CC) $(SRC) $(CFLAGS) --shared -o skeleton.so

all: skeleton.so

idls:
	orbit-idl-2 --nostubs $(IDLS)

$(OBJECTS):idls

$(OBJETCS): $(SRC)

clean:
	rm skeleton.so workaround* -f

