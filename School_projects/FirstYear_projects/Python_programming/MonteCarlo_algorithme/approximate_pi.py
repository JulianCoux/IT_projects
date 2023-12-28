#!/usr/bin/env python3

"""
Programme permettant de calculer une approximation de Pi
Calculer en utilisant la methode de Monte-Carlo
"""

import random
import sys
from math import sqrt

def genere_points(nb_points):
    """
    Calcul 10 approximations du nombre pi et renvoie "nb_point" coordonnées de points
    Points qui figureront dans le gif
    """
    ensemble_point = []
    nb_valid = 0
    approx_pi = []
    nb_img = 1

    for i in range(nb_points):
        pt_x = random.uniform(-1, 1)
        pt_y = random.uniform(-1, 1)

        longueur = sqrt(pt_x**2 + pt_y**2)
        if longueur <= 1:   #le point est a l'interieur du cercle => True
            ensemble_point.append((pt_x, pt_y, True))
            nb_valid += 1
        else:               #le point est a l'exterieur du cerlce => False
            ensemble_point.append((pt_x, pt_y, False))
        if i == int((nb_img*nb_points)/10):     #calculer pi tous les 10% du tirage des points
            nb_img += 1
            approx_pi.append(4 * (nb_valid / i))
            #print(approx_pi[-1]) #afficher la dernière valeur calculee
    approx_pi.append(4 * (nb_valid / nb_points))
    return ensemble_point, approx_pi


if __name__ == "__main__":
    if len(sys.argv) != 2:
        raise ValueError("Erreur ! Il manque des arguments")

    ensemble, pi = genere_points(int(sys.argv[1]))
    print(pi[-1])
