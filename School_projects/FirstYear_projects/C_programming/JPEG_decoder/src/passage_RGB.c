#include <stdlib.h>
#include <stdio.h>
#include <stdint.h>


int taille_chaine(char* str) {
    int length = 0;
    while (*str != '.') {
        length++;
        str++;
    }
    return length;
}


char* modif_nom(int taille, char* nom, int nb_comp) {
    char* nouveau_nom = malloc((taille + 5)*sizeof(char));
    int i = 0;
    while (nom[i] != '.') {
        nouveau_nom[i] = nom[i];
        i++;
    }
    nouveau_nom[i] = '.';
    i += 1;
    nouveau_nom[i] = 'p';
    i += 1;
    if (nb_comp == 1){ // N&B
        nouveau_nom[i] = 'g';
    } else { //couleur
        nouveau_nom[i] = 'p';
    }
    i += 1;
    nouveau_nom[i] = 'm';
    i += 1;
    nouveau_nom[i] = '\0';
    return nouveau_nom;
}


uint8_t*** tri_matice(uint8_t*** matrice, uint32_t echantillonage_largeur, uint32_t echantillonage_hauteur, int nb_comp, int nb_mcu_largeur){
    uint8_t*** new_matrice = malloc(nb_comp*sizeof(uint8_t**));
    if(echantillonage_largeur == 2 && echantillonage_hauteur == 1){         //echantillonnage horizontale
        printf("horizontale\n");
        for(int i=0; i<nb_comp; i+=6){
            new_matrice[i] = matrice[i];
            new_matrice[i+1] = matrice[i+2];
            new_matrice[i+2] = matrice[i+4];
            new_matrice[i+3] = matrice[i+1];
            new_matrice[i+4] = matrice[i+3];
            new_matrice[i+5] = matrice[i+5];
        }
    }
    else if(echantillonage_largeur == 1 && echantillonage_hauteur == 2){    //echantillonnage verticale
        printf("verticale\n");
        int indice_i = 0;
        for(int i=0; i<nb_comp; i+=6){
            new_matrice[indice_i] = matrice[i];
            new_matrice[indice_i+1] = matrice[i+2];
            new_matrice[indice_i+2] = matrice[i+4];
            new_matrice[indice_i+nb_mcu_largeur*3] = matrice[i+1];
            new_matrice[indice_i+nb_mcu_largeur*3+1] = matrice[i+3];
            new_matrice[indice_i+nb_mcu_largeur*3+2] = matrice[i+5];
            indice_i += 3;
            if(indice_i%(nb_mcu_largeur*3) == 0){
                indice_i += nb_mcu_largeur*3;
            }
        }
    }
    else if(echantillonage_largeur*echantillonage_hauteur == 4){            //echantillonnage horizontale et verticale
        printf("horizontale et verticale\n");
        int indice_i = 0;
        for(int i=0; i<nb_comp; i+=12){
            new_matrice[indice_i] = matrice[i];
            new_matrice[indice_i+1] = matrice[i+4];
            new_matrice[indice_i+2] = matrice[i+8];
            new_matrice[indice_i+nb_mcu_largeur*3] = matrice[i+2];
            new_matrice[indice_i+nb_mcu_largeur*3+1] = matrice[i+6];
            new_matrice[indice_i+nb_mcu_largeur*3+2] = matrice[i+10];
            new_matrice[indice_i+3] = matrice[i+1];
            new_matrice[indice_i+4] = matrice[i+5];
            new_matrice[indice_i+5] = matrice[i+9];
            new_matrice[indice_i+nb_mcu_largeur*3+3] = matrice[i+3];
            new_matrice[indice_i+nb_mcu_largeur*3+4] = matrice[i+7];
            new_matrice[indice_i+nb_mcu_largeur*3+5] = matrice[i+11];
            indice_i += 6;
            if(indice_i%(nb_mcu_largeur*3) == 0){
                indice_i += nb_mcu_largeur*3;
            }
        }
    }
    else{
        free(new_matrice);
        return matrice;
    }
    return new_matrice;
}



void transfo_nb_MCU_1_tronc(int n_pixels_l, int n_pixels_h, int n_largeur, uint8_t*** matrice, char* nom_image){
    FILE *fp;
    fp = fopen(nom_image, "w");
    fputs("P2\n",fp);
    fprintf(fp,"%i %i %i\n",n_pixels_l, n_pixels_h, 255);

    int MCU_l_max = n_pixels_l/8;
    int reste_l = n_pixels_l - MCU_l_max*8;


    int MCU_h_max = n_pixels_h/8;
    int reste_h = n_pixels_h - MCU_h_max*8;   


    for (int ind_hauteur=0; ind_hauteur < MCU_h_max; ind_hauteur++){
        for (int y=0; y<8; y++){
            for (int ind_largeur=0; ind_largeur < MCU_l_max ; ind_largeur++){
                for (int x=0; x<8; x++){
                    int indice = ind_largeur+ind_hauteur*n_largeur;
                    fprintf(fp,"%u",matrice[indice][y][x]);
                    fputs("\n",fp);
                }
            }
            for (int x=0; x<reste_l; x++){
                int indice=MCU_l_max+ind_hauteur*n_largeur;
                fprintf(fp,"%u",matrice[indice][y][x]);
                fputs("\n",fp);
            }
        }
    }

    for (int y=0; y<reste_h; y++){
        for (int ind_largeur=0; ind_largeur < MCU_l_max ; ind_largeur++){
            for (int x=0; x<8; x++){
                int indice=ind_largeur+MCU_h_max*n_largeur;
                fprintf(fp,"%u",matrice[indice][y][x]);
                fputs("\n",fp);
            }
        }
        for (int x=0; x<reste_l; x++){
            int indice=MCU_l_max+MCU_h_max*n_largeur;
            fprintf(fp,"%u",matrice[indice][y][x]);
            fputs("\n",fp);
        }
    }
    fclose(fp);
}




