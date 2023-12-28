#include <stdlib.h>
#include <stdio.h>
#include <stdint.h>
#include <math.h>
#include "spatial.h"

#define PI 3.14159265358979323846



float c(int epsilon){
    if (epsilon == 0){
        return 1/(sqrt(2));
    } else {
        return 1;
    }
}



double s(int x, int y, int16_t** matrice){
    double res = 0;
    for (int lambda = 0; lambda < 8; lambda++){
        for (int mu = 0; mu < 8; mu++){
            res += c(lambda)*c(mu)*cos(((2*x + 1)*lambda*PI)/(16))*cos(((2*y + 1)*mu*PI)/(16))*matrice[lambda][mu];
        }
    }
    res = 0.25*res;
    res=round(res);
    return res;
}


void free_spatial(uint8_t** matrice){
    for(int i = 0; i < 8; i++){ 
        free(matrice[i]);
    }
    free(matrice);
}


uint8_t** spatial(int16_t** matrice)
{
    //On parcourt toute la matrice et on appelle la formule de transformation sur chaque element
    uint8_t** nouvelle_matrice = malloc(8 * sizeof(uint8_t *)); 
    for(int i = 0; i < 8; i++){ 
        nouvelle_matrice[i] = malloc(8 * sizeof(uint8_t));
    }
    
    double nouvelle_val;
    uint8_t val_converti;
    for (int x = 0; x < 8; x++){
        for (int y = 0; y < 8; y++){
            nouvelle_val = s(x, y, matrice);
            nouvelle_val += 128.0;
            if (nouvelle_val < 0){
                nouvelle_val = 0;
            } else if (nouvelle_val > 255){
                nouvelle_val = 255;
            }
            val_converti = (uint8_t)nouvelle_val;
            nouvelle_matrice[x][y] = val_converti;
       }
    }

    return nouvelle_matrice;
}


//---------------------------------------//
//------------- IDCT RAPIDE -------------//
//---------------------------------------//
double papillon0(double O0, double O1){
    return (O0 + O1)/2;
}

double papillon1(double O0, double O1){
    return (O0 - O1)/2;
}

double rota0(double O0, double O1, float k, int n){
    return O0*(1/k)*cos((n*PI)/16) - O1*(1/k)*sin((n*PI)/16);
}

double rota1(double O0, double O1, float k, int n){
    return O1*(1/k)*cos((n*PI)/16) + O0*(1/k)*sin((n*PI)/16);
}

double* idct(int16_t* tableau){
    //Stage 1
    double stock_0 = tableau[0];
    double stock_1 = tableau[1];
    double stock_2 = tableau[2];
    double stock_3 = tableau[3];
    double stock_4 = tableau[4];
    double stock_5 = tableau[5];
    double stock_6 = tableau[6];
    double stock_7 = tableau[7];
    double stock_7_1 = stock_7;
    
    stock_7 = papillon1(stock_1, stock_7);
    stock_1 = papillon0(stock_1, stock_7_1);
    stock_3 = tableau[3]/sqrt(2);
    stock_5 = tableau[5]/sqrt(2);
    
    // //Stage 2
    double stock_0_1 = stock_0;
    stock_0 = papillon0(stock_0, stock_4);
    stock_4 = papillon1(stock_0_1, stock_4);
    double stock_3_1 = stock_3;
    stock_3 = papillon1(stock_1, stock_3);
    stock_1 = papillon0(stock_1, stock_3_1);
    stock_7_1 = stock_7;
    stock_7 = papillon0(stock_7, stock_5);
    stock_5 = papillon1(stock_7_1, stock_5);

    double stock_2_1 = stock_2;
    stock_2 = rota0(stock_2, stock_6, sqrt(2), 6);
    stock_6 = rota1(stock_2_1, stock_6, sqrt(2), 6);
    
    // //Stage 3
    stock_0_1 = stock_0;
    stock_0 = papillon0(stock_0, stock_6);
    stock_6 = papillon1(stock_0_1, stock_6);
    double stock_4_1 = stock_4;
    stock_4 = papillon0(stock_4, stock_2);
    stock_2 = papillon1(stock_4_1, stock_2);
    stock_7_1 = stock_7;
    stock_7 = rota0(stock_7, stock_1, 1, 3);
    stock_1 = rota1(stock_7_1, stock_1, 1, 3);
    stock_3_1 = stock_3;
    stock_3 = rota0(stock_3, stock_5, 1, 1);
    stock_5 = rota1(stock_3_1, stock_5, 1, 1);
    
    //Stage 4
    double* tableau_a_rendre = calloc(8,sizeof(double));
    tableau_a_rendre[3] = (stock_7 + stock_6)/2;
    tableau_a_rendre[4] = (stock_6 - stock_7)/2;
    tableau_a_rendre[2] = (stock_3 + stock_2)/2;
    tableau_a_rendre[5] = (stock_2 - stock_3)/2;
    tableau_a_rendre[1] = (stock_5 + stock_4)/2;
    tableau_a_rendre[6] = (stock_4 - stock_5)/2;
    tableau_a_rendre[0] = (stock_1 + stock_0)/2;
    tableau_a_rendre[7] = (stock_0 - stock_1)/2;
    
    return tableau_a_rendre;
}


