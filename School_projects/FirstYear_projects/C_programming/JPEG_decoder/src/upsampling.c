#include <stdlib.h>
#include <stdio.h>
#include <stdint.h>
#include "upsampling.h"


/*Fonction qui libère l'espace associé aux matrices de upsampling
Attention : les matrices de Y ne sont pas réallouées puisque ce sont les mêmes que en sortie de spatial*/
void free_upsampling(uint8_t*** matrice_upsampling, int taille_for, int nb_comp_Y, int taille){
    for(int i=0; i<taille_for; i=i+nb_comp_Y+taille){
        for(int j=0; j<8; j++){
            for(int h=0; h<taille; h++){
                free(matrice_upsampling[i+nb_comp_Y+h][j]);
            }
        }
        for(int h=0; h<taille; h++){
            free(matrice_upsampling[i+nb_comp_Y+h]);
        }
    }
}



/*Fonction qui renvoie deux matrices à partie de une seule
Utilisée pour l'échantillonnage horizontal*/
uint8_t*** upsampling_matrice_hor(uint8_t** matrice_spatial){
    uint8_t*** matrice = malloc(2*sizeof(uint8_t**));
    uint8_t** matrice_upsampling_1 = malloc(8*sizeof(uint8_t*));
    uint8_t** matrice_upsampling_2 = malloc(8*sizeof(uint8_t*));
    for(int i=0; i<8; i++){
        matrice_upsampling_1[i] = malloc(8*sizeof(uint8_t));
        matrice_upsampling_2[i] = malloc(8*sizeof(uint8_t));
    }
    for(int i=0; i<8; i++){
        uint8_t indice_1 = 0;
        uint8_t indice_2 = 0;
        for(int j=0; j<8; j++){
            if(j<4){
                matrice_upsampling_1[i][indice_1] = matrice_spatial[i][j];
                matrice_upsampling_1[i][indice_1+1] = matrice_spatial[i][j];
                indice_1 += 2;
            }
            else{
                matrice_upsampling_2[i][indice_2] = matrice_spatial[i][j];
                matrice_upsampling_2[i][indice_2+1] = matrice_spatial[i][j];
                indice_2 += 2;
            }
        }
    }
    matrice[0] = matrice_upsampling_1;
    matrice[1] = matrice_upsampling_2;

    return matrice;
}


/*Fonction qui renvoie deux matrices à partie de une seule
Utilisée pour l'échantillonnage verticale*/
uint8_t*** upsampling_matrice_vert(uint8_t** matrice_spatial){
    uint8_t*** matrice = malloc(2*sizeof(uint8_t**));
    uint8_t** matrice_upsampling_1 = malloc(8*sizeof(uint8_t*));
    uint8_t** matrice_upsampling_2 = malloc(8*sizeof(uint8_t*));
    for(int i=0; i<8; i++){
        matrice_upsampling_1[i] = malloc(8*sizeof(uint8_t));
        matrice_upsampling_2[i] = malloc(8*sizeof(uint8_t));
    }
    uint8_t indice_1 = 0;
    uint8_t indice_2 = 0;
    for(int i=0; i<8; i++){
        for(int j=0; j<8; j++){
            if(i<4){
                matrice_upsampling_1[indice_1][j] = matrice_spatial[i][j];  
                matrice_upsampling_1[indice_1+1][j] = matrice_spatial[i][j];
            }
            else{
                matrice_upsampling_2[indice_2][j] = matrice_spatial[i][j];
                matrice_upsampling_2[indice_2+1][j] = matrice_spatial[i][j];
            }
        }
        if(i<4){
            indice_1 += 2;
        }
        else{
            indice_2 += 2;
        }
    }
    matrice[0] = matrice_upsampling_1;
    matrice[1] = matrice_upsampling_2;

    return matrice;
}


