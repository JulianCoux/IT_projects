import gui.GUISimulator;
import java.awt.Color;
import gui.Rectangle;
import java.util.Random;
import java.awt.Point;

public class TestJeux_immig {
    public static void main(String[] args) {
        // Définition des paramètres du plateau et des cellules
        int taille_plateau_larg = 1000;
        int taille_plateau_haut = 1000;
        int nb_cel_larg = 100;
        int nb_cel_haut = 100;
        int index_i = 0;  // Indice pour remplir la liste des cellules
        int nb_etats = 4;  // Nombre d'états possibles pour chaque cellule
        int[][] liste = new int[nb_cel_larg * nb_cel_haut][3];  // Tableau pour stocker les informations sur chaque cellule
        Random rand = new Random();

        // Remplissage de la liste avec des coordonnées et des états aléatoires pour chaque cellule
        for (int i = 0; i < nb_cel_haut; i++) {
            for (int j = 0; j < nb_cel_larg; j++) {
                liste[index_i][0] = j;  // Coordonnée en x
                liste[index_i][1] = i;  // Coordonnée en y
                liste[index_i][2] = rand.nextInt(nb_etats);  // État aléatoire entre 0 et nb_etats-1
                index_i++;
            }
        }

        // Création d'une interface graphique avec un fond noir
        GUISimulator gui = new GUISimulator(taille_plateau_larg, taille_plateau_haut, Color.BLACK);
        
        // Création d'un plateau immig avec les cellules spécifiées, la taille du plateau et l'interface graphique
        gui.setSimulable(new Plateau_immig(liste, nb_cel_larg, nb_cel_haut, gui, nb_etats, taille_plateau_larg, taille_plateau_haut));;
    }
}
