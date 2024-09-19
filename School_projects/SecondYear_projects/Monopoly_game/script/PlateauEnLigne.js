const socket = io()
const nomJoueursInit = []
const urlParameters = new URLSearchParams(window.location.search)
const roomID = urlParameters.get('roomID')
const username = urlParameters.get('username')
let indexJoueur

function main(){
    console.log(`client ${socket.id} has fully load`)
    /* Le client prévient le serveur que la redirection a bien marché
    * et demande à rejoindre la room identifiée par roomID */
    socket.emit('in game', roomID);

    /* Initialisation des pions */
    socket.on('start game', (joueurs, joueurCourant, turnOf) => {
        /* Mise en place du sens de jeu */
        for (let i=0; i<turnOf; i++){
            const first = joueurs.shift()
            joueurs.push(first)
        }
        /* Remplissage de nomJoueursInit */
        joueurs.forEach((joueur) => {
            nomJoueursInit.push(joueur.username);
        })
        initDisplay();
        creerPionsHtml(nomJoueursInit);
        nomJoueursInit.forEach((nomJoueur, index) => {
            if (nomJoueur === username){
                indexJoueur = index
            }
            const newPosition = 0;
            dicoJoueurs[nomJoueur] = new Joueur(nomJoueur, couleurs[index], 1500, newPosition, index + 1); // Ajout du joueur au dictionnaire avec le nom comme clé
            putCSStoPlayer(nomJoueur, index);
        })
        console.log("Initialisation de la position des pions terminée")
        initPlateau()
        console.log('first turn', joueurCourant)
        setPlayer(joueurCourant)
        setComptePlayer(username)
        if(joueurCourant === username) playMyTurn()
        else notMyTurn()
    })

    document.getElementById("buttonSuivant").addEventListener("click", finishMyTurn, false);
    document.getElementById("img-param").addEventListener("click", allerAuxParametres, false); //TODO: A modifier
    document.querySelector('.btn-maison').addEventListener('click', addMaison, false)
}

socket.on('nextTurn', (joueurCourant, joueurs) => {
    console.log('next turn')
    console.log('joueurs : ', joueurs)
    updatePlateau(joueurs)
    setPlayer(joueurCourant)
    if(joueurCourant === username) playMyTurn()
    else notMyTurn()
})

socket.on('updateMoving', (joueur) => {
    console.log('update move')
    updatePosition(joueur)
})

socket.on('afficheEnchere', (joueur, joueurSuivant, valeurEnchere, couleur) => {
    console.log('affiche form enchere')
    tabJoueurEnchere = joueur;
    if(tabJoueurEnchere.length == 0){
        //Tout le monde s'est couché
        console.log('plus personne dans Enchere')
        document.querySelector(".login-box").style.display = "none";
        //réactiverBouton
        if (username == document.getElementById("nom_joueur").textContent){
            reactiverBoutons(username)
        }
    } else {
        updateEnchere(joueur, joueurSuivant, valeurEnchere, couleur)
    }
})

socket.on('suppEnchere', (joueurs) => {
    console.log('end Enchere')
    //Supp le form
    document.querySelector(".login-box").style.display = "none";
    //Callupdateplateau
    updatePlateau(joueurs)
    //réactiverBouton
    if (username == document.getElementById("nom_joueur").textContent){
        reactiverBoutons(username)
    }
})

socket.on('update ranking', (joueurs) => {
    joueurs.forEach(joueur => {
        dicoJoueurs[joueur.nom].setSolde(joueur.solde)
        if (username === joueur.nom){
            let compteJoueur = document.getElementById("solde_compte")
            compteJoueur.textContent = joueur.solde
        }
    })
    updateRanking()
})

socket.on('draw card', (cardType) => {
    let pioche
    if (cardType === 'chance'){
        pioche = piocheChance
    } else {
        pioche = piocheCommunaute
    }
    tirerCarte(pioche)
})


// Normalisation des couleurs en format hexadécimal
function normaliserCouleur(couleur) {
    if (couleur.startsWith('#')) {
        return couleur.toLowerCase();
    } else if (couleur.startsWith('rgb')) {
        let valeurs = couleur.match(/\d+/g);
        return '#' + ((1 << 24) + (parseInt(valeurs[0]) << 16) + (parseInt(valeurs[1]) << 8) + parseInt(valeurs[2])).toString(16).slice(1);
    }
}

