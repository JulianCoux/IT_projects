#ifndef _ZIGZAG_H_
#define _ZIGZAG_H_

#include <stdlib.h>
#include <stdio.h>
#include <stdint.h>

/*
ecriture de coefficient diagonaux dans le sens descente
entrée : La matrice à initialiser, le tableau dont on va se servir pour initialiser, l'indice du tableau où on est, les indices x et y du dernier éléments initialisé dans la matrice et le nombre d'élément a initialisé 
sortie : rien mais la matrice est modifié
*/
extern void diag_montante(int16_t** matrice, int16_t* tab, int indice_tab, int indice_x_mat, int indice_y_mat, int nb);

/*
ecriture de coefficient diagonaux dans le sens descente
entrée : La matrice à initialiser, le tableau dont on va se servir pour initialiser, l'indice du tableau où on est, les indices x et y du dernier éléments initialisé dans la matrice et le nombre d'élément a initialisé 
sortie : rien mais la matrice est modifié
*/
extern void diag_descendante(int16_t** matrice, int16_t* tab, int indice_tab, int indice_x_mat, int indice_y_mat, int nb);

/*
Fonction libérant l'espace mémoire associé à une matrice
entrée : la matrice
sortie : rien mais l'espace mémoire est libéré
*/
extern void free_zigzag(int16_t** matrice);

/*
Fonction renvoyant une matrice en lui attribuant les bonnes valeures
entrée : le tableau de valeur
sortie : une matrice d'entier signé sur 16 bits écrit en zigzag par rapport au tableau
*/
extern int16_t** zigzag (int16_t* tab);

#endif /* _SPATIAL_H_ */
