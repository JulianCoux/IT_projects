#include <stdlib.h>
#include <stdio.h>
#include <math.h>
#include "lecture_entete.h"
#include "huffman.h"
#include "ac_dc.h"
#include "quantification.h"
#include "zigzag.h"
#include "spatial.h"
#include "upsampling.h"
#include "passage_RGB.h"


int main(int argc, char **argv)
{
//--------------------------------------------------------//
//--------------- Récupération de l'entête ---------------//
//--------------------------------------------------------//
    if (argc != 2) {
        fprintf(stderr, "Usage: %s fichier.jpeg\n", argv[0]);
    	return EXIT_FAILURE;
    }
    FILE* fichier = fopen(argv[1], "r");
    verif_SOI(fichier);                                                                 //verif SOI
    char* liste_APPO = verif_APPO(fichier);                                             //verif APPO

    char* list_car = lecture_cara(fichier, 2);
    uint8_t nb_tab_quant = 0;
    struct data_DQT* liste_data_DQT;
    if(((uint8_t)list_car[0] == 255) && ((uint8_t)list_car[1] == 254)){    //On est pas obligé d'avoir la section COM 
        free_lecture_cara(list_car);
        verif_COM(fichier);                                                             //verif COM
        list_car = lecture_cara(fichier, 2);
        if(((uint8_t)list_car[0] == 255) && ((uint8_t)list_car[1] == 219)){
            free_lecture_cara(list_car);
            liste_data_DQT = verif_DQT(fichier, &nb_tab_quant);       //verif DQT
        }
    }
    else if(((uint8_t)list_car[0] == 255) && ((uint8_t)list_car[1] == 219)){
        free_lecture_cara(list_car);
        liste_data_DQT = verif_DQT(fichier, &nb_tab_quant);            //verif DQT
    }

    uint8_t precision = 8;
    uint32_t largeur = 0; 
    uint32_t hauteur = 0; 
    //En sortant de verif_DQT, on a déjà vérifié que après il y avait un SOF0
    uint32_t nb_comp = 0;
    struct Start_Of_Frame N_comp_SOF0 = verif_SOF0(fichier, &precision, &largeur ,&hauteur, &nb_comp); // verif SOf0


    int nb_MCU_largeur;
    if (largeur % 8 == 0) {
        nb_MCU_largeur = (largeur/8);
    } else {
        nb_MCU_largeur = (largeur/8) + 1;
    }
    int nb_MCU_hauteur;
    if (hauteur % 8 == 0) {
        nb_MCU_hauteur = (hauteur/8);
    } else {
        nb_MCU_hauteur = (hauteur/8) + 1;
    }
    int nb_MCU_tot = nb_MCU_hauteur*nb_MCU_largeur;

    list_car = lecture_cara(fichier, 2);
    struct Huffman* table_huff;
    int taille_dht = 0;
    if (((uint8_t)list_car[0] == 255) && ((uint8_t)list_car[1] == 196)){
        free_lecture_cara(list_car);
        table_huff = verif_DHT(fichier, &taille_dht);                            //verif DHT
        //QUAND ON EST LA C QU ON A DEJA VERIFIE QU ON AVAIT LE CODE DE SOS
        taille_dht ++;
    } else {
        return EXIT_FAILURE;
    }


    
    uint8_t Ss_SOS = 0;
    uint8_t Se_SOS = 63;
    uint8_t Ah_SOS = 0;
    uint8_t Al_SOS = 0;
    struct Start_Of_Scan N_comp_SOS = verif_SOS(fichier, &Ss_SOS, &Se_SOS, &Ah_SOS, &Al_SOS);       //verif SOS

    //récupérer tous les caractères suivants
    struct BitStream bitstream = recupere_bitstream(fichier);
    


    //---------------------------------------------------//
    //--------------- Calcul code Huffman ---------------//
    //---------------------------------------------------//
    printf("\n[CALCUL CODE HUFFMAN] :\n");

    struct code_Huffman* codes_de_Huffman = malloc(taille_dht*sizeof(struct code_Huffman));

    for(int i=0; i<taille_dht; i++){  //calcul du code pour chaque tableau de Huffman
        
        if(table_huff[i].tableau == NULL){
            return EXIT_FAILURE;
        }

        //Création des différents tableaux utiles au décodage
        uint16_t verif_len = 0;
        for(int j=0; j<16; j++){
            verif_len += table_huff[i].tableau[j];
        }
        printf("nombre symboles : %i\n", verif_len);
        codes_de_Huffman[i].nb_codes = verif_len;   //nombre de mots symboles
        codes_de_Huffman[i].tableau_symboles = malloc(verif_len*sizeof(char));
        for(int j=0; j<verif_len; j++){
            codes_de_Huffman[i].tableau_symboles[j] = table_huff[i].tableau[j+16];
        }


        codes_de_Huffman[i].tableau_codes = calcul_code_huff(table_huff[i].tableau, verif_len);

        //Affichage correspondance : symbole <=> mot de code
        printf("    [Code huffman n°%i]\n", i+1);
        for(int j=0; j<verif_len; j++){
            printf("        symbole %x => %s\n", (uint8_t)codes_de_Huffman[i].tableau_symboles[j], codes_de_Huffman[i].tableau_codes[j]);
        }

    }
    printf("[FIN HUFFMAN]\n");



    //-----------------------------------------------------//
    //--------------- Codage de AC et de DC ---------------//
    //-----------------------------------------------------//
    printf("\n[CODAGE AC/DC] : \n");

    char** bit = sequence_bit(bitstream.list_bitstream, bitstream.size);

    free(bitstream.list_bitstream);

    int16_t** tab_bloc_MCU_NB = malloc(nb_MCU_tot*sizeof(int16_t*));
    uint32_t echantillonage = N_comp_SOF0.tab_echant_hor[0]*N_comp_SOF0.tab_echant_vert[0];
    uint32_t echantillonage_hauteur = N_comp_SOF0.tab_echant_vert[0];
    uint32_t echantillonage_largeur = N_comp_SOF0.tab_echant_hor[0];
    
    uint32_t vrai_taille;
    if (largeur%(8*echantillonage_largeur) != 0){
        if (hauteur%(8*echantillonage_hauteur) != 0){
            vrai_taille = (largeur/(8*echantillonage_largeur) + 1)*(hauteur/(8*echantillonage_hauteur) + 1);
        } else {
            vrai_taille = (largeur/(8*echantillonage_largeur) + 1)*(hauteur/(8*echantillonage_hauteur));
        }
    } else {
        if (hauteur%(8*echantillonage_hauteur) != 0){
            vrai_taille = (largeur/(8*echantillonage_largeur))*(hauteur/(8*echantillonage_hauteur) + 1);
        } else {
            vrai_taille = (largeur/(8*echantillonage_largeur))*(hauteur/(8*echantillonage_hauteur));
        }
    }
    
    int16_t** tab_bloc_MCU = malloc(vrai_taille*(2 + echantillonage)*sizeof(int16_t*)); 
    if (nb_comp == 1){ // N&B
        //Chercher les tables DC:
        int indice_DC = 0;
        for(int i=0; i<taille_dht; i++){
            if(table_huff[i].type == 0){
                indice_DC = i;
                break;
            }
        }

        //Chercher les tables AC:
        int indice_AC = 0;
        for(int i=0; i<taille_dht; i++){
            if(table_huff[i].type == 1){
                indice_AC = i;
                break;
            }
        }

        int16_t difference = 0;
        int indice_bitstream = 0;
        for(int i = 0; i<nb_MCU_tot; i++){
            int16_t* tab = tableau(bit, codes_de_Huffman[indice_DC].tableau_codes, codes_de_Huffman[indice_DC].nb_codes, codes_de_Huffman[indice_AC].tableau_codes, codes_de_Huffman[indice_AC].nb_codes, codes_de_Huffman[indice_DC].tableau_symboles, codes_de_Huffman[indice_AC].tableau_symboles, &difference, &indice_bitstream);
            // printf("    Tableau %i : \n", i);
            // for (int j = 0; j < 64; j++){
            //     printf("%i ", tab[j]);
            // }
            // printf("\n");
            tab_bloc_MCU_NB[i] = tab;
        }
        
    }
    else { //couleur
        //chercher les tables DC pour Y, Cb et Cr
        
        int indice_DC_Y = 0;
        int indice_DC_Cb = 0;
        int indice_DC_Cr = 0;
        for(int i=0; i < taille_dht; i++){
            if(table_huff[i].type == 0 && table_huff[i].indice == N_comp_SOS.tab_Ihdc[0]){
                indice_DC_Y = i;
                continue;
            }
            if (table_huff[i].type == 0 && table_huff[i].indice == N_comp_SOS.tab_Ihdc[1]){
                indice_DC_Cb = i;
            }
            if (table_huff[i].type == 0 && table_huff[i].indice == N_comp_SOS.tab_Ihdc[1]){
                indice_DC_Cr = i;
            }
        }

        //chercher les tables AC pour Y, Cb et Cr
        int indice_AC_Y = 0;
        int indice_AC_Cb = 0;
        int indice_AC_Cr = 0;
        for(int i=0; i < taille_dht; i++){
            if(table_huff[i].type == 1 && table_huff[i].indice == N_comp_SOS.tab_Ihac[0]){
                indice_AC_Y = i;
                continue;
            }
            if (table_huff[i].type == 1 && table_huff[i].indice == N_comp_SOS.tab_Ihac[1]){
                indice_AC_Cb = i;
            }
            if (table_huff[i].type == 1 && table_huff[i].indice == N_comp_SOS.tab_Ihac[1]){
                indice_AC_Cr = i;
            }
        }
        int16_t difference_Y = 0;
        int16_t difference_Cb = 0;
        int16_t difference_Cr = 0;
        int indice_bitstream = 0;
        int indice_bloc = 0;
        for(uint32_t i = 0; i<vrai_taille; i++){
            tableau_couleur(bit, &echantillonage, codes_de_Huffman[indice_DC_Y].tableau_codes, codes_de_Huffman[indice_DC_Y].nb_codes, codes_de_Huffman[indice_DC_Y].tableau_symboles, codes_de_Huffman[indice_AC_Y].tableau_codes, codes_de_Huffman[indice_AC_Y].nb_codes, codes_de_Huffman[indice_AC_Y].tableau_symboles, codes_de_Huffman[indice_DC_Cb].tableau_codes, codes_de_Huffman[indice_DC_Cb].nb_codes, codes_de_Huffman[indice_DC_Cb].tableau_symboles, codes_de_Huffman[indice_AC_Cb].tableau_codes, codes_de_Huffman[indice_AC_Cb].nb_codes, codes_de_Huffman[indice_AC_Cb].tableau_symboles, codes_de_Huffman[indice_DC_Cr].tableau_codes, codes_de_Huffman[indice_DC_Cr].nb_codes, codes_de_Huffman[indice_DC_Cr].tableau_symboles, codes_de_Huffman[indice_AC_Cr].tableau_codes, codes_de_Huffman[indice_AC_Cr].nb_codes, codes_de_Huffman[indice_AC_Cr].tableau_symboles, &difference_Y, &difference_Cb, &difference_Cr, &indice_bitstream, tab_bloc_MCU, &indice_bloc);
        }

        // for(uint32_t i=0; i<vrai_taille*(2 + echantillonage); i++){
        //     printf("Tableau[%i] = ", i);
        //     for (int j = 0; j < 64; j++){
        //         printf("%i ", tab_bloc_MCU[i][j]);
        //     }
        //     printf("\n");
        // }
    }
    printf("    OK\n");
    printf("[FIN CODAGE AC/DC]\n");

    free_bit_acdc(bit, 8*bitstream.size);
    free_all_huffman(codes_de_Huffman, taille_dht);


    //------------------------------------------------------//
    //--------------- Quantification inverse ---------------//
    //------------------------------------------------------//
    printf("\n[QUANTIFICATION] :\n");

    int nb_comp_Y = N_comp_SOF0.tab_echant_hor[0] * N_comp_SOF0.tab_echant_vert[0];
    if (nb_comp == 1){
        int8_t tab_quant[64];
        for(int i=0; i<64; i++){
            tab_quant[i] = dec_to_hex(liste_data_DQT[0].liste_DQT[i]);
        }   
        
        for(int i = 0; i<nb_MCU_tot; i++){
            tab_quantifie(tab_bloc_MCU_NB[i], tab_quant);
        }
    }
    else{
        int8_t tab_quant[3][64];
        int index_quantif_Y = N_comp_SOF0.tab_Iq[0];
        int index_quantif_Cb = N_comp_SOF0.tab_Iq[1];
        int index_quantif_Cr = N_comp_SOF0.tab_Iq[2];
        for(int i=0; i<64; i++){
            tab_quant[index_quantif_Y][i] = dec_to_hex(liste_data_DQT[index_quantif_Y].liste_DQT[i]);
            tab_quant[index_quantif_Cb][i] = dec_to_hex(liste_data_DQT[index_quantif_Cb].liste_DQT[i]);
            tab_quant[index_quantif_Cr][i] = dec_to_hex(liste_data_DQT[index_quantif_Cr].liste_DQT[i]);
        }
        int index_i = 0;
        for(uint32_t i=0; i<vrai_taille*(2+echantillonage); i+=2+nb_comp_Y){
            index_i ++;
            for(int h=0; h<nb_comp_Y; h++){
                tab_quantifie(tab_bloc_MCU[i+h], tab_quant[index_quantif_Y]);
            }
            tab_quantifie(tab_bloc_MCU[i+nb_comp_Y], tab_quant[index_quantif_Cb]);
            tab_quantifie(tab_bloc_MCU[i+nb_comp_Y+1], tab_quant[index_quantif_Cr]);
            // printf("\nmcu n°%i", i);
            // for(int h=0; h<nb_comp_Y; h++){
            //     printf("\ncomponent Y_%i\n", h);
            //     for(int j = 0; j < 64; j++){
            //         printf("%i ", tab_bloc_MCU[i+h][j]);
            //     }
            // }
            // printf("\ncomponent Cb\n");
            // for(int j = 0; j < 64; j++){
            //     printf("%i ", tab_bloc_MCU[i+nb_comp_Y][j]);
            // }
            // printf("\ncomponent Cr\n");
            // for(int j = 0; j < 64; j++){
            //     printf("%i ", tab_bloc_MCU[i+nb_comp_Y+1][j]);
            // }
            // printf("\n");
        }
    }

    printf("    OK\n");
    printf("[FIN QUANTIFICATION]\n");

    free_all_DQT(liste_data_DQT, nb_tab_quant);

    int new_mcu_tot = 0;
    if (nb_comp == 1){
        new_mcu_tot = nb_MCU_tot;
    }
    else{
        new_mcu_tot = vrai_taille * (2 +echantillonage);
    }

    //---------------------------------------//
    //--------------- Zig Zag ---------------//
    //---------------------------------------//
    printf("\n[ZIG ZAG] :\n");
    int16_t*** list_matrice = malloc(new_mcu_tot*sizeof(int16_t**));
    if(nb_comp == 1){
        for(int i = 0; i<new_mcu_tot; i++){
            list_matrice[i] = zigzag(tab_bloc_MCU_NB[i]);
        }
    }
    else{
        for(int i = 0; i<new_mcu_tot; i++){
            list_matrice[i] = zigzag(tab_bloc_MCU[i]);
        }
        // for(int i = 0; i<new_mcu_tot; i++){
        //     printf("matrice n°%i\n", i);
        //     for (int x = 0; x < 8; x++){
        //         for (int y = 0; y < 8; y++){
        //             printf("%i ",  list_matrice[i][x][y]);
        //         }
        //         printf("\n");
        //     }
        //     printf("\n");
        // }
    }
    

    printf("    OK\n");
    printf("[FIN ZIG ZAG]\n");

    if(nb_comp != 1){
        for (uint32_t i = 0; i < vrai_taille*(2+echantillonage); i++){
            free_tableau(tab_bloc_MCU[i]);
        }
    }
    else{
        for (int i = 0; i < new_mcu_tot; i++){
            free_tableau(tab_bloc_MCU_NB[i]);
        }
    }
    free(tab_bloc_MCU);
    free(tab_bloc_MCU_NB);

    //--------------------------------------------------//
    //--------------- Passage en Spatial ---------------//
    //--------------------------------------------------//
    printf("\n[SPATIAL] :\n");
    uint8_t*** matrice_spatiale = malloc(new_mcu_tot*sizeof(uint8_t**));
    for(int i = 0; i<new_mcu_tot; i++){
        //matrice_spatiale[i] = spatial(list_matrice[i]);
        // Rapide, ou pas...
        matrice_spatiale[i] = spatial_rapide(list_matrice[i]);
        // printf("matrice n°%i\n", i);
        // for (int x = 0; x < 8; x++){
        //     for (int y = 0; y < 8; y++){
        //         printf("%u ", matrice_spatiale[i][x][y]);
        //     }
        //     printf("\n");
        // }
        // printf("\n");
    }

    
    printf("    OK\n");
    printf("[FIN SPATIAL]\n");


    //------------------------------------------//
    //--------------- Upsampling ---------------//
    //------------------------------------------//
    printf("\n[UPSAMPLING] :\n");
    int nb_Cb = new_mcu_tot/(nb_comp_Y+2);
    uint8_t*** matrice_upsampling = malloc((new_mcu_tot+2*nb_Cb*(echantillonage_largeur*echantillonage_hauteur - 1))*sizeof(uint8_t**));

    if(nb_comp != 1 && nb_comp_Y > 1){
        //nb_comp_Y => nombre de composantes Y
        if(echantillonage_largeur == 2 && echantillonage_hauteur == 1){
            printf("horizontal\n");
            int indice_spatiale = 0;
            for(uint32_t i=0; i<(new_mcu_tot+2*nb_Cb*(echantillonage_largeur*echantillonage_hauteur - 1)); i=i+nb_comp_Y+4){
                for(int j=0; j<nb_comp_Y; j++){
                    matrice_upsampling[i+j] = matrice_spatiale[indice_spatiale];
                    indice_spatiale ++;
                }
                uint8_t*** matrice = upsampling_matrice_hor(matrice_spatiale[indice_spatiale]);
                matrice_upsampling[i+nb_comp_Y] = matrice[0];
                matrice_upsampling[i+nb_comp_Y+1] = matrice[1];
                free(matrice);
                indice_spatiale ++;
                matrice = upsampling_matrice_hor(matrice_spatiale[indice_spatiale]);
                matrice_upsampling[i+nb_comp_Y+2] = matrice[0];
                matrice_upsampling[i+nb_comp_Y+3] = matrice[1];
                free(matrice);
                indice_spatiale ++;
            }
        }
        else if(echantillonage_largeur == 1 && echantillonage_hauteur == 2){
            printf("vertical\n");
            int indice_spatiale = 0;
            for(uint32_t i=0; i<(new_mcu_tot+2*nb_Cb*(echantillonage_largeur*echantillonage_hauteur - 1)); i=i+nb_comp_Y+4){
                for(int j=0; j<nb_comp_Y; j++){
                    matrice_upsampling[i+j] = matrice_spatiale[indice_spatiale];
                    indice_spatiale ++;
                }
                uint8_t*** matrice = upsampling_matrice_vert(matrice_spatiale[indice_spatiale]);
                matrice_upsampling[i+nb_comp_Y] = matrice[0];
                matrice_upsampling[i+nb_comp_Y+1] = matrice[1];
                free(matrice);
                indice_spatiale ++;
                matrice = upsampling_matrice_vert(matrice_spatiale[indice_spatiale]);
                matrice_upsampling[i+nb_comp_Y+2] = matrice[0];
                matrice_upsampling[i+nb_comp_Y+3] = matrice[1];
                free(matrice);
                indice_spatiale ++;
            }
        }
        else if(echantillonage_largeur*echantillonage_hauteur == 4){
            printf("vertical et horizontal\n");
            int indice_spatiale = 0;
            for(uint32_t i=0; i<(new_mcu_tot+2*nb_Cb*(echantillonage_largeur*echantillonage_hauteur - 1)); i=i+nb_comp_Y+8){
                for(int j=0; j<nb_comp_Y; j++){
                    matrice_upsampling[i+j] = matrice_spatiale[indice_spatiale];
                    indice_spatiale ++;
                }
                uint8_t*** matrice = upsampling_matrice_4(matrice_spatiale[indice_spatiale]);
                matrice_upsampling[i+nb_comp_Y] = matrice[0];
                matrice_upsampling[i+nb_comp_Y+1] = matrice[1];
                matrice_upsampling[i+nb_comp_Y+2] = matrice[2];
                matrice_upsampling[i+nb_comp_Y+3] = matrice[3];
                free(matrice);
                indice_spatiale ++;
                matrice = upsampling_matrice_4(matrice_spatiale[indice_spatiale]);
                matrice_upsampling[i+nb_comp_Y+4] = matrice[0];
                matrice_upsampling[i+nb_comp_Y+5] = matrice[1];
                matrice_upsampling[i+nb_comp_Y+6] = matrice[2];
                matrice_upsampling[i+nb_comp_Y+7] = matrice[3];
                free(matrice);
                indice_spatiale ++;
            }
        }

        // printf("\nMatrices upsampling : \n");
        // for(int i=0; i<(new_mcu_tot+2*nb_Cb*(echantillonage_largeur*echantillonage_hauteur - 1)); i++){
        //     printf("matrice n°%i : \n", i);
        //     for(int x=0; x<8; x++){
        //         for(int y=0; y<8; y++){
        //             printf("%x ", matrice_upsampling[i][x][y]);
        //         }
        //         printf("\n");
        //     }
        //     printf("\n");
        // }
    }
    printf("    OK\n");
    printf("[FIN UPSAMPLING] :\n");


    //------------------------------------------//
    //--------------- RGB et PPM ---------------//
    //------------------------------------------//
    printf("\n[RGB et PPM] :\n");

    char* nom = argv[1];
    int taille_nom = taille_chaine(nom); //renvoie la taille sans l'extention jpeg 
    char* nouveau_nom = modif_nom(taille_nom, nom, nb_comp); //renvoie le nom avec l'extention ppm ou pgm
    if(nb_comp == 1){
        printf("Noir et blanc\n");
        transfo_nb_MCU_1_tronc(largeur, hauteur, nb_MCU_largeur, matrice_spatiale, nouveau_nom);
    }
    else{
        printf("couleur\n");
        if(nb_comp != 1 && nb_comp_Y > 1){
            uint8_t*** matrice = tri_matice(matrice_upsampling, echantillonage_largeur, echantillonage_hauteur, (new_mcu_tot+2*nb_Cb*(echantillonage_largeur*echantillonage_hauteur - 1)), nb_MCU_largeur);
            transfo_couleur_MCU_2(echantillonage_largeur, echantillonage_hauteur, largeur, hauteur, nb_MCU_largeur, matrice, nouveau_nom);
            free(matrice);
        } else{
            transfo_couleur_MCU_2(echantillonage_largeur, echantillonage_hauteur, largeur, hauteur, nb_MCU_largeur, matrice_spatiale, nouveau_nom);
        }
    }
    
    free(nouveau_nom);
    printf("facile...\n");

    printf("[FIN RGB et PPM]\n\n");



    for (int i = 0; i < new_mcu_tot; i++){
        free_spatial(matrice_spatiale[i]);
    }
    free(matrice_spatiale);
    free_lecture_cara(liste_APPO);
    free_all_SOF0(N_comp_SOF0);
    free_all_DHT(table_huff, taille_dht);
    free_all_SOS(N_comp_SOS);

    for(int i = 0; i<new_mcu_tot; i++){
        free_zigzag(list_matrice[i]);
    }
    free(list_matrice);

    if(nb_comp != 1 && nb_comp_Y > 1){
        free_upsampling(matrice_upsampling, (new_mcu_tot+2*nb_Cb*(echantillonage_largeur*echantillonage_hauteur - 1)), nb_comp_Y, echantillonage_largeur*echantillonage_hauteur*2);
    }
    free(matrice_upsampling);

    fclose(fichier);
    
    return EXIT_SUCCESS;
}


