#include <stdlib.h>
#include <stdio.h>
#include <stdint.h>
#include "lecture_entete.h"

void free_lecture_cara(char* liste_car) {
    free(liste_car);
}



/*
Fonction qui lit des caractères du fichier
entrée : le fichier à lire ET le nombre de caractères à lire
sortie : la chaine de caractère lue
*/
char* lecture_cara(FILE* fichier, uint32_t taille){
    char* liste_car = malloc(taille * sizeof(char));
    if(fichier != NULL){
        char car;
        for(uint32_t i = 0; i<taille; i++){
            car = fgetc(fichier);
            liste_car[i] = car;
        }
    }
    else{
        printf("Erreur lecture fichier\n");
        free(liste_car);
        return NULL;
    }
    return liste_car;
}


/*
Fonction qui vérifie que les premiers caractères du fichier
correspondent au Start Of Image
entrée : le fichier à lire
sortie : 1 si on a les bons caractères
*/
int verif_SOI(FILE* fichier){
    //On récupère les deux premiers octets du fichier
    char* list_car = lecture_cara(fichier, 2);
    uint8_t ff = (uint8_t)list_car[0];
    if (ff == 255){
        printf("[SOI]  ");
        uint8_t d8 = (uint8_t)list_car[1];
        if (d8 == 216){
            free_lecture_cara(list_car);
            printf("verif début ok\n");
            return 0;
        }
    }
    free_lecture_cara(list_car);
    return EXIT_FAILURE;
}


/*
Fonction qui vérifie et lit les informations de APPO
entrée : le fichier à lire
sortie : la chaine de caractère lue
*/
char* verif_APPO(FILE* fichier){
    char* list_car = lecture_cara(fichier, 2);
    if (((uint8_t)list_car[0] == 255) && ((uint8_t)list_car[1] == 224)){    //on vérifie si on a bien ff e0
        printf("[APPO] ");
        free_lecture_cara(list_car);
        list_car = lecture_cara(fichier, 2);
        uint32_t taille = ((uint8_t)list_car[0]*(16*16)) + (uint8_t)list_car[1];
        free_lecture_cara(list_car);
        printf("length : %u\n", taille);
        char* liste_APPO = lecture_cara(fichier, 5);
        printf("       format encapsulation : %s\n", liste_APPO);
        char* list_car_1 = lecture_cara(fichier, 1);
        uint8_t version_JFIF_1 = (uint8_t)list_car_1[0];
        char* list_car_2 = lecture_cara(fichier, 1);
        uint8_t version_JFIF_2 = (uint8_t)list_car_2[0];
        printf("       version %s : %u.%u\n", liste_APPO, version_JFIF_1, version_JFIF_2);
        free(list_car_1);
        free(list_car_2);
        list_car = lecture_cara(fichier, 7); 
        free_lecture_cara(list_car);
        return liste_APPO;

    }
    else{
        printf("ERREUR APPO !!\n");
        return NULL;
    }
}



/*
Fonction qui vérifie et lit  les infos de COM
entrée  : le fichier à lire
sortie : 1 si on a les bons caractères
*/
int verif_COM(FILE* fichier){
    printf("[COM] ");
    char* list_car = lecture_cara(fichier, 2);
    uint32_t taille = ((uint8_t)list_car[0]*(16*16)) + (uint8_t)list_car[1];
    printf(" length : %u\n", taille);
    free_lecture_cara(list_car);

    list_car = lecture_cara(fichier, taille - 2);
    list_car = realloc(list_car, (taille - 1)*sizeof(char));
    list_car[taille - 2] = '\0';
    printf("       commentaire : %s\n", list_car);
    free_lecture_cara(list_car);
    return 1;
    
}


void free_all_DQT(struct data_DQT* liste, int taille){
    for(int i = 0; i<taille; i++){
        free(liste[i].liste_DQT);
    }
    free(liste);
}

/*Fonction qui teste si un indice Iq existe déjà
*/
uint8_t verif_indice_DQT(uint8_t indice_DQT_tab[], uint8_t indice_DQT){
    for(int i=0; i<4; i++){
        if(indice_DQT == indice_DQT_tab[i]){
            return i;
        }
    }
    return 255;
}

