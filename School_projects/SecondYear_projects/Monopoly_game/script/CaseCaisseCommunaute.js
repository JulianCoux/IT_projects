class CaseCaisseCommunaute{
    #urlImage;
    #deplacement;
    #teleportation; // Si vrai on se tp si faux on appelle la fonction pour avancer et si on passe par la case départ c'est cool
    #recul; // un entier on recul du nombre de case
    #perte;
    #choix; // on peut avoir le choix de tirer une carte chance plutôt
    #gain;
    #joueurs; // Si vrai le gain sera en fonction des autres joueurs et pas de la banque
    #cartePrison;

    constructor(url, deplacement, teleportation, recul, perte, choix, gain, joueurs, cartePrison) {
        this.#urlImage = url;
        this.#deplacement = deplacement;
        this.#teleportation = teleportation;
        this.#recul = recul;
        this.#perte = perte;
        this.#choix = choix;
        this.#gain = gain;
        this.#joueurs = joueurs;
        this.#cartePrison = cartePrison;
    }

    traiteCarte(){
        document.querySelector(".chanceCommunaute").style.display = "block";
        let boutonCommunaute = document.createElement("button");
        if (this.#deplacement || this.#recul || this.#deplacement === 0) {
            boutonCommunaute.textContent = "Y aller";
        } else if (this.#perte || this.#choix){
            boutonCommunaute.textContent = "Payer";
        } else {
            boutonCommunaute.textContent = "Récupérer";
        }
        boutonCommunaute.className = "acheter"; // juste pour du visuel aucun lien avec le nom
        document.querySelector(".chanceCommunaute").appendChild(boutonCommunaute);

        if (this.#choix){
            // rajouter un bouton pour piocher une carte chance
            var boutonChance = document.createElement("button");
            boutonChance.textContent = "Piocher une carte chance"
            boutonChance.className = "acheter"
            document.querySelector(".chanceCommunaute").appendChild(boutonChance);
            boutonChance.addEventListener("click", () => {
                
                // Supprimer les bouton

                boutonCommunaute.remove();
                boutonChance.remove();
                const nomJoueur = document.getElementById("nom_joueur").textContent;

                reactiverBoutons(nomJoueur);          


                // On met tout en none pour continuer à jouer
                var carte = document.getElementById("carte");
                carte.style.display = "none";
                var proprio = document.querySelector(".chanceCommunaute");
                proprio.style.display = "none";
                afficheCarte(7); //Pour appeler la fonction qui affiche une carte chance
            });
        }

        // Ajouter un gestionnaire d'événement sur le bouton
        boutonCommunaute.addEventListener("click", () => {
            document.getElementById("carte").style.display = "none";
            let isCarte = false;
            const nomJoueur = document.getElementById("nom_joueur").textContent;
            if (this.#deplacement || this.#deplacement === 0){
                if (this.#teleportation){
                    //Se tp
                    isCarte = teleportationPion(this.#deplacement, nomJoueur);
                } else {
                    //Avancer
                    let newPos = 0;
                    // Vérifier avant si on a un tableau si oui trouver la gare la plus proche
                    if(Array.isArray(this.#deplacement)){
                        let currentPos = dicoJoueurs[nomJoueur].getPosition();
                        if(currentPos <= 5 || currentPos > 35){
                            newPos = 5;
                        }
                        else if(currentPos > 5 && currentPos <= 15){
                            newPos = 15;
                        }
                        else if(currentPos > 15 && currentPos <= 25){
                            newPos = 25;
                        }
                        else{
                            newPos = 35;
                        }
                    }
                    else {
                        newPos = this.#deplacement;
                    }

                    deplacementPionTirageCarte(newPos, nomJoueur, true);
                }
            } else if (this.#recul){
                //un recule jusqu'à la case : this.#recul
                deplacementPionTirageCarte(this.#recul, nomJoueur, false); 
                
            } else if (this.#perte){

                // SuppSolde(perte) sur le bon joueur
                
                dicoJoueurs[nomJoueur].suppSolde(this.#perte);
                const solde = document.getElementById("solde_compte");
                if (solde.textContent < this.#perte){
                    document.getElementById("alertePerdu").style.display = "block";
                    desactiverBoutons();
                    return
                }
                // Ca c'est juste visuel
                solde.textContent -= this.#perte; 
                // Ca c'est ce qui sera sauvegardé pour le prochain tour
                newSoldeBancaire(nomJoueur)
                pot_commun += this.#perte
                
            } else if (this.#choix){
                dicoJoueurs[nomJoueur].suppSolde(this.#choix);
                const solde = document.getElementById("solde_compte");
                if (solde.textContent < this.#choix){
                    document.getElementById("alertePerdu").style.display = "block";
                    desactiverBoutons();
                    return
                }
                // Ca c'est juste visuel
                solde.textContent -= this.#choix; 
                // Ca c'est ce qui sera sauvegardé pour le prochain tour
                newSoldeBancaire(nomJoueur)
                pot_commun += this.#choix
                boutonChance.remove(); 
            }else if (this.#gain){
                if (this.#joueurs){
                    // addSolde(gain*nbjoueru) et suppSOlde sur tous les autres joueurs
                    const nomJoueurGagnant = document.getElementById("nom_joueur").textContent;
                    const joueurGagnant = dicoJoueurs[nomJoueurGagnant];
                    const gainTotal = this.#gain * (Object.keys(dicoJoueurs).length - 1); // Calcul du gain total

                    // Parcourir tous les joueurs
                    for (const nomJoueur in dicoJoueurs) {
                        if (nomJoueur !== nomJoueurGagnant) { // Vérifier que ce n'est pas le joueur gagnant
                            const joueur = dicoJoueurs[nomJoueur];
                            joueur.suppSolde(this.#gain); // Retirer le gain du solde du joueur
                        }
                    }

                    // Mettre à jour le solde affiché pour le joueur gagnant
                    joueurGagnant.addSolde(gainTotal);
                    newSoldeBancaire(nomJoueurGagnant);
                } else {
                    // AddSolde(gain) sur le bon joueur
                    dicoJoueurs[nomJoueur].addSolde(this.#gain);
                    const solde = document.getElementById("solde_compte");
                    // Ca c'est juste visuel
                    solde.textContent += this.#gain; 
                    // Ca c'est ce qui sera sauvegardé pour le prochain tour
                    newSoldeBancaire(nomJoueur)
                }
            } else {
                // ajouter la carte libérer de prison au joueur #cartePrison
                // ajouter la carte libérer de prison au joueur #cartePrison
                let objetJoueur = dicoJoueurs[nomJoueur];
                objetJoueur.addFreedom(this);
                ajouterCarte("freedom", this)
            }
            // Supprimer le bouton
            boutonCommunaute.remove();

            if (!(this.#deplacement || this.#deplacement === 0 || this.#recul)){
                reactiverBoutons(nomJoueur);   
            }

            if((this.#teleportation && !isCarte) || this.#deplacement === 0){
                document.getElementById("carte").style.display = "none";
                reactiverBoutons(nomJoueur);
            }  

            var proprio = document.querySelector(".chanceCommunaute");
            proprio.style.display = "none";
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

// Création du tableau avec toutes les cartes Caisses de communauté
let caisseCommunaute = [
    new CaseCaisseCommunaute("../styles/images/communaute/communaute1.png", 0, true, false, 0, 0, 0, false, null),
    new CaseCaisseCommunaute("../styles/images/communaute/communaute2.png", null, false, false, 0, 0, 200, false, null),
    new CaseCaisseCommunaute("../styles/images/communaute/communaute3.png", null, false, false, 50, 0, 0, false, null),
    new CaseCaisseCommunaute("../styles/images/communaute/communaute4.png", null, false, false, 0, 0, 50, false, null),
    new CaseCaisseCommunaute("../styles/images/communaute/communaute5.png", 40, true, false, 0, 0, 0, false, null),
    new CaseCaisseCommunaute("../styles/images/communaute/communaute6.png", null, false, 3, 0, 0, 0, false, null),
    new CaseCaisseCommunaute("../styles/images/communaute/communaute7.png", null, false, false, 0, 0, 100, false, null),
    new CaseCaisseCommunaute("../styles/images/communaute/communaute8.png", null, false, false, 0, 0, 20, true, null),
    new CaseCaisseCommunaute("../styles/images/communaute/communaute9.png", null, false, false, 0, 0, 20, false, null),
    new CaseCaisseCommunaute("../styles/images/communaute/communaute10.png", null, false, false, 0, 0, 50, false, null),
    new CaseCaisseCommunaute("../styles/images/communaute/communaute11.png", null, false, false, 50, 0, 0, false, null),
    new CaseCaisseCommunaute("../styles/images/communaute/communaute12.png", null, false, false, 0, 25, 0, false, null),
    new CaseCaisseCommunaute("../styles/images/communaute/communaute13.png", [5, 15, 25, 35], false, false, 0, 0, 0, false, null),
    new CaseCaisseCommunaute("../styles/images/communaute/communaute14.png", null, false, false, 0, 0, 20, false, null),
    new CaseCaisseCommunaute("../styles/images/communaute/communaute15.png", null, false, false, 0, 0, 500, false, null),
    new CaseCaisseCommunaute("../styles/images/communaute/communaute16.png", null, false, false, 0, 0, 0, false, true)
];



// ON a déjà cette fonction sur carte chance mais jsp comment y avoir accès ici
function melangerTableau(tableau) {
    for (let i = tableau.length - 1; i > 0; i--) {
        const j = Math.floor(Math.random() * (i + 1));
        [tableau[i], tableau[j]] = [tableau[j], tableau[i]];
    }
}

// Mélanger aléatoirement le tableau de cartes
melangerTableau(caisseCommunaute);

// Ensuite, vous pouvez ajouter les cartes à la pioche dans l'ordre
let piocheCommunaute = [];
for (let i = 0; i < caisseCommunaute.length; i++) {
    piocheCommunaute.push(caisseCommunaute[i]);
}

// ON a déjà cette fonction sur carte chance mais jsp comment y avoir accès ici
function tirerCarte(pioche) {
    if (mode === 'online'){
        socket.to(roomID).emit('draw card', roomID, 'community')
    }
    return pioche.shift(); // Retire la première carte de la pioche et la renvoie
}

