#! /bin/sh

# Auteur : gl25
# Version initiale : 01/01/2024

# Test minimaliste de decompile.
cd "$(dirname "$0")"/../../.. || exit 1

PATH=./src/test/script/launchers:"$PATH"




# Tests valides
for cas_de_test in src/test/deca/context/valid/OwnTests_stepB/valid/*/*.deca
do
    if test_decompile "$cas_de_test" 2>&1 | grep -q -e "$cas_de_test:[0-9][0-9]*:"
    then
        echo "Echec inattendu pour test_decompile sur $cas_de_test."
        exit 1
    else
        echo "Succes attendu de test_decompile sur $cas_de_test."
    fi
done
