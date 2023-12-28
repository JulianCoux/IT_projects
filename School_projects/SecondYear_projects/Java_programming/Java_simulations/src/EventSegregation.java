import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

// Classe représentant un événement de ségrégation
public class EventSegregation extends Event {
    
    // Attributs de la classe
    private int taille_haut;                            // Taille de la hauteur du plateau
    private int taille_larg;                            // Taille de la largeur du plateau
    private int[][] nb_voisins;                         // Tableau du nombre de voisins pour chaque cellule
    private int K;                                       // Seuil de voisins pour la ségrégation
    private Map<Coordonnee_segregation, Integer> dico_plateau;  // Dictionnaire pour stocker l'état de chaque cellule
    private List<Coordonnee_segregation> cood_a_zero;            // Liste des coordonnées des cellules vacantes

    // Constructeur prenant en paramètres la date, la taille du plateau, le tableau de voisins, le seuil K, le dictionnaire de cellules et la liste des cellules vacantes
    public EventSegregation(long date, int taille_haut, int taille_larg, int[][] nb_voisins, int k, Map<Coordonnee_segregation, Integer> dico_plateau, List<Coordonnee_segregation> cood_a_zero) {
        super(date);
        this.taille_haut = taille_haut;
        this.taille_larg = taille_larg;
        this.nb_voisins = nb_voisins;
        K = k;
        this.dico_plateau = dico_plateau;
        this.cood_a_zero = cood_a_zero;
    }

    // Méthode d'exécution de l'événement
    @Override
    public void execute() {
        List<Coordonnee_segregation> cood_a_supprimer = new ArrayList<>(); // Liste pour stocker les coordonnées des cellules vacantes
        // Parcours de toutes les cellules du plateau
        for (int i = 0; i < this.taille_haut; i++) {
            for (int j = 0; j < this.taille_larg; j++) {
                Coordonnee_segregation cood_new_vacant = new Coordonnee_segregation(j, i);

                // Vérification si le nombre de voisins dépasse K et la cellule n'est pas déjà vacante
                if (this.nb_voisins[i][j] > K && this.dico_plateau.get(cood_new_vacant) != 0) {
                    int etat = this.dico_plateau.get(cood_new_vacant);

                    // Recherche d'une cellule vacante pour le déplacement
                    Coordonnee_segregation cood_new_habit = cood_habit_vacant();

                    // Si une cellule vacante est trouvée, effectuer le déplacement
                    if (cood_new_habit != null) {
                        this.cood_a_zero.remove(cood_new_habit);
                        this.dico_plateau.put(cood_new_habit, etat);
                        cood_a_supprimer.add(cood_new_vacant);
                    } else {
                        continue;
                    }
                }
            }
        }

        // Marquer les cellules déplacées comme vacantes pour le tour suivant
        for (Coordonnee_segregation coordinate : cood_a_supprimer) {
            this.dico_plateau.put(coordinate, 0);
            this.cood_a_zero.add(coordinate);
        }
    }

    // Cette méthode retourne une coordonnée d'habitation vacante choisie aléatoirement parmi la liste disponible
    public Coordonnee_segregation cood_habit_vacant() {
        if (!this.cood_a_zero.isEmpty()) {
            Random random = new Random(); // Initialisation du générateur de nombres aléatoires
            int randomIndex = random.nextInt(this.cood_a_zero.size()); // Sélection d'un index aléatoire
            return this.cood_a_zero.get(randomIndex); // Récupération de la coordonnée correspondante
        } else {
            return null; // Retourne null si la liste est vide
        }
    }
}

