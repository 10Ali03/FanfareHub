package metier;

import java.sql.Timestamp;

public class Evenement {
    private int id;
    private String nom;
    private Timestamp horodatage;
    private int duree;
    private String lieu;
    private String description;

    public Evenement(int id, String nom, Timestamp horodatage, int duree, String lieu, String description) {
        this.id = id;
        this.nom = nom;
        this.horodatage = horodatage;
        this.duree = duree;
        this.lieu = lieu;
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public String getNom() {
        return nom;
    }

    public Timestamp getHorodatage() {
        return horodatage;
    }

    public int getDuree() {
        return duree;
    }

    public String getLieu() {
        return lieu;
    }

    public String getDescription() {
        return description;
    }
}
