import gui.GUISimulator;
import gui.Rectangle;
import java.awt.Color;
import java.awt.Point;

//La classe Plateau_conway représente le plateau de jeu pour la simulation du jeu de la vie.
//Elle étend la classe abstraite Plateau.
public class Plateau_conway extends Plateau {
    private Point[] tab_vie;  // Tableau de points représentant les cellules vivantes
    private int nb_cel_vie;   // Nombre de cellules vivantes
    private Cellule_conway[][] plateau;  // Plateau de cellules Conway
    private int[][] nb_voisins;  // Tableau pour stocker le nombre de voisins de chaque cellule
    private int taille_cell_larg;  // Taille en largeur de chaque cellule sur l'interface graphique
    private int taille_cell_haut;  // Taille en hauteur de chaque cellule sur l'interface graphique
    private EventManager eventManager;
    // Constructeur
    public Plateau_conway(Point[] tab_vie, int taille_larg, int taille_haut, GUISimulator gui) {
        super(taille_larg, taille_haut, gui);
        this.tab_vie = tab_vie;
        this.nb_cel_vie = tab_vie.length;
        this.taille_cell_larg = 500 / super.taille_larg;
        this.taille_cell_haut = 500 / super.taille_haut;
        this.plateau = new Cellule_conway[super.taille_haut][super.taille_larg];
        this.nb_voisins = new int[super.taille_haut][super.taille_larg];
        this.eventManager = new EventManager();
        // Initialisation du plateau avec des cellules mortes
        for (int i = 0; i < super.taille_haut; i++) {
            for (int j = 0; j < super.taille_larg; j++) {
                plateau[i][j] = new Cellule_conway(false);
            }
        }

        // Initialisation des cellules vivantes
        for (int i = 0; i < nb_cel_vie; i++) {
            plateau[tab_vie[i].y][tab_vie[i].x].set_etat(true);
        }

        // Affichage initial du plateau
        display_plateau();

        // Initialisation du nombre de voisins pour chaque cellule
        start_nb_voisins();
    }


    // Méthode pour initialiser le nombre de voisins de chaque cellule
    @Override
    public void start_nb_voisins(){
        for(int i=0; i<super.taille_haut; i++){
            for(int j=0; j<super.taille_larg  ; j++){
                int count_voisins = 0;
                // Calcul du nombre de voisins en utilisant les règles du jeu de la vie
                count_voisins += this.plateau[((i - 1) % super.taille_haut + super.taille_haut) % super.taille_haut][((j - 1) % super.taille_larg + super.taille_larg) % super.taille_larg].get_etat() ? 1:0; //en haut à gauche
                count_voisins += this.plateau[i][((j - 1) % super.taille_larg + super.taille_larg) % super.taille_larg].get_etat() ? 1:0; //à gauche
                count_voisins += this.plateau[((i + 1) % super.taille_haut + super.taille_haut) % super.taille_haut][((j - 1) % super.taille_larg + super.taille_larg) % super.taille_larg].get_etat() ? 1:0; //en bas à gauche
                count_voisins += this.plateau[((i - 1) % super.taille_haut + super.taille_haut) % super.taille_haut][j].get_etat() ? 1:0; //en haut
                count_voisins += this.plateau[((i + 1) % super.taille_haut + super.taille_haut) % super.taille_haut][j].get_etat() ? 1:0; //en bas
                count_voisins += this.plateau[((i - 1) % super.taille_haut + super.taille_haut) % super.taille_haut][((j + 1) % super.taille_larg + super.taille_larg) % super.taille_larg].get_etat() ? 1:0; //en haut à droite
                count_voisins += this.plateau[i][((j + 1) % super.taille_larg + super.taille_larg) % super.taille_larg].get_etat() ? 1:0; //à droite
                count_voisins += this.plateau[((i + 1) % super.taille_haut + super.taille_haut) % super.taille_haut][((j + 1) % super.taille_larg + super.taille_larg) % super.taille_larg].get_etat() ? 1:0; //en bas à droite
                // Stockage du nombre de voisins dans le tableau nb_voisins
                this.nb_voisins[i][j] = count_voisins;
            }
        }
    }

    // Méthode pour afficher le plateau sur l'interface graphique
    @Override
    public void display_plateau() {
        super.gui.reset();
        for (int i = 0; i < super.taille_haut; i++) {
            for (int j = 0; j < super.taille_larg; j++) {
                if (this.plateau[i][j].get_etat()) {
                    // Si la cellule est vivante, affiche un rectangle bleu
                    super.gui.addGraphicalElement(new Rectangle(this.taille_cell_larg * j, this.taille_cell_haut * i, Color.BLUE, Color.BLUE, this.taille_cell_larg));
                } else {
                    // Sinon, affiche un rectangle blanc
                    super.gui.addGraphicalElement(new Rectangle(this.taille_cell_larg * j, this.taille_cell_haut * i, Color.WHITE, Color.WHITE, this.taille_cell_larg));
                }
            }
        }
    }

    // Méthode pour obtenir une représentation sous forme de chaîne de caractères du plateau
    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < super.taille_haut; i++) {
            for (int j = 0; j < super.taille_larg; j++) {
                result.append(this.plateau[i][j].toString());
            }
            result.append('\n');
        }
        return result.toString();
    }

    // Méthode pour réinitialiser le plateau
    @Override
    public void restart() {
        for (int i = 0; i < super.taille_haut; i++) {
            for (int j = 0; j < super.taille_larg; j++) {
                // Réinitialise toutes les cellules à l'état mort
                this.plateau[i][j].set_etat(false);
            }
        }

        // Initialise les cellules vivantes à l'état vivant
        for (int i = 0; i < this.nb_cel_vie; i++) {
            plateau[this.tab_vie[i].y][this.tab_vie[i].x].set_etat(true);
        }

        // Affiche le plateau mis à jour
        display_plateau();

        // Recalcule le nombre de voisins
        start_nb_voisins();
    }

    // Méthode pour calculer la prochaine étape du jeu
    @Override
    public void next() {
    	//Utilisation du gestionnaire d'événement
        eventManager.addEvent(new EventConway(eventManager.getCurrentDate(), this.gui, super.taille_haut, super.taille_larg, this.nb_voisins, this.plateau));
        eventManager.next();

        // Recalcule le nombre de voisins
        start_nb_voisins();

        // Affiche le plateau mis à jour
        display_plateau();
    }
}


