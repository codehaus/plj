#ifndef PLJ_PLANSTORE_H
#define PLJ_PLANSTORE_H

extern void** plantable;
int	store_plantable(void*);
int	remove_plantable_entry(unsigned int);
void	init_plantable(void);

#define free_plantable_entry(a)		plantalbe[a] = NULL;

#endif