void transfo_couleur_MCU_2(int h1, int v1, int n_pixels_l, int n_pixels_h, int n_largeur, uint8_t*** matrice, char* nom_image){
    FILE *fp = fopen(nom_image, "w");
    fputs("P3\n",fp);
    int largeur=n_pixels_l;
    int hauteur=n_pixels_h;
    
    fprintf(fp,"%i %i\n",largeur, hauteur);
    fputs("255\n",fp);

    int bloc_l_max = n_pixels_l/8;
    int reste_l = n_pixels_l - bloc_l_max*8;

    int bloc_h_max = n_pixels_h/8;
    int reste_h = n_pixels_h - bloc_h_max*8;   

    if(h1==2 && v1==1 && ((bloc_l_max%2==0 && reste_l!=0) || (bloc_l_max%2==1 && n_pixels_l%8==0))){
        n_largeur ++;
    }

    for (int ind_hauteur=0; ind_hauteur < bloc_h_max; ind_hauteur++){
        for (int y=0; y<8; y++){
            for (int ind_largeur=0; ind_largeur < bloc_l_max ; ind_largeur++){
                for (int x=0; x<8; x++){
                    float new_val_R1=matrice[ind_hauteur*3*n_largeur+ind_largeur*3][y][x] - 0.0009267*((matrice[(ind_hauteur*3*n_largeur)+(ind_largeur*3)+1][y][x])-128) + 1.4016868*((matrice[(ind_hauteur*3*n_largeur)+(ind_largeur*3)+2][y][x])-128);
                    float new_val_G1=matrice[ind_hauteur*3*n_largeur+ind_largeur*3][y][x] - 0.3436954*((matrice[(ind_hauteur*3*n_largeur)+(ind_largeur*3)+1][y][x])-128) - 0.7141690*((matrice[(ind_hauteur*3*n_largeur)+(ind_largeur*3)+2][y][x])-128);
                    float new_val_B1=matrice[ind_hauteur*3*n_largeur+ind_largeur*3][y][x] + 1.7721604*((matrice[(ind_hauteur*3*n_largeur)+(ind_largeur*3)+1][y][x])-128) + 0.0009902*((matrice[(ind_hauteur*3*n_largeur)+(ind_largeur*3)+2][y][x])-128);  
                    if (new_val_R1 > 255){
                        new_val_R1 = 255;
                    }
                    if (new_val_R1 < 0){
                        new_val_R1 = 0;
                    }
                    if (new_val_G1 > 255){
                        new_val_G1 = 255;
                    }
                    if (new_val_G1 < 0){
                        new_val_G1 = 0;
                    }
                    if (new_val_B1 > 255){
                        new_val_B1 = 255;
                    }
                    if (new_val_B1 < 0){
                        new_val_B1 = 0;
                    }
                    uint8_t new_val_R = (uint8_t) new_val_R1;
                    uint8_t new_val_G = (uint8_t) new_val_G1;
                    uint8_t new_val_B = (uint8_t) new_val_B1;

                    fprintf(fp,"%u ",new_val_R);
                    fprintf(fp,"%u ",new_val_G);
                    fprintf(fp,"%u ",new_val_B);

                    fputs("\n",fp);
                    
                }
            }
            for (int x=0; x<reste_l; x++){
                float new_val_R1=matrice[ind_hauteur*3*n_largeur+bloc_l_max*3][y][x] - 0.0009267*(matrice[ind_hauteur*3*n_largeur+bloc_l_max*3+1][y][x]-128) + 1.4016868*(matrice[ind_hauteur*3*n_largeur+bloc_l_max*3+2][y][x]-128);
                float new_val_G1=matrice[ind_hauteur*3*n_largeur+bloc_l_max*3][y][x] - 0.3436954*(matrice[ind_hauteur*3*n_largeur+bloc_l_max*3+1][y][x]-128) - 0.7141690*(matrice[ind_hauteur*3*n_largeur+bloc_l_max*3+2][y][x]-128);
                float new_val_B1=matrice[ind_hauteur*3*n_largeur+bloc_l_max*3][y][x] + 1.7721604*(matrice[ind_hauteur*3*n_largeur+bloc_l_max*3+1][y][x]-128) + 0.0009902*(matrice[ind_hauteur*3*n_largeur+bloc_l_max*3+2][y][x]-128);  
                if (new_val_R1 > 255){
                    new_val_R1 = 255;
                }
                if (new_val_R1 < 0){
                    new_val_R1 = 0;
                }
                if (new_val_G1 > 255){
                    new_val_G1 = 255;
                }
                if (new_val_G1 < 0){
                    new_val_G1 = 0;
                }
                if (new_val_B1 > 255){
                    new_val_B1 = 255;
                }
                if (new_val_B1 < 0){
                    new_val_B1 = 0;
                }
                uint8_t new_val_R = (uint8_t) new_val_R1;
                uint8_t new_val_G = (uint8_t) new_val_G1;
                uint8_t new_val_B = (uint8_t) new_val_B1;

                fprintf(fp,"%u ",new_val_R);
                fprintf(fp,"%u ",new_val_G);
                fprintf(fp,"%u ",new_val_B);

                fputs("\n",fp);

            }
        }
    }

    for (int y=0; y<reste_h; y++){
        for (int ind_largeur=0; ind_largeur < bloc_l_max ; ind_largeur++){
            for (int x=0; x<8; x++){
                float new_val_R1=matrice[bloc_h_max*3*n_largeur+ind_largeur*3][y][x] - 0.0009267*(matrice[bloc_h_max*3*n_largeur+ind_largeur*3+1][y][x]-128) + 1.4016868*(matrice[bloc_h_max*3*n_largeur+ind_largeur*3+2][y][x]-128);
                float new_val_G1=matrice[bloc_h_max*3*n_largeur+ind_largeur*3][y][x] - 0.3436954*(matrice[bloc_h_max*3*n_largeur+ind_largeur*3+1][y][x]-128) - 0.7141690*(matrice[bloc_h_max*3*n_largeur+ind_largeur*3+2][y][x]-128);
                float new_val_B1=matrice[bloc_h_max*3*n_largeur+ind_largeur*3][y][x] + 1.7721604*(matrice[bloc_h_max*3*n_largeur+ind_largeur*3+1][y][x]-128) + 0.0009902*(matrice[bloc_h_max*3*n_largeur+ind_largeur*3+2][y][x]-128);  
                if (new_val_R1 > 255){
                    new_val_R1 = 255;
                }
                if (new_val_R1 < 0){
                    new_val_R1 = 0;
                }
                if (new_val_G1 > 255){
                    new_val_G1 = 255;
                }
                if (new_val_G1 < 0){
                    new_val_G1 = 0;
                }
                if (new_val_B1 > 255){
                    new_val_B1 = 255;
                }
                if (new_val_B1 < 0){
                    new_val_B1 = 0;
                }
                uint8_t new_val_R = (uint8_t) new_val_R1;
                uint8_t new_val_G = (uint8_t) new_val_G1;
                uint8_t new_val_B = (uint8_t) new_val_B1;
                fprintf(fp,"%u ",new_val_R);
                fprintf(fp,"%u ",new_val_G);
                fprintf(fp,"%u ",new_val_B);
                fputs("\n",fp);
            }
        }
        for (int x=0; x<reste_l; x++){
                float new_val_R1=matrice[bloc_h_max*3*n_largeur+bloc_l_max*3][y][x] - 0.0009267*(matrice[bloc_h_max*3*n_largeur+bloc_l_max*3+1][y][x]-128) + 1.4016868*(matrice[bloc_h_max*3*n_largeur+bloc_l_max*3+2][y][x]-128);
                float new_val_G1=matrice[bloc_h_max*3*n_largeur+bloc_l_max*3][y][x] - 0.3436954*(matrice[bloc_h_max*3*n_largeur+bloc_l_max*3+1][y][x]-128) - 0.7141690*(matrice[bloc_h_max*3*n_largeur+bloc_l_max*3+2][y][x]-128);
                float new_val_B1=matrice[bloc_h_max*3*n_largeur+bloc_l_max*3][y][x] + 1.7721604*(matrice[bloc_h_max*3*n_largeur+bloc_l_max*3+1][y][x]-128) + 0.0009902*(matrice[bloc_h_max*3*n_largeur+bloc_l_max*3+2][y][x]-128);  
                if (new_val_R1 > 255){
                    new_val_R1 = 255;
                }
                if (new_val_R1 < 0){
                    new_val_R1 = 0;
                }
                if (new_val_G1 > 255){
                    new_val_G1 = 255;
                }
                if (new_val_G1 < 0){
                    new_val_G1 = 0;
                }
                if (new_val_B1 > 255){
                    new_val_B1 = 255;
                }
                if (new_val_B1 < 0){
                    new_val_B1 = 0;
                }
                uint8_t new_val_R = (uint8_t) new_val_R1;
                uint8_t new_val_G = (uint8_t) new_val_G1;
                uint8_t new_val_B = (uint8_t) new_val_B1;
                fprintf(fp,"%u ",new_val_R);
                fprintf(fp,"%u ",new_val_G);
                fprintf(fp,"%u ",new_val_B);
                fputs("\n",fp);
            }
    }
}