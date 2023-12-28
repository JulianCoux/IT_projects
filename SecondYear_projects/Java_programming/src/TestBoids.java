import gui.GUISimulator ;
import java.awt.Color ;
import gui.Rectangle;



public class TestBoids{
    public static void main (String[] args){
        int taille_fenetre_larg = 500;
        int taille_fenetre_haut = 500;
        final int NB_BOIDS_PROIE = 30;
        final int NB_BOIDS_PREDATEUR = 5;
        
        GUISimulator gui = new GUISimulator(taille_fenetre_larg, taille_fenetre_haut, Color.WHITE);
        gui.setSimulable(new Simu_Boids(gui, NB_BOIDS_PROIE, NB_BOIDS_PREDATEUR, taille_fenetre_larg, taille_fenetre_haut));

        // System.out.println("bonjour\n");
    }
}