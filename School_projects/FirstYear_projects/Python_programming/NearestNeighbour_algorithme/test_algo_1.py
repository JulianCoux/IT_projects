#!/usr/bin/env python

import random
import time
from sys import argv, setrecursionlimit

from geo.point import Point
from geo.tycat import tycat
from geo.segment import Segment
from matplotlib import pyplot as plt


def print_components_sizes(distance, points):
    """
    affichage des tailles triees de chaque composante
    """
    segments = []
    dict_voisins = {}
    for i in range(len(points)):
        if points[i] not in dict_voisins:
                dict_voisins[points[i]] = []
        for j in range(i+1, len(points)):
            if points[j] not in dict_voisins:
                dict_voisins[points[j]] = []
            distance_pt = points[i].distance_to(points[j])
            if distance_pt <=  distance and i != j:
                segments.append(Segment([points[i], points[j]]))
                dict_voisins[points[i]].append(points[j])
                dict_voisins[points[j]].append(points[i])

    #tycat(points, segments)

    """
    Compter le nombre de points dans chaque ensemble
    Essayer par récurrence dans la fonction recherche_voisins
    La liste points_vu sert à garder en mémoire tous les points 
    Pour lesquels on sait qu'ils font déjà parti d'un ensemble
    """
    points_vu = {}
    liste_blocs = []
    for value in points:
        if value not in points_vu:
            points_vu, nb_points = recherche_voisins(value, dict_voisins, points_vu, 1)
            liste_blocs.append(nb_points)
    liste_blocs.sort(reverse=True)
    print(liste_blocs)


def recherche_voisins(point, dict_voisins, points_vu, nb_points):
    """
    Parcourir les ensembles de proche en proche
    Ajouter le point vu dans points_vu
    incrémenter nb_points
    si il n'y a plus de voisins qu'on a pas déjà vu => retourner les valeurs
    """

    points_vu[point] = True
    
    for value in dict_voisins[point]:
        if value not in points_vu:
            nb_points += 1
            points_vu, nb_points = recherche_voisins(value, dict_voisins, points_vu, nb_points)
    return points_vu, nb_points



def genere_pts(nb_points):
    points = []
    for _ in range(nb_points):
        pt_x = random.uniform(0,1)
        pt_y = random.uniform(0,1)
        points.append(Point([pt_x, pt_y]))
    return points



def main():
    setrecursionlimit(20000)
    if len(argv) != 3:
        raise ValueError("Erreur ! arguments : nb_points, distance")
    
    """ 
    On passe en paramètre lors de l'appel de la fonction : 
    argv[1] => nombre de points max à créer
    argv[2] => distance max entre les points
    """
    
    graph_x = []  #le nombre de points  
    graph_y = []  #le temps d'exécution
    for nb_pts in range(0, int(argv[1])+1, 500):
        points = genere_pts(nb_pts)
        tp1 = time.time()
        print_components_sizes(0.05, points)
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
