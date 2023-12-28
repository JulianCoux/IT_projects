// Classe représentant un événement de progression dans le jeu Immig
public class EventImmig extends Event {
    
    // Attributs de la classe
    private int taille_haut;                // Taille de la hauteur du plateau
    private int taille_larg;                // Taille de la largeur du plateau
    private Cellule_immig[][] plateau;     // Plateau de cellules Immig
    private int nb_etats;                   // Nombre d'états possibles pour une cellule
    private int[][][] nb_voisins;           // Tableau du nombre de voisins pour chaque état de chaque cellule

    // Constructeur prenant en paramètres la date, la taille du plateau, le plateau de cellules, le nombre d'états et le tableau de voisins
    public EventImmig(long date, int taille_haut, int taille_larg, Cellule_immig[][] plateau, int nb_etats, int[][][] nb_voisins) {
        super(date);
        this.taille_haut = taille_haut;
        this.taille_larg = taille_larg;
        this.plateau = plateau;
        this.nb_etats = nb_etats;
        this.nb_voisins = nb_voisins;
    }

    // Méthode d'exécution de l'événement
    @Override
    public void execute() {
        // Progression d'une étape dans le jeu
        for (int i = 0; i < this.taille_haut; i++) {
            for (int j = 0; j < this.taille_larg; j++) {
                // Vérifier les conditions pour changer l'état de la cellule
                if (this.plateau[i][j].get_etat() < this.nb_etats - 1) {
                    // Si l'état actuel de la cellule est inférieur au nombre maximal d'états
                    if (this.nb_voisins[i][j][this.plateau[i][j].get_etat() + 1] >= 3) {
                        // Changer l'état de la cellule si le nombre de voisins de l'état suivant est suffisant
                        this.plateau[i][j].set_etat(this.plateau[i][j].get_etat() + 1);
                    }
                } else {
                    // Si l'état actuel est le dernier état possible, revenir à l'état initial si le nombre de voisins est suffisant
                    if (this.nb_voisins[i][j][0] >= 3) {
                        this.plateau[i][j].set_etat(0);
                    }
                }
            }
        }
    }
}

