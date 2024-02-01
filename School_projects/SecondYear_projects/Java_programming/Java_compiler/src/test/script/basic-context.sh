#! /bin/sh

# Auteur : gl25
# Version initiale : 01/01/2024

# Test minimaliste de la vérification contextuelle.
# Le principe et les limitations sont les mêmes que pour basic-synt.sh
cd "$(dirname "$0")"/../../.. || exit 1

PATH=./src/test/script/launchers:"$PATH"


# exemple de définition d'une fonction
test_context_invalide () {
    # $1 = premier argument.
    if test_context "$1" 2>&1 | grep -q -e "$1:[0-9][0-9]*:"
    then
        echo "Echec attendu pour test_context sur $1."
    else
        echo "Succes inattendu de test_context sur $1."
        exit 1
    fi
}

for cas_de_test in src/test/deca/context/invalid/OwnTests_stepB/invalid/*/*.deca
do
    test_context_invalide "$cas_de_test"
done


# Tests valides
for cas_de_test in src/test/deca/context/valid/OwnTests_stepB/valid/*/*.deca
do
    if test_context "$cas_de_test" 2>&1 | grep -q -e "$cas_de_test:[0-9][0-9]*:"
    then
        echo "Echec inattendu pour test_context sur $cas_de_test."
        exit 1
    else
        echo "Succes attendu de test_context sur $cas_de_test."
    fi
done
