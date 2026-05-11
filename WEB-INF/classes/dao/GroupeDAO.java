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
}
