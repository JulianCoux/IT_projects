#!/usr/bin/env python3
from pickle import FALSE, TRUE
import subprocess
import sys
import os

def normalize_output(output):
    return output.replace(' ', '').replace('\t', '')

def run_test(input_file):
    test_script = "src/test/script/launchers/test_decompile"

    try:
        # Affiche le contenu du fichier pris en paramètre
        with open(input_file, 'r') as file:
            file_content = file.read()
            print("\nFichier deca :\n" + file_content)

        # Exécute le sous-processus et récupère la sortie formatée
        subprocess_output = subprocess.check_output([test_script, input_file], stderr=subprocess.STDOUT)

        print("\nSortie decompile :\n" + subprocess_output.decode("utf-8"))

        
    except subprocess.CalledProcessError as e:
        print(f"Erreur lors de l'exécution du test test_decompile: {e}")
        sys.exit(1)

if __name__ == "__main__":
    if len(sys.argv) < 2:
        print("Utilisation: python3 nom_du_script.py")
        sys.exit(1)

    input_file_name = sys.argv[1]

    run_test(input_file_name)