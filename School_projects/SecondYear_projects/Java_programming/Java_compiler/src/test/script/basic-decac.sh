#! /bin/sh

# Test de l'interface en ligne de commande de decac.
# On ne met ici qu'un test trivial, a vous d'en ecrire de meilleurs.


# Se déplacer dans le répertoire du script
cd "$(dirname "$0")"/../../.. || exit 1

PATH=./src/main/bin:"$PATH"

decac_moins_b=$(decac -b)

if [ "$?" -ne 0 ]; then
    echo "ERREUR: decac -b a termine avec un status different de zero."
    exit 1
fi

if [ "$decac_moins_b" = "" ]; then
    echo "ERREUR: decac -b n'a produit aucune sortie"
    exit 1
fi

if echo "$decac_moins_b" | grep -i -e "erreur" -e "error"; then
    echo "ERREUR: La sortie de decac -b contient erreur ou error"
    exit 1
fi

echo "Pas de probleme detecte avec decac -b."


decac_moins_p=$(decac -p src/test/deca/context/valid/provided/hello-world.deca )

if [ "$?" -ne 0 ]; then
    echo "ERREUR: decac -p a termine avec un status different de zero."
    exit 1
fi

if [ "$decac_moins_p" = "" ]; then
    echo "ERREUR: decac -p n'a produit aucune sortie"
    exit 1
fi

if echo "$decac_moins_p" | grep -i -e "erreur" -e "error"; then
    echo "ERREUR: La sortie de decac -p contient erreur ou error"
    exit 1
fi

echo "Pas de probleme detecte avec decac -p."

decac_moins_v=$(decac -v src/test/deca/context/valid/provided/hello-world.deca )

if [ "$?" -ne 0 ]; then
    echo "ERREUR: decac -v a termine avec un status different de zero."
    exit 1
fi

if [ "$decac_moins_p" = "" ]; then
    echo "ERREUR: decac -v n'a produit aucune sortie"
    exit 1
fi

if echo "$decac_moins_v" | grep -i -e "erreur" -e "error"; then
    echo "ERREUR: La sortie de decac -v contient erreur ou error"
    exit 1
fi

echo "Pas de probleme detecte avec decac -v."

decac_moins_n=$(decac -n src/test/deca/context/valid/provided/hello-world.deca )

if [ "$?" -ne 0 ]; then
    echo "ERREUR: decac -n a termine avec un status different de zero."
    exit 1
fi

if echo "$decac_moins_n" | grep -i -e "erreur" -e "error"; then
    echo "ERREUR: La sortie de decac -n contient erreur ou error"
    exit 1
fi

echo "Pas de probleme detecte avec decac -n."

decac_moins_r=$(decac -r 5 src/test/deca/context/valid/provided/hello-world.deca )

if [ "$?" -ne 0 ]; then
    echo "ERREUR: decac -r a termine avec un status different de zero."
    exit 1
fi


if echo "$decac_moins_r" | grep -i -e "erreur" -e "error"; then
    echo "ERREUR: La sortie de decac -r contient erreur ou error"
    exit 1
fi

echo "Pas de probleme detecte avec decac -r."

decac_moins_d=$(decac -d src/test/deca/context/valid/provided/hello-world.deca )

if [ "$?" -ne 0 ]; then
    echo "ERREUR: decac -d a termine avec un status different de zero."
    exit 1
fi

if [ "$decac_moins_d" = "" ]; then
    echo "ERREUR: decac -d n'a produit aucune sortie"
    exit 1
fi

if echo "$decac_moins_d" | grep -i -e "erreur" -e "error"; then
    echo "ERREUR: La sortie de decac -d contient erreur ou error"
    exit 1
fi

echo "Pas de probleme detecte avec decac -d."

decac_moins_P=$(decac -P src/test/deca/context/valid/provided/hello-world.deca )

if [ "$?" -ne 0 ]; then
    echo "ERREUR: decac -P a termine avec un status different de zero."
    exit 1
fi


if echo "$decac_moins_P" | grep -i -e "erreur" -e "error"; then
    echo "ERREUR: La sortie de decac -P contient erreur ou error"
    exit 1
fi

echo "Pas de probleme detecte avec decac -P."