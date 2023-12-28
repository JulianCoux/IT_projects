import java.awt.Point;

public class TestBalls {
    public static void main(String[] args) {
        // Création de trois points représentant les positions initiales des balles
        Point p1 = new Point(1, 2);
        Point p2 = new Point(0, 4);
        Point p3 = new Point(5, 2);

        // Création d'un tableau de points
        Point[] liste = {p1, p2, p3};

        // Création d'un objet Balls avec les positions initiales des balles
        Balls balles = new Balls(liste);

        // Affichage des positions courantes des balles
        System.out.println(balles);

        // Translation des balles
        for (int i = 0; i < 3; i++){
            balles.translate(4, 9, balles.pos_courante_balles[i], i, balles.pos_courante_balles);
        }

        // Affichage des nouvelles positions des balles après la translation
        System.out.println(balles);

        // Réinitialisation des balles aux positions initiales
        balles.reInit();

        // Affichage des positions après la réinitialisation
        System.out.println(balles);
    }
}
