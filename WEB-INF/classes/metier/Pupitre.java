package metier;

public class Pupitre {
    private int id;
    private String nom;

    public Pupitre(int id, String nom) {
        this.id = id;
        this.nom = nom;
    }

    public int getId() { return id; }
    public String getNom() { return nom; }
}
