#include <stdlib.h>
#include <stdio.h>
#include <stdint.h>
#include <string.h>
#include <math.h>
#include "ac_dc.h"


//Libère l'espace mémoire associé à la séquence de bit
void free_bit_acdc(char** bit, int taille){
    for(int i = 0; i<taille; i++){
        free(bit[i]);
    }
    free(bit);
}


//modification des symboles en hexadecimal
int dec_to_hex(int dec){
    int hex = 0;
    int base = 1;
    while (dec > 0) {
        hex += (dec % 16) * base;
        dec /= 16;
        base *= 10;
    }
    return hex;
}


uint8_t trouve_indice_huff(char** sequence, int* indice, int taille_huff, char** code_huff){
    int flag = 0;
    int indice_huff = 0;
    for (int i = 0; i < 16 && flag == 0; i++){
        char mot_courant[i+2];
        //crée le mot courant
        for (int j = 0; j < i + 1; j++){
            mot_courant[j] = sequence[*indice + j][0];
        }
        mot_courant[i+1] = '\0';
        for (int x = 0; x < taille_huff; x++){
            if (strcmp(mot_courant, code_huff[x]) == 0){
                indice_huff = x;
                flag = 1;
                *indice += i + 1;
                break;
            }
        }
    }
    return indice_huff;
}



//On récupère n bit de la séquence entière à partir de e
char* recupere_n_bit(char** sequence, uint8_t n, int e){
    char* recupere_bit = malloc((n+1)*sizeof(char));

    for (int i = e; i < e + n; i++){
        recupere_bit[i - e] = sequence[i][0];
    }
    recupere_bit[n] = '\0';
    return recupere_bit;
}



int16_t calcul_indice_magnitude(char* bit, uint8_t magnitude){
    int16_t n = 0;
    for (int i = 0; i < magnitude; i++) {
        if (bit[i] == '1') {
            n += pow(2, magnitude - i - 1);
        }
    }
    if(bit != NULL && bit[0] == '0'){
        n = n - (pow(2, magnitude)-1);
    }
    return n;
}



int16_t verif_DC(char** bit, char** code, int taille, char* symbole, int* indice_bitstream, int16_t* difference){
    uint8_t indice = trouve_indice_huff(bit, indice_bitstream, taille, code);
    char symbole_dc = symbole[indice];
    uint8_t magn1 = ((uint8_t)symbole_dc / 16);
    uint8_t magn2 = (uint8_t)symbole_dc - magn1*16;
    uint8_t magnitude = magn1*10 + magn2;
    char* recupere_bit = recupere_n_bit(bit, magnitude, *indice_bitstream);
    *indice_bitstream += magnitude;
    int16_t n = calcul_indice_magnitude(recupere_bit, magnitude);
    n += *difference;
    *difference = n;
    free(recupere_bit);
    return n;
}


int16_t verif_AC(char** bit, char** code, int taille, char* symbole, int* indice_bitstream, int* coeff_0){
    uint8_t indice = trouve_indice_huff(bit, indice_bitstream, taille, code);
    int16_t n = 0;
    char symbole_ac = symbole[indice];
    uint8_t coeff_zero = ((uint8_t)symbole_ac / 16);
    uint8_t magnitude_ac = (uint8_t)symbole_ac - coeff_zero*16;
    *coeff_0 = coeff_zero;
    char* recupere_bit = recupere_n_bit(bit, magnitude_ac, *indice_bitstream);
    *indice_bitstream += magnitude_ac;
    n = calcul_indice_magnitude(recupere_bit, magnitude_ac);
    free(recupere_bit);
    return n;
}


void free_tableau(int16_t* tab) {
    free(tab);
}

