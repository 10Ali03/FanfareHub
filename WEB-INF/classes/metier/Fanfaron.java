package metier;
import java.sql.Timestamp;

public class Fanfaron {
    // Identifiant fonctionnel utilise pour se connecter.
    private String nomFanfaron;
    // Email du compte (unique en base).
    private String email;
    // Mot de passe (hache en base, en clair seulement au moment de la saisie).
    private String mdp;
    // Prenom civil.
    private String prenom;
    // Nom civil.
    private String nom;
    // Genre (homme/femme/autre selon contrainte SQL).
    private String genre;
    // Contrainte alimentaire (aucune/vegetarien/vegan/sans porc).
    private String contraintesAlim;
    // Role applicatif (utilisateur/admin).
    private String role;
    // Date de creation du compte.
    private Timestamp  dateCreation;
    // Derniere connexion (nullable tant que jamais connecte).
    private Timestamp  derniereconnexion;

    public Fanfaron(String nomFanfaron, String email, String mdp, String prenom, String nom, String genre, String contraintesAlim, String role, Timestamp  dateCreation, Timestamp  derniereconnexion){
        // Constructeur principal: utilise partout (DAO/servlets/admin).
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

    // Getter login.
    public String getNomFanfaron(){
        return nomFanfaron;
    }

    // Getter email.
    public String getEmail(){
        return email;
    }

    // Getter mdp (utile en phase d'insertion uniquement).
    public String getMdp(){
        return mdp;
    }

    // Getter prenom.
    public String getPrenom(){
        return prenom;
    }

    // Getter nom.
    public String getNom(){
        return nom;
    }

    // Getter genre.
    public String getGenre(){
        return genre;
    }

    // Getter contrainte alimentaire.
    public String getContraintesAlim(){
        return contraintesAlim;
    }

    // Getter role.
    public String getRole(){
        return role;
    }

    // Getter date creation.
    public Timestamp  getDateCreation(){
        return dateCreation;
    }

    // Getter derniere connexion.
    public Timestamp  getDerniereConnexion(){
        return derniereconnexion;
    }
    
    
    // Setter login (utilise en edition admin).
    public void setNomFanfaron(String nomFanfaron){
        this.nomFanfaron = nomFanfaron;
    }

    // Setter email.
    public void setEmail(String email){
        this.email = email;
    }

    // Setter mdp.
    public void setMdp(String mdp){
        this.mdp = mdp;
    }

    // Setter prenom.
    public void setPrenom(String prenom){
        this.prenom = prenom;
    }

    // Setter nom.
    public void setNom(String nom){
        this.nom = nom;
    }

    // Setter genre.
    public void setGenre(String genre){
        this.genre = genre;
    }

    // Setter contraintes.
    public void setContraintesAlim(String contraintesAlim){
        this.contraintesAlim = contraintesAlim;
    }

    // Setter role.
    public void setRole(String role){
        this.role = role;
    }

    // Setter date creation.
    public void setDateCreation(Timestamp  dateCreation){
        this.dateCreation = dateCreation;
    }

    // Setter derniere connexion.
    public void setDerniereConnexion(Timestamp  dernierConnexion){
        this.derniereconnexion = dernierConnexion;
    }
}
