package metier;

public class Groupe {
    private int id;
    private String nom;

    public Groupe(int id, String nom) {
        this.id = id;
        this.nom = nom;
    }

    public int getId() { return id; }
    public String getNom() { return nom; }
}
