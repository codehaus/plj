#include "plantable.h"
#include "pljelog.h"

void** plantable;
int plantable_size = -1;

#define DEFAULT_PLANTABLE_SIZE			8

int
store_plantable(void* plan) {
	int i;
	if(plantable_size == -1)
		init_plantable();
	for(i = 0; i < plantable_size; i++) {
		if(plantable[i] == NULL) {
			plantable[i] = plan;
			return i;
		}
	}
	plantable_size++;
	plantable = SPI_repalloc(plantable, plantable_size);
	plantable[i-1] = plan;
	return plantable_size -1;
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