function updateEnchere(joueur, joueurSuivant, valeurEnchere, couleur){
    // On initialise le champ pseudo
    
    let selectElement = document.getElementById("pseudo");
    selectElement.style.display = "none";
    let labelElement = document.getElementById("valeurEnchere");
    labelElement.style.display = "block";
    let h3Element = document.getElementById("tourEnchere");
    h3Element.style.display = "block";
    let buttonElement = document.getElementsByClassName("annuleForm");
    let button2Element = document.getElementsByClassName("validForm");
    document.getElementById("joueurEnchere").innerHTML = joueurSuivant;
    document.getElementById("joueurEnchere").style.color = dicoJoueurs[joueurSuivant].getCouleur();
    document.getElementById("prixEnchere").innerHTML = valeurEnchere;
    document.getElementById("prixEnchere").style.color = couleur;
    buttonElement[0].innerHTML = "SE COUCHER";
    button2Element[0].innerHTML = "ENCHÉRIR";
    if(joueurSuivant !== username){
        //BLOQUER LES BOUTON?S
        buttonElement[0].disabled = true;
        button2Element[0].disabled = true;
    } else {
        if (normaliserCouleur(dicoJoueurs[username].getCouleur()) == normaliserCouleur(document.getElementById("prixEnchere").style.color)){
            //Remporte l'enchère
            console.log(username + " a remporté l'enchère")
            let price = document.getElementById("prixEnchere").textContent
            dicoJoueurs[username].suppSolde(parseInt(price));
            //récup la position du joueur qui a lancé les enchères
            let joueurCourant = document.getElementById("nom_joueur").textContent
            let cartePosition = dicoJoueurs[joueurCourant].getPosition()
            let casePlateau;
        
            let dict;
            let classe = null;

            if (carteDict.hasOwnProperty(cartePosition)){
                dict = carteDict;
            } else if (gareDict.hasOwnProperty(cartePosition)){
                dict = gareDict;
                classe = "gare"
            } else {
                dict = compagnieDict;
                classe = "compagnie"
            }
            casePlateau = dict[cartePosition];
        
            if (carteDict.hasOwnProperty(cartePosition)){
                dicoJoueurs[username].ajouterPropriete(casePlateau.getCouleur(), casePlateau);
                if (casePlateau.getCouleur() === "bleu clair"){
                    classe = "bleuClair"
                }
                else if (casePlateau.getCouleur() === "bleu foncé"){
                    classe = "bleuFonce"
                } else {
                    classe = casePlateau.getCouleur();
                }
            } else if (gareDict.hasOwnProperty(cartePosition)){
                dicoJoueurs[username].ajouterGare(casePlateau);
            } else {
                dicoJoueurs[username].ajouterCompagnie(casePlateau);
            }

            newSoldeBancaire(username);
            ajouterPionSurCarte(dict, username, cartePosition);
            //ajouter la carte à droite
            ajouterCarte(classe, casePlateau);

            let joueurs = Object.values(dicoJoueurs).map(joueur => joueur.getObject());
            //Créer socket pour dire à tout le monde que l'enchère est fini
            socket.emit('endEnchere', roomID, joueurs)
            return
        }
        buttonElement[0].disabled = false;
        button2Element[0].disabled = false;
    }
    
    // On affiche le form des enchere
    document.querySelector(".login-box").style.display = "block";

}


function updatePlateau(updatedPlayers){
    updatedPlayers.forEach(updatedPlayer => {
        let nomJoueur = updatedPlayer.nom
        updatePosition(updatedPlayer)
        // mise à jour du solde
        dicoJoueurs[nomJoueur].setSolde(updatedPlayer.solde)
        updateBatiment(nomJoueur, updatedPlayer.compagnies, dicoJoueurs[nomJoueur].getCompagnie())
        updateBatiment(nomJoueur, updatedPlayer.gares, dicoJoueurs[nomJoueur].getGares())
        updatePropriete(updatedPlayer.nom, updatedPlayer.proprietes, dicoJoueurs[updatedPlayer.nom].getPropriete())
    })
    // mise à jour du classement des joueurs
    updateRanking()
}

