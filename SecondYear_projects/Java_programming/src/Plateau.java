import gui.Simulable;
import gui.GUISimulator;

// Classe parente abstraite
public abstract class Plateau implements Simulable {
    // Attributs protégés pour la taille du plateau
    protected int taille_larg;
    protected int taille_haut;

    // Attribut protégé pour l'interface graphique
    protected GUISimulator gui;

    // Constructeur de la classe Plateau
    public Plateau(int taille_larg, int taille_haut, GUISimulator gui) {
        this.taille_larg = taille_larg;
        this.taille_haut = taille_haut;
        this.gui = gui;
    }

    // Méthode abstraite pour redémarrer le plateau
    public abstract void restart();

    // Méthode abstraite pour passer à l'étape suivante du plateau
    public abstract void next();

    // Méthode abstraite pour initialiser le nombre de voisins pour chaque cellule
    public abstract void start_nb_voisins();

    // Méthode abstraite pour afficher graphiquement le plateau
    public abstract void display_plateau();
}
