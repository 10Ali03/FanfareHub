package metier;

public class InscriptionEvenement {
    // Nom du fanfaron participant (donnee de presentation).
    private String nomFanfaron;
    // Nom du pupitre choisi pour cet evenement.
    private String pupitre;
    // Statut de participation: present / incertain / absent.
    private String statut;

    public InscriptionEvenement(String nomFanfaron, String pupitre, String statut) {
        // Objet DTO (Data Transfer Object) construit depuis les requetes SQL.
        this.nomFanfaron = nomFanfaron;
        this.pupitre = pupitre;
        this.statut = statut;
    }

    // Getter affiche dans la colonne "Fanfaron" de la JSP.
    public String getNomFanfaron() {
        return nomFanfaron;
    }

    // Getter affiche dans la colonne "Instrument" de la JSP.
    public String getPupitre() {
        return pupitre;
    }

    // Getter affiche dans la colonne "Statut" et pour la couleur CSS.
    public String getStatut() {
        return statut;
    }
}
