class Joueur {
    #nom;
    #couleur;
    #solde;
    #proprietes;
    #position;
    #nbTours;
    #compteur_prison;
    #compteur_double;
    #numero;  //utile pour la superposition des pions
    #gares;
    #compagnies;
    #freedom;
    #nbMaison;
    #nbHotel;

    constructor(nom, couleur, solde, position, numero, proprietes = {}, nbTours = 0, compteur_prison = 0, compteur_double = 0, gares = [], compagnies = [], freedom = null, nbMaison = 0, nbHotel = 0) {
        this.#nom = nom;
        this.#couleur = couleur;
        this.#solde = solde;
        this.#position = position;
        this.#nbTours = nbTours;
        this.#compteur_double = compteur_double;
        this.#compteur_prison = compteur_prison;
        this.#numero = numero;
        this.#proprietes = proprietes;
        this.#gares = gares;
        this.#compagnies = compagnies;
        this.#freedom = freedom;
        this.#nbHotel = nbHotel;
        this.#nbMaison = nbMaison;
    }

    // Méthode pour ajouter une propriété au joueur
    ajouterPropriete(couleur, casePropriete) {
        if (!this.#proprietes[couleur]) {
            this.#proprietes[couleur] = []; // Si la couleur n'existe pas encore, initialiser un tableau
        }
        this.#proprietes[couleur].push(casePropriete);
        casePropriete.addProprio(this)
    }

    getNumero(){
        return this.#numero;
    }

    reinitCompteurDouble(){
        this.#compteur_double = 0
    }

    reinitCompteurPrison(){
        this.#compteur_prison = 0
    }

    incrCompteurDouble(){
        this.#compteur_double += 1;
    }

    incrCompteurPrison(){
        this.#compteur_prison += 1;
    }

    getcompteurDouble(){
        return this.#compteur_double;
    }

    getcompteurPrison(){
        return this.#compteur_prison;
    }

    setPosition(newPos){
        this.#position = newPos;
    }

    getPosition(){
        return this.#position;
    }

    setNbTours(newNbTours){
        this.#nbTours = newNbTours;
    }

    getNbTours(){
        return this.#nbTours;
    }

    incrNbTours(){
        this.#nbTours += 1;
    }

    addSolde(augmentation){
        this.#solde += augmentation;
    }

    setSolde(newSolde){
        this.#solde = newSolde;
    }

    suppSolde(diminution){
        this.#solde -= diminution;
    }

    getNom(){
        return this.#nom;
    }

    getCouleur(){
        return this.#couleur;
    }

    getSolde(){
        return this.#solde;
    }

    getPropriete(){
        return this.#proprietes;
    }

    ajouterGare(caseGare){
        this.#gares.push(caseGare);
        caseGare.addProprio(this);
    }

    ajouterCompagnie(caseCompagnie){
        this.#compagnies.push(caseCompagnie);
        caseCompagnie.addProprio(this);
    };

    getGares(){
        return this.#gares;
    }

    getCompagnie(){
        return this.#compagnies;
    }

    addFreedom(carteLiberer){
        this.#freedom = carteLiberer;
    }

    getFreedom(){
        return this.#freedom;
    }

    suppFreedom(){
        this.#freedom = null;
    }

    getNbHotel(){
        return this.#nbHotel;
    }

    setNbHotel(nb){
        this.#nbHotel += nb
    }

    getNbMaison(){
        return this.#nbMaison;
    }

    setNbMaison(nb){
        this.#nbMaison += nb
    }

    getObject(){
        //il faut faire un getObject sur tous les éléments ayant une classe
        // propriété
        // const tableauPropriete = Object.values(this.getPropriete()).map(propriete => propriete.getObject());
        const dicoPropriete = {};

        // Parcourir les clés du dictionnaire this.#propriete
        for (const key in this.#proprietes) {
            if (this.#proprietes.hasOwnProperty(key)) {
                // Récupérer le tableau d'objets pour la clé actuelle
                const tableauObjets = this.#proprietes[key];

                // Map sur chaque objet pour obtenir le numéro de case
                // Assigner le tableau de numéros de case au dictionnaire avec la même clé tableauObjets.map(objet => objet.getObject());
                dicoPropriete[key] = tableauObjets.map(objet => objet.getNumeroCase());
            }
        }

        //Gare à tester
        const tableauGare = this.#gares.map(gare => gare.getNumeroCase());

        //COmpagnie à tester
        const tableauCompagnie = this.#compagnies.map(compagnie => compagnie.getNumeroCase());

        //freedom à tester
        let res = null;
        if (this.#freedom){
            res = this.#freedom.getUrl();
        }
        const freedomUrl = res;

        return {
            nom:this.#nom,
            couleur: this.#couleur,
            solde: this.#solde,
            proprietes: dicoPropriete,//Juste avece couple {couleur:position}
            position: this.#position,
            nbTours: this.#nbTours,
            compteur_prison: this.#compteur_prison,
            compteur_double: this.#compteur_double,
            numero: this.#numero,
            gares: tableauGare,
            compagnies: tableauCompagnie,
            freedom: freedomUrl,
            nbMaison: this.#nbMaison,
            nbHotel: this.#nbHotel
        }
    }

}
