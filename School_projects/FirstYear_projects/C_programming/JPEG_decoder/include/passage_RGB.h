#ifndef _PASSAGE_RGB_H_
#define _PASSAGE_RGB_H_


#include <stdlib.h>
#include <stdio.h>
#include <stdint.h>

/*
entrée : une chaîne de caractère finissant par jpg ou jpeg
sortie : la taille de la chaîne sans l'extention
*/
extern int taille_chaine(char* str);

/*
entrée : la taille du nom sans extention, le nom et le nb de comp qui sert à savoir si on est en noir et blanc
sortie : une chaine de caractère avec une extention ppm ou pgm
*/
extern char* modif_nom(int taille, char* nom, int nb_comp);


/*
Cette fonction fait la trasformation en PPM et crée le fichier PPM pour une image en noir et blanc
*/
extern void transfo_nb_MCU_1_tronc(int n_pixels_l, int n_pixels_h, int n_largeur, uint8_t*** matrice, char* nom_image);

/*
Cette fonction fait la trasformation en PPM et crée le fichier PPM pour une image en couleur
*/
extern void transfo_couleur_MCU_2(int h1, int v1, int n_pixels_l, int n_pixels_h, int n_largeur, uint8_t*** matrice, char* nom_image);

/*
On réécrit les MCUs de manière à regrouper les groupes ensembles
ex : Y_0, Y_1, Cb_0, Cb_1, Cr_0, Cr_1 --> Y_0, Cb_0, Cr_0, Y_1, Cb_1, Cr_1
*/
extern uint8_t*** tri_matice(uint8_t*** matrice, uint32_t echantillonage_largeur, uint32_t echantillonage_hauteur, int nb_comp, int nb_mcu_largeur);


#endif /* _PASSAGE_RGB_H_ */