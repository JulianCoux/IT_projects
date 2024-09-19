class CasePropriete {
    #nom;
    #urlImage;
    #proprietaire;
    #prix;
    #loyer;
    #couleur;
    #nombreMaison;
    #nombreHotel;
    #prixMaison;
    #prixHotel;
    #nbLot;
    #pionProprietaire_TOP;
    #pionProprietaire_LEFT;
    #numeroCase;

    constructor(nom, urlImage, proprietaire, prix, loyer, couleur, nombreMaison, nombreHotel, prixMaison, prixHotel, nbLot, pionProprietaire_TOP, pionProprietaire_LEFT, numeroCase) {
        this.#nom = nom;
        this.#urlImage = urlImage;
        this.#proprietaire = proprietaire;
        this.#prix = prix;
        this.#loyer = loyer;
        this.#couleur = couleur;
        this.#nombreMaison = nombreMaison;
        this.#nombreHotel = nombreHotel;
        this.#prixMaison = prixMaison;
        this.#prixHotel = prixHotel;
        this.#nbLot = nbLot; //Nb de carte de la même couleur
        this.#pionProprietaire_TOP = pionProprietaire_TOP;
        this.#pionProprietaire_LEFT = pionProprietaire_LEFT;
        this.#numeroCase = numeroCase;
    }

    // Méthode pour calculer le loyer associé à cette carte propriété
    calculLoyer() {
        const proprio = this.#proprietaire;
        const listePropriete = proprio.getPropriete();
        if (listePropriete[this.#couleur].length === this.#nbLot) {
            if (this.#nombreHotel === 1) {
                return this.#loyer[6];
            } else if (this.#nombreMaison !== 0) {
                return this.#loyer[1 + this.#nombreMaison];
            } else {
                return this.#loyer[1];
            }
        } else {
            return this.#loyer[0];
        }
    }

    // Getters
    getNom() {
        return this.#nom;
    }

    getUrl() {
        return this.#urlImage;
    }

    getProprietaire() {
        return this.#proprietaire;
    }

    getPrix() {
        return this.#prix;
    }

    getLoyer() {
        return this.#loyer;
    }

    getCouleur() {
        return this.#couleur;
    }

    getNombreMaison() {
        return this.#nombreMaison;
    }

    getNombreHotel() {
        return this.#nombreHotel;
    }

    getPrixMaison() {
        return this.#prixMaison;
    }

    getPrixHotel() {
        return this.#prixHotel;
    }

    getNbLot() {
        return this.#nbLot;
    }

    addProprio(joueur){
        this.#proprietaire = joueur;
    }

    setNbMaison(nb) {
        this.#nombreMaison += nb;
    }

    setNbHotel(nb){
        this.#nombreHotel += nb;
    }

    getCoodPionProprietaire(){
        return [this.#pionProprietaire_TOP, this.#pionProprietaire_LEFT];
    }

    getNumeroCase(){
        return this.#numeroCase;
    }

    getObject(){
        return {
            nom: this.#nom,
            urlImage: this.#urlImage,
            proprietaire: this.#proprietaire,
            prix: this.#prix,
            loyer: this.#loyer,
            couleur: this.#couleur,
            nombreMaison: this.#nombreMaison,
            nombreHotel: this.#nombreHotel,
            prixMaison: this.#prixMaison,
            prixHotel: this.#prixHotel,
            nbLot: this.#nbLot,
            pionProprietaire_TOP: this.#pionProprietaire_TOP,
            pionProprietaire_LEFT: this.#pionProprietaire_LEFT,
            numeroCase: this.#numeroCase
        };
    }
}


// Dictionnaires des propriétés avec comme clé la position sur le plateau
var carteDict = {
    1 : new CasePropriete("Boulevard Gambetta", "../styles/images/carte/boulevardGambetta.png", null, 60, [2, 4, 10, 30, 90, 160, 250], "marron", 0, 0, 50, 30, 2, 88, 80, 1),
    3 : new CasePropriete("Cours la Fontaine", "../styles/images/carte/coursLaFontaine.png", null, 60, [4, 8, 20, 60, 180, 320, 450], "marron", 0, 0, 50, 30, 2, 88, 64, 3),
    6 : new CasePropriete("Pont du Vercors", "../styles/images/carte/pontDuVercors.png", null, 100, [6, 12, 30, 90, 270, 400, 550], "bleu clair", 0, 0, 50, 50, 3, 88, 39, 6),
    8 : new CasePropriete("Chemin de la Bastille", "../styles/images/carte/cheminDeLaBastille.png", null, 100, [6, 12, 30, 90, 270, 400, 550], "bleu clair", 0, 0, 50, 50, 3, 88, 23, 8),
    9 : new CasePropriete("Rue de la Paix", "../styles/images/carte/rueDeLaPaix.png", null, 120, [8, 16, 40, 100, 300, 450, 600], "bleu clair", 0, 0, 50, 60, 3, 88, 15, 9),
    11 : new CasePropriete("Place de l'Étoile", "../styles/images/carte/placeDeLEtoile.png", null, 140, [10, 20, 50, 150, 450, 625, 750], "rose", 0, 0, 100, 70, 3, 80, 12, 11),
    13 : new CasePropriete("Boulevard Jean Pain", "../styles/images/carte/boulevardJeanPain.png", null, 140, [10, 20, 50, 150, 450, 625, 750], "rose", 0, 0, 100, 70, 3, 64, 12, 13),
    14 : new CasePropriete("Rue de la Poste", "../styles/images/carte/rueDeLaPoste.png", null, 160, [12, 24, 60, 180, 500, 700, 900], "rose", 0, 0, 100, 80, 3, 56, 12, 14),
    16 : new CasePropriete("Avenue de l'Europe", "../styles/images/carte/avenueDeLEurope.png", null, 180, [14, 28, 70, 200, 550, 700, 900], "orange", 0, 0, 100, 90, 3, 40, 12, 16),
    18 : new CasePropriete("Cours Jean Jaurès", "../styles/images/carte/coursJeanJaures.png", null, 180, [14, 28, 70, 200, 550, 700, 950], "orange", 0, 0, 100, 90, 3, 23, 12, 18),
    19 : new CasePropriete("Rue de la Biscuiterie", "../styles/images/carte/rueDeLaBiscuiterie.png", null, 200, [16, 32, 80, 220, 600, 800, 1000], "orange", 0, 0, 100, 100, 3, 15, 12, 19),
    21 : new CasePropriete("Place Notre Dame", "../styles/images/carte/placeNotreDame.png", null, 220, [18, 36, 90, 250, 700, 875, 1050], "rouge", 0, 0, 150, 110, 3, 12, 20, 21),
    23 : new CasePropriete("Boulevard Maréchal Foch", "../styles/images/carte/boulevardMarechalFoch.png", null, 220, [18, 36, 90, 250, 700, 875, 1050], "rouge", 0, 0, 150, 110, 3, 12, 36, 23),
    24 : new CasePropriete("Rue Marcel Peretto", "../styles/images/carte/rueMarcelPeretto.png", null, 240, [20, 40, 100, 300, 750, 925, 1100], "rouge", 0, 0, 150, 120, 3, 12, 44, 24),
    26 : new CasePropriete("Rue des Fleurs", "../styles/images/carte/rueDesFleurs.png", null, 260, [22, 44, 110, 330, 800, 975, 1150], "jaune", 0, 0, 150, 130, 3, 12, 61, 26),
    27 : new CasePropriete("Avenue Alsace-Lorraine", "../styles/images/carte/avenueAlsaceLorraine.png", null, 260, [22, 44, 110, 330, 800, 975, 1150], "jaune", 0, 0, 150, 130, 3, 12, 69, 27),
    29 : new CasePropriete("Pont de la Porte de France", "../styles/images/carte/pontDeLaPorteDeFrance.png", null, 280, [24, 48, 120, 360, 850, 1025, 1200], "jaune", 0, 0, 150, 140, 3, 12, 85, 29),
    31 : new CasePropriete("Jardin de Ville", "../styles/images/carte/jardinDeVille.png", null, 300, [26, 52, 130, 390, 900, 1100, 1275], "vert", 0, 0, 200, 150, 3, 20, 88, 31),
    32 : new CasePropriete("Boulevard Joseph Vallier", "../styles/images/carte/boulevardJosephVallier.png", null, 300, [26, 52, 130, 390, 900, 1100, 1275], "vert", 0, 0, 200, 150, 3, 28, 88, 32),
    34 : new CasePropriete("Place du Dr.Girard", "../styles/images/carte/placeDuDrGirard.png", null, 320, [28, 56, 150, 450, 1000, 1200, 1400], "vert", 0, 0, 200, 160, 3, 44, 88, 34),
    37 : new CasePropriete("Rue Lafayette", "../styles/images/carte/rueLafayette.png", null, 350, [35, 70, 175, 500, 1100, 1300, 1500], "bleu foncé", 0, 0, 200, 175, 2, 69, 88, 37),
    39 : new CasePropriete("Rue des Mathématiques", "../styles/images/carte/rueDesMathematiques.png", null, 400, [50, 100, 200, 600, 1400, 1700, 2000], "bleu foncé", 0, 0, 200, 200, 2, 85, 88, 39)
};