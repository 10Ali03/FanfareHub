package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import metier.Fanfaron;

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
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
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

    public int findIdByNomFanfaron(String nomFanfaron) throws SQLException {
        String sql = "SELECT id FROM fanfaron WHERE nom_fanfaron = ?";
        try (Connection con = dbConnectionManager.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, nomFanfaron);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt("id");
            }
        }
        return -1;
    }

    public java.util.List<Integer> findPupitreIdsByFanfaron(int idFanfaron) throws SQLException {
        String sql = "SELECT id_instrument FROM appartenir WHERE id_fanfaron = ?";
        java.util.List<Integer> ids = new java.util.ArrayList<>();
        try (Connection con = dbConnectionManager.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idFanfaron);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) ids.add(rs.getInt(1));
            }
        }
        return ids;
    }

    public java.util.List<Integer> findGroupeIdsByFanfaron(int idFanfaron) throws SQLException {
        String sql = "SELECT id_groupe FROM impliquer WHERE id_fanfaron = ?";
        java.util.List<Integer> ids = new java.util.ArrayList<>();
        try (Connection con = dbConnectionManager.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idFanfaron);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) ids.add(rs.getInt(1));
            }
        }
        return ids;
    }

    public void saveChoix(int idFanfaron, String[] pupitres, String[] groupes) throws SQLException {
        String delApp = "DELETE FROM appartenir WHERE id_fanfaron = ?";
        String delImp = "DELETE FROM impliquer WHERE id_fanfaron = ?";
        String insApp = "INSERT INTO appartenir(id_fanfaron, id_instrument) VALUES (?, ?)";
        String insImp = "INSERT INTO impliquer(id_fanfaron, id_groupe) VALUES (?, ?)";

        try (Connection con = dbConnectionManager.getConnection()) {
            con.setAutoCommit(false);
            try {
                try (PreparedStatement ps = con.prepareStatement(delApp)) {
                    ps.setInt(1, idFanfaron);
                    ps.executeUpdate();
                }
                try (PreparedStatement ps = con.prepareStatement(delImp)) {
                    ps.setInt(1, idFanfaron);
                    ps.executeUpdate();
                }

                if (pupitres != null) {
                    try (PreparedStatement ps = con.prepareStatement(insApp)) {
                        for (String p : pupitres) {
                            ps.setInt(1, idFanfaron);
                            ps.setInt(2, Integer.parseInt(p));
                            ps.addBatch();
                        }
                        ps.executeBatch();
                    }
                }

                if (groupes != null) {
                    try (PreparedStatement ps = con.prepareStatement(insImp)) {
                        for (String g : groupes) {
                            ps.setInt(1, idFanfaron);
                            ps.setInt(2, Integer.parseInt(g));
                            ps.addBatch();
                        }
                        ps.executeBatch();
                    }
                }

                con.commit();
            } catch (Exception e) {
                con.rollback();
                throw e;
            } finally {
                con.setAutoCommit(true);
            }
        }
    }

    public java.util.List<Fanfaron> findAllFanfarons() throws SQLException {
        String query = "SELECT * FROM fanfaron ORDER BY nom_fanfaron";
        java.util.List<Fanfaron> fanfarons = new java.util.ArrayList<>();
        try (Connection con = dbConnectionManager.getConnection();
             PreparedStatement ps = con.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                fanfarons.add(new Fanfaron(
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
                ));
            }
        }
        return fanfarons;
    }

    public boolean deleteByNomFanfaron(String nomFanfaron) throws SQLException {
        String query = "DELETE FROM fanfaron WHERE nom_fanfaron = ?";
        try (Connection con = dbConnectionManager.getConnection();
             PreparedStatement ps = con.prepareStatement(query)) {
            ps.setString(1, nomFanfaron);
            return ps.executeUpdate() > 0;
        }
    }

    public boolean updateByNomFanfaron(String ancienNomFanfaron, Fanfaron fanfaron, String newMdpOrNull) throws SQLException {
        String query = "UPDATE fanfaron SET nom_fanfaron = ?, email = ?, mot_de_passe = CASE WHEN ? IS NULL OR ? = '' THEN mot_de_passe ELSE digest(?, 'sha256') END, prenom = ?, nom = ?, genre = ?, contraintes_alimentaires = ?, role = ? WHERE nom_fanfaron = ?";
        try (Connection con = dbConnectionManager.getConnection();
             PreparedStatement ps = con.prepareStatement(query)) {
            ps.setString(1, fanfaron.getNomFanfaron());
            ps.setString(2, fanfaron.getEmail());
            ps.setString(3, newMdpOrNull);
            ps.setString(4, newMdpOrNull);
            ps.setString(5, newMdpOrNull);
            ps.setString(6, fanfaron.getPrenom());
            ps.setString(7, fanfaron.getNom());
            ps.setString(8, fanfaron.getGenre());
            ps.setString(9, fanfaron.getContraintesAlim());
            ps.setString(10, fanfaron.getRole());
            ps.setString(11, ancienNomFanfaron);
            return ps.executeUpdate() > 0;
        }
    }
}
