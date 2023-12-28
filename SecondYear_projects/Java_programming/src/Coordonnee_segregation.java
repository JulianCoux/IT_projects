import java.util.Objects;

// Classe représentant des coordonnées pour la simulation de ségrégation
public class Coordonnee_segregation {
    // Attributs représentant les coordonnées x et y
    int x;
    int y;

    // Constructeur de la classe Coordonnee_segregation
    public Coordonnee_segregation(int x, int y) {
        this.x = x;
        this.y = y;
    }

    // Redéfinition de la méthode equals pour comparer deux objets de type Coordonnee_segregation
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;  // Si les deux objets sont les mêmes, ils sont égaux
        if (o == null || getClass() != o.getClass()) return false;  // Si l'objet à comparer est null ou n'est pas une instance de Coordonnee_segregation, ils ne sont pas égaux
        Coordonnee_segregation that = (Coordonnee_segregation) o;  // Conversion de l'objet à comparer en Coordonnee_segregation
        return x == that.x && y == that.y;  // Comparaison des valeurs de x et y
    }

    // Redéfinition de la méthode hashCode pour générer un code de hachage basé sur les valeurs de x et y
    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }
}