function updatePosition(updatedPlayer){
    // mise à jour du placement des pions
    let nomJoueur = updatedPlayer.nom
    let positionJoueur = updatedPlayer.position
    dicoJoueurs[nomJoueur].setPosition(positionJoueur)
    let newIndice = calcIndicePion(positionJoueur, updatedPlayer.numero);
    let pion = document.getElementById(nomJoueur);
    pion.style.top = newIndice[0] + "%";
    pion.style.left = newIndice[1] + "%";
    dicoJoueurs[nomJoueur].setPosition(positionJoueur);
}

function updatePropriete(nomJoueur, tabPropriete, ancienTableau) {
    const clefs = Object.keys(tabPropriete);
    clefs.forEach(couleurCarte => {
        let tableauPosition = tabPropriete[couleurCarte];
        let tableauAncienPosition = ancienTableau[couleurCarte];

        if(tableauAncienPosition == null){
            tableauPosition.forEach(idPropriete => {
                let proprieteCorrespondante = carteDict[idPropriete];
                dicoJoueurs[nomJoueur].ajouterPropriete(couleurCarte, proprieteCorrespondante);
                ajouterPionSurCarte(carteDict, nomJoueur, idPropriete);
            })
        }
        else{
            tableauPosition.forEach(idPropriete => {
                let proprieteCorrespondante = carteDict[idPropriete];
                
                if (!tableauAncienPosition.includes(proprieteCorrespondante)) {
                    // C'est une nouvelle propriété
                    dicoJoueurs[nomJoueur].ajouterPropriete(couleurCarte, proprieteCorrespondante);
                    ajouterPionSurCarte(carteDict, nomJoueur, idPropriete);
                }
            })
        }

    });
}

function updateBatiment(nomJoueur, tableauPropriete, oldPropriete){
    if (tableauPropriete.length !== oldPropriete.length){
        // Ajout donc trouver le nouveau
        tableauPropriete.forEach(propriete => {
            let proprieteCorrespondante = compagnieDict[propriete] || gareDict[propriete];
            if (!oldPropriete.includes(proprieteCorrespondante)) {
                // Nouvelle Propriété
                if (proprieteCorrespondante instanceof CaseCompagnie) {
                    // propriete est une instance de la classe CaseCompagnie
                    dicoJoueurs[nomJoueur].ajouterCompagnie(proprieteCorrespondante)
                    ajouterPionSurCarte(compagnieDict, nomJoueur, propriete)
                } else {
                    // propriete n'est pas une instance de la classe CaseGare
                    dicoJoueurs[nomJoueur].ajouterGare(proprieteCorrespondante)
                    ajouterPionSurCarte(gareDict, nomJoueur, propriete)
                }
            }
        })
    }
}

function playMyTurn(){
    console.log('my turn')
    if (dicoJoueurs[username].getPosition() == 40) {
        document.getElementById("btnSortirPrison").style.display = "block";
    }
    document.querySelector('.btn-maison').disabled = false // réactivation du bouton pour acheter des maisons
    document.getElementById("lancer_des").disabled = false; // réactivation du bouton pour lancer les dés
    document.getElementById("buttonSuivant").disabled = true;
}

function notMyTurn(){
    console.log('not my turn')
    document.querySelector('.btn-maison').disabled = true
    document.getElementById("lancer_des").disabled = true; // désactivation du bouton pour lancer les dés
    document.getElementById("buttonSuivant").disabled = true;
}

function finishMyTurn(){
    document.getElementById("lancer_des").disabled = true;
    document.getElementById("buttonSuivant").disabled = true;
    document.getElementById("de_1").innerHTML = '';
    document.getElementById("de_2").innerHTML = '';

    let joueurs = Object.values(dicoJoueurs).map(joueur => joueur.getObject());
    console.log('finishTurn[dicoJoueurs]', dicoJoueurs)
    console.log('finishTurn[joueurs]', joueurs)
    socket.emit('finishTurn', roomID, joueurs)
}

window.onload = main