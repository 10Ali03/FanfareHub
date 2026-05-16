package metier;

public class Participer {
    // Nom du fanfaron participant (donnee de presentation).
    private int fanfaronId;
    private int evenementId;
    // Nom du pupitre choisi pour cet evenement.
    private int instrumentId;
    // Statut de participation: present / incertain / absent.
    private String statut;

    public Participer(int fanfaronId, int evenementId, int instrumentId, String statut) {
        // Objet DTO (Data Transfer Object) construit depuis les requetes SQL.
        this.fanfaronId = fanfaronId;
        this.evenementId = evenementId;
        this.instrumentId = instrumentId;
        this.statut = statut;
    }

    // Getter affiche dans la colonne "Fanfaron" de la JSP.
    public int getFanfaronId() {
        return fanfaronId;
    }

    public int getEvenementId() {
        return evenementId;
    }

    // Getter affiche dans la colonne "Instrument" de la JSP.
    public int getInstrumentId() {
        return instrumentId;
    }

    // Getter affiche dans la colonne "Statut" et pour la couleur CSS.
    public String getStatut() {
        return statut;
    }
}
