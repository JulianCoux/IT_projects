// Définition d'une classe abstraite Event
abstract class Event implements Comparable<Event> {
    // Attribut privé représentant la date de l'événement
    private long date;

    // Constructeur prenant en paramètre la date de l'événement
    public Event(long date) {
        this.date = date;
    }

    // Méthode pour obtenir la date de l'événement
    public long getDate() {
        return this.date;
    }

    // Méthode abstraite à implémenter par les classes filles
    public abstract void execute();

    // Méthode de comparaison pour l'interface Comparable
    @Override
    public int compareTo(Event e) {
        // On compare les dates pour déterminer l'ordre de tri
        // Retourne un nombre positif si la date de this est supérieure à celle de e
        if (this.date > e.date) {
            return 1;
        }
        // Retourne un nombre négatif si la date de this est inférieure à celle de e
        if (this.date < e.date) {
            return -1;
        }
        // Retourne 0 si les dates sont égales
        return 0;
    }
}

