#include <stdlib.h>
#include <stdio.h>
#include <stdint.h>
#include "zigzag.h"


// Modif la matrice en diagonale montante en prenant les indices du dernier trucs ajouter de tab et le nb de trucs à modif
void diag_montante(int16_t** matrice, int16_t* tab, int indice_tab, int indice_x_mat, int indice_y_mat, int nb){
    for (int i = 1; i < nb + 1; i++){
        matrice[indice_x_mat - i][indice_y_mat + i] = tab[indice_tab + i];
    }
}

// Modif la matrice en diagonale descendante en prenant les indices du dernier trucs ajouter de tab et le nb de trucs à modif
void diag_descendante(int16_t** matrice, int16_t* tab, int indice_tab, int indice_x_mat, int indice_y_mat, int nb){
    for (int i = 1; i < nb + 1; i++){
        matrice[indice_x_mat + i][indice_y_mat - i] = tab[indice_tab + i];
    }
}

void free_zigzag(int16_t** matrice) {
    for (int i = 0; i < 8; i++) {
        free(matrice[i]);
    }
    free(matrice);
}

int16_t** zigzag(int16_t* tab){
    int16_t** matrice = malloc(8 * sizeof(int16_t *)); 
    for(int i = 0; i < 8; i++){ 
        matrice[i] = malloc(8 * sizeof(int16_t));
    }
    //On initialise les 4 premiers termes à la main
    matrice[0][0] = tab[0];
    matrice[0][1] = tab[1];
    matrice[1][0] = tab[2];
    matrice[2][0] = tab[3];
    int x = 2;
    int y = 0;
    int j = 3;
    int cpt = 3;
    //La boucle ajoute de 4 a 28 les trucs
    for (int i = 3; i < 8; i ++){
        if (x == 0){
            diag_descendante(matrice, tab, j, x, y, i-1);
            x = i;
            y = 0;
            j += cpt;
            matrice[x][y] = tab[j];
        } else{
            diag_montante(matrice, tab, j, x, y, i - 1);
            x = 0;
            y = i;
            j += cpt;
            matrice[x][y] = tab[j];
        }
        cpt += 1;
    }
    /*
    Ici normalement:
    x vaut 0
    y vaut 7
    j vaut 28
    cpt vaut 8
    */
    //La boucle ajoute de 29 à 61
    for (int i = 1; i < 7; i++){
        if (x == 7){
            diag_montante(matrice, tab, j, x, y, cpt - 1);
            x = i;
            y = 7;
            j += cpt;
            matrice[x][y] = tab[j];
        } else{
            diag_descendante(matrice, tab, j, x, y, cpt - 1);
            x = 7;
            y = i;
            j += cpt;
            matrice[x][y] = tab[j];
        }
        cpt -= 1;
    }
    //On initialise les deux derniers termes à la main
    matrice[7][6] = tab[62];
    matrice[7][7] = tab[63];

    return matrice;
}
