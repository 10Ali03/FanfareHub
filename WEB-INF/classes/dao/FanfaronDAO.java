package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class FanfaronDAO {
    private final DbConnectionManager dbConnectionManager;

    public FanfaronDAO(DbConnectionManager dbManager){
        this.dbConnectionManager = dbManager;
    }
    
    public Fanfaron findByNomFanfaron(String nom_fanfaron){
        String query = "SELECT * FROM fanfaron WHERE nom_fanfaron = ?";
        try (Connection con = dbConnectionManager.getConnection();
         PreparedStatement ps = con.prepareStatement(query)) {
            ps.setString(1, nom_fanfaron);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new Fanfaron(
                        rs.getString("nom_fanfaron"),
                        rs.getString("email"),
                        rs.getString("mot_de_passe"),
                        rs.getString("prenom"),
                        rs.getString("nom"),
                        rs.getString("genre"),
                        rs.getString("contraintes_alimentaires"),
                        rs.getString("role"),
                        rs.getTimestamp("date_creation"),
                        rs.getTimestamp("derniere_connexion")
                );
            } 
        } catch (SQLException e) {}
        
        return null;

    }

    public boolean create(Fanfaron fanfaron){

        String query = "INSERT INTO fanfaron (nom_fanfaron, email, mot_de_passe, prenom, nom, genre, contraintes_alimentaires, role, date_creation, derniere_connexion) VALUES (?, ?, digest(?, 'sha256'), ?, ?, ?, ?, ?, ?, ?)";

        try (Connection con = dbConnectionManager.getConnection();
         PreparedStatement ps = con.prepareStatement(query)) {
            ps.setString(1, fanfaron.getNomFanfaron());
            ps.setString(2, fanfaron.getEmail());
            ps.setString(3, fanfaron.getMdp());
            ps.setString(4, fanfaron.getPrenom());
            ps.setString(5, fanfaron.getNom());
            ps.setString(6, fanfaron.getGenre());
            ps.setString(7, fanfaron.getContraintesAlim());
            ps.setString(8, fanfaron.getRole());
            ps.setTimestamp(9, fanfaron.getDateCreation());
            ps.setTimestamp(10, fanfaron.getDerniereConnexion());
        
            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }


    public Fanfaron verifIdentif(String nom_fanfaron, String mdp){

        String query = "SELECT * FROM fanfaron WHERE nom_fanfaron = ? AND mot_de_passe = digest(?, 'sha256')";
        try (Connection con = dbConnectionManager.getConnection();
         PreparedStatement ps = con.prepareStatement(query)) {
            ps.setString(1, nom_fanfaron);
            ps.setString(2, mdp);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new Fanfaron(
                        rs.getString("nom_fanfaron"),
                        rs.getString("email"),
                        rs.getString("mot_de_passe"),
                        rs.getString("prenom"),
                        rs.getString("nom"),
                        rs.getString("genre"),
                        rs.getString("contraintes_alimentaires"),
                        rs.getString("role"),
                        rs.getTimestamp("date_creation"),
                        rs.getTimestamp("derniere_connexion")
                );
            } 
        } catch (SQLException e) {
        e.printStackTrace(); 
        }
        
        return null;

    }
    
}
