#ifndef _QUANTIFICATION_H_
#define _QUANTIFICATION_H_

#include <stdlib.h>
#include <stdio.h>
#include <stdint.h>

/*
Fonction modifiant la matrice pour la quantification inverse
entrée : matrice et tableau de quantif
sortie : aucune mais la matrice est modifié
*/
extern void tab_quantifie(int16_t* tab_avant_quantification, int8_t* tab_quantification);

#endif /* _QUANTIFICATION_H_ */
