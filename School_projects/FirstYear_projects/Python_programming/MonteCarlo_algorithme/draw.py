#!/usr/bin/env python3

"""
Programme permettant d'afficher l'approximation du nombre Pi
Doit afficher tous les points renvoyes par approximate_pi.py
"""
import sys
import subprocess
import approximate_pi


def modif_cod_points(point):
    """
    On passe sur un graphique (0,0);(2,2)
    """
    point_x = point[0] + 1
    point_y = abs(2 - (point[1] + 1))
    new_x = int((point_x * taille_ppm)/2)
    new_y = int((point_y * taille_ppm)/2)
    return new_x, new_y


def init_7_seg():
    """Fonction pour initialiser les valeur de l'afficheur 7 segments
    Fonctionnement comme un radio réveil
    num = (a, b, c, d, e, f, g)
    a,b,... étant les segments (horizontaux ou verticaux) du nombre sur
    un afficheur 7 segments
    """
    num_0 = (1,1,1,1,1,1,0)
    num_1 = (0,1,1,0,0,0,0)
    num_2 = (1,1,0,1,1,0,1)
    num_3 = (1,1,1,1,0,0,1)
    num_4 = (0,1,1,0,0,1,1)
    num_5 = (1,0,1,1,0,1,1)
    num_6 = (1,0,1,1,1,1,1)
    num_7 = (1,1,1,0,0,0,0)
    num_8 = (1,1,1,1,1,1,1)
    num_9 = (1,1,1,1,0,1,1)
    tab_7_seg = [num_0, num_1, num_2, num_3, num_4, num_5, num_6, num_7, num_8, num_9]
    return tab_7_seg


def aff_horiz(num, point_depart, dict, taille_seg, coef):
    """
    Fonction pour afficher les points horizontaux de notre nombre
    """
    if num[0]:  #a
        for j in range(coef):
            for i in range(taille_seg+coef):
                dict[(point_depart[0]+i, point_depart[1]+j)] = "noir"
                #le x change mais pas le y
    if num[6]:  #g
        for j in range(coef):
            for i in range(taille_seg+coef):
                dict[(point_depart[0]+i, point_depart[1]+taille_seg+j)] = "noir"
                #le x change mais pas le y
    if num[3]:  #d
        for j in range(coef):
            for i in range(taille_seg+coef):
                dict[(point_depart[0]+i, point_depart[1]+2*taille_seg+j)] = "noir"
                #le x change mais pas le y
    return dict


def aff_verti(num, point_depart, dict, taille_seg, coef):
    """
    Fonction pour ajouter les points verticaux de notre nombre
    """
    if num[5]:  #f
        for j in range(coef):
            for i in range(taille_seg+coef):
                dict[(point_depart[0]+j, point_depart[1]+i)] = "noir"
                #le y change mais pas le x
    if num[4]:  #e
        for j in range(coef):
            for i in range(taille_seg+coef):
                dict[(point_depart[0]+j, point_depart[1]+taille_seg+i)] = "noir"
                #le y change mais pas le x
    if num[1]:  #b
        for j in range(coef):
            for i in range(taille_seg+coef):
                dict[(point_depart[0]+taille_seg+j, point_depart[1]+i)] = "noir"
                #le y change mais pas le x
    if num[2]:  #c
        for j in range(coef):
            for i in range(taille_seg+coef):
                dict[(point_depart[0]+taille_seg+j, point_depart[1]+taille_seg+i)] = "noir"
                #le y change mais pas le x
    return dict


def aff_point(point_depart, dict, taille_seg, coef):
    """
    Fonction pour afficher le point séparant les chiffres
    """
    for j in range(coef):
        for i in range(coef):
            dict[(point_depart[0]+i, point_depart[1]+2*taille_seg+coef-j)] = "noir"
            #le x change mais pas le y
    return dict



