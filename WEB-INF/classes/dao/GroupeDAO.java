package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import metier.Groupe;

public class GroupeDAO {
    private final DbConnectionManager dbConnectionManager;

    public GroupeDAO(DbConnectionManager dbManager) {
        this.dbConnectionManager = dbManager;
    }

    public List<Groupe> findAll() throws SQLException {
        String sql = "SELECT id, nom FROM groupe ORDER BY nom";
        List<Groupe> groupes = new ArrayList<>();

        try (Connection con = dbConnectionManager.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                groupes.add(new Groupe(rs.getInt("id"), rs.getString("nom")));
            }
        }

        return groupes;
    }
}
