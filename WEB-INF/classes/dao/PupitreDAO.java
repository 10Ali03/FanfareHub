package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import metier.Pupitre;

public class PupitreDAO {
    private final DbConnectionManager dbConnectionManager;

    public PupitreDAO(DbConnectionManager dbManager) {
        this.dbConnectionManager = dbManager;
    }

    public List<Pupitre> findAll() throws SQLException {
        String sql = "SELECT id, nom FROM pupitre ORDER BY nom";
        List<Pupitre> pupitres = new ArrayList<>();

        try (Connection con = dbConnectionManager.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                pupitres.add(new Pupitre(rs.getInt("id"), rs.getString("nom")));
            }
        }

        return pupitres;
    }
}
