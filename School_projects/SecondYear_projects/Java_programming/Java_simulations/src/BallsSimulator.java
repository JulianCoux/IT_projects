import gui.Simulable;
import gui.GUISimulator;
import gui.Rectangle;
import java.awt.Color;
import java.awt.Point;

// La classe BallsSimulator implémente l'interface Simulable pour la simulation
public class BallsSimulator implements Simulable {
    private GUISimulator gui;
    private Balls balles;
    private EventManager eventManager;
    private int nb_balle;
    private Point[] liste_translate;
    // Constructeur prenant une interface graphique et un tableau de Points représentant les positions initiales des balles
    public BallsSimulator(GUISimulator gui, Point[] tab_pts, Point[] liste_translate, int nb_balle) {
        this.gui = gui;
        this.balles = new Balls(tab_pts);
        this.eventManager = new EventManager();
        this.liste_translate = liste_translate;
        this.nb_balle = nb_balle;
        balles.reInit();
        displayBalls(); // Affiche les balles sur l'interface graphique
    }

    // Méthode appelée lors de l'avancement de la simulation
    @Override
    public void next() {
        eventManager.addEvent(new EventBalls(eventManager.getCurrentDate(), this.balles, this.gui, this.liste_translate, this.nb_balle));
        eventManager.next();
        displayBalls(); // Rafraîchit l'affichage des balles sur l'interface graphique
        System.out.println(balles.toString()); // Affiche les positions actuelles des balles dans la console
    }

    // Méthode appelée lors de la réinitialisation de la simulation
    @Override
    public void restart() {
        balles.reInit(); // Réinitialise les positions courantes des balles aux positions initiales
        displayBalls(); // Rafraîchit l'affichage des balles sur l'interface graphique
        System.out.println(balles.toString()); // Affiche les positions actuelles des balles dans la console
    }

    // Méthode privée pour afficher les balles sur l'interface graphique
    private void displayBalls() {
        gui.reset(); // Réinitialise l'interface graphique
        for (Point point : balles.pos_courante_balles) {
            // Ajoute un rectangle représentant chaque balle sur l'interface graphique
            gui.addGraphicalElement(new Rectangle(point.x, point.y, Color.BLUE, Color.BLUE, 10));
        }
    }


}
