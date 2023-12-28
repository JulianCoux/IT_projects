#ifndef _SPATIAL_H_
#define _SPATIAL_H_

#include <stdlib.h>
#include <stdio.h>
#include <stdint.h>
#include <math.h>

/*
Fonction renvoyant 1/racine(2) si epsilon = 0 sinon 1
entrée : un entier
sortie : un float
*/
extern float c(int epsilon);

/*
Fonction renvoyant l'élément a ajouté dans la nouvelle matrice
entrée : position x, position y, matrice fréquentielle
sortie : la valeur du calcul en float
*/
extern double s(int x, int y, int16_t** matrice);

/*
Fonction libérant l'espace mémoire associé à la matrice prise en paramètre
*/
extern void free_spatial(uint8_t** matrice);

/*
Cette fonction renvoie une nouvelle matrice dans le domaine spatial
entrée : la matrice fréquentielle et la matrice spatiale
sortie : rien mais la matrice spatiale est modifié
Faire attention à bien déclarer nouvelle dans le main
*/
extern uint8_t** spatial(int16_t** matrice);

/*
Fonction renvoyant (O0 + O1) / 2
*/
extern double papillon0(double O0, double O1);

/*
Fonction renvoyant (O0 - O1) / 2

*/
extern double papillon1(double O0, double O1);

/*
Fonction renvoyant le résultat après application de la formule de rotation pour I0
*/
extern double rota0(double O0, double O1, float k, int n);

/*
Fonction renvoyant le résultat après application de la formule de rotation pour I1
*/
extern double rota1(double O0, double O1, float k, int n);

/*
Fonction appliquant la formule pour l'idct rapide en 1D
entrée : un tableau d'entier
sortie : un tableau de double avec appliquation de la formule
*/
extern double* idct(int16_t* tableau);

/*
Fonction appliquant la formule pour l'idct rapide en 1D
entrée : un tableau de double
sortie : un tableau de double avec appliquation de la formule
*/
extern double* idct_colonne(double* tableau);

/*
Fonction appliquant l'idct rapide en 2D
entrée : une matrice 8x8 d'entiers signés sur 16 bits
sortie : une matrice 8x8 d'entiers non signés sur 8 bits après appliquation de idct rapide
*/
extern uint8_t** spatial_rapide(int16_t** matrice);



#endif /* _SPATIAL_H_ */
