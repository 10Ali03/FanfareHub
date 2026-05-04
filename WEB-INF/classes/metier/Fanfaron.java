package metier;
import java.sql.Timestamp;

public class Fanfaron {
    private String nomFanfaron;
    private String email;
    private String mdp;
    private String prenom;
    private String nom;
    private String genre;
    private String contraintesAlim;
    private String role;
    private Timestamp  dateCreation;
    private Timestamp  derniereconnexion;

    public Fanfaron(String nomFanfaron, String email, String mdp, String prenom, String nom, String genre, String contraintesAlim, String role, Timestamp  dateCreation, Timestamp  derniereconnexion){
        this.nomFanfaron = nomFanfaron;
        this.email = email;
        this.mdp = mdp;
        this.prenom = prenom;
        this.nom = nom;
        this.genre = genre;
        this.contraintesAlim = contraintesAlim;
        this.role = role;
        this.dateCreation = dateCreation;
        this.derniereconnexion = derniereconnexion;
    }

    public String getNomFanfaron(){
        return nomFanfaron;
    }

    public String getEmail(){
        return email;
    }

    public String getMdp(){
        return mdp;
    }

    public String getPrenom(){
        return prenom;
    }

    public String getNom(){
        return nom;
    }

    public String getGenre(){
        return genre;
    }

    public String getContraintesAlim(){
        return contraintesAlim;
    }

    public String getRole(){
        return role;
    }

    public Timestamp  getDateCreation(){
        return dateCreation;
    }

    public Timestamp  getDerniereConnexion(){
        return derniereconnexion;
    }
    
    
    public void setNomFanfaron(String nomFanfaron){
        this.nomFanfaron = nomFanfaron;
    }

    public void setEmail(String email){
        this.email = email;
    }

    public void setMdp(String mdp){
        this.mdp = mdp;
    }

    public void setPrenom(String prenom){
        this.prenom = prenom;
    }

    public void setNom(String nom){
        this.nom = nom;
    }

    public void setGenre(String genre){
        this.genre = genre;
    }

    public void setContraintesAlim(String contraintesAlim){
        this.contraintesAlim = contraintesAlim;
    }

    public void setRole(String role){
        this.role = role;
    }

    public void setDateCreation(Timestamp  dateCreation){
        this.dateCreation = dateCreation;
    }

    public void setDerniereConnexion(Timestamp  dernierConnexion){
        this.derniereconnexion = dernierConnexion;
    }
}
