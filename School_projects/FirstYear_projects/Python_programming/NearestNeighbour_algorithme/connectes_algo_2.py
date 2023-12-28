#!/usr/bin/env python3

from timeit import timeit
from sys import argv

from geo.point import Point


def load_instance(filename):
    """
    loads .pts file.
    returns distance limit and points.
    """
    with open(filename, "r") as instance_file:
        lines = iter(instance_file)
        distance = float(next(lines))
        points = [Point([float(f) for f in l.split(",")]) for l in lines]

    return distance, points


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


def main():
    """
    ne pas modifier: on charge une instance et on affiche les tailles
    """
    for instance in argv[1:]:
        distance, points = load_instance(instance)
        print_components_sizes(distance, points)


main()
