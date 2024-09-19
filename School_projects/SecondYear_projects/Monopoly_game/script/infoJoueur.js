//Fait en tant que test pour des joueurs
let fileJoueurs = null
let nomCurrentJoueur = null


function main2(){
    fileJoueurs = [...nomJoueursInit]
    initPartie()
    document.getElementById("lancer_des").disabled = false; // réactivation du bouton pour lancer les dés
    document.getElementById("buttonSuivant").disabled = true;
    document.getElementById("buttonSuivant").addEventListener("click", finTourJoueur, false);
    document.getElementById("img-param").addEventListener("click", allerAuxParametres, false);
    document.querySelector('.btn-maison').addEventListener('click', addMaison, false)
}
/*********************
 On est sorti du MAIN
*********************/

function initPartie(){
    const nomCurrentJoueur = prendrePremierJoueur()
    mettreJoueurALaFin(nomCurrentJoueur)
    tourJoueur(nomCurrentJoueur)
}

function allerAuxParametres(){
    document.getElementById("info_jeux").style.display = "none";
    document.getElementById("param_jeux_local").style.display = "block";
    main_param()
}

function setPlayer(nomCurrentJoueur) {
    let nomJoueur = document.getElementById("nom_joueur")
    nomJoueur.textContent = nomCurrentJoueur
    nomJoueur.style.color = dicoJoueurs[nomCurrentJoueur].getCouleur()
}

function setComptePlayer(nomCurrentJoueur) {
    //supp toutes les propriétés de l'ancien joueur et remettre les nouvelles
    suppPropriete();
    addAllPropriete(nomCurrentJoueur);

    let compteJoueur = document.getElementById("solde_compte")
    compteJoueur.textContent = dicoJoueurs[nomCurrentJoueur].getSolde()

    let compteJoueurComplet = document.getElementById("prix_complet")
    compteJoueurComplet.style.color = dicoJoueurs[nomCurrentJoueur].getCouleur()
}

function tourJoueur(nomCurrentJoueur){
    setPlayer(nomCurrentJoueur)
    setComptePlayer(nomCurrentJoueur)

    if (dicoJoueurs[nomCurrentJoueur].getPosition() == 40) {
        document.getElementById("btnSortirPrison").style.display = "block";
    }
}

function finTourJoueur(){
    document.getElementById("lancer_des").disabled = false; // réactivation du bouton pour lancer les dés
    document.getElementById("buttonSuivant").disabled = true;
    const nomNextJoueur = prendrePremierJoueur()
    mettreJoueurALaFin(nomNextJoueur)
    tourJoueur(nomNextJoueur)
}

function removePlayer(joueur) {
    let index = fileJoueurs.indexOf(joueur);
    if (index !== -1) {
        fileJoueurs.splice(index, 1);
    }
}

function mettreJoueurALaFin(joueur) {
    fileJoueurs.push(joueur); // Ajoute la carte à la fin de la pioche
}

function prendrePremierJoueur() {
    return fileJoueurs.shift(); // Retire la première carte de la pioche et la renvoie
}

function updateRanking() {
    let tabCompteJoueurs = [];

    for (const [nom, joueur] of Object.entries(dicoJoueurs)) {
        const solde = joueur.getSolde();
        tabCompteJoueurs.push({ nom, solde });
    }

    tabCompteJoueurs.sort((a, b) => b.solde - a.solde);

    const classementTable = document.querySelector('.classement_joueurs table');

    // Supprimer toutes les lignes du corps de la table
    while (classementTable.rows.length > 0) {
        classementTable.deleteRow(0);
    }

    // Créer une ligne pour les noms des joueurs et une ligne pour leurs comptes en banque
    let nomsRow = "<tr>";
    let argentRow = "<tr>";

    // Boucle à travers les joueurs et construit les lignes du tableau
    tabCompteJoueurs.forEach(joueur => {
        const couleurNom = dicoJoueurs[joueur.nom].getCouleur(); // Récupère la couleur correspondante du joueur
        nomsRow += `<td style="color: ${couleurNom}; font-weight: bold;">${joueur.nom}</td>`;
        argentRow += `<td>${joueur.solde} <span class="icone-monnaie"></span></td>`;
    });

    // Fermer les lignes
    nomsRow += "</tr>";
    argentRow += "</tr>";

    // Ajouter les lignes au tableau
    classementTable.innerHTML += nomsRow + argentRow;
}

function classementJoueurs(){
    if (mode === 'online') {
        let joueurs = Object.values(dicoJoueurs).map(joueur => joueur.getObject());
        socket.emit('new ranking', roomID, joueurs)
    } else {
        updateRanking()
    }
}

function newSoldeBancaire(idDuPion){
    let compteJoueur = document.getElementById("solde_compte")
    compteJoueur.textContent = dicoJoueurs[idDuPion].getSolde()

    classementJoueurs()
}

//Supp l'affichage de toutes les propriétés
function suppPropriete(){
    // Sélectionnez la div parent
    let parentDiv = document.querySelector('.info_glob');

    
    // Parcourez les éléments enfants
    for (let i = 1; i < parentDiv.children.length; i++) {
        let child = parentDiv.children[i];
        // Supp les img
        while (child.firstChild) {
            child.removeChild(child.firstChild);
        }
    }
}

function addAllPropriete(joueur){
    const joueurObj = dicoJoueurs[joueur];
    const propriete = joueurObj.getPropriete();
    let classe = null;
    for (let key in propriete) {
        if (propriete.hasOwnProperty(key)) {
            if (key === "bleu foncé"){
                classe = "bleuFonce";
            } else if (key === "bleu clair"){
                classe = "bleuClair";
            } else {
                classe = key;
            }
            let value = propriete[key];
            for (const element of value){
                ajouterCarte(classe, element);
            }
        }
    }
    const gare = joueurObj.getGares();
    for (const element of gare) {
        ajouterCarte("gare", element);
    }
    
    const compagnie = joueurObj.getCompagnie();
    
    for (const element of compagnie) {
        ajouterCarte("compagnie", element);
    }
    
    const freedom = joueurObj.getFreedom();
    if (freedom){
        ajouterCarte("freedom", freedom);
    }
}


// window.addEventListener("load", main2, false);


window.addEventListener('beforeunload', function(event) {
    // Affiche une boîte de dialogue pour demander à l'utilisateur s'il veut vraiment recommencer la partie
    const confirmationMessage = "Êtes-vous sûr de vouloir recommencer la partie ?";
    event.returnValue = confirmationMessage; // For Gecko, Trident, Chrome & Safari
    return confirmationMessage; // For other browsers
});
