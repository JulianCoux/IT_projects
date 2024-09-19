#!/usr/bin/env python

import matplotlib.pyplot as plt
import numpy as np

def read_data(file_path):
    with open(file_path, 'r') as file:
        lines = file.readlines()
        data = [tuple(map(int, line.strip().split())) for line in lines]
    return data


def interpolate_data_temps(data, num_points):
    x = np.arange(len(data))
    x_new = np.linspace(0, len(data) - 1, num_points)
    y = [item[0] for item in data]
    y_new = np.interp(x_new, x, y)
    return x_new, y_new

def interpolate_data_noeuds(data, num_points):
    x = np.arange(len(data))
    x_new = np.linspace(0, len(data) - 1, num_points)
    y = [item[1] for item in data]
    y_new = np.interp(x_new, x, y)
    return x_new, y_new


def plot_graph_temps(data1, data2, title):
    num_points = max(len(data1), len(data2))
    x1, y1 = interpolate_data_temps(data1, num_points)
    x2, y2 = interpolate_data_temps(data2, num_points)

    plt.plot(x1, y1, color='red', label='joueur Rouge')
    plt.plot(x2, y2, color='blue', label='joueur Bleu')

    plt.xlabel('Numéro jeu')
    plt.ylabel('Temps (ms)')
    plt.title(title)
    plt.legend()
    plt.grid(True)
    plt.show()

def plot_graph_noeuds(data1, data2, title):
    num_points = max(len(data1), len(data2))
    x1, y1 = interpolate_data_noeuds(data1, num_points)
    x2, y2 = interpolate_data_noeuds(data2, num_points)

    plt.plot(x1, y1, color='red', label='joueur Rouge')
    plt.plot(x2, y2, color='blue', label='joueur Bleu')

    plt.xlabel('Numéro jeu')
    plt.ylabel('Nb noeuds')
    plt.title(title)
    plt.legend()
    plt.grid(True)
    plt.show()



def main():
    red_data = read_data('rouge.txt')
    blue_data = read_data('bleu.txt')

    if(len(red_data) == 0 or len(blue_data)==0):
        print("Aucune partie à exécuter...")
        return 1


    plot_graph_temps(red_data, blue_data, 'Temps de jeux')
    plot_graph_noeuds(red_data, blue_data, 'Nombre de noeuds')

    # Truncate the content of the files
    with open('rouge.txt', 'w') as file:
        file.truncate(0)
    with open('bleu.txt', 'w') as file:
        file.truncate(0)

if __name__ == "__main__":
    main()