/*Fonction qui vérifie et lit les informations de DQT, les tables de quantification
entrée : le fichier à lire
sortie : structure contenant, la chaine de caractère lue, la précision et l'indice IQ
*/
struct data_DQT* verif_DQT(FILE* fichier, uint8_t* nb_tab_DQT){
    uint8_t encore_DQT = 1;
    uint8_t indice_DQT_tab[] = {255,255,255,255};
    struct data_DQT* liste_data_DQT = malloc(1*sizeof(struct data_DQT));

    while(encore_DQT == 1 && *nb_tab_DQT<=4){
        printf("[DQT] ");
        char* list_car = lecture_cara(fichier, 2);
        uint32_t taille = ((uint8_t)list_car[0]*(16*16)) + (uint8_t)list_car[1];
        printf(" length : %u\n", taille);
        free_lecture_cara(list_car);
        uint8_t nb_DQT_courant = taille/65;
        printf("       nb de tableaux de quant : %i\n", nb_DQT_courant);
        liste_data_DQT = realloc(liste_data_DQT, (*nb_tab_DQT+nb_DQT_courant)*sizeof(struct data_DQT));
        for(uint8_t i = *nb_tab_DQT; i<nb_DQT_courant+*nb_tab_DQT; i++){
            list_car = lecture_cara(fichier, 1);
            //Récupérer la précision
            uint8_t precis = ((uint8_t)list_car[0]/16);
            uint8_t precision = 0;
            if(precis == 0){
                precision = 8;
                printf("       precision : %u\n", precision);
            }
            else if(precis == 1){
                precision = 16;
                printf("       precision : %u\n", precision);
            }
            else{
                printf("Problème précision = NULL\n");
            }
            //Récupérer IQ
            uint8_t indice_Iq = (uint8_t)list_car[0] - precis*16;
            printf("       indice IQ : %u\n", indice_Iq);

            //verifier si on a déjà lu une table avec cet indice Iq : 
            uint8_t etat_indice = verif_indice_DQT(indice_DQT_tab, indice_Iq);

            if(etat_indice == 255){
                liste_data_DQT[i].indice_Iq = indice_Iq;
                liste_data_DQT[i].precision = precision;
                liste_data_DQT[i].liste_DQT = lecture_cara(fichier, taille-3);
                indice_DQT_tab[i] = indice_Iq;
            }
            else{
                liste_data_DQT[etat_indice].indice_Iq = indice_Iq;
                liste_data_DQT[etat_indice].precision = precision;
                liste_data_DQT[etat_indice].liste_DQT = lecture_cara(fichier, taille-3);
            }
            free_lecture_cara(list_car);
        }
        *nb_tab_DQT += nb_DQT_courant;

        list_car=lecture_cara(fichier,2);
        if(((uint8_t)list_car[0] == 255) && ((uint8_t)list_car[1] == 219)){ //DQT
            encore_DQT = 1;
            free_lecture_cara(list_car);
        }
        else if(((uint8_t)list_car[0]==255) && ((uint8_t)list_car[1]==192)){ //SOF0
            encore_DQT = 0;
            free_lecture_cara(list_car);
            break;
        }
        else{
            printf("ERREUR: ni DQT, ni SOF0\n");
        }
    }
    return liste_data_DQT;
}


void free_all_SOF0(struct Start_Of_Frame liste){
    free(liste.tab_Ic);
    free(liste.tab_echant_hor);
    free(liste.tab_echant_vert);
    free(liste.tab_Iq);
}

