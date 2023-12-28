// Classe représentant un événement de message
public class MessageEvent extends Event {

    // Attribut de la classe
    private String message;

    // Constructeur prenant en paramètres la date et le message
    public MessageEvent(int date, String message) {
        super(date);
        this.message = message;
    }

    // Méthode d'exécution de l'événement
    @Override
    public void execute() {
        // Affiche la date de l'événement suivi du message
        System.out.println(this.getDate() + ": " + this.message);
    }
}

