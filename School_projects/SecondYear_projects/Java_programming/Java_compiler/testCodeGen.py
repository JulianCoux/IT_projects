import os
import re
from sys import argv

# Fonction renvoyant les résultats attendu pour l'étape C
def extraire_resultats(chemin_fichier):
    with open(chemin_fichier, 'r') as fichier:
        resultat_trouve = False
        resultats = []

        for ligne in fichier:
            if "// Resultats:" in ligne:
                resultat_trouve = True
            elif resultat_trouve:
                # Vérifier si la ligne commence par "//" et ne contient que des espaces après
                # C'est notre condition d'arrêt
                match = re.match(r'//\s*$', ligne)
                if match:
                    break
                # On récupère le tecte après //
                match = re.search(r'//\s*(.*)', ligne)
                if match:
                    resultat = match.group(1).strip()
                    resultats.append(resultat)

    return resultats


if __name__ == "__main__":
    # On récup le chemin vers notre fichier à tester
    chemin_fichier = argv[1]
    resultats_fichier = extraire_resultats(chemin_fichier)
    print(resultats_fichier)
