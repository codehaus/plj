#include "executor/spi.h"
#include "pljelog.h"

#include "plantable.h"

void** plantable;
int plantable_size = -1;

#define DEFAULT_PLANTABLE_SIZE			8

int
store_plantable(void* plan) {
	int i;
	void** new_plantable = NULL;
	if(plantable_size == -1)
		init_plantable();
	for(i = 0; i < plantable_size; i++) {
		if(plantable[i] == NULL) {
			plantable[i] = plan;
			return i;
		}
	}

	new_plantable = SPI_palloc(plantable_size + 1);
	for(i = 0; i <  plantable_size; i++){
		new_plantable[i] = plantable[i];
	}
	
	plantable_size += 10;
	plantable[i] = plan;
	return i;
}

void
init_plantable(void) {
	int i;
	plantable = SPI_palloc(DEFAULT_PLANTABLE_SIZE * sizeof(Oid));
	for(i = 0; i< DEFAULT_PLANTABLE_SIZE; i++) {
		plantable[i] = NULL;
	}
	plantable_size = DEFAULT_PLANTABLE_SIZE;
}

