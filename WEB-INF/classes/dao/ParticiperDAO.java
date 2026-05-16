package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import metier.Participer;

public class ParticiperDAO {

    // Gestionnaire de connexion partage.
    private final DbConnectionManager db;

    public ParticiperDAO(DbConnectionManager db) {
        // Injection du manager pour tous les acces SQL.
        this.db = db;
    }

    public List<Participer> findInscriptionsByEvenement(int evenementId) throws SQLException {
        // Liste complete des participants d'un evenement.
        // Tri: pupitre puis statut (present->incertain->absent) puis nom fanfaron.
        String sql = "SELECT * " +
                "FROM participer " +
                "WHERE id_evenement = ? " ;
        List<Participer> list = new ArrayList<>();
        try (Connection con = db.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, evenementId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new Participer(
                            rs.getInt("id_fanfaron"),
                            rs.getInt("id_evenement"),
                            rs.getInt("id_instrument"),
                            rs.getString("statut")
                    ));
                }
            }
        }
        return list;
    }
    
}
