// Importation des classes nécessaires pour l'interface graphique
import gui.Simulable;
import gui.GUISimulator;

// Importation de la classe Point pour représenter les coordonnées des balles
import java.awt.*;

// Classe représentant un événement de déplacement des balles
public class EventBalls extends Event {

    // Attributs de la classe
    private GUISimulator gui;         // Interface graphique
    private Balls balles;             // Objet contenant les balles
    private Point[] liste_translate;   // Tableau de coordonnées de translation pour chaque balle
    private int nb_balle;              // Nombre de balles

    // Constructeur prenant en paramètres la date, l'objet Balls, l'interface graphique, le tableau de translation, et le nombre de balles
    public EventBalls(long date, Balls balles, GUISimulator gui, Point[] liste_translate, int nb_balle) {
        super(date);
        this.balles = balles;
        this.gui = gui;
        this.liste_translate = liste_translate;
        this.nb_balle = nb_balle;
    }

    // Méthode d'exécution de l'événement
    @Override
    public void execute() {
        // Boucle sur chaque balle pour effectuer la translation
        for (int i = 0; i < nb_balle; i++) {
            // Appel de la méthode translate de l'objet Balls pour déplacer la balle
            this.balles.translate(liste_translate[i].x, liste_translate[i].y, balles.pos_courante_balles[i], i, this.liste_translate);
        }
    }
}

