#!/usr/bin/env python3
import subprocess
import sys
import os

def run_test(test_name, input_file):
    test_script = f"src/test/script/launchers/{test_name}"

    try:
        subprocess.run([test_script, input_file], check=True)
    except subprocess.CalledProcessError as e:
        print(f"Erreur lors de l'exécution du test {test_name}: {e}")
        sys.exit(1)

if __name__ == "__main__":
    tests_to_run = ["test_lex", "test_synt", "test_context"]

    if len(sys.argv) < 2:
        print("Utilisation: python3 nom_du_script.py <fichier_de_test.deca>")
        sys.exit(1)

    input_file_name = sys.argv[1]

    for test_name in tests_to_run:
        print("\n" + test_name + "\n")
        run_test(test_name, input_file_name)
