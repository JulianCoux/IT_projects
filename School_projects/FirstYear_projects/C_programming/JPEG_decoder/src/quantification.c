#include <stdlib.h>
#include <stdio.h>
#include <stdint.h>
#include "quantification.h"

/*
Fonction renvoyant la liste des 64 élément en appliquant la quantification inverse
entrée : liste des 64 éléments avant modif et la table de quantification
sortie : la liste des 64 éléments après modif
*/
void tab_quantifie(int16_t* tab_avant_quantification, int8_t* tab_quantification){
    for (int32_t i = 0; i < 64; i++){
        tab_avant_quantification[i] = tab_avant_quantification[i] * tab_quantification[i];
    }
}


