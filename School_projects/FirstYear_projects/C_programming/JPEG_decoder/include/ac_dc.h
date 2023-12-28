#ifndef _AC_DC_H_
#define _AC_DC_H_

#include <stdlib.h>
#include <stdio.h>
#include <stdint.h>
#include <string.h>
#include <math.h>


/*Fonction qui converti un nombre décimal en un nombre hexadecimal*/
extern int dec_to_hex(int dec);

/*
Fonction qui libère l'espace mémoire associé à une séquence de bit
entrée : la séquence et la taille de cette séquence
sortie : rien mais l'espace mémoire a était libéré
*/
extern void free_bit_acdc(char** bit, int taille);

/*
Cette fonction renvoie l'indice au quel on trouve un code de huffman
entrée : sequence de bit, indice au quel on est dans la sequence, la taille de la table de huffman et le tableau de code de huffman
sortie : l'indice correspondant au premier code de huff
*/
extern uint8_t trouve_indice_huff(char** sequence, int* indice, int taille_huff, char** code_huff);


/*
Cette fonction renvoie un char* de taille n bit de la séquence entière à partir de e
entrée : séquence de bit, taille, premier élément
sortie : une chaine de caractère de bit de taille n
*/
extern char* recupere_n_bit(char** sequence, uint8_t n, int e);

/*
Cette fonction renvoie l'indice trouvé dans la table de magnitude
entrée : chaine de caractère composé de 0 et de 1 et la magnitude associé
sortie : la valeur dans la table de magnitude
*/
extern int16_t calcul_indice_magnitude(char* bit, uint8_t magnitude);

/*
Cette fonction renvoie le coefficient DC
entrée : la séquence de bit, le tableau de code de huffman dc, la taille de ce tableau, le tableau de symbole de code de huffman dc, l'indice auquel on est dans la séquence de bit
sortie : le coefficient DC 
*/
extern int16_t verif_DC(char** bit, char** code, int taille, char* symbole, int* indice_bitstream, int16_t* difference);

/*
Cette fonction renvoie le coefficient AC
entrée : la séquence de bit, le tableau de code de huffman ac, la taille de ce tableau, le tableau de symbole de code de huffman ac, l'indice auquel on est dans la séquence de bit et un pointeur vers un entier correspondnat au nombre de coeff 0
sortie : le coefficient AC et on modifie la valeur associé au pointeur pour savoir combien de coefficient 0 on a a ajouté 
*/
extern int16_t verif_AC(char** bit, char** code, int taille, char* symbole, int* indice_bitstream, int* coeff_0);

/*
Fonction libérant l'espace associé au tableau pris en paramètre
*/
extern void free_tableau(int16_t* tab);

//AFFREUX
void tableau_couleur(char** sequence, uint32_t* echantillonage, char** code_dc_Y, int taille_dc_Y, char* val_dc_Y, char** code_ac_Y, int taille_ac_Y, char* val_ac_Y, char** code_dc_Cb, int taille_dc_Cb, char* val_dc_Cb, char** code_ac_Cb, int taille_ac_Cb, char* val_ac_Cb, char** code_dc_Cr, int taille_dc_Cr, char* val_dc_Cr, char** code_ac_Cr, int taille_ac_Cr, char* val_ac_Cr, int16_t* difference_Y, int16_t* difference_Cb, int16_t* difference_Cr, int* indice_bitstream, int16_t** tab_bloc_MCU, int* indice_bloc);



/*
Cette fonction est la fonction a appelé dans le main
entrée : sequence de bit, tableau de code huff dc, taille de ce tableau, tableau de code huff ac, taille de ce tableau, tableau de val dc et tableau de val ac
sortie : un tableau de int16_t composé de 64 élément, le premier est coeff ac et les autres sont dc
*/
extern int16_t* tableau(char** sequence, char** code_dc, int taille_dc, char** code_ac, int taille_ac, char* val_dc, char* val_ac, int16_t* difference, int* indice_bitstream);

/*
Cette fonction transforme le bitstream en séquence de bit
entrée : le bitstream
sortie : séquence de bit
*/
extern char** sequence_bit(char* bitstream, int taille);

#endif /* _AC_DC_H_ */