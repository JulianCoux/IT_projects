#! /bin/sh

# Auteur : gl25
# Version initiale : 01/01/2024

# Base pour un script de test de la lexicographie.
# On teste un fichier valide et un fichier invalide.
# Il est conseillé de garder ce fichier tel quel, et de créer de
# nouveaux scripts (en s'inspirant si besoin de ceux fournis).

# Il faudrait améliorer ce script pour qu'il puisse lancer test_lex
# sur un grand nombre de fichiers à la suite.

# On se place dans le répertoire du projet (quel que soit le
# répertoire d'où est lancé le script) :
cd "$(dirname "$0")"/../../.. || exit 1

PATH=./src/test/script/launchers:"$PATH"

# exemple de définition d'une fonction
test_lex_invalide () {
    # $1 = premier argument.
    if test_lex "$1" 2>&1 | grep -q -e "$1:[0-9][0-9]*:"
    then
        echo "Echec attendu pour test_lex sur $1."
    else
        echo "Succes inattendu de test_lex sur $1."
        exit 1
    fi
}

for cas_de_test in src/test/deca/syntax/invalid/OwnTests_stepA/invalid/lexer/*.deca
do
    test_lex_invalide "$cas_de_test"
done


# Tests valides
for cas_de_test in src/test/deca/syntax/valid/OwnTests_stepA/valid/*.deca
do
    if test_lex "$cas_de_test" 2>&1 | grep -q -e "$cas_de_test:[0-9][0-9]*:"
    then
        echo "Echec inattendu pour test_lex sur $cas_de_test."
        exit 1
    else
        echo "Succes attendu de test_lex sur $cas_de_test."
    fi
done


