#ifndef PLJ_PLANSTORE_H
#define PLJ_PLANSTORE_H

extern void** plantable;
int	store_plantable(void*);
void	init_plantable(void);

#define free_plantable_entry(a)		plantalbe[a] = NULL;

#endif

