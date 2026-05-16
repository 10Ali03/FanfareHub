package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import metier.Participer;

public class ParticiperDAO {

    // Gestionnaire de connexion partage.
    private final DbConnectionManager db;

    public ParticiperDAO(DbConnectionManager db) {
        // Injection du manager pour tous les acces SQL.
        this.db = db;
    }

    public List<HashMap<String, Object>> findInscriptionsByEvenement(int evenementId) throws SQLException {
        // Liste complete des participants d'un evenement.
        // Tri: pupitre puis statut (present->incertain->absent) puis nom fanfaron.
        String sql = "SELECT p.*, f.nom_fanfaron, pu.nom " +
        "FROM participer p " +
        "JOIN fanfaron f ON f.id = p.id_fanfaron " +
        "JOIN pupitre pu ON pu.id = p.id_instrument " +
        "WHERE p.id_evenement = ? " +
        "ORDER BY pu.nom, CASE p.statut WHEN 'present' THEN 1 WHEN 'incertain' THEN 2 WHEN 'absent' THEN 3 ELSE 4 END, f.nom_fanfaron";
        List<HashMap<String, Object>> list = new ArrayList<>();
        try (Connection con = db.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, evenementId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Participer p = new Participer(
                            rs.getInt("id_fanfaron"),
                            rs.getInt("id_evenement"),
                            rs.getInt("id_instrument"),
                            rs.getString("statut")
                    );

                    HashMap<String, Object> map = new HashMap<>();
                    map.put("participer", p);
                    map.put("nomFanfaron", rs.getString("nom_fanfaron"));
                    map.put("nomPupitre", rs.getString("nom"));
                    list.add(map);
                }
            }
        }
        return list;
    }
    
}
