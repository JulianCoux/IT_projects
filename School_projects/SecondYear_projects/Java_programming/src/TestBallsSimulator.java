import gui.GUISimulator;
import java.awt.Color;
import gui.Rectangle;
import java.awt.Point;
import java.util.Random;

// Classe de test pour BallsSimulator
public class TestBallsSimulator {
    public static void main(String[] args) {
        // Création d'une interface graphique avec un fond noir
        GUISimulator gui = new GUISimulator(500, 500, Color.BLACK);
        int nb_balle = 15;
        // Création d'un tableau de points représentant des cellules vivantes
        Point[] liste = new Point[nb_balle];
        Point[] liste_translate = new Point[nb_balle];
        // Création de quelques points pour représenter des cellules vivantes
        Random rand = new Random();
        for (int i = 0; i < nb_balle; i++) {
            Point p = new Point(rand.nextInt(500), rand.nextInt(500));
            Point t = new Point(rand.nextInt(21) - 10, rand.nextInt(21) - 10);
            liste[i] = p;
            liste_translate[i] = t;
        }


        // Création d'un objet BallsSimulator et association avec l'interface graphique
        gui.setSimulable(new BallsSimulator(gui, liste, liste_translate, nb_balle));
    }
}
