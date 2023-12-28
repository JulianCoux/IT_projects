import java.util.Objects;

public class Coordonnee {
    float x;
    float y;

    public Coordonnee(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public Coordonnee plus(Coordonnee autreCoordonnee) {
        float newX = this.x + autreCoordonnee.x;
        float newY = this.y + autreCoordonnee.y;
        return new Coordonnee(newX, newY);
    }

    public Coordonnee moins(Coordonnee autreCoordonnee) {
        float newX = this.x - autreCoordonnee.x;
        float newY = this.y - autreCoordonnee.y;
        return new Coordonnee(newX, newY);
    }
    
    public Coordonnee division(float diviseur) {
        float newX = this.x / diviseur;
        float newY = this.y / diviseur;
        return new Coordonnee(newX, newY);
    }

    public Coordonnee mult(float multiplicateur) {
        float newX = this.x * multiplicateur;
        float newY = this.y * multiplicateur;
        return new Coordonnee(newX, newY);
    }

    // public Coordonnee division_vecteurs(Coordonnee autreCoordonnee) {
    //     float newX = (float) this.x / autreCoordonnee.x;
    //     float newY = (float) this.y / autreCoordonnee.y;
    //     return new Coordonnee(newX, newY);
    // }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Coordonnee that = (Coordonnee) o;
        return x == that.x && y == that.y;
    }
}