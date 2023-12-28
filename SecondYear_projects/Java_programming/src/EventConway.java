// Importation de la classe GUISimulator pour l'interface graphique
import gui.GUISimulator;

// Classe représentant un événement de mise à jour de la simulation Conway
public class EventConway extends Event {
    
    // Attributs de la classe
    private GUISimulator gui;               // Interface graphique
    private int taille_haut;                // Taille de la hauteur du plateau
    private int taille_larg;                // Taille de la largeur du plateau
    private int[][] nb_voisins;             // Tableau du nombre de voisins pour chaque cellule
    private Cellule_conway[][] plateau;     // Plateau de cellules Conway

    // Constructeur prenant en paramètres la date, l'interface graphique, la taille du plateau, le tableau de voisins et le plateau de cellules
    public EventConway(long date, GUISimulator gui, int taille_haut, int taille_larg, int[][] nb_voisins, Cellule_conway[][] plateau) {
        super(date);
        this.gui = gui;
        this.taille_haut = taille_haut;
        this.taille_larg = taille_larg;
        this.nb_voisins = nb_voisins;
        this.plateau = plateau;
    }

    // Méthode d'exécution de l'événement
    @Override
    public void execute() {
        // Parcours de chaque cellule du plateau
        for (int i = 0; i < this.taille_haut; i++) {
            for (int j = 0; j < this.taille_larg; j++) {
                // Applique les règles du jeu de la vie pour déterminer le prochain état de chaque cellule
                if (nb_voisins[i][j] == 3 && !this.plateau[i][j].get_etat()) {
                    // Naissance d'une cellule si elle a exactement 3 voisins et est morte
                    this.plateau[i][j].set_etat(true);
                } else if ((nb_voisins[i][j] < 2 || nb_voisins[i][j] > 3) && this.plateau[i][j].get_etat()) {
                    // Mort d'une cellule si elle a moins de 2 ou plus de 3 voisins et est vivante
                    this.plateau[i][j].set_etat(false);
                }
            }
        }
    }
}

