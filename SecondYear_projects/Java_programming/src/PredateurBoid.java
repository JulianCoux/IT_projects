import gui.Simulable;
import gui.GUISimulator;
import gui.Rectangle;
import java.awt.Color;

import java.util.List;

// Classe héritée de Boid pour représenter une classe spécifique de Boids, les PredateurBoids
public class PredateurBoid extends Boid {

    // Constructeur initialisant un PredateurBoid avec des attributs spécifiques
    public PredateurBoid(float cod_x, float cod_y, float vitesse_dx, float vitesse_dy, float vitesse_max, int limite_voisinage, Color couleur){
        super(cod_x, cod_y, vitesse_dx, vitesse_dy, vitesse_max, limite_voisinage, couleur);
    }

    
    /*Les prédateurs sont seulement attiré par les proies
    Ils ne s'occupent pas des autres prédateurs
    Ils sont capable de les détécter de très loin, plus loin qu'eux
    */
    public Coordonnee regle1(List<Boid> boids){
        Coordonnee current_c = new Coordonnee(0, 0);
        int nb_voisins = 0;
        for (Boid boid : boids) {
            //Il faut parcourir que les Proies
            if(boid.distance_boid(this) < this.getLimiteVoisinage()){
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

    
    /*Les Boids essaient de se tenir à une petite distance de leurs Proies
    Mais ils sont moins attirés par les proies que elles le sont entre elles */
    public Coordonnee regle2(List<Boid> boids){
        Coordonnee current_c = new Coordonnee(0, 0);
        for (Boid boid : boids) {
            if(!boid.equals(this)){
                if(boid.distance_boid(this) < 20){
                    current_c = current_c.moins(boid.getPosition().moins(this.getPosition()));
                }
            }
        }
        return current_c.division(50);
    }

    
    //Les boids essaient de faire correspondre la vitesse avec les boids proches
    public Coordonnee regle3(List<Boid> boids){
        Coordonnee current_c = new Coordonnee(0, 0);
        int nb_voisins = 0;
        for (Boid boid : boids) {
            if(boid.distance_boid(this) < this.getLimiteVoisinage()){
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
}