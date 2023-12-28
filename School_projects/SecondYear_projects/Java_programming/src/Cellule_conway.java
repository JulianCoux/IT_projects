// Classe représentant une cellule dans le jeu de la vie de Conway
public class Cellule_conway {
    private boolean etat; // true = VIVANT -- false = MORT

    // Constructeur, initialise la cellule avec l'état spécifié
    public Cellule_conway(boolean etat_bool) {
        this.etat = etat_bool;
    }

    // Méthode pour obtenir l'état de la cellule (VIVANT ou MORT)
    public boolean get_etat() {
        return this.etat;
    }

    // Méthode pour définir un nouvel état pour la cellule
    public void set_etat(boolean new_etat) {
        this.etat = new_etat;
    }

    // Surcharge de la méthode toString pour obtenir une représentation sous forme de chaîne de caractères de l'objet Cellule_conway
    @Override
    public String toString() {
        // Si la cellule est vivante, retourne "1", sinon retourne "0"
        return this.etat ? "1" : "0";
    }
}