void tableau_couleur(char** sequence, uint32_t* echantillonage, char** code_dc_Y, int taille_dc_Y, char* val_dc_Y, char** code_ac_Y, int taille_ac_Y, char* val_ac_Y, char** code_dc_Cb, int taille_dc_Cb, char* val_dc_Cb, char** code_ac_Cb, int taille_ac_Cb, char* val_ac_Cb, char** code_dc_Cr, int taille_dc_Cr, char* val_dc_Cr, char** code_ac_Cr, int taille_ac_Cr, char* val_ac_Cr, int16_t* difference_Y, int16_t* difference_Cb, int16_t* difference_Cr, int* indice_bitstream, int16_t** tab_bloc_MCU, int* indice_bloc){
    //POUR Y
    int indice_tab;
    for (uint32_t i = 0; i < *echantillonage; i++){
        int16_t* tab_Y = malloc((64)*sizeof(int16_t));
        int16_t coeff_dc_Y = verif_DC(sequence, code_dc_Y, taille_dc_Y, val_dc_Y, indice_bitstream, difference_Y);
        tab_Y[0] = coeff_dc_Y;
        indice_tab = 1;

        while (indice_tab < 64){
            int coeff_zero = 0;
            int16_t coeff_ac_Y = verif_AC(sequence, code_ac_Y, taille_ac_Y, val_ac_Y, indice_bitstream, &coeff_zero);
            for (int i = 0; i < coeff_zero; i++){
                tab_Y[indice_tab] = 0;
                indice_tab += 1;
            }
            if(coeff_zero == 0 && coeff_ac_Y == 0){
                for(indice_tab; indice_tab < 64; indice_tab ++){
                    tab_Y[indice_tab] = 0;
                }
            }
            if(indice_tab != 64){
                //On a ajouté le coeff ac
                tab_Y[indice_tab] = coeff_ac_Y;
                indice_tab += 1;
            }
        }
        tab_bloc_MCU[*indice_bloc] = tab_Y;
        *indice_bloc += 1;
    }

    //POUR Cb
    int16_t* tab_Cb = malloc((64)*sizeof(int16_t));
    int16_t coeff_dc_Cb = verif_DC(sequence, code_dc_Cb, taille_dc_Cb, val_dc_Cb, indice_bitstream, difference_Cb);
    tab_Cb[0] = coeff_dc_Cb;
    indice_tab = 1;
    while (indice_tab < 64){
        int coeff_zero = 0;
        int16_t coeff_ac_Cb = verif_AC(sequence, code_ac_Cb, taille_ac_Cb, val_ac_Cb, indice_bitstream, &coeff_zero);
        for (int i = 0; i < coeff_zero; i++){
            tab_Cb[indice_tab] = 0;
            indice_tab += 1;
        }
        if(coeff_zero == 0 && coeff_ac_Cb == 0){
            for(indice_tab; indice_tab < 64; indice_tab ++){
                tab_Cb[indice_tab] = 0;
            }
        }
        if(indice_tab != 64){
            //On a ajouté le coeff ac
            tab_Cb[indice_tab] = coeff_ac_Cb;
            indice_tab += 1;
        }
    }
    tab_bloc_MCU[*indice_bloc] = tab_Cb;
    *indice_bloc += 1;

    //POUR Cr
    int16_t* tab_Cr = malloc((64)*sizeof(int16_t));
    int16_t coeff_dc_Cr = verif_DC(sequence, code_dc_Cr, taille_dc_Cr, val_dc_Cr, indice_bitstream, difference_Cr);
    tab_Cr[0] = coeff_dc_Cr;
    indice_tab = 1;
    while (indice_tab < 64){
        int coeff_zero = 0;
        int16_t coeff_ac_Cr = verif_AC(sequence, code_ac_Cr, taille_ac_Cr, val_ac_Cr, indice_bitstream, &coeff_zero);
        for (int i = 0; i < coeff_zero; i++){
            tab_Cr[indice_tab] = 0;
            indice_tab += 1;
        }
        if(coeff_zero == 0 && coeff_ac_Cr == 0){
            for(indice_tab; indice_tab < 64; indice_tab ++){
                tab_Cr[indice_tab] = 0;
            }
        }
        if(indice_tab != 64){
            //On a ajouté le coeff ac
            tab_Cr[indice_tab] = coeff_ac_Cr;
            indice_tab += 1;
        }
    }
    tab_bloc_MCU[*indice_bloc] = tab_Cr;
    *indice_bloc += 1;
}



int16_t* tableau(char** sequence, char** code_dc, int taille_dc, char** code_ac, int taille_ac, char* val_dc, char* val_ac,  int16_t* difference, int* indice_bitstream){
    int16_t* tab = malloc((64)*sizeof(int16_t));
    int16_t coeff_dc = verif_DC(sequence, code_dc, taille_dc, val_dc, indice_bitstream, difference);
    tab[0] = coeff_dc;
    int indice_tab = 1;
    while (indice_tab < 64){
        int coeff_zero = 0;
        int16_t coeff_ac = verif_AC(sequence, code_ac, taille_ac, val_ac, indice_bitstream, &coeff_zero);
        for (int i = 0; i < coeff_zero; i++){
            tab[indice_tab] = 0;
            indice_tab += 1;
        }
        if(coeff_zero == 0 && coeff_ac == 0){
            for(indice_tab; indice_tab < 64; indice_tab ++){
                tab[indice_tab] = 0;
            }
        }
        if(indice_tab != 64){
            //On a ajouté le coeff ac
            tab[indice_tab] = coeff_ac;
            indice_tab += 1;
        }
    }
    return tab;
}

//Ca renvoie un tableau de bit
char** sequence_bit(char* bitstream, int taille){
    char** bit = malloc((8*taille)*sizeof(char*)); 
    for(int i = 0; i < taille; i++){ 
        char octet_courant = bitstream[i];
        for(int j = 7; j >= 0; j--){
            int bit_courant = (octet_courant >> j) & 1;
            if (bit_courant == 0){
                char* zero = malloc(2*sizeof(char));
                zero[0] = '0';
                zero[1] = '\0';
                bit[8*i + (7 - j)] = zero;
            } else {
                char* un = malloc(2*sizeof(char));
                un[0] = '1';
                un[1] = '\0';
                bit[8*i + (7 - j)] = un;
            }
            
        }
    }
    return bit;
}
