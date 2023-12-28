import gui.Simulable;
import gui.GUISimulator;
import gui.Rectangle;
import java.awt.Color;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class Simu_Boids implements Simulable{

    private GUISimulator gui;
    //private List<Boid> boids;
    private List<Boid> boids_proie;
    private List<Boid> boids_predateur;
    private List<Boid> pos_init_boids_proie;
    private List<Boid> pos_init_boids_predateur;
    private double nb_boids;
    private int taille_fenetre_larg;
    private int taille_fenetre_haut;
    private final int taille_boid = 5;
    private final int v_max_boid = 15;
    private final int limite_voisinage_proie = 50;
    private final int limite_voisinage_predateur = 150;
    private EventManager eventManager;
    private double nb_boids_proies;
    private double nb_boids_predateurs;

    public Simu_Boids(GUISimulator gui, int nb_boids_proies, int nb_boids_predateurs, int taille_fenetre_larg, int taille_fenetre_haut) {
        this.gui = gui;
        this.nb_boids = nb_boids;
        this.taille_fenetre_larg = taille_fenetre_larg;
        this.taille_fenetre_haut = taille_fenetre_haut;
        
        //On crée 2 listes de boids
        this.boids_proie = new ArrayList<>();               //Une de type Proie
        this.boids_predateur = new ArrayList<>();           //Une de type Predateur

        //On crée 2 autres listes qui vont stocker les premiers paramètres des boids
        //pour les restituer en cas de restart
        this.pos_init_boids_proie = new ArrayList<>();
        this.pos_init_boids_predateur = new ArrayList<>();
        this.eventManager = new EventManager();            //Object de type EventManager qui sert à gérer les évênements
        this.nb_boids_proies = nb_boids_proies;
        this.nb_boids_predateurs = nb_boids_predateurs;
        
        //Création des Boids Proies
        for(int i=0; i<nb_boids_proies; i++){
            random_boid_proie(Color.BLUE);
        }

        //Création des Boids Prédateurs
        for(int i=0; i<nb_boids_predateurs; i++){
            random_boid_predateur(Color.RED);
        }

        draw_boids();
    }


    public void random_boid_proie(Color couleur){   //Génère un boid aux propriétés aléatoires
        Random random = new Random();
        //Calcul aléatoire des coordonnées
        int rand_x = random.nextInt(this.taille_fenetre_larg);
        int rand_y = random.nextInt(this.taille_fenetre_haut);

        //Calcul aléatoire de la vitesse de déplacement et la direction
        int rand_dx = random.nextInt(21) - 10;  //entre -10 et 10
        int rand_dy = random.nextInt(21) - 10;

        //On crée un object de type ProieBoid qu'on ajoute à la liste boids_proie
        //Son type dynamique est Boid pour pouvoir utiliser les méthodes de ProieBoid dans la classe PredateurBoid
        Boid boid = new ProieBoid(rand_x, rand_y, rand_dx, rand_dy, 10, this.limite_voisinage_proie, couleur);
        this.boids_proie.add(boid);

        //Pareil pour la liste de sauvegarde
        Boid boid_copie = new ProieBoid(rand_x, rand_y, rand_dx, rand_dy, 10, this.limite_voisinage_proie, couleur);
        this.pos_init_boids_proie.add(boid_copie);   //copie de la liste pour garder une version originale des positions des Boids
    }

    public void random_boid_predateur(Color couleur){   //Génère un boid aux propriétés aléatoires
        Random random = new Random();
        int rand_x = random.nextInt(this.taille_fenetre_larg);
        int rand_y = random.nextInt(this.taille_fenetre_haut);

        int rand_dx = random.nextInt(21) - 10;
        int rand_dy = random.nextInt(21) - 10;

        Boid boid = new PredateurBoid(rand_x, rand_y, rand_dx, rand_dy, 10, this.limite_voisinage_predateur, couleur);
        this.boids_predateur.add(boid);

        Boid boid_copie = new PredateurBoid(rand_x, rand_y, rand_dx, rand_dy, 10, this.limite_voisinage_predateur, couleur);
        this.pos_init_boids_predateur.add(boid_copie);   //copie de la liste pour garder une version originale des positions des Boids
    }

    public void draw_boids() {
        /*Les prédateurs en rouge
          Les proies en bleu */
        gui.reset();    //On efface l'écran
        for (Boid boid : this.boids_proie) {
            //On ajoute les boids de boids_proie sous forme de carré
            gui.addGraphicalElement(new Rectangle((int)boid.getX(), (int)boid.getY(), boid.getColor(), boid.getColor(), this.taille_boid));
        }
        for (Boid boid : this.boids_predateur) {
            //On ajoute les boids de boids_predateur sous forme de carré
            gui.addGraphicalElement(new Rectangle((int)boid.getX(), (int)boid.getY(), boid.getColor(), boid.getColor(), this.taille_boid));
        }
    }


    @Override
    public void restart() {
        this.boids_proie.clear();
        this.boids_predateur.clear();
        for (Boid boid : this.pos_init_boids_proie) {
            // Création d'une nouvelle instance de Boid avec les mêmes valeurs que pos_init_boids_proie
            Boid newBoid = new ProieBoid(boid.getX(), boid.getY(), boid.getDx(), boid.getDy(), boid.getMaxSpeed(), limite_voisinage_proie, boid.getColor());
            this.boids_proie.add(newBoid);
        }
        for (Boid boid : this.pos_init_boids_predateur) {
            // Création d'une nouvelle instance de Boid avec les mêmes valeurs que pos_init_boids_predateur
            Boid newBoid = new PredateurBoid(boid.getX(), boid.getY(), boid.getDx(), boid.getDy(), boid.getMaxSpeed(), limite_voisinage_predateur, boid.getColor());
            this.boids_predateur.add(newBoid);
        }
        draw_boids();
    }


    @Override
    public void next() {
        //On ajoute le nouvel évênement à eventManager
        eventManager.addEvent(new EventBoids(eventManager.getCurrentDate(), boids_proie, boids_predateur, gui, taille_fenetre_larg, taille_fenetre_haut));
        //On appelle la méthode .next() de eventManager qui executera tous les évênements en attente qui sont prêt à être exécuté
        eventManager.next();
        draw_boids();
    }

    @Override
    public String toString() {
        String var1 = "";
        for (Boid boid : this.pos_init_boids_proie) {
            // Création d'une nouvelle instance de Boid avec les mêmes valeurs que pos_init_boids_proie
            var1 += "Les proies : \n";
            var1 += boid.toString();
        }
        for (Boid boid : this.pos_init_boids_predateur) {
            // Création d'une nouvelle instance de Boid avec les mêmes valeurs que pos_init_boids_predateur
            var1 += "Les prédateurs : \n";
            var1 += boid.toString();
        }
        return var1;
    }
}