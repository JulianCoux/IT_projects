#!/usr/bin/env python

import random
import time
from sys import argv, setrecursionlimit

from geo.point import Point
from geo.tycat import tycat
from geo.segment import Segment
from matplotlib import pyplot as plt


#----------------------- Aymeric----------------------- #
def print_components_sizes(distance, points):
    """
    affichage des tailles triees de chaque composante
    """
    components_sizes = liste_tailles(distance, points)
    components_sizes.sort(reverse = True)
    print(components_sizes)
    return

def nbr_points_proche(distance, points) :
    """
    Retourne le nombre de points de la composante auquel appartient le 
    premier point de points
    """
    points_proches = [points.pop(0)]
    nbr_points_composante = 1
    while points_proches != [] :
        centre = points_proches.pop()
        indice_ajout = 0
        for i in range (len(points)) :
            if centre.distance_to(points[i - indice_ajout]) <= distance :
                points_proches.append(points.pop(i - indice_ajout))
                indice_ajout += 1
                nbr_points_composante += 1
    return nbr_points_composante

def liste_tailles(distance, points):
    liste_tailles = []
    while points != [] :
        liste_tailles.append(nbr_points_proche(distance, points))
    return liste_tailles



def genere_pts(nb_points):
    points = []
    for _ in range(nb_points):
        pt_x = random.uniform(0,1)
        pt_y = random.uniform(0,1)
        points.append(Point([pt_x, pt_y]))
    return points


def main():
    if len(argv) != 3:
        raise ValueError("Erreur ! arguments : nb_points, distance")
    
    """ 
    On passe en paramètre lors de l'appel de la fonction : 
    argv[1] => nombre de points max à créer
    argv[2] => distance max entre les points
    """
    
    graph_x = []  #le nombre de points  
    graph_y = []  #le temps d'exécution
    for nb_pts in range(20, int(argv[1])+1, 20):
        points = genere_pts(nb_pts)
        tp1 = time.time()
        print_components_sizes(float(argv[2]), points)
        tps = time.time() - tp1
        print(f"test {nb_pts} points en {tps} secondes")
        graph_y.append(tps)
        graph_x.append(nb_pts)


    plt.title(f"Graphe {nb_pts} points")
    plt.xlabel("Nombre de points")
    plt.ylabel("Temps recherche voisins (secondes)")
    plt.plot(graph_x, graph_y)
    plt.plot(graph_x, graph_y, 'k+')
    plt.show()


main()
