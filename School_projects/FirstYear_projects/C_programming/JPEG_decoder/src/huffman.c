#include <stdint.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include "huffman.h"


/*Fonction qui libère l'espace occupé par les codes de huffman et les symboles*/
void free_all_huffman(struct code_Huffman* codes_de_Huffman, int nb_codes){
    for(int i=0; i<nb_codes; i++){
        free(codes_de_Huffman[i].tableau_symboles);
        for (int j = 0; j < codes_de_Huffman[i].nb_codes; j++) {
            free(codes_de_Huffman[i].tableau_codes[j]);
        }
        free(codes_de_Huffman[i].tableau_codes);
    }
    free(codes_de_Huffman);
}


/*Fonction qui libère l'espace occupé par l'arbre binaire*/
void free_tree(struct Noeud* racine) {
    if (racine == NULL) {
        return;
    }
    free_tree(racine->fils_gauche);
    free_tree(racine->fils_droit);
    
    if(racine->etat == 0 || racine->fils_gauche != NULL){
        free(racine->mot);
    }
    free(racine);
}




/*Fonction qui crée la racine de l'arbre binaire
Renvoie un struct Noeud correspondant à la racine*/
struct Noeud* creer_racine(void){
    struct Noeud* new_noeud = malloc(sizeof(struct Noeud));
    struct Noeud* f_gauche = malloc(sizeof(struct Noeud));
    struct Noeud* f_droit = malloc(sizeof(struct Noeud));

    //Création du Noeud
    new_noeud->mot = NULL;
    new_noeud->taille = 0;
    new_noeud->etat = 0;
    new_noeud->fils_droit = f_droit;
    new_noeud->fils_gauche = f_gauche;

    //Création de son fils gauche
    f_gauche->mot = malloc(2*sizeof(char));
    strcpy(f_gauche->mot, "0");
    f_gauche->taille = 1;
    f_gauche->etat = 0;
    f_gauche->fils_gauche = NULL;
    f_gauche->fils_droit = NULL;

    //Création de son fils droit
    f_droit->mot = malloc(2*sizeof(char));
    strcpy(f_droit->mot, "1");
    f_droit->taille = 1;
    f_droit->etat = 0;
    f_droit->fils_gauche = NULL;
    f_droit->fils_droit = NULL;

    return new_noeud;
}


/*Fonction qui prend un noeud en entrée et qui lui ajoute des fils*/
void ajout_fils(struct Noeud** parent, char* mot){
    struct Noeud* f_gauche = malloc(sizeof(struct Noeud));
    struct Noeud* f_droit = malloc(sizeof(struct Noeud));

    int taille_tab_mot = ((*parent)->taille)+2;
    
    //Création de son fils gauche
    char* new_mot_g = malloc((taille_tab_mot)*sizeof(char));
    f_gauche->mot = strcat(strcpy(new_mot_g, mot), "0");
    f_gauche->taille = ((*parent)->taille) + 1;
    f_gauche->etat = 0;
    f_gauche->fils_droit = NULL;
    f_gauche->fils_gauche = NULL;

    //Création de son fils droit
    char* new_mot_d = malloc(taille_tab_mot*sizeof(char));
    f_droit->mot = strcat(strcpy(new_mot_d, mot), "1");
    f_droit->taille = ((*parent)->taille) + 1;
    f_droit->etat = 0;
    f_droit->fils_droit = NULL;
    f_droit->fils_gauche = NULL;

    (*parent)->fils_gauche = f_gauche;
    (*parent)->fils_droit = f_droit;
}


/*Fonction qui parcours l'arbre jusqu'à trouver le mot de la taille correspondante*/
char* recherche_mot_len(uint8_t len_mot, struct Noeud* noeud){
    uint8_t taille = noeud->taille;
    char* mot_code = "test";
    struct Noeud* parcours_abr = noeud;
    
    while(taille != len_mot){
        if(parcours_abr->fils_droit == NULL || parcours_abr->fils_gauche == NULL){
            ajout_fils(&parcours_abr, parcours_abr->mot);
        }
        
        if(parcours_abr->fils_gauche->etat == 0){
            taille = parcours_abr->fils_gauche->taille;
            parcours_abr = parcours_abr->fils_gauche;
        }
        else if(parcours_abr->fils_droit->etat == 0){
            taille = parcours_abr->fils_droit->taille;
            if(taille == len_mot){
                /*si on passe par le fils droit, ça veut dire que l'état du fils gauche est 1
                Donc si on s'arrête sur un fils droit, le parent doit aussi être a 1 */
                parcours_abr->etat = 1;
            }
            parcours_abr = parcours_abr->fils_droit;  //si on passe par le fils gauche, ça veut dire que l'état du fils gauche est 0
        }
        else{
            parcours_abr->etat = 1;
            return NULL;
        }
    }
    parcours_abr->etat = 1;
    mot_code = parcours_abr->mot;

    return mot_code;
}


/*Fonction qui parcours le tableau de l'entête pour savoir combien de mots de longueur i il va falloir décoder*/
char** calcul_code_huff(char* tableau_entete, int taille_code){
    struct Noeud* racine = creer_racine();
    char** tableau_codes = malloc((taille_code)*sizeof(char*));

    int index_code = 0;
    for(int i=0; i<16; i++){
        if(tableau_entete[i] == 0){     //aucun mot de code de longueur i+1
            continue;
        }
        else{
            for(int j=0; j<tableau_entete[i]; j++){
                char* mot_code_test = recherche_mot_len(i+1, racine);
                if(mot_code_test == NULL){
                    j--;
                }
                else{
                    tableau_codes[index_code] = mot_code_test;
                    index_code ++;
                }
            }
        }
    }
    free_tree(racine);
    return tableau_codes;
}