/*Fonction qui vérifie et lit les informations de SOF0
entrée : le fichier à lire, un pointeur vers la précision / largeur / hauteur / nb_comp
sortie : struct SOF0
*/
struct Start_Of_Frame verif_SOF0(FILE* fichier, uint8_t* precision, uint32_t* largeur, uint32_t* hauteur, uint32_t* nb_comp){
    struct Start_Of_Frame N_SOF0;
    printf("[SOF0] ");
    char* list_car = lecture_cara(fichier, 2);
    uint32_t taille = (uint8_t)list_car[0]*16*16+(uint8_t)list_car[1];
    printf("length : %u\n", taille);
    free_lecture_cara(list_car);
    list_car = lecture_cara(fichier, 1);
    *precision = list_car[0];
    free(list_car);
    printf("       precision : %u\n", *precision);
    char* tab_hauteur=lecture_cara(fichier, 2);
    *hauteur = (uint8_t)tab_hauteur[0]*16*16+(uint8_t)tab_hauteur[1];
    free(tab_hauteur);
    printf("       hauteur : %u\n", *hauteur);
    char* tab_largeur = lecture_cara(fichier, 2);
    *largeur = (uint8_t)tab_largeur[0]*16*16+(uint8_t)tab_largeur[1];
    printf("       largeur : %u\n", *largeur);
    free(tab_largeur);
    char* list_car_N = lecture_cara(fichier,1);
    *nb_comp = (uint8_t)list_car_N[0];
    printf("       N : %u\n", *nb_comp);

    N_SOF0.tab_Ic = malloc(*nb_comp * sizeof(uint8_t));
    N_SOF0.tab_echant_hor = malloc(*nb_comp * sizeof(uint8_t));
    N_SOF0.tab_echant_vert = malloc(*nb_comp * sizeof(uint8_t));
    N_SOF0.tab_Iq = malloc(*nb_comp * sizeof(uint8_t));

    char* tab_affichage[] = {"Y", "Cb", "Cr", "rien"};
    for(uint32_t i = 0; i<*nb_comp; i++){
        printf("       --- %s ---\n", tab_affichage[i]);
        list_car = lecture_cara(fichier, 1);
        N_SOF0.tab_Ic[i] = (uint8_t)list_car[0];
        printf("            Ic[%u] : %u\n", i, N_SOF0.tab_Ic[i]);
        free_lecture_cara(list_car);
        list_car = lecture_cara(fichier, 1);
        //Récupérer IhDC
        N_SOF0.tab_echant_hor[i] = ((uint8_t)list_car[0]/16);
        printf("            fact échant horizontal[%u] : %u\n", i, N_SOF0.tab_echant_hor[i]);
        //récupérer facteur échant vertical
        N_SOF0.tab_echant_vert[i] = (uint8_t)list_car[0] - N_SOF0.tab_echant_hor[i]*16;
        free_lecture_cara(list_car);
        printf("            fact echant vertical[%u] : %u\n", i, N_SOF0.tab_echant_vert[i]);
        list_car = lecture_cara(fichier, 1);
        N_SOF0.tab_Iq[i] = (uint8_t)list_car[0];
        free_lecture_cara(list_car);
        printf("            Iq[%u] = %u\n", i, N_SOF0.tab_Iq[i]);
    }
    free(list_car_N);
    
    return N_SOF0;
}



void free_all_DHT(struct Huffman* liste, int taille){
    for(int i = 0; i<taille; i++){
        free(liste[i].tableau);
    }
    free(liste);
}

