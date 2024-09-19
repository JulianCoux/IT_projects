class CaseChance{
    #urlImage;
    #deplacement;
    #teleportation; // Si vrai on se tp si faux on appelle la fonction pour avancer et si on passe par la case départ c'est cool
    #recul; // un entier on recul du nombre de case
    #perte;
    #maison; // Si vrai la perte est en fonction des maisons et des hôtels
    #gain;
    #cartePrison;

    constructor(url, deplacement, teleportation, recul, perte, maison, gain, cartePrison) {
        this.#urlImage = url;
        this.#deplacement = deplacement;
        this.#teleportation = teleportation;
        this.#recul = recul;
        this.#perte = perte;
        this.#maison = maison;
        this.#gain = gain;
        this.#cartePrison = cartePrison;
    }

    //Les boutons se bloquent pas comme il faut
    traiteCarte(){
        document.querySelector(".chanceCommunaute").style.display = "block";
        let isCarte = false;
        let bouttonChance = document.createElement("button");
        if (this.#deplacement || this.#recul || this.#deplacement === 0) {
            bouttonChance.textContent = "Y aller";
        } else if (this.#perte){
            bouttonChance.textContent = "Payer";
        } else {
            bouttonChance.textContent = "Récupérer";
        }
        bouttonChance.className = "acheter"; // juste pour du visuel aucun lien avec le nom
        document.querySelector(".chanceCommunaute").appendChild(bouttonChance);

        // Ajouter un gestionnaire d'événement sur le bouton
        bouttonChance.addEventListener("click", () => {
            document.getElementById("carte").style.display = "none";
            const nomJoueur = document.getElementById("nom_joueur").textContent;
            if (this.#deplacement || this.#deplacement === 0){
                if (this.#teleportation){
                    // se teleporter
                    isCarte = teleportationPionChance(this.#deplacement, nomJoueur);
                } else {
                    //Avancer
                    deplacementPionTirageCarte(this.#deplacement, nomJoueur, true); 
                }
            } else if (this.#recul){
                //on recule de : this.#reculer cases
                const newPosition = (dicoJoueurs[nomJoueur].getPosition() - this.#recul) % 40;
                deplacementPionTirageCarte(newPosition, nomJoueur, false); 
                
            } else if (this.#perte){
                if (this.#maison){
                    // Perte en fonction des maisons et hôtels que le joueur possède

                    //Récup le joueur puis son nb hotel et maison
                    
                    const nbHotel = dicoJoueurs[nomJoueur].getNbHotel();
                    const nbMaison = dicoJoueurs[nomJoueur].getNbMaison();
                    const total = this.#perte[0]*nbMaison + this.#perte[1]*nbHotel;
                    const solde = document.getElementById("solde_compte");
                    if (solde.textContent < total){
                        document.getElementById("alertePerdu").style.display = "block";
                        desactiverBoutons();
                        return
                    }
                    dicoJoueurs[nomJoueur].suppSolde(total);

                    // Ca c'est juste visuel
                    solde.textContent -= total; 
                    // Ca c'est ce qui sera sauvegardé pour le prochain tour
                    newSoldeBancaire(nomJoueur)
                    pot_commun += total;
                } else {
                    // SuppSolde(perte) sur le bon joueur
                    
                    const solde = document.getElementById("solde_compte");
                    if (solde.textContent < this.#perte){
                        document.getElementById("alertePerdu").style.display = "block";
                        desactiverBoutons();                        
                        return
                    }
                    dicoJoueurs[nomJoueur].suppSolde(this.#perte);

                    // Ca c'est juste visuel
                    solde.textContent -= this.#perte; 
                    // Ca c'est ce qui sera sauvegardé pour le prochain tour
                    newSoldeBancaire(nomJoueur)
                    pot_commun += this.#perte;
    
                }
            } else if (this.#gain){ // [OK]
                // AddSolde(gain) sur le bon joueur
                
                dicoJoueurs[nomJoueur].addSolde(this.#gain);
                const solde = document.getElementById("solde_compte");
                // Ca c'est juste visuel
                solde.textContent += this.#gain; 
                // Ca c'est ce qui sera sauvegardé pour le prochain tour
                newSoldeBancaire(nomJoueur)
                
    
            } else { // [OK]
                // ajouter la carte libérer de prison au joueur #cartePrison
                let objetJoueur = dicoJoueurs[nomJoueur];
                objetJoueur.addFreedom(this);
                ajouterCarte("freedom", this);
            }

            // Supprimer le bouton
            bouttonChance.remove();

            if (!(this.#deplacement || this.#deplacement === 0 || this.#recul)){
                reactiverBoutons(nomJoueur);   
            }

            if(this.#teleportation && !isCarte){
                document.getElementById("carte").style.display = "none";
                reactiverBoutons(nomJoueur);
            } 
            
            

            document.querySelector(".chanceCommunaute").style.display = "none";
        });

        
    }

    remettreEnDessousDeLaPioche(pioche) {
        pioche.push(this); // Ajoute la carte à la fin de la pioche
    }

    getUrl(){
        return this.#urlImage;
    }
    getFreedom(){
        return this.#cartePrison;
    }
}

// Création du tableau avec toutes les cartes Chance
let toutesLesCartes = [
    new CaseChance("../styles/images/chance/chance1.png", 39, true, 0, 0, false, 0, null), // Le deuxième est la position ou on doit se tp
    new CaseChance("../styles/images/chance/chance2.png", 0, true, 0, 0, false, 0, null),
    new CaseChance("../styles/images/chance/chance3.png", 16, false, 0, 0, false, 0, null),
    new CaseChance("../styles/images/chance/chance4.png", 32, false, 0, 0, false, 0, null),
    new CaseChance("../styles/images/chance/chance5.png", null, false, 0, [20, 30], true, 0, null),
    new CaseChance("../styles/images/chance/chance6.png", 25, false, 0, 0, false, 0, null),
    new CaseChance("../styles/images/chance/chance7.png", null, false, 0, 0, false, 100, null),
    new CaseChance("../styles/images/chance/chance8.png", null, false, 0, 0, false, 500, null),
    new CaseChance("../styles/images/chance/chance9.png", null, false, 3, 0, false, 0, null),
    new CaseChance("../styles/images/chance/chance10.png", 40, true, 0, 0, false, 0, null),
    new CaseChance("../styles/images/chance/chance11.png", null, false, 0, [50, 100], true, 0, null),
    new CaseChance("../styles/images/chance/chance12.png", null, false, 0, 50, false, 0, null),
    new CaseChance("../styles/images/chance/chance13.png", null, false, 0, 100, false, 0, null),
    new CaseChance("../styles/images/chance/chance14.png", null, false, 0, 0, false, 200, null),
    new CaseChance("../styles/images/chance/chance15.png", null, false, 0, 50, false, 0, null),
    new CaseChance("../styles/images/chance/chance16.png", null, false, 0, 0, false, 0, true),
];

// Fonction pour mélanger aléatoirement un tableau
function melangerTableau(tableau) {
    for (let i = tableau.length - 1; i > 0; i--) {
        const j = Math.floor(Math.random() * (i + 1));
        [tableau[i], tableau[j]] = [tableau[j], tableau[i]];
    }
}

// Mélanger aléatoirement le tableau de cartes
melangerTableau(toutesLesCartes);

// Ensuite, vous pouvez ajouter les cartes à la pioche dans l'ordre
let piocheChance = [];
for (let i = 0; i < toutesLesCartes.length; i++) {
    piocheChance.push(toutesLesCartes[i]);
}

// Tirer une carte Chance
function tirerCarte(pioche) {
    if (mode === 'online'){
        socket.emit('draw card', roomID, 'chance')
    }
    return pioche.shift(); // Retire la première carte de la pioche et la renvoie
}

function teleportationPionChance(newPos, nomJoueur){
    classNomJoueur = dicoJoueurs[nomJoueur];
    currentPos = classNomJoueur.getPosition();
    newIndice = calcIndicePion(newPos, classNomJoueur.getNumero());
    let isCarte = false;
    if(newPos === 40){
        document.getElementById('alertPrison').style.display = 'block';
        desactiverBoutons();
        //le joueur va en prison
        rejoue = false;
        dicoJoueurs[nomJoueur].reinitCompteurPrison();  //on réinitialise le compteur de double => déjà en prison
    }
    else if(newPos === 0){
        dicoJoueurs[nomJoueur].addSolde(200);
        newSoldeBancaire(nomJoueur)
    }
    else{
        isCarte = afficheCarte(newPos);
    }
    
    let pion = document.getElementById(nomJoueur);
    pion.style.top = newIndice[0] + "%"; 
    pion.style.left = newIndice[1] + "%";
    dicoJoueurs[nomJoueur].setPosition(newPos);

    return isCarte;
}









