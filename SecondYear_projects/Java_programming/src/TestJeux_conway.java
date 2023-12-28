import gui.GUISimulator;
import java.awt.Color;
import gui.Rectangle;
import java.util.Random;

import java.awt.Point;

public class TestJeux_conway {
    public static void main(String[] args) {
        int nb_point = 5;
        // Création d'un tableau de points représentant des cellules vivantes
        Point[] liste = new Point[nb_point];

        // Création de quelques points pour représenter des cellules vivantes
        Random rand = new Random();
        for (int i = 0; i < nb_point; i++){
            Point p;
            do {
                p = new Point(rand.nextInt(4), rand.nextInt(4));
            } while (pointExiste(p, liste, i));
            liste[i] = p;
        }

        // Création d'une interface graphique avec un fond noir
        GUISimulator gui = new GUISimulator(500, 500, Color.BLACK);

        // Création d'un plateau de Conway avec les cellules vivantes spécifiées, taille 5x5 et l'interface graphique
        Plateau_conway plateau = new Plateau_conway(liste, 5, 5, gui);

        // Définition du plateau de Conway comme l'objet à simuler dans l'interface graphique
        gui.setSimulable(plateau);
        
    }

    // Méthode pour vérifier si un point existe déjà dans le tableau
    private static boolean pointExiste(Point p, Point[] tableau, int index) {
        for (int i = 0; i < index; i++) {
            if (tableau[i].equals(p)) {
                return true; // Le point existe déjà dans le tableau
            }
        }
        return false; // Le point est unique dans le tableau
    }
}
