#ifndef _HUFFMAN_H_
#define _HUFFMAN_H_

#include <stdint.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>

/*Structure de donnée pour chaque noeud de l'arbre binaire*/
struct Noeud{
    char* mot;
    uint8_t taille;
    uint8_t etat;  //0 quand on peut encore parcourir cette branche (pas de préfixe), 1 sinon
    struct Noeud* fils_gauche;
    struct Noeud* fils_droit;
};

//Structure qui contient les codes de Huffman des symboles
struct code_Huffman{
    
    uint8_t nb_codes;               //nombre de symboles
    char* tableau_symboles;      //tableau des symboles
    char** tableau_codes;           //tableau des codes correspondants
};

/*Fonction qui crée la racine de l'arbre binaire
Entrée : rien
Sortie : un Noeud contenant seulement deux fils, ce noeud est la racine de l'arbre
*/
extern struct Noeud* creer_racine(void);


/*Fonction qui prend un noeud en entrée et qui lui ajoute des fils
Entrée : le parent à qui il faut rajouter des fils
       | le mot de ce parent, qui sera la base du mot de ses fils
Sortie : on modifie la valeur pointée par le parent, donc on ne renvoie rien
*/
extern void ajout_fils(struct Noeud** parent, char* mot);


/*Fonction qui parcours l'arbre jusqu'à trouver le mot de la taille correspondante
Entrée : la longueur du mot pour lequel on veut trouver un code
       | le Noeud à partir duquel on cherche
Sortie : le mot correspondant, de longueur "len_mot"
*/
extern char* recherche_mot_len(uint8_t len_mot, struct Noeud* noeud);


/*Fonction qui parcours le tableau de l'entête pour savoir combien de mots de longueur i il va falloir décoder
Entrée : le tableau de huffman donné par l'entête
       | le nombre de symboles à décoder
Sortie : un tableau contenant tous les mots de codes dans l'ordre
*/
extern char** calcul_code_huff(char* tableau_entete, int taille_code);


/*Fonction qui libère l'espace occupé par les codes de huffman et les symboles*/
extern void free_all_huffman(struct code_Huffman* codes_de_Huffman, int nb_codes);


/*Fonction qui libère l'espace occupé par l'arbre binaire*/
extern void free_tree(struct Noeud* racine);

#endif