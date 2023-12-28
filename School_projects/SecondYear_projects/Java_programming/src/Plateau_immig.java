import gui.Simulable;
import gui.GUISimulator;
import gui.Rectangle;
import java.awt.Color;
import java.util.Arrays;
import java.awt.Point;

//La classe Plateau_segregation représente le plateau de jeu pour la simulation de l'immigration.
//Elle étend la classe abstraite Plateau.
public class Plateau_immig extends Plateau {
    // Variables globales à la classe
    private int[][] tab_vie;
    private int nb_cel_vie;
    private Cellule_immig[][] plateau;
    private int[][][] nb_voisins;
    private int taille_cell_larg;
    private int taille_cell_haut;
    private int nb_etats;
    private Color[] tab_couleur;
    private EventManager eventManager;

    // Constructeur de la classe Plateau_immig
    public Plateau_immig(int[][] tab_vie, int taille_larg, int taille_haut, GUISimulator gui, int nb_etats, int taille_plateau_larg, int taille_plateau_haut) {
        super(taille_larg, taille_haut, gui);
        this.tab_vie = tab_vie;
        this.nb_cel_vie = tab_vie.length;
        this.taille_cell_larg = taille_plateau_larg / super.taille_larg;
        this.taille_cell_haut = taille_plateau_haut / super.taille_haut;
        this.nb_etats = nb_etats;
        this.plateau = new Cellule_immig[super.taille_haut][super.taille_larg];
        this.nb_voisins = new int[super.taille_haut][super.taille_larg][nb_etats];
        this.tab_couleur = new Color[nb_etats];
        this.eventManager = new EventManager();

        // Initialisation des couleurs pour chaque état
        int u = nb_etats - 1;
        for (int i = 0; i < nb_etats; i++) {
            int greyValue = 255 * i / (nb_etats - 1);  // Calcul de la valeur de gris pour chaque nuance
            this.tab_couleur[u] = new Color(greyValue, greyValue, greyValue);  // Noir => etat="dernier_etat"
            u--;
        }

        // Mettre tout à 0 par défaut
        for (int i = 0; i < super.taille_haut; i++) {
            for (int j = 0; j < super.taille_larg; j++) {
                plateau[i][j] = new Cellule_immig(0);
            }
        }

        // Initialiser les bonnes valeurs des états
        for (int i = 0; i < nb_cel_vie; i++) {
            this.plateau[tab_vie[i][1]][tab_vie[i][0]].set_etat(tab_vie[i][2]);
        }
        display_plateau();
        start_nb_voisins();
    }

    @Override
    public void start_nb_voisins() {
        for (int i = 0; i < super.taille_haut; i++) {
            for (int j = 0; j < super.taille_larg; j++) {
                // Initialiser un tableau à 0 pour compter le nombre de voisins de chaque état
                int[] count_voisins = new int[nb_etats];
                Arrays.fill(count_voisins, 0);

                // Compter les voisins pour chaque état
                count_voisins[this.plateau[((i - 1) % super.taille_haut + super.taille_haut) % super.taille_haut][((j - 1) % super.taille_larg + super.taille_larg) % super.taille_larg].get_etat()] += 1; // en haut à gauche
                count_voisins[this.plateau[i][((j - 1) % super.taille_larg + super.taille_larg) % super.taille_larg].get_etat()] += 1; // à gauche
                count_voisins[this.plateau[((i + 1) % super.taille_haut + super.taille_haut) % super.taille_haut][((j - 1) % super.taille_larg + super.taille_larg) % super.taille_larg].get_etat()] += 1; // en bas à gauche
                count_voisins[this.plateau[((i - 1) % super.taille_haut + super.taille_haut) % super.taille_haut][j].get_etat()] += 1; // en haut
                count_voisins[this.plateau[((i + 1) % super.taille_haut + super.taille_haut) % super.taille_haut][j].get_etat()] += 1; // en bas
                count_voisins[this.plateau[((i - 1) % super.taille_haut + super.taille_haut) % super.taille_haut][((j + 1) % super.taille_larg + super.taille_larg) % super.taille_larg].get_etat()] += 1; // en haut à droite
                count_voisins[this.plateau[i][((j + 1) % super.taille_larg + super.taille_larg) % super.taille_larg].get_etat()] += 1; // à droite
                count_voisins[this.plateau[((i + 1) % super.taille_haut + super.taille_haut) % super.taille_haut][((j + 1) % super.taille_larg + super.taille_larg) % super.taille_larg].get_etat()] += 1; // en bas à droite

                // Assigner le tableau de voisins à la position correspondante dans le tableau 3D nb_voisins
                this.nb_voisins[i][j] = count_voisins;
            }
        }
    }

    @Override
    public void display_plateau() {
        // Réinitialiser l'interface graphique
        gui.reset();
        // Parcourir le tableau du plateau
        for (int i = 0; i < super.taille_haut; i++) {
            for (int j = 0; j < super.taille_larg; j++) {
                // Créer un rectangle représentant l'état de la cellule
                // avec une couleur correspondante
                gui.addGraphicalElement(new Rectangle(this.taille_cell_larg * j, this.taille_cell_haut * i, this.tab_couleur[this.plateau[i][j].get_etat()], this.tab_couleur[this.plateau[i][j].get_etat()], this.taille_cell_larg));
            }
        }
    }
    
    @Override
    public String toString() {
        String var1 = "";
        for (int i = 0; i < super.taille_haut; i++) {
            for (int j = 0; j < super.taille_larg; j++) {
                var1 += this.plateau[i][j].toString();
            }
            var1 += '\n';
        }
        return var1;
    }
    
    @Override
    public void restart() {
        // Mettre toutes les cellules à l'état 0 par défaut
        for (int i = 0; i < super.taille_haut; i++) {
            for (int j = 0; j < super.taille_larg; j++) {
                this.plateau[i][j].set_etat(0);
            }
        }
        // Initialiser les bonnes valeurs des états pour les cellules vivantes
        for (int i = 0; i < this.nb_cel_vie; i++) {
            this.plateau[this.tab_vie[i][1]][this.tab_vie[i][0]].set_etat(this.tab_vie[i][2]);
        }
        // Mettre à jour l'affichage et recalculer les voisins
        display_plateau();
        start_nb_voisins();
    }
    
    @Override
    public void next() {
    	//Utilisation du gestionnaire d'événement
        eventManager.addEvent(new EventImmig(eventManager.getCurrentDate(), super.taille_haut, super.taille_larg, this.plateau, this.nb_etats, this.nb_voisins));
        eventManager.next();
        // Mettre à jour l'affichage et recalculer les voisins
        display_plateau();
        start_nb_voisins();
    }
    
}
