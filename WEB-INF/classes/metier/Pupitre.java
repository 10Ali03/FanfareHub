package metier;

public class Pupitre {
    // Identifiant technique en base (PK table pupitre).
    private int id;
    // Libelle metier du pupitre (ex: trompette, clarinette...).
    private String nom;

    public Pupitre(int id, String nom) {
        // Constructeur simple utilise par les DAO lors du mapping ResultSet -> objet.
        this.id = id;
        this.nom = nom;
    }

    // Getter ID: utile pour les formulaires (value des checkbox/select).
    public int getId() { return id; }
    // Getter nom: utile pour l'affichage dans les JSP.
    public String getNom() { return nom; }
}
