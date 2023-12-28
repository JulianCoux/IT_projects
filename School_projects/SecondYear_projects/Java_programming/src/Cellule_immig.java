public class Cellule_immig {
    private int etat;  // L'état de la cellule (1 pour VIVANTE, 0 pour MORTE)

    // Constructeur de la classe Cellule_immig
    public Cellule_immig(int etat_bool) {
        this.etat = etat_bool;
    }

    // Méthode pour obtenir l'état de la cellule
    public int get_etat() {
        return this.etat;
    }

    // Méthode pour définir l'état de la cellule
    public void set_etat(int new_etat) {
        this.etat = new_etat;
    }

    // Méthode pour représenter la cellule sous forme de chaîne de caractères
    @Override
    public String toString() {
        String var1 = "";
        var1 += etat; 
        return var1; 
    }
}