double* idct_colonne(double* tableau){
    //Stage 1
    double stock_0 = tableau[0];
    double stock_1 = tableau[1];
    double stock_2 = tableau[2];
    double stock_3 = tableau[3];
    double stock_4 = tableau[4];
    double stock_5 = tableau[5];
    double stock_6 = tableau[6];
    double stock_7 = tableau[7];

    double stock_7_1 = stock_7;
    stock_7 = papillon1(stock_1, stock_7);
    stock_1 = papillon0(stock_1, stock_7_1);
    
    stock_3 = tableau[3]/sqrt(2);
    stock_5 = tableau[5]/sqrt(2);
   
    // //Stage 2
    double stock_0_1 = stock_0;
    stock_0 = papillon0(stock_0, stock_4);
    stock_4 = papillon1(stock_0_1, stock_4);
    
    double stock_3_1 = stock_3;
    stock_3 = papillon1(stock_1, stock_3);
    stock_1 = papillon0(stock_1, stock_3_1);
    
    stock_7_1 = stock_7;
    stock_7 = papillon0(stock_7, stock_5);
    stock_5 = papillon1(stock_7_1, stock_5);

    double stock_2_1 = stock_2;
    stock_2 = rota0(stock_2, stock_6, sqrt(2), 6);
    stock_6 = rota1(stock_2_1, stock_6, sqrt(2), 6);
   
    // //Stage 3
    stock_0_1 = stock_0;
    stock_0 = papillon0(stock_0, stock_6);
    stock_6 = papillon1(stock_0_1, stock_6);
    
    double stock_4_1 = stock_4;
    stock_4 = papillon0(stock_4, stock_2);
    stock_2 = papillon1(stock_4_1, stock_2);
    
    stock_7_1 = stock_7;
    stock_7 = rota0(stock_7, stock_1, 1, 3);
    stock_1 = rota1(stock_7_1, stock_1, 1, 3);
    
    stock_3_1 = stock_3;
    stock_3 = rota0(stock_3, stock_5, 1, 1);
    stock_5 = rota1(stock_3_1, stock_5, 1, 1);
    
    //Stage 4
    double* tableau_a_rendre = calloc(8,sizeof(double));
    tableau_a_rendre[3] = (stock_7 + stock_6)/2;
    tableau_a_rendre[4] = (stock_6 - stock_7)/2;
    tableau_a_rendre[2] = (stock_3 + stock_2)/2;
    tableau_a_rendre[5] = (stock_2 - stock_3)/2;
    tableau_a_rendre[1] = (stock_5 + stock_4)/2;
    tableau_a_rendre[6] = (stock_4 - stock_5)/2;
    tableau_a_rendre[0] = (stock_1 + stock_0)/2;
    tableau_a_rendre[7] = (stock_0 - stock_1)/2;
    
    
    return tableau_a_rendre;
}

uint8_t** spatial_rapide(int16_t** matrice){
    uint8_t** nouvelle_matrice = calloc(8 , sizeof(uint8_t *));
    // idct sur les lignes
    for (uint8_t i=0;i<8;i++){
        nouvelle_matrice[i] = calloc(8, sizeof(uint8_t));
    }
    double** matrice_float = calloc(8,sizeof(double*));
    for (int i = 0; i < 8; i++){
        matrice_float[i] = idct(matrice[i]);
    }
    
    // idct sur les colonnes
    for (int j = 0; j < 8; j++){
        double* recup_colonne = calloc(8,sizeof(double));
        for (int x = 0; x < 8; x++){
            recup_colonne[x] = matrice_float[x][j];
        }
        double* colonne = idct_colonne(recup_colonne);
        for (int y = 0; y < 8; y++){
            matrice_float[y][j] = 8*colonne[y];
        }
        free(recup_colonne);
        free(colonne);
    }
    // écriture dans la matrice à renvoyer
    for (int x = 0; x < 8; x++){
        for (int y = 0; y < 8; y++){
            matrice_float[x][y] += 128;
            if (matrice_float[x][y] < 0){
                nouvelle_matrice[x][y] = 0;
            } else if (matrice_float[x][y] > 255){
                nouvelle_matrice[x][y] = 255;
            } else {
                nouvelle_matrice[x][y] = round(matrice_float[x][y]);
            }

       }
    }
    for(int i=0; i<8; i++){
        free(matrice_float[i]);
    }
    free(matrice_float);

    return nouvelle_matrice;
}

