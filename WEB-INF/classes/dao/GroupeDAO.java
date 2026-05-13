package dao;

import metier.Groupe;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class GroupeDAO {
    private final DbConnectionManager db;

    public GroupeDAO(DbConnectionManager db) {
        this.db = db;
    }

    public List<Groupe> findAll() throws SQLException {
        String sql = "SELECT id, nom FROM groupe ORDER BY nom";
        List<Groupe> list = new ArrayList<>();

        try (Connection con = db.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(new Groupe(rs.getInt("id"), rs.getString("nom")));
            }
        }
        return list;
    }

    public Groupe findById(int id) throws SQLException {
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
        String sql = "INSERT INTO groupe(nom) VALUES (?)";
        try (Connection con = db.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, nom);
            return ps.executeUpdate() > 0;
        }
    }

    public boolean update(int id, String nom) throws SQLException {
        String sql = "UPDATE groupe SET nom = ? WHERE id = ?";
        try (Connection con = db.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, nom);
            ps.setInt(2, id);
            return ps.executeUpdate() > 0;
        }
    }

    public boolean delete(int id) throws SQLException {
        String delImpliquer = "DELETE FROM impliquer WHERE id_groupe = ?";
        String delGroupe = "DELETE FROM groupe WHERE id = ?";

        try (Connection con = db.getConnection()) {
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
                con.rollback();
                throw e;
            } finally {
                con.setAutoCommit(true);
            }
        }
    }
}
