package dao;

import metier.Groupe;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class GroupeDAO {
    // Gestionnaire de connexion JDBC partagé (singleton injecté).
    private final DbConnectionManager db;

    public GroupeDAO(DbConnectionManager db) {
        this.db = db;
    }

    public List<Groupe> findAll() throws SQLException {
        // Liste de référence des groupes pour affichage dans les formulaires/vues.
        String sql = "SELECT id, nom FROM groupe ORDER BY nom";
        List<Groupe> list = new ArrayList<>();

        try (Connection con = db.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                // Mapping ligne SQL -> POJO metier Groupe.
                list.add(new Groupe(rs.getInt("id"), rs.getString("nom")));
            }
        }
        return list;
    }

    public Groupe findById(int id) throws SQLException {
        // Recherche d'un groupe pour pre-remplir un formulaire d'edition.
        String sql = "SELECT id, nom FROM groupe WHERE id = ?";
        try (Connection con = db.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Groupe(rs.getInt("id"), rs.getString("nom"));
                }
            }
        }
        return null;
    }

    public boolean create(String nom) throws SQLException {
        // Creation simple d'un groupe.
        String sql = "INSERT INTO groupe(nom) VALUES (?)";
        try (Connection con = db.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, nom);
            return ps.executeUpdate() > 0;
        }
    }

    public boolean update(int id, String nom) throws SQLException {
        // Mise a jour du nom d'un groupe existant.
        String sql = "UPDATE groupe SET nom = ? WHERE id = ?";
        try (Connection con = db.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, nom);
            ps.setInt(2, id);
            return ps.executeUpdate() > 0;
        }
    }

    public boolean delete(int id) throws SQLException {
        // Suppression logique en 2 temps:
        // 1) retirer les dependances dans la table de liaison (impliquer)
        // 2) supprimer le groupe lui-meme
        String delImpliquer = "DELETE FROM impliquer WHERE id_groupe = ?";
        String delGroupe = "DELETE FROM groupe WHERE id = ?";

        try (Connection con = db.getConnection()) {
            // Transaction explicite pour garantir la coherence des suppressions.
            con.setAutoCommit(false);
            try {
                try (PreparedStatement ps = con.prepareStatement(delImpliquer)) {
                    ps.setInt(1, id);
                    ps.executeUpdate();
                }
                int updated;
                try (PreparedStatement ps = con.prepareStatement(delGroupe)) {
                    ps.setInt(1, id);
                    updated = ps.executeUpdate();
                }
                con.commit();
                return updated > 0;
            } catch (Exception e) {
                // En cas d'echec partiel, retour etat initial.
                con.rollback();
                throw e;
            } finally {
                // Restaure le mode par defaut de la connexion.
                con.setAutoCommit(true);
            }
        }
    }
}
