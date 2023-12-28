#ifndef _LECTURE_ENTETE_H_
#define _LECTURE_ENTETE_H_

#include <stdlib.h>
#include <stdio.h>
#include <stdint.h>

//Structure du SOF0
struct Start_Of_Frame{
    uint8_t* tab_Ic;
    uint8_t* tab_echant_hor;
    uint8_t* tab_echant_vert;
    uint8_t* tab_Iq;
};

//Structure de DQT
struct data_DQT{
    uint8_t precision;
    uint8_t indice_Iq;
    char* liste_DQT;
};

//Structure d'une table de huffman
struct Huffman
{
    uint8_t type;
    uint8_t indice;
    char* tableau;
};

//Structure du SOS
struct Start_Of_Scan{
    uint8_t* tab_Ic;
    uint8_t* tab_Ihdc;
    uint8_t* tab_Ihac;
};

//Structure du bitstream
struct BitStream{
    uint32_t size;
    char* list_bitstream;
};


/*Toutes les fonctions qui servent à libérer l'espace alloué par malloc*/
extern void free_all_DQT(struct data_DQT* liste, int taille);

extern void free_all_SOS(struct Start_Of_Scan N_SOS);

extern void free_all_DHT(struct Huffman* liste, int taille);

extern void free_all_SOF0(struct Start_Of_Frame liste);

extern void free_lecture_cara(char* liste_car);


/*
Fonction qui lit des caractères du fichier
entrée : le fichier à lire ET le nombre de caractères à lire
sortie : la chaine de caractère lue
*/
extern char* lecture_cara(FILE* fichier, uint32_t taille);


/*
Fonction qui vérifie que les premiers caractères du fichier
correspondent au Start Of Image
entrée : le fichier à lire
sortie : 1 si on a les bons caractères
*/
extern int verif_SOI(FILE* fichier);

/*
Fonction qui vérifie et lit les informations de APPO
entrée : le fichier à lire
sortie : la chaine de caractère lue
*/
extern char* verif_APPO(FILE* fichier);

/*Fonction qui vérifie et lit  les infos de COM
entrée  : le fichier à lire
sortie : 1 si on a les bons caractères
*/
extern int verif_COM(FILE* fichier);

/*Fonction qui teste si un indice Iq existe déjà
Entrée : tableau des indice Iq déjà utilisés
Sortie : -renvoie 255 si l'indice n'est pas encore utilisé
         -sinon, renvoie à quel tableau correspond cet indice
*/
uint8_t verif_indice_DQT(uint8_t indice_DQT_tab[], uint8_t indice_DQT);

/*Fonction qui vérifie et lit les informations de DQT
entrée : le fichier à lire
sortie : la chaine de caractère lue, la précision et l'indice IQ
*/
extern struct data_DQT* verif_DQT(FILE* fichier, uint8_t* nb_tab_DQT);

/*Fonction qui vérifie et lit les informations de SOF0
entrée : le fichier à lire, un pointeur vers la précision / largeur / hauteur / nb_comp
sortie : struct SOF0
*/
extern struct Start_Of_Frame verif_SOF0(FILE* fichier, uint8_t* precision, uint32_t* largeur, uint32_t* hauteur, uint32_t* nb_comp);

/*Fonction vérifiant que on a bien une table DHT
entrée : le fichier à lire et traiter
sortie : un tableau de structure huffman de taille 8
*/
extern struct Huffman* verif_DHT(FILE* fichier, int* taille_dht);


/*Fonction qui vérifie et lit  les infos de SOS
entrée  : le fichier à lire
sortie : ... jsp encore
*/
extern struct Start_Of_Scan verif_SOS(FILE* fichier, uint8_t* Ss_SOS, uint8_t* Se_SOS, uint8_t* Ah_SOS, uint8_t* Al_SOS);

/*Fonction qui récupère le bitstream : le codage brut de l'image, qui sera ensuité décodé pour obtenir une image.ppm*/
extern struct BitStream recupere_bitstream(FILE* fichier);



#endif