import gui.Simulable;
import gui.GUISimulator;
import java.util.List;

public class EventBoids extends Event {
    // Attributs de la classe
    private GUISimulator gui;
    private List<Boid> boids_proie;
    private List<Boid> boids_predateur;
    private int fenetre_larg;
    private int fenetre_haut;

    // Constructeur prenant en paramètre des données nécessaires à l'événement
    public EventBoids(long date,List<Boid> boids_proie, List<Boid> boids_predateur, GUISimulator gui, int fenetre_larg, int fenetre_haut){
        super(date);
        this.boids_proie = boids_proie;
        this.boids_predateur = boids_predateur;
        this.gui = gui;
        this.fenetre_larg = fenetre_larg;
        this.fenetre_haut = fenetre_haut;
    }

    @Override
    // Méthode pour exécuter l'événement
    public void execute() {
        Coordonnee v1, v2, v3, v4; // Variables pour stocker les résultats des règles

        // Boucle pour les boids considérés comme des proies
        for (Boid boid : this.boids_proie) {
            v1 = boid.regle1(boids_proie); // Règle 1 pour les proies
            v2 = boid.regle2(boids_proie); // Règle 2 pour les proies
            v3 = boid.regle3(boids_proie); // Règle 3 pour les proies
            v4 = boid.regle4(boids_proie, fenetre_larg, fenetre_haut); // Règle 4 pour les proies

            // Calcul de la nouvelle vitesse du boid
            Coordonnee nouvelleVitesse = boid.getVitesse().plus(v1).plus(v2).plus(v3).plus(v4);
            boid.setVitesse(nouvelleVitesse); // Mise à jour de la vitesse

            // Calcul de la nouvelle position du boid
            Coordonnee newPosition = boid.getPosition().plus(boid.getVitesse());
            boid.setPosition(newPosition); // Mise à jour de la position
        }

        // Boucle pour les boids considérés comme des prédateurs
        for (Boid boid : this.boids_predateur) {
            v1 = boid.regle1(boids_proie); // Règle 1 pour les prédateurs (en utilisant les proies)
            v2 = boid.regle2(boids_predateur); // Règle 2 pour les prédateurs
            v3 = boid.regle3(boids_proie); // Règle 3 pour les prédateurs (en utilisant les proies)
            v4 = boid.regle4(boids_predateur, fenetre_larg, fenetre_haut); // Règle 4 pour les prédateurs

            // Calcul de la nouvelle vitesse du boid
            Coordonnee nouvelleVitesse = boid.getVitesse().plus(v1).plus(v2).plus(v3).plus(v4);
            boid.setVitesse(nouvelleVitesse); // Mise à jour de la vitesse

            // Calcul de la nouvelle position du boid
            Coordonnee newPosition = boid.getPosition().plus(boid.getVitesse());
            boid.setPosition(newPosition); // Mise à jour de la position
        }
    }
}