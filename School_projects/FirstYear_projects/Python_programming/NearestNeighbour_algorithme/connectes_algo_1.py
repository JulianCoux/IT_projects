#!/usr/bin/env python3
"""
compute sizes of all connected components.
sort and display.
"""

from sys import argv
from sys import argv, setrecursionlimit

from geo.point import Point
from geo.tycat import tycat
from geo.segment import Segment

"""
Regarder méthode clustering avec KMeans
"""


def load_instance(filename):
    """
    loads .pts file.
    returns distance limit and points.
    """
    with open(filename, "r") as instance_file:
        lines = iter(instance_file)
        distance = float(next(lines))   #distance minimale pour connecter 2 points
        points = [Point([float(f) for f in l.split(",")]) for l in lines]

    return distance, points


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


def main():
    """
    ne pas modifier: on charge une instance et on affiche les tailles
    """
    setrecursionlimit(50000)
    for instance in argv[1:]:
        distance, points = load_instance(instance)
        print_components_sizes(distance, points)


main()
