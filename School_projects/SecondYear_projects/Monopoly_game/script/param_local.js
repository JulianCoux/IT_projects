let joueur_suppr = "";

function main_param(){
    document.getElementById("supp_joueur").style.display = "none";
    document.getElementById("alert_supprimer").style.display = "none";
    
    function show_joueurs(){
        const divJoueurs = document.getElementById("supp_joueur");
        divJoueurs.innerHTML = "";
        document.getElementById("supp_joueur").style.display = "inline-block";

        nomJoueursInit.forEach(joueur => {
            // Créer un nouvel élément div pour chaque joueur
            let newDivJoueur = document.createElement("div");
            
            // Créer un bouton d'icône de suppression
            let boutonSupprimer = document.createElement("button");
            boutonSupprimer.innerHTML = "<img src='../styles/images/moins.png' alt='Supprimer joueur' class='img-suppr'>";
    
            // Ajouter un gestionnaire d'événement pour le clic sur le bouton
            boutonSupprimer.addEventListener("click", function() {
                // Code pour supprimer le joueur
                joueur_suppr = joueur;
                document.getElementById("alert_supprimer").style.display = "flex";
                document.getElementById("joueur_a_suppr").textContent = joueur;
                document.getElementById("supp_joueur").style.display = "none";
            });
            
            // Créer un élément pour afficher le nom du joueur
            let nomJoueur = document.createTextNode(joueur);
    
            // Ajouter le bouton et le nom du joueur au div du joueur
            newDivJoueur.appendChild(boutonSupprimer);
            newDivJoueur.appendChild(nomJoueur);
    
            // Ajouter le div du joueur au conteneur des joueurs
            divJoueurs.appendChild(newDivJoueur);
        });
    }

    document.getElementById("btnFinPartie").addEventListener("click", finir_partie, false);
    document.getElementById("img-home").addEventListener("click", goBackHome, false);
    document.getElementById("supprimer_joueurs").addEventListener("click", show_joueurs, false);
}
/*********************
 On est sorti du MAIN
*********************/

function finir_partie(){
    localStorage.clear()
    window.location.href = "../index.html";
}


function supprUnJoueurPerdu(){
    const idPion = document.getElementById("nom_joueur").textContent;
    if(nomJoueursInit.length > 2){
        //on le supprime
        joueur_suppr = idPion;
        alerteSupprOUIJoueur();
        document.getElementById("alertePerdu").style.display = "none";
        document.getElementById("lancer_des").disabled = false;
    }
    else {
        //la partie doit se terminer
        document.getElementById("alertePerdu").style.display = "none";
        document.getElementById("alerteGagne").style.display = "block";

        let index = nomJoueursInit.indexOf(joueur_suppr);
        if (index !== -1) {
            nomJoueursInit.splice(index, 1);
        }
        document.getElementById("grandGagnant").textContent = nomJoueursInit[0];
    }
}


function closeAlerteGagne(){
    finir_partie();
}


function goBackHome(){
    document.getElementById("info_jeux").style.display = "block";
    document.getElementById("param_jeux_local").style.display = "none";
}

function alerteSupprOUIJoueur(){
    //enlever joueur_a_suppr de la liste des joueurs
    console.log("On supprime : ", joueur_suppr);
    // Trouver l'index de l'élément à supprimer dans le tableau
    let index = nomJoueursInit.indexOf(joueur_suppr);

    if(nomJoueursInit.length == 2){
        alert("Vous ne pouvez pas avoir moins de 2 joueur, veuillez terminer la partie")
    }
    else{
        if(nomCurrentJoueur == joueur_suppr){  //autoriser que le passe au joueur suivant
            // document.getElementById("buttonSuivant").disabled = false;
            // document.getElementById("lancer_des").disabled = true;
            finTourJoueur()
        }
        
        supprProprietes(joueur_suppr);
        removePlayer(joueur_suppr);
        
        //supprimer le joueur du tableau des noms initiaux et du dictionnaire
        if (index !== -1) {
            nomJoueursInit.splice(index, 1);
        }
        if (joueur_suppr in dicoJoueurs) {
            delete dicoJoueurs[joueur_suppr];
        }

        //il faut aussi supprimer le pion du plateau
        let divASupprimer = document.getElementById(joueur_suppr);
        if (divASupprimer) {
            divASupprimer.remove();
        }

        classementJoueurs()

        goBackHome()
    }

    document.getElementById("alert_supprimer").style.display = "none";
}


function supprProprietes(idPion){
    let proprietes = dicoJoueurs[idPion].getPropriete();
    let gares = dicoJoueurs[idPion].getGares();
    let compagnies = dicoJoueurs[idPion].getCompagnie();

    for (let cle in proprietes) {
        let tableau = proprietes[cle];
        for (let i = 0; i < tableau.length; i++) {
            let casePropriete = tableau[i];
            casePropriete.addProprio(null);

            //Pour supprimer les pions des propriétaires au dessus des cartes
            const numCase = casePropriete.getNumeroCase()
            const numId = idPion + "_" + numCase;
            document.getElementById(numId).remove()
        }
    }

    for (let i=0; i<gares.length; i++){
        let caseGare= gares[i];
        caseGare.addProprio(null);

        //Pour supprimer les pions des propriétaires au dessus des cartes
        const numCase = caseGare.getNumeroCase()
        const numId = idPion + "_" + numCase;
        document.getElementById(numId).remove()
    }

    for (let i=0; i<compagnies.length; i++){
        let caseCompagnie = compagnies[i];
        caseCompagnie.addProprio(null);

        //Pour supprimer les pions des propriétaires au dessus des cartes
        const numCase = caseCompagnie.getNumeroCase()
        const numId = idPion + "_" + numCase;
        document.getElementById(numId).remove()
    }
    
}


function alerteSupprNONJoueur(){
    //revenir au menu simplement
    document.getElementById("alert_supprimer").style.display = "none";
    
}