/*Fonction qui renvoie quatre matrices à partie de une seule
Utilisée lorsque on échantillonne en horizontal et en vertical*/
uint8_t*** upsampling_matrice_4(uint8_t** matrice_spatial){
    uint8_t*** matrice = malloc(4*sizeof(uint8_t**));
    uint8_t** matrice_upsampling_1 = malloc(8*sizeof(uint8_t*));
    uint8_t** matrice_upsampling_2 = malloc(8*sizeof(uint8_t*));
    uint8_t** matrice_upsampling_3 = malloc(8*sizeof(uint8_t*));
    uint8_t** matrice_upsampling_4 = malloc(8*sizeof(uint8_t*));
    for(int i=0; i<8; i++){
        matrice_upsampling_1[i] = malloc(8*sizeof(uint8_t));
        matrice_upsampling_2[i] = malloc(8*sizeof(uint8_t));
        matrice_upsampling_3[i] = malloc(8*sizeof(uint8_t));
        matrice_upsampling_4[i] = malloc(8*sizeof(uint8_t));
    }
    uint8_t indice_1_i = 0;
    uint8_t indice_2_i = 0;
    uint8_t indice_3_i = 0;
    uint8_t indice_4_i = 0;
    for(int i=0; i<8; i++){
        uint8_t indice_1_if = 0;
        uint8_t indice_1_else = 0;
        uint8_t indice_2_if = 0;
        uint8_t indice_2_else = 0;
        uint8_t indice_3_if = 0;
        uint8_t indice_3_else = 0;
        uint8_t indice_4_if = 0;
        uint8_t indice_4_else = 0;
        for(int j=0; j<8; j++){
            if(i<2){
                if(j<4){
                    matrice_upsampling_1[indice_1_i][indice_1_if] = matrice_spatial[i][j];
                    matrice_upsampling_1[indice_1_i][indice_1_if+1] = matrice_spatial[i][j];
                    matrice_upsampling_1[indice_1_i+2][indice_1_if] = matrice_spatial[i][j];
                    matrice_upsampling_1[indice_1_i+2][indice_1_if+1] = matrice_spatial[i][j];
                    indice_1_if += 2;
                }
                else{
                    matrice_upsampling_1[indice_1_i+1][indice_1_else] = matrice_spatial[i][j];
                    matrice_upsampling_1[indice_1_i+1][indice_1_else+1] = matrice_spatial[i][j];
                    matrice_upsampling_1[indice_1_i+3][indice_1_else] = matrice_spatial[i][j];
                    matrice_upsampling_1[indice_1_i+3][indice_1_else+1] = matrice_spatial[i][j];
                    indice_1_else += 2;
                }
            }
            else if(i>=2 && i<4){
                if(j<4){
                    matrice_upsampling_2[indice_2_i][indice_2_if] = matrice_spatial[i][j];
                    matrice_upsampling_2[indice_2_i][indice_2_if+1] = matrice_spatial[i][j];
                    matrice_upsampling_2[indice_2_i+2][indice_2_if] = matrice_spatial[i][j];
                    matrice_upsampling_2[indice_2_i+2][indice_2_if+1] = matrice_spatial[i][j];
                    indice_2_if += 2;
                }
                else{
                    matrice_upsampling_2[indice_2_i+1][indice_2_else] = matrice_spatial[i][j];
                    matrice_upsampling_2[indice_2_i+1][indice_2_else+1] = matrice_spatial[i][j];
                    matrice_upsampling_2[indice_2_i+3][indice_2_else] = matrice_spatial[i][j];
                    matrice_upsampling_2[indice_2_i+3][indice_2_else+1] = matrice_spatial[i][j];
                    indice_2_else += 2;
                }
            }
            else if(i>=4 && i<6){
                if(j<4){
                    matrice_upsampling_3[indice_3_i][indice_3_if] = matrice_spatial[i][j];
                    matrice_upsampling_3[indice_3_i][indice_3_if+1] = matrice_spatial[i][j];
                    matrice_upsampling_3[indice_3_i+2][indice_3_if] = matrice_spatial[i][j];
                    matrice_upsampling_3[indice_3_i+2][indice_3_if+1] = matrice_spatial[i][j];
                    indice_3_if += 2;
                }
                else{
                    matrice_upsampling_3[indice_3_i+1][indice_3_else] = matrice_spatial[i][j];
                    matrice_upsampling_3[indice_3_i+1][indice_3_else+1] = matrice_spatial[i][j];
                    matrice_upsampling_3[indice_3_i+3][indice_3_else] = matrice_spatial[i][j];
                    matrice_upsampling_3[indice_3_i+3][indice_3_else+1] = matrice_spatial[i][j];
                    indice_3_else += 2;
                }
            }
            else{
                if(j<4){
                    matrice_upsampling_4[indice_4_i][indice_4_if] = matrice_spatial[i][j];
                    matrice_upsampling_4[indice_4_i][indice_4_if+1] = matrice_spatial[i][j];
                    matrice_upsampling_4[indice_4_i+2][indice_4_if] = matrice_spatial[i][j];
                    matrice_upsampling_4[indice_4_i+2][indice_4_if+1] = matrice_spatial[i][j];
                    indice_4_if += 2;
                }
                else{
                    matrice_upsampling_4[indice_4_i+1][indice_4_else] = matrice_spatial[i][j];
                    matrice_upsampling_4[indice_4_i+1][indice_4_else+1] = matrice_spatial[i][j];
                    matrice_upsampling_4[indice_4_i+3][indice_4_else] = matrice_spatial[i][j];
                    matrice_upsampling_4[indice_4_i+3][indice_4_else+1] = matrice_spatial[i][j];
                    indice_4_else += 2;
                }
            }
        }
        if(i<2){
            indice_1_i += 4;
        }
        else if(i>=2 && i<4){
            indice_2_i += 4;
        }
        else if(i>=4 && i<6){
            indice_3_i += 4;
        }
        else{
            indice_4_i += 4;
        }
    }
    matrice[0] = matrice_upsampling_1;
    matrice[1] = matrice_upsampling_2;
    matrice[2] = matrice_upsampling_3;
    matrice[3] = matrice_upsampling_4;

    return matrice;
}
