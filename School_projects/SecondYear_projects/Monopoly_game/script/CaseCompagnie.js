class CaseCompagnie {
    #urlImage;
    #proprietaire;
    #pionProprietaire_TOP;
    #pionProprietaire_LEFT;
    #numeroCase;

    constructor(url, proprietaire, pionProprietaire_TOP, pionProprietaire_LEFT, numeroCase){
        this.#urlImage = url;
        this.#proprietaire = proprietaire;
        this.#pionProprietaire_TOP = pionProprietaire_TOP;
        this.#pionProprietaire_LEFT = pionProprietaire_LEFT;
        this.#numeroCase = numeroCase;
    }

    getUrl(){
        return this.#urlImage;
    }

    getProprietaire(){
        return this.#proprietaire = this.#proprietaire;
    }

    getPrix(){
        return 150;
    }

    calculLoyer(){
        // 4* le montant des dés si pas les deux sinon 10*
        const nbCompagnie = this.#proprietaire.getCompagnie().length;
        let mult;
        if (nbCompagnie === 2){
            mult = 10;
        } else {
            mult = 4;
        }
        return sommeDesDes*mult; 
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

// Dictionnaires des compagnies avec comme clé la position sur le plateau
const compagnieDict = {
    12: new CaseCompagnie("../styles/images/compagnie/compagnieElectricite.png", null, 72, 12, 12),
    28: new CaseCompagnie("../styles/images/compagnie/compagnieEau.png", null, 12, 77, 28)
};