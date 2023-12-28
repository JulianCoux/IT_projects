import gui.GUISimulator;
import java.awt.Color;
import gui.Rectangle;
import java.util.Random;
import java.util.Map;
import java.util.HashMap;
import java.awt.Point;

public class TestJeux_segregation {
    public static void main(String[] args) {
        // Initialisation du dictionnaire d'états des cellules
        Map<Coordonnee_segregation, Integer> dico = new HashMap<>();

        // Paramètres pour la taille du plateau et le nombre de cellules
        int taille_plateau_larg = 700;
        int taille_plateau_haut = 700;
        int nb_cel_larg = 100; // Nombre de cellules en largeur
        int nb_cel_haut = 100; // Nombre de cellules en hauteur
        int nb_couleurs = 2; // Nombre C de couleurs différentes
        Random rand = new Random();

        // Nombre de cellules vides
        int NB_CEL_VIDE = 5000;

        // Initialisation aléatoire des états des cellules occupées
        for (int i = 0; i < nb_cel_haut; i++) {
            for (int j = 0; j < nb_cel_larg; j++) {
                Coordonnee_segregation liste = new Coordonnee_segregation(j, i);
                int etat = rand.nextInt(nb_couleurs) + 1; // 2 couleurs + 0 qui est le cas où la maison n'est pas habitée
                dico.put(liste, etat); // key: couple entier (x,y) -- valeur: entier (0 = vacant, 1..c = couleur)
            }
        }

        // Initialisation aléatoire des cellules vides
        int cod_x;
        int cod_y;
        for (int i = 0; i < NB_CEL_VIDE; i++) {
            cod_x = rand.nextInt(nb_cel_larg);
            cod_y = rand.nextInt(nb_cel_haut);
            Coordonnee_segregation cood = new Coordonnee_segregation(cod_x, cod_y);
            dico.put(cood, 0);
        }

        // Initialisation de l'interface graphique
        GUISimulator gui = new GUISimulator(taille_plateau_larg, taille_plateau_haut, Color.BLACK);

        // Paramètre K pour le modèle de ségrégation
        int COND_K = 2;

        // Initialisation et affichage du plateau dans l'interface graphique
        gui.setSimulable(new Plateau_segregation(dico, nb_cel_larg, nb_cel_haut, gui, COND_K, nb_couleurs, taille_plateau_larg, taille_plateau_haut));
    }
}
