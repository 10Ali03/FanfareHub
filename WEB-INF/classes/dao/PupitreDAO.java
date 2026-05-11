package dao;

import metier.Pupitre;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PupitreDAO {
    private final DbConnectionManager db;

    public PupitreDAO(DbConnectionManager db) {
        this.db = db;
    }

    public List<Pupitre> findAll() throws SQLException {
        String sql = "SELECT id, nom FROM pupitre ORDER BY nom";
        List<Pupitre> list = new ArrayList<>();

        try (Connection con = db.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(new Pupitre(rs.getInt("id"), rs.getString("nom")));
            }
        }
        return list;
    }
}
