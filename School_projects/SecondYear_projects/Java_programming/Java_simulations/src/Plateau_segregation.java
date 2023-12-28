import gui.Simulable;
import gui.GUISimulator;
import gui.Rectangle;
import java.awt.Color;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;
import java.awt.Point;
import java.util.Map;
import java.util.HashMap;
import java.util.Random;




//La classe Plateau_segregation représente le plateau de jeu pour la simulation de ségrégation.
//Elle étend la classe abstraite Plateau.
public class Plateau_segregation extends Plateau {
    // Variables globales à la classe
    private int[][] nb_voisins; // Matrice pour stocker le nombre de voisins de chaque cellule
    private int taille_cell_larg; // Largeur de chaque cellule sur le plateau
    private int taille_cell_haut; // Hauteur de chaque cellule sur le plateau
    private int nb_etats; // Nombre d'états possibles pour une cellule
    private Color[] tab_couleur; // Tableau de couleurs pour représenter différents états
    private int K; // Paramètre K pour le modèle de ségrégation
    private Map<Coordonnee_segregation, Integer> dico_plateau; // Dictionnaire pour stocker l'état de chaque cellule
    private Map<Coordonnee_segregation, Integer> dico; // Dictionnaire initial contenant les états initiaux des cellules
    private List<Coordonnee_segregation> cood_a_zero = new ArrayList<>(); // Liste des coordonnées initialisées à zéro
    private EventManager eventManager;

    // Constructeur de la classe Plateau_segregation
    public Plateau_segregation(Map<Coordonnee_segregation, Integer> dico, int taille_larg, int taille_haut, GUISimulator gui, int K, int nb_couleurs_C, int taille_plateau_larg, int taille_plateau_haut) {
        super(taille_larg, taille_haut, gui);
        // Initialisation des variables de la classe
        this.dico = dico;
        this.K = K;
        this.taille_cell_larg = taille_plateau_larg / super.taille_larg;
        this.taille_cell_haut = taille_plateau_haut / super.taille_haut;
        this.nb_etats = nb_couleurs_C;
        this.nb_voisins = new int[super.taille_haut][super.taille_larg];
        this.tab_couleur = new Color[nb_couleurs_C + 1];
        this.dico_plateau = new HashMap<>(dico);
        this.eventManager = new EventManager();

        // Initialisation des couleurs avec un dégradé
        tab_couleur[0] = Color.WHITE;
        int increment = 50;
        for (int i = 1; i <= nb_couleurs_C; i++) {
            int r = i * increment % 256;
            int g = (i * increment * 2) % 256;
            int b = (i * increment * 3) % 256;
            tab_couleur[i] = new Color(r, g, b);
        }

        // Initialisation des coordonnées à zéro
        init_cood_zero();

        // Affichage du plateau initial
        display_plateau();

        // Calcul du nombre de voisins pour chaque cellule
        start_nb_voisins();
    }


//Cette méthode calcule le nombre de voisins de chaque cellule sur le plateau, en prenant en compte les états des cellules voisines.
@Override
public void start_nb_voisins() {
    // Parcours de toutes les cellules du plateau
    for (int i = 0; i < super.taille_haut; i++) {
        for (int j = 0; j < super.taille_larg; j++) {
            // Initialisation du compteur de voisins
            int count_voisins = 0;
            Coordonnee_segregation cood_base = new Coordonnee_segregation(j, i);

            // Si l'état de la cellule de base est 0, on passe à la cellule suivante
            if (this.dico_plateau.get(cood_base) == 0) {
                continue;
            }

            // Parcours des cellules voisines (autour de la cellule de base)
            for (int x = -1; x <= 1; x++) {
                for (int y = -1; y <= 1; y++) {
                    if (x == 0 && y == 0) {
                        continue; // On ignore la cellule de base elle-même
                    }

                    // Calcul des coordonnées de la cellule voisine, prenant en compte les bords du plateau
                    int neighborY = ((i + x) % super.taille_haut + super.taille_haut) % super.taille_haut;
                    int neighborX = ((j + y) % super.taille_larg + super.taille_larg) % super.taille_larg;
                    Coordonnee_segregation cood = new Coordonnee_segregation(neighborX, neighborY);

                    // Vérification des états des cellules voisines
                    if (this.dico_plateau.get(cood_base) != this.dico_plateau.get(cood) && this.dico_plateau.get(cood) != 0) {
                        count_voisins++;
                    }
                }
            }

            // Enregistrement du nombre de voisins dans la matrice correspondante
            this.nb_voisins[i][j] = count_voisins;
            }
        }
    }


    
     //Cette méthode réinitialise l'affichage graphique du plateau.
    @Override
    public void display_plateau() {
        gui.reset(); // Réinitialisation de l'interface graphique

        // Parcours de toutes les cellules du plateau
        for (int i = 0; i < super.taille_haut; i++) {
            for (int j = 0; j < super.taille_larg; j++) {
                Coordonnee_segregation cood = new Coordonnee_segregation(j, i);

                // Calcul de la position et ajout d'un rectangle coloré à l'interface graphique
                gui.addGraphicalElement(new Rectangle(this.taille_cell_larg * j, this.taille_cell_haut * i, this.tab_couleur[this.dico_plateau.get(cood)], this.tab_couleur[this.dico_plateau.get(cood)], this.taille_cell_larg));
            }
        }
    }

    
     //Cette méthode génère une représentation textuelle du plateau.
    @Override
    public String toString() {
        String result = "";

        for (int i = 0; i < super.taille_haut; i++) {
            for (int j = 0; j < super.taille_larg; j++) {
                Coordonnee_segregation cood = new Coordonnee_segregation(j, i);

                result += this.dico_plateau.get(cood);
            }

            result += '\n';
        }

        return result;
    }

    
    //Cette méthode redémarre le plateau en réinitialisant les états des cellules,
    @Override
    public void restart() {
        this.dico_plateau = new HashMap<>(dico); // Réinitialisation des états des cellules
        cood_a_zero.clear(); // Effacement des coordonnées initialisées à zéro
        init_cood_zero(); // Initialisation des nouvelles coordonnées à zéro
        display_plateau(); // Mise à jour de l'affichage graphique
        start_nb_voisins(); // Recalcul du nombre de voisins pour chaque cellule
    }


    //Cette méthode effectue une itération du modèle de ségrégation, en déplaçant les individus
    @Override
    public void next() {
    	//Utilisation du gestionnaire d'événement
        eventManager.addEvent(new EventSegregation(eventManager.getCurrentDate(), super.taille_haut, super.taille_larg, this.nb_voisins, this.K, this.dico_plateau, this.cood_a_zero));
        eventManager.next();

        // Mettre à jour l'affichage graphique et recalculer le nombre de voisins pour le prochain tour
        display_plateau();
        start_nb_voisins();
    }



    
    //Cette méthode initialise la liste cood_a_zero avec les coordonnées des cellules vacantes
    public void init_cood_zero() {
        for (Map.Entry<Coordonnee_segregation, Integer> entry : this.dico_plateau.entrySet()) {
            if (entry.getValue() == 0) {
                this.cood_a_zero.add(entry.getKey()); // Ajout de la coordonnée à la liste des cellules vacantes
            }
        }
    }

}
