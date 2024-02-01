#!/usr/bin/env python3
from pickle import FALSE, TRUE
import subprocess
import sys
import os

def read_file(file_path):
    with open(file_path, 'r') as file:
        return file.read()

def find_expected_tokens_comment(source_code):
    lines = source_code.split('\n')
    in_comment_block = False
    expected_tokens_lines = []
    for line in lines:
        line = line.strip()
        if line.startswith('// Tokens attendus:'):
            in_comment_block = True
        else :
            break
        if in_comment_block:
            expected_tokens_lines.append(line[len('// Tokens attendus:'):].strip())

    return '\n'.join(expected_tokens_lines)


def parse_expected_tokens(tokens_block):
    # Utiliser un ensemble pour stocker les débuts de lignes des tokens attendus
    expected_tokens_set = set(token.strip() for token in tokens_block.split(','))
    return expected_tokens_set

def main():
    import sys
    if len(sys.argv) < 2:
        print("Usage: python3 lexer_test.py <source_file>")
        sys.exit(1)
    try:
        test_script = "src/test/script/launchers/test_lex"
        input_file_name = sys.argv[1]
        source_code = read_file(input_file_name)
        expected_tokens_block = find_expected_tokens_comment(source_code)
       

        if expected_tokens_block:
            # Parse le bloc de commentaires pour obtenir les tokens attendus
            expected_tokens = parse_expected_tokens(expected_tokens_block)
            
            
        # Exécute le sous-processus et récupère la sortie formatée
        subprocess_output = subprocess.check_output([test_script, input_file_name], stderr=subprocess.STDOUT)
        output_lines = subprocess_output.decode("utf-8").split('\n')

        # Vérifie que chaque début de ligne est présent dans les débuts de lignes des tokens attendus
        for line in output_lines:
            token_start = line.split(':')[0].strip()
            if token_start and token_start not in expected_tokens:
                print("Test_lex [ECHEC]")
                print(f"Erreur : Token inattendu trouvé : {line}")
                return
        print("Test_lex [OK]")

        
    except Exception as e:
        print("An error occurred:", e)

if __name__ == '__main__':
    main()