def genere_ppm_file(nb_points, taille_ppm, nb_dec):
    """Genere une image au format PPM
    Points a l'interieur du cercle en Vert et ceux a l'exterieur en Rouge
    """

    ensemble_point, approx_pi = approximate_pi.genere_points(nb_points)
    """approx_pi est un tableau qui contient les 10 valeurs de pi
    ensemble_point est un tableau a 2 dimensions contenant tous les points et
    (True ou False) pour indiquer si il est dans ou hors du cercle"""


    for i in range(len(approx_pi)):     #ecrire pi avec le nombre de décimales demandés
        approx_pi[i] = f'{approx_pi[i]:.{nb_dec}f}'
        #approx_pi[i] = str(approx_pi[i])
        #approx_pi[i] = approx_pi[i][:nb_dec+2]

    #print("pi = ", approx_pi[-1])

    ensemble_point_dict = {}
    ecrire_pi_dict = {}
    nb_img = 1
    for i in range(len(ensemble_point)):
        """
        Creation du dictionnaire contenant tous les points
        cle = (x,y), valeur = True/False => point dans le cercle ?
        """
        ecrire_pi_dict.clear()      #Réinitialiser le dictionnaire pi à chaque boucle
        x, y = modif_cod_points(ensemble_point[i])
        ensemble_point_dict[(x, y)] = ensemble_point[i][2]
        #Initialisation du dictionnaire contenant les points du calcul de pi

        if i == int((nb_img*nb_points)/10)-1:
            pi_str = approx_pi[nb_img - 1]

            taille_seg = int(0.05*taille_ppm)
            coef = int(0.01*taille_ppm)
            num_7_seg = init_7_seg()
            nb_first = pi_str[0]
            nb_reste = pi_str[2:]
            num_aff = num_7_seg[int(nb_first)]

            pt_start = (int(0.3*taille_ppm), int(0.45*taille_ppm))

            #ajout du premier chiffre
            ecrire_pi_dict = aff_horiz(num_aff, pt_start, ecrire_pi_dict, taille_seg, coef)
            ecrire_pi_dict = aff_verti(num_aff, pt_start, ecrire_pi_dict, taille_seg, coef)

            #ajout de la virgule
            pt_start = (pt_start[0]+taille_seg+int(0.02*taille_ppm), pt_start[1])
            ecrire_pi_dict = aff_point(pt_start, ecrire_pi_dict, taille_seg, coef)

            pt_start = (pt_start[0]+int(0.02*taille_ppm), pt_start[1])

            #ajout des chiffres après la virgule
            for nombre in nb_reste:
                num_aff = num_7_seg[int(nombre)]
                ecrire_pi_dict = aff_horiz(num_aff, pt_start, ecrire_pi_dict, taille_seg, coef)
                ecrire_pi_dict = aff_verti(num_aff, pt_start, ecrire_pi_dict, taille_seg, coef)

                pt_start = (pt_start[0]+taille_seg+(int(0.025*taille_ppm)), pt_start[1])


            """Création de toutes les images .ppm
            Vérification de si des points de l'image appartiennent a un des dictionnaire
            Ecrire dans le fichier 000 (noir) si le point sert à écrire pi
            001 (bleu) si le point est dans le cerce
            101 (rose) si le point est hors du cercle
            111 (blanc) si le pixel n'a pas de points
            """
            fichier = open(f"img{nb_img - 1}_{pi_str[0]}-{pi_str[2:]}.ppm", "w")
            #fichier.write("P3\n")   #P2 pour gagner en mémoire
            #fichier.write(f'{taille_ppm} {taille_ppm} 1\n')
            fichier.write("P2\n")
            fichier.write(f'{taille_ppm} {taille_ppm} \n9\n')
            for i in range(taille_ppm):
                for j in range(taille_ppm):
                    if (j,i) in ecrire_pi_dict:
                        #fichier.write("0 0 0  ")
                        fichier.write("0 ")
                    elif (j,i) in ensemble_point_dict:
                        if ensemble_point_dict[(j,i)]:
                            #fichier.write("0 0 1  ")
                            fichier.write("5 ")
                        else:
                            #fichier.write("1 0 1  ")
                            fichier.write("1 ")
                    else:
                        #fichier.write("1 1 1  ")
                        fichier.write("9 ")
                fichier.write("\n")

            if nb_img == 10:
                fichier.write("\n")
                fichier.write("FINNNNNN")
            nb_img += 1

    fichier.close()
    #probleme : ne pas créer trop vite le gif sinon l'image 9 n'est pas encore crée
    #Le problème était que le fichier était encore ouvert, ce qui empêchait sa convertion

    subprocess.run(["convert", "-delay", "100", "img*.ppm", "pi.gif"])


if __name__ == "__main__":
    if len(sys.argv) != 4:
        raise ValueError("Erreur ! Il manque des arguments")

    taille_ppm = int(sys.argv[1])
    nb_points = int(sys.argv[2])
    nb_dec = int(sys.argv[3])

    if taille_ppm < 100:
        raise ValueError("Erreur ! La taille de l'image doit être supérieur ou égal à 100")
    if nb_points <= 100:
        raise ValueError("Erreur ! Le nombre de points doit être supérieur à 100")
    if 1 < nb_dec > 5:
        raise ValueError("Erreur ! Le nombre de chiffres après la virgule entre 1 et 5")

    genere_ppm_file(nb_points, taille_ppm, nb_dec)
    
