//Pour vider le localStorage : localStorage.clear();
const nomJoueursInit = JSON.parse(localStorage.getItem('nomsJoueurs')) || [];
//MARCHE PAS AVEC FIREFOX => il faut créer un serveur local avec npx et npm

function main(){
    initPions()
    initPlateau()
}

/**********************************************
 Fonction qui initialise les pions
 Chaque nom de joueur devient une clé pour le dictionnaire qui associe le nom avec la position du pion
 **********************************************/
function initPions(){
    initDisplay()

//A remplacer par juste le nom des pions
    creerPionsHtml(nomJoueursInit)
    // let divs = document.querySelectorAll('.pionsJeux div');

    //On récup dans le local storage
    const tableauJoueur = JSON.parse(localStorage.getItem('tableauJoueur'));

    // Parcours des divs et ajout de leurs IDs et d'un entier associé dans le tableau
    nomJoueursInit.forEach((nomJoueur, index) => {
        const newPosition = 0;
        let joueur = null;
        if (tableauJoueur){
            joueur = new Joueur(
                tableauJoueur[index].nom, tableauJoueur[index].couleur, tableauJoueur[index].solde,
                tableauJoueur[index].position, tableauJoueur[index].numero, undefined,
                tableauJoueur[index].nbTours, tableauJoueur[index].compteur_prison, tableauJoueur[index].compteur_double,
                undefined, undefined, undefined, tableauJoueur[index].nbMaison,
                tableauJoueur[index].nbHotel
            )
            // Après avoir créer le joueur il faut ajouter ses propriete propriété mettre un propriétaire pour chque propriété
            const tableauPositionPropriete = Object.values(tableauJoueur[index].proprietes).flat();
            for (let i = 0; i < tableauPositionPropriete.length; i++){
                let proprieteRecup = carteDict[tableauPositionPropriete[i]];
                proprieteRecup.addProprio(joueur);
                // proprieteRecup.setNbMaison(tableauPositionPropriete[i].nbMaisons);
                // proprieteRecup.setNbHotel(tableauPositionPropriete[i].nbHotels)
                joueur.ajouterPropriete(proprieteRecup.getCouleur(), proprieteRecup);
                ajouterPionSurCarte(carteDict, tableauJoueur[index].nom, tableauPositionPropriete[i], tableauJoueur[index].couleur);

            }
            // Recréer le tableau de gare, compagnie
            const tableauGarePosition = tableauJoueur[index].gares;
            for (let i = 0; i < tableauGarePosition.length; i++){
                let gareRecup = gareDict[tableauGarePosition[i]];
                gareRecup.addProprio(joueur);
                joueur.ajouterGare(gareRecup);
                ajouterPionSurCarte(gareDict, tableauJoueur[index].nom, tableauGarePosition[i], tableauJoueur[index].couleur);
            }
            const tableauCompagniePosition = tableauJoueur[index].compagnies;
            for (let i = 0; i < tableauCompagniePosition.length; i++){
                let compagnieRecup = compagnieDict[tableauCompagniePosition[i]];
                compagnieRecup.addProprio(joueur);
                joueur.ajouterCompagnie(compagnieRecup);
                ajouterPionSurCarte(compagnieDict, tableauJoueur[index].nom, tableauCompagniePosition[i], tableauJoueur[index].couleur);
            }
            // Mettre la carte libérer de prison ou non et l'enlever de la pioche correspondante s'il l'a
            if (tableauJoueur[index].freedom){
                //Chercher si c'est une carte chance ou non
                let pioche = null;
                if (tableauJoueur[index].freedom.includes("../styles/images/communaute")) {
                    pioche = piocheCommunaute;
                } else {
                    pioche = piocheChance;
                }
                //Enlever de la bonne pioche
                for (let i = 0; i < pioche.length; i++){
                    if (pioche[i].getUrl() === tableauGarePosition[index].freedom){
                        joueur.addFreedom(pioche[i]);
                        pioche.splice(i, 1);
                        break;
                    }
                }
            }

        } else {
            joueur = new Joueur(nomJoueur, couleurs[index], 1500, newPosition, index+1);
        }
        dicoJoueurs[nomJoueur] = joueur; // Ajout du joueur au dictionnaire avec le nom comme clé
        putCSStoPlayer(nomJoueur, index)
    });

    console.log("Initialisation de la position des pions terminée")

    main2()
}

window.addEventListener("load", main, false);

/**********************************************
 Fonction qui va demander à l'utilisateur qui rafraichi la page si il est sur de vouloir le faire
 Car en cas de refraichissement, il peut perdre ses données de partie
 **********************************************/
window.addEventListener('beforeunload', function(event) {
    // Affiche une boîte de dialogue pour demander à l'utilisateur s'il veut vraiment recommencer la partie
    let confirmationMessage = "Êtes-vous sûr de vouloir recommencer la partie ?";
    console.log("On ajoute dans le localSorage");
    event.returnValue = confirmationMessage; // For Gecko, Trident, Chrome & Safari
    // localStorage.setItem('nomsJoueurs', JSON.stringify(nomsJoueurs));
    let tableauJoueurLocal = Object.values(dicoJoueurs).map(joueur => joueur.getObject());
    this.localStorage.setItem('tableauJoueur', JSON.stringify(tableauJoueurLocal));
    return confirmationMessage; // For other browsers
});