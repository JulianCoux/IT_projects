import java.awt.Point;

// La classe Balls représente un ensemble de balles avec des fonctionnalités associées
public class Balls {
    // Les positions courantes et initiales des balles
    protected Point[] pos_courante_balles;
    private Point[] pos_init_balles;

    // Le nombre de balles
    public int nb_balles;

    // Constructeur prenant un tableau de Points représentant les positions initiales des balles
    public Balls(Point[] var1) {
        // Initialisation du nombre de balles
        this.nb_balles = var1.length;

        // Initialisation des positions initiales des balles en clonant le tableau fourni
        this.pos_init_balles = (Point[]) var1.clone();

        // Clonage des positions initiales pour les positions courantes des balles
        this.pos_courante_balles = (Point[]) var1.clone();
    }

    // Méthode pour déplacer une balles selon les coordonnées fournies
    public void translate(int var1, int var2, Point balle, int i, Point[] liste_translate) {
        balle.x += var1;
        balle.y += var2;
        if (balle.x >= 500){
            liste_translate[i].x -= 2*liste_translate[i].x;
        } else if (balle.x <= 0) {
            liste_translate[i].x += -2*liste_translate[i].x;
        }
        if (balle.y >= 500){
            liste_translate[i].y -= 2*liste_translate[i].y;
        } else if (balle.y <= 0) {
            liste_translate[i].y += -2*liste_translate[i].y;
        }
    }

    // Méthode pour réinitialiser les positions courantes des balles aux positions initiales
    public void reInit() {
        for (int var1 = 0; var1 < this.nb_balles; ++var1) {
            // Clonage des positions initiales pour les positions courantes des balles
            this.pos_courante_balles[var1] = (Point) this.pos_init_balles[var1].clone();
        }
    }

    // Méthode pour obtenir une représentation sous forme de chaîne de caractères de l'objet Balls
    public String toString() {
        String var1 = "[";

        // Parcours de toutes les balles pour les ajouter à la chaîne de caractères
        for (int var2 = 0; var2 < this.nb_balles; ++var2) {
            var1 += this.pos_courante_balles[var2].toString();

            // Ajout d'une virgule si ce n'est pas la dernière balle
            if (var2 != nb_balles - 1) {
                var1 += ", ";
            }
        }

        var1 += "]";
        return var1;
    }
}