/*
Fonction vérifiant que on a bien une table DHT
entrée : le fichier à lire et traiter
sortie : un tableau de structure huffman de taille 8
*/
struct Huffman* verif_DHT(FILE* fichier, int* taille_dht){
    struct Huffman* tableau = malloc(sizeof(struct Huffman));
    while(1){
        printf("[DHT] ");
        char* list_car = lecture_cara(fichier, 2);
        uint32_t taille = ((uint8_t)list_car[0]*(16*16)) + (uint8_t)list_car[1];
        printf(" length : %u\n", taille);
        free_lecture_cara(list_car);
        //verif de DC ou AC et erreur
        //*type = 1 ou 0
        //*erreur = 0, 1, 2 ou 3
        list_car = lecture_cara(fichier, 1);
        uint8_t test_type = ((uint8_t)list_car[0]/16);
        uint8_t test_indice = (uint8_t)list_car[0] - test_type*16;
        free_lecture_cara(list_car);
        if (test_type == 0 || test_type == 1){
            //table huffman
            //construction de la structure de la table
            struct Huffman huff;
            huff.tableau = lecture_cara(fichier, taille - 3);
            huff.type = test_type;
            printf("       huff type[%i] : %u", *taille_dht, huff.type);
            if(huff.type == 0){
                printf(" (DC)\n");
            }
            else{
                printf(" (AC)\n");
            }
            huff.indice = test_indice;
            printf("       huff indice[%i] : %u\n", *taille_dht, huff.indice);
            //printf("       huff tableau[%i] : ", *taille_dht);
            // for(uint8_t j=0; j<taille-3; j++){
            //     printf(" %x", (uint8_t)huff.tableau[j]);
            // }
            //printf("\n");
            tableau[*taille_dht] = huff;
        } else {
            printf("Erreur type DHT\n");
            return NULL;
        }
        
        list_car = lecture_cara(fichier, 2);
        if (((uint8_t)list_car[0] == 255) && ((uint8_t)list_car[1] == 196)){ // DHT
            *taille_dht = *taille_dht+1;
            tableau = realloc(tableau, (*taille_dht+1)*sizeof(struct Huffman));
            free_lecture_cara(list_car);
        } else if(((uint8_t)list_car[0] == 255) && ((uint8_t)list_car[1] == 218)){ // SOS
            free_lecture_cara(list_car);
            break;
        } else{
            free_lecture_cara(list_car);
            return NULL;
        }
    }
    return tableau;
}


void free_all_SOS(struct Start_Of_Scan N_SOS){
    free(N_SOS.tab_Ic);
    free(N_SOS.tab_Ihdc);
    free(N_SOS.tab_Ihac);
}



/*
Fonction qui vérifie et lit  les infos de SOS
entrée  : le fichier à lire
sortie : les infos à rendre
*/
struct Start_Of_Scan verif_SOS(FILE* fichier, uint8_t* Ss_SOS, uint8_t* Se_SOS, uint8_t* Ah_SOS, uint8_t* Al_SOS){
    struct Start_Of_Scan N_SOS;
    printf("[SOS] ");
    char* list_car = lecture_cara(fichier, 2);
    uint32_t taille = ((uint8_t)list_car[0]*(16*16)) + (uint8_t)list_car[1];
    printf(" length : %u\n", taille);
    free_lecture_cara(list_car);
    list_car = lecture_cara(fichier, 1);
    uint8_t nbr_comp = (uint8_t)list_car[0];
    printf("       N : %u\n", nbr_comp);
    free_lecture_cara(list_car);
    if((uint32_t)2*nbr_comp+6 == taille){
        N_SOS.tab_Ic = malloc(nbr_comp * sizeof(uint8_t));
        N_SOS.tab_Ihdc = malloc(nbr_comp * sizeof(uint8_t));
        N_SOS.tab_Ihac = malloc(nbr_comp * sizeof(uint8_t));

        char* tab_affichage[] = {"Y", "Cb", "Cr"};
        for(uint8_t i=0; i<nbr_comp; i++){
            printf("       --- %s ---\n", tab_affichage[i]);
            list_car = lecture_cara(fichier, 1);
            N_SOS.tab_Ic[i] = (uint8_t)list_car[0];
            printf("            Ic[%u] : %u\n", i, N_SOS.tab_Ic[i]);
            free_lecture_cara(list_car);
            list_car = lecture_cara(fichier, 1);
            //Récupérer IhDC
            N_SOS.tab_Ihdc[i] = ((uint8_t)list_car[0]/16);
            printf("            IhDC[%u] : %u\n", i, N_SOS.tab_Ihdc[i]);
            //récupérer IhAC
            N_SOS.tab_Ihac[i] = (uint8_t)list_car[0] - N_SOS.tab_Ihdc[i]*16;
            printf("            IhAC[%u] : %u\n", i, N_SOS.tab_Ihac[i]);
            free_lecture_cara(list_car);
        }
        list_car = lecture_cara(fichier, 3);
        *Ss_SOS = (uint8_t)list_car[0];
        printf("       Ss : %u\n", *Ss_SOS);
        *Se_SOS = (uint8_t)list_car[1];
        printf("       Se : %u\n", *Se_SOS);
        *Ah_SOS = ((uint8_t)list_car[2]/16);
        printf("       Ah : %u\n", *Ss_SOS);
        *Al_SOS = (uint8_t)list_car[2] - (*Ah_SOS)*16;
        printf("       Al : %u\n", *Ss_SOS);
        free_lecture_cara(list_car);
    }
    else{
        printf("Problème de taille avec N !!\n");
    }
    return N_SOS;
}


/*Fonction qui récupère le bitstream : le codage brut de l'image, qui sera ensuité décodé pour obtenir une image.ppm*/
struct BitStream recupere_bitstream(FILE* fichier){
    //printf("\n[BitStream] : \n");
    struct BitStream info_bitstream;
    int taille_max = 100;
    int index = 0;
    info_bitstream.list_bitstream  = malloc(taille_max*sizeof(char));
    char cara_1;
    char cara_2;
    cara_1 = fgetc(fichier);
    int sortie = 0;

    while(sortie == 0){
        if((uint8_t)cara_1==255){
            cara_2 = fgetc(fichier);
            if((uint8_t)cara_2==217){
                sortie = 1;
                break;
            }
        }
        if(index >= taille_max){
            taille_max += 1;
            info_bitstream.list_bitstream = realloc(info_bitstream.list_bitstream , taille_max * sizeof(char));
        }
        info_bitstream.list_bitstream[index] = cara_1;
        if((uint8_t)cara_1 == 255){
            cara_1 = fgetc(fichier);
        }
        else{
            cara_1 = fgetc(fichier);
        }
        index += 1;
    }

    info_bitstream.size = index;
    //printf("\n");
    printf("[EOI]  verif fin ok\n");

    return info_bitstream;
}




/*Idée pour simplifier nos programmes et faire en sorte que ça marche 
peu importe le nombre et l'ordre des données
A voir si c'est mieux  \_(ツ)_/¯
*/
/*int main_test(int argc, char **argv){
    if (argc != 2) {
        fprintf(stderr, "Usage: %s fichier.jpeg\n", argv[0]);
    	return EXIT_FAILURE;
    }

    FILE* fichier = fopen(argv[1], "r");

    char* list_car = lecture_cara(fichier, 2);
    uint8_t ff = (uint8_t)list_car[0];
    if (ff == 255){
        do{
            list_car = lecture_cara(fichier, 2);
            switch((uint8_t)list_car[1]){
                case 224 : 
                    char* liste_APPO = verif_APPO(fichier);
                    break;
                case 254:
                    verif_COM(fichier);
                    break;
                case 219:
                    uint8_t precision_DQT = 0;
                    uint8_t indiceIQ_DQT = 0;
                    char* liste_DQT = verif_DQT(fichier, &precision_DQT, &indiceIQ_DQT);
                    break;
                case 192:
                    uint8_t precision = 8;
                    uint32_t largeur = 0; 
                    uint32_t hauteur = 0; 
                    struct Start_Of_Frame N_comp_SOF0 = verif_SOF0(fichier, &precision, &largeur ,&hauteur); // verif SOf0
                    break;
                case 196:
                    struct Huffman* table_huff = verif_DHT(fichier); 
                    break;
                case 218:
                    uint8_t Ss_SOS = 0;
                    uint8_t Se_SOS = 63;
                    uint8_t Ah_SOS = 0;
                    uint8_t Al_SOS = 0;
                    struct Start_Of_Scan N_comp_SOS = verif_SOS(fichier, &Ss_SOS, &Se_SOS, &Ah_SOS, &Al_SOS);       //verif SOS
                    break;
                default:
                    printf("Erreur lecture !!\n");
                    fclose(fichier);
                    return EXIT_FAILURE;
            }
        }while((uint8_t)list_car[1] != 217);
    }
    fclose(fichier);
    return EXIT_SUCCESS;
}*/