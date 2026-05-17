package dao;

import metier.Pupitre;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PupitreDAO {
    // Gestionnaire de connexion JDBC partagé (singleton injecté).
    private final DbConnectionManager db;

    public PupitreDAO(DbConnectionManager db) {
        this.db = db;
    }

    public List<Pupitre> findAll() throws SQLException {
        // Liste de référence des pupitres pour affichage dans les vues/formulaires.
        String sql = "SELECT id, nom FROM pupitre ORDER BY nom";
        List<Pupitre> list = new ArrayList<>();

        try (Connection con = db.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                // Mapping ligne SQL -> POJO metier Pupitre.
                list.add(new Pupitre(rs.getInt("id"), rs.getString("nom")));
            }
        }
        return list;
    }

    public Pupitre findById(int id) throws SQLException {
        // Recherche d'un pupitre pour edition ciblée.
        String sql = "SELECT id, nom FROM pupitre WHERE id = ?";
        try (Connection con = db.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Pupitre(rs.getInt("id"), rs.getString("nom"));
                }
            }
        }
        return null;
    }

    public boolean create(String nom) throws SQLException {
        // Creation d'un nouveau pupitre.
        String sql = "INSERT INTO pupitre(nom) VALUES (?)";
        try (Connection con = db.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, nom);
            return ps.executeUpdate() > 0;
        }
    }

    public boolean update(int id, String nom) throws SQLException {
        // Mise a jour du nom d'un pupitre existant.
        String sql = "UPDATE pupitre SET nom = ? WHERE id = ?";
        try (Connection con = db.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, nom);
            ps.setInt(2, id);
            return ps.executeUpdate() > 0;
        }
    }

    public boolean delete(int id) throws SQLException {
        // Suppression en cascade applicative:
        // 1) table appartenir (groupes/pupitres d'un fanfaron)
        // 2) table participation (inscriptions evenement)
        // 3) table pupitre
        String delAppartenir = "DELETE FROM appartenir WHERE id_instrument = ?";
        String delParticipation = "DELETE FROM participation WHERE id_instrument = ?";
        String delPupitre = "DELETE FROM pupitre WHERE id = ?";

        try (Connection con = db.getConnection()) {
            // Transaction explicite pour garantir la coherence globale.
            con.setAutoCommit(false);
            try {
                try (PreparedStatement ps = con.prepareStatement(delAppartenir)) {
                    ps.setInt(1, id);
                    ps.executeUpdate();
                }
                try (PreparedStatement ps = con.prepareStatement(delParticipation)) {
                    ps.setInt(1, id);
                    ps.executeUpdate();
                }
                int updated;
                try (PreparedStatement ps = con.prepareStatement(delPupitre)) {
                    ps.setInt(1, id);
                    updated = ps.executeUpdate();
                }
                con.commit();
                return updated > 0;
            } catch (Exception e) {
                // Annule toutes les suppressions si une etape echoue.
                con.rollback();
                throw e;
            } finally {
                // Restaure le mode par defaut de la connexion.
                con.setAutoCommit(true);
            }
        }
    }

    public List<Pupitre> findByFanfaron(int fanfaronId) throws SQLException {
        // Recupere les pupitres lies a un fanfaron via la table de liaison appartenir.
        String sql = "SELECT id, nom FROM pupitre p INNER JOIN appartenir a ON p.id = a.id_instrument WHERE a.id_fanfaron = ?";
        List<Pupitre> list = new ArrayList<>();
        
        try (Connection con = db.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)){
             ps.setInt(1, fanfaronId);
             ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new Pupitre(rs.getInt("id"), rs.getString("nom")));
            }
        }
        return list;
    }
}
