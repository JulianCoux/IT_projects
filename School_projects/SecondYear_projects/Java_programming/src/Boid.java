import gui.Simulable;
import gui.GUISimulator;
import gui.Rectangle;
import java.awt.Color;

import java.util.List;

public class Boid{
    //Variables globales à la classe
    private Coordonnee position;
    private Coordonnee vitesse;
    private float vitesse_max;
    private int limite_voisinage;
    private Color color_boid;
    
    // Constructeur initialisant un Boid avec des attributs spécifiques
    public Boid(float cod_x, float cod_y, float vitesse_dx, float vitesse_dy, float vitesse_max, int limite_voisinage, Color color_boid){
        this.position = new Coordonnee(cod_x, cod_y);
        this.vitesse = new Coordonnee(vitesse_dx, vitesse_dy);
        this.vitesse_max = vitesse_max;
        this.limite_voisinage = limite_voisinage;
        this.color_boid = color_boid;
    }


/*On définit toutes les règles des Boid ici car si un Object de type dynamique Boid mais qui est construit comme un ProieBoid */
    // Méthode de règle 1 : voler vers le centre de masse des Boids voisins
    public Coordonnee regle1(List<Boid> boids){
        Coordonnee current_c = new Coordonnee(0, 0);
        int nb_voisins = 0;
        for (Boid boid : boids) {
            if(boid.distance_boid(this) < this.limite_voisinage){
                if(!boid.equals(this)){
                    current_c = current_c.plus(boid.getPosition());
                    nb_voisins ++;
                }
            }
        }
        if (nb_voisins >= 1) {
            //System.out.println("On a " + nb_voisins + " voisins");
            current_c = current_c.division(nb_voisins);
            Coordonnee diff = current_c.moins(this.getPosition());
            return diff.division(100);
        }
        else{
            return new Coordonnee(0, 0);
        }
    }


    // Méthode de règle 2 : se tenir à une petite distance des autres objets (y compris des autres Boids)
    public Coordonnee regle2(List<Boid> boids){
        Coordonnee current_c = new Coordonnee(0, 0);
        for (Boid boid : boids) {
            if(!boid.equals(this)){
                if(boid.distance_boid(this) < 20){
                    current_c = current_c.moins(boid.getPosition().moins(this.getPosition()));
                }
            }
        }
        return current_c.division(20);
    }


    // Méthode de règle 3 : faire correspondre la vitesse avec les boids proches
    public Coordonnee regle3(List<Boid> boids){
        Coordonnee current_c = new Coordonnee(0, 0);
        int nb_voisins = 0;
        for (Boid boid : boids) {
            if(boid.distance_boid(this) < this.limite_voisinage){
                if(!boid.equals(this)){
                    current_c = current_c.plus(boid.getVitesse());
                    nb_voisins ++;
                }
            }
        }
        if (nb_voisins >= 1) {
            current_c = current_c.division(nb_voisins);
            Coordonnee diff = current_c.moins(this.getVitesse());
            return diff.division(8);
        }
        else{
            return new Coordonnee(0, 0);
        }

    }


    // Méthode de règle 4 : ajuster la position si le Boid est hors de la fenêtre
    public Coordonnee regle4(List<Boid> boids, int taille_fenetre_larg, int taille_fenetre_haut){
        Coordonnee current_c = new Coordonnee(0,0);
        if(this.getPosition().x < 0){
            current_c.x = 10;
        }
        else if(this.getPosition().x > taille_fenetre_larg){
            current_c.x = -10;
        }

        if(this.getPosition().y < 0){
            current_c.y = 10;
        }
        else if(this.getPosition().y > taille_fenetre_haut){
            current_c.y = -10;
        }
        return current_c;
    }

    // Méthode limitant la vitesse du Boid
    public void limite_vitesse(){
        Coordonnee current_c = new Coordonnee(0, 0);
        float norme_vect = this.getNormeVitesse();
        if(norme_vect > this.getMaxSpeed()){
            Coordonnee res_div = this.getVitesse().division(norme_vect);
            this.setVitesse(res_div.mult(this.getMaxSpeed()));
        }
    }
    // Getter de la limite de voisinage
    public int getLimiteVoisinage(){
        return this.limite_voisinage;
    }

    // Getter de la couleur
    public Color getColor(){
        return this.color_boid;
    }

    // Calcul de la distance en valeur absolue entre deux Boid
    public float distance_boid(Boid b){
        float res = (float) (Math.pow((float)this.position.x - b.position.x, 2) + Math.pow((float)this.position.y - b.position.y, 2));
        return (float) Math.sqrt(res);
    }

    // Calcul de la norme du vecteur vitesse
    public float getNormeVitesse(){
        return (float) Math.sqrt((float) Math.pow(this.vitesse.x, 2) + Math.pow(this.vitesse.y, 2));
    }

    public Coordonnee getPosition(){
        return this.position;
    }

    public Coordonnee getVitesse(){
        return this.vitesse;
    }    

    public float getX() {
        return this.position.x;
    }

    public float getY() {
        return this.position.y;
    }

    public float getDx() {
        return this.vitesse.x;
    }

    public float getDy() {
        return this.vitesse.y;
    }

    public float getMaxSpeed() {
        return this.vitesse_max;
    }

    public void setX(float x) {
        this.position.x = x;
    }

    public void setY(float y) {
        this.position.y = y;
    }

    public void setDx(float dx) {
        this.vitesse.x = dx;
    }

    public void setDy(float dy) {
        this.vitesse.y = dy;
    }

    public void setMaxSpeed(float vitesse_max) {
        this.vitesse_max = vitesse_max;
    }

    public void setPosition(Coordonnee cood){
        this.position.x = cood.x;
        this.position.y = cood.y;
    }

    public void setVitesse(Coordonnee cood){
        this.vitesse.x = cood.x;
        this.vitesse.y = cood.y;
    }    

    @Override
    public String toString() {
        String var1 = "";
        var1 += "x = " + this.position.x + " // y = " + this.position.y;
        return var1;
    }

    
    public boolean equals(Boid b) {
        return this.position.equals(b.position) && this.vitesse.equals(b.vitesse);
    }
}
