package metier;

public class InscriptionEvenement {
    private String nomFanfaron;
    private String pupitre;
    private String statut;

    public InscriptionEvenement(String nomFanfaron, String pupitre, String statut) {
        this.nomFanfaron = nomFanfaron;
        this.pupitre = pupitre;
        this.statut = statut;
    }

    public String getNomFanfaron() {
        return nomFanfaron;
    }

    public String getPupitre() {
        return pupitre;
    }

    public String getStatut() {
        return statut;
    }
}
