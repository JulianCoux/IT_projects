#ifndef _UPSAMPLING_H_
#define _UPSAMPLING_H_

#include <stdlib.h>
#include <stdio.h>
#include <stdint.h>

/*Fonction qui libère l'espace associé aux matrices de upsampling
Attention : les matrices de Y ne sont pas réallouées puisque ce sont les mêmes que en sortie de spatial
*/
extern void free_upsampling(uint8_t*** matrice_upsampling, int taille_for, int nb_comp_Y, int taille);


/*Fonction qui renvoie deux matrices à partie de une seule
on réécrit les matrices Cb et Cr pour en faire 2 de chaque, chaque ligne est de taille 8
le but est de doubles les coefficients de chaque ligne
les 8 premiers pour Cb1 et les 8 suivants pour Cb2
Entrée : matrice à doubler pour Cb ou Cr
Sortie : une tableau de deux matrices (soit Cb1 et Cb2, soit Cr1 et Cb2)
(Pour l'échantillonnage horizontal)*/
extern uint8_t*** upsampling_matrice_hor(uint8_t** matrice_spatial);
    

/*Fonction qui renvoie deux matrices à partie de une seule
Entrée : matrice à doubler pour Cb ou Cr
Sortie : une tableau de deux matrices (soit Cb1 et Cb2, soit Cr1 et Cb2)
(Pour l'échantillonnage vertical)*/
extern uint8_t*** upsampling_matrice_vert(uint8_t** matrice_spatial);


/*Fonction qui renvoie quatre matrices à partie de une seule
Entrée : matrice à quadrupler pour Cb ou Cr
Sortie : une tableau de 4 matrices (soit Cb1, Cb2, Cb3 et Cb4, soit Cr1 et Cb2)
(Pour l'échantillonne horizontal et vertical)*/
extern uint8_t*** upsampling_matrice_4(uint8_t** matrice_spatial);


#endif /* _UPSAMPLING_H_ */
