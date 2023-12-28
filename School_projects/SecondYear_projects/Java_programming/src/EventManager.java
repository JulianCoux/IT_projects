import java.util.PriorityQueue;

// Classe responsable de la gestion des événements
public class EventManager {
    private long currentDate;                    // Date actuelle de la simulation
    private PriorityQueue<Event> eventQueue;     // File de priorité pour stocker les événements

    // Constructeur initialisant la file de priorité et la date actuelle
    public EventManager() {
        this.eventQueue = new PriorityQueue<>();
        this.currentDate = 0;
    }

    // Méthode pour ajouter un événement à la file de priorité
    public void addEvent(Event e) {
        eventQueue.add(e);
    }

    // Méthode pour passer au prochain événement
    public void next() {
        this.currentDate++;
        // Traiter tous les événements planifiés à la date actuelle
        while (!eventQueue.isEmpty() && eventQueue.peek().getDate() <= this.currentDate) {
            Event event = eventQueue.poll();
            event.execute();
        }
    }

    // Méthode pour obtenir la date actuelle
    public long getCurrentDate() {
        return currentDate;
    }

    // Méthode pour définir la date actuelle
    public void setCurrentDate(long currentDate) {
        this.currentDate = currentDate;
    }

    // Méthode pour vérifier si la file d'événements est vide
    public boolean isFinished() {
        return eventQueue.isEmpty();
    }

    // Méthode pour réinitialiser la file d'événements
    public void restart() {
        this.eventQueue.clear();
    }
}

