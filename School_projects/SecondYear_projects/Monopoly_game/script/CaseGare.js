class CaseGare {
    #urlImage;
    #proprietaire;
    #pionProprietaire_TOP;
    #pionProprietaire_LEFT;
    #numeroCase;

    constructor(url, proprietaire, pionProprietaire_TOP, pionProprietaire_LEFT, numeroCase) {
        this.#urlImage = url;
        this.#proprietaire = proprietaire;
        this.#pionProprietaire_TOP = pionProprietaire_TOP;
        this.#pionProprietaire_LEFT = pionProprietaire_LEFT;
        this.#numeroCase = numeroCase;
    }

    calculLoyer(){
        // Aller chercher le nombre de gare que le propriétaire possède et return [25, 50, 100, 200];
        const loyer = [25, 50, 100, 200]
        const gares = this.#proprietaire.getGares();
        return loyer[gares.length - 1];
    }

    getUrl(){
        return this.#urlImage;
    }

    getProprietaire(){
        return this.#proprietaire;
    }

    getPrix(){
        return 200;
    }

    addProprio(joueur){
        this.#proprietaire = joueur;
    }

    getCoodPionProprietaire(){
        return [this.#pionProprietaire_TOP, this.#pionProprietaire_LEFT];
    }

    getNumeroCase(){
        return this.#numeroCase;
    }
}

// Dictionnaires des gares avec comme clé la position sur le plateau
const gareDict = {
    5: new CaseGare("../styles/images/arret/victorHugo.png", null, 88, 47, 5),
    15: new CaseGare("../styles/images/arret/gareGrenoble.png", null, 48, 12, 15),
    25: new CaseGare("../styles/images/arret/chavant.png", null, 12, 53, 25),
    35: new CaseGare("../styles/images/arret/hubertDubedout.png", null, 53, 88, 35)
};