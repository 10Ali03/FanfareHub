package dao;

import metier.Evenement;
import metier.InscriptionEvenement;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class EvenementDAO {
    // Gestionnaire de connexion partage.
    private final DbConnectionManager db;

    public EvenementDAO(DbConnectionManager db) {
        // Injection du manager pour tous les acces SQL.
        this.db = db;
    }

    public List<Evenement> findAll() throws SQLException {
        // Liste des evenements, du plus recent au plus ancien.
        String sql = "SELECT id, nom, horodatage, duree, lieu, description FROM evenement ORDER BY horodatage DESC";
        List<Evenement> list = new ArrayList<>();
        try (Connection con = db.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            // Mapping ligne SQL -> objet metier.
            while (rs.next()) {
                list.add(new Evenement(
                        rs.getInt("id"),
                        rs.getString("nom"),
                        rs.getTimestamp("horodatage"),
                        rs.getInt("duree"),
                        rs.getString("lieu"),
                        rs.getString("description")
                ));
            }
        }
        return list;
    }

    public Evenement findById(int id) throws SQLException {
        // Recherche d'un evenement par cle primaire.
        String sql = "SELECT id, nom, horodatage, duree, lieu, description FROM evenement WHERE id = ?";
        try (Connection con = db.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Evenement(
                            rs.getInt("id"),
                            rs.getString("nom"),
                            rs.getTimestamp("horodatage"),
                            rs.getInt("duree"),
                            rs.getString("lieu"),
                            rs.getString("description")
                    );
                }
            }
        }
        // Introuvable.
        return null;
    }

    public boolean isCommissionPrestationMember(int fanfaronId) throws SQLException {
        // Verifie l'appartenance au groupe "commission prestation".
        // lower(trim()) rend le test tolerant a la casse/espaces.
        String sql = "SELECT 1 FROM impliquer i JOIN groupe g ON g.id = i.id_groupe " +
                "WHERE i.id_fanfaron = ? AND LOWER(TRIM(g.nom)) = 'commission prestation'";
        try (Connection con = db.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, fanfaronId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    public boolean create(Evenement e, int proposerId) throws SQLException {
        // Compatibilite schema A: evenement sans id_fanfaron.
        String insertEvtWithoutOwner = "INSERT INTO evenement (nom, horodatage, duree, lieu, description) VALUES (?, ?, ?, ?, ?)";
        // Compatibilite schema B: evenement avec id_fanfaron NOT NULL.
        String insertEvtWithOwner = "INSERT INTO evenement (nom, horodatage, duree, lieu, description, id_fanfaron) VALUES (?, ?, ?, ?, ?, ?)";
        // Schema A: table lien proposer.
        String insertProp = "INSERT INTO proposer (id_evenement, id_fanfaron) VALUES (?, ?)";
        try (Connection con = db.getConnection()) {
            // Transaction explicite pour garantir la coherence.
            con.setAutoCommit(false);
            try {
                // Detection dynamique du schema en base.
                boolean hasOwnerColumn = hasEvenementOwnerColumn(con);
                // ID auto-genere du nouvel evenement.
                int newId = -1;
                int n;

                if (hasOwnerColumn) {
                    // Insertion directe avec id_fanfaron.
                    try (PreparedStatement psEvt = con.prepareStatement(insertEvtWithOwner, PreparedStatement.RETURN_GENERATED_KEYS)) {
                        psEvt.setString(1, e.getNom());
                        psEvt.setTimestamp(2, e.getHorodatage());
                        psEvt.setInt(3, e.getDuree());
                        psEvt.setString(4, e.getLieu());
                        psEvt.setString(5, e.getDescription());
                        psEvt.setInt(6, proposerId);
                        n = psEvt.executeUpdate();
                        try (ResultSet keys = psEvt.getGeneratedKeys()) {
                            if (keys.next()) newId = keys.getInt(1);
                        }
                    }
                } else {
                    // Insertion evenement seule (schema avec table proposer).
                    try (PreparedStatement psEvt = con.prepareStatement(insertEvtWithoutOwner, PreparedStatement.RETURN_GENERATED_KEYS)) {
                        psEvt.setString(1, e.getNom());
                        psEvt.setTimestamp(2, e.getHorodatage());
                        psEvt.setInt(3, e.getDuree());
                        psEvt.setString(4, e.getLieu());
                        psEvt.setString(5, e.getDescription());
                        n = psEvt.executeUpdate();
                        try (ResultSet keys = psEvt.getGeneratedKeys()) {
                            if (keys.next()) newId = keys.getInt(1);
                        }
                    }
                }

                // Verification insertion.
                if (n <= 0) {
                    con.rollback();
                    return false;
                }

                // Verification recuperation ID.
                if (newId <= 0) {
                    con.rollback();
                    return false;
                }

                if (!hasOwnerColumn) {
                    // Dans le schema sans id_fanfaron, on renseigne proposer.
                    try (PreparedStatement psProp = con.prepareStatement(insertProp)) {
                        psProp.setInt(1, newId);
                        psProp.setInt(2, proposerId);
                        psProp.executeUpdate();
                    }
                }

                // Validation transaction.
                con.commit();
                return true;
            } catch (Exception ex) {
                // En cas d'erreur, retour etat base avant transaction.
                con.rollback();
                throw ex;
            } finally {
                // Retour mode auto-commit standard.
                con.setAutoCommit(true);
            }
        }
    }

    private boolean hasEvenementOwnerColumn(Connection con) throws SQLException {
        // Interroge le catalogue pour detecter "evenement.id_fanfaron".
        String sql = "SELECT 1 FROM information_schema.columns WHERE table_name = 'evenement' AND column_name = 'id_fanfaron'";
        try (PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            return rs.next();
        }
    }

    public boolean update(Evenement e) throws SQLException {
        // Mise a jour standard d'un evenement.
        String sql = "UPDATE evenement SET nom = ?, horodatage = ?, duree = ?, lieu = ?, description = ? WHERE id = ?";
        try (Connection con = db.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, e.getNom());
            ps.setTimestamp(2, e.getHorodatage());
            ps.setInt(3, e.getDuree());
            ps.setString(4, e.getLieu());
            ps.setString(5, e.getDescription());
            ps.setInt(6, e.getId());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean delete(int id) throws SQLException {
        // Suppression evenement (les tables liees suivent selon FK).
        String sql = "DELETE FROM evenement WHERE id = ?";
        try (Connection con = db.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    public void replaceParticipation(int fanfaronId, int evenementId, int instrumentId, String statut) throws SQLException {
        // "Replace" logique: delete ancienne participation, puis insert nouvelle.
        String del = "DELETE FROM participer WHERE id_fanfaron = ? AND id_evenement = ?";
        String ins = "INSERT INTO participer (id_fanfaron, id_evenement, id_instrument, statut) VALUES (?, ?, ?, ?)";
        try (Connection con = db.getConnection()) {
            // Transaction pour eviter un etat intermediaire incoherent.
            con.setAutoCommit(false);
            try (PreparedStatement psDel = con.prepareStatement(del);
                 PreparedStatement psIns = con.prepareStatement(ins)) {
                // Suppression ancienne ligne.
                psDel.setInt(1, fanfaronId);
                psDel.setInt(2, evenementId);
                psDel.executeUpdate();

                // Insertion nouvelle ligne.
                psIns.setInt(1, fanfaronId);
                psIns.setInt(2, evenementId);
                psIns.setInt(3, instrumentId);
                psIns.setString(4, statut);
                psIns.executeUpdate();

                con.commit();
            } catch (Exception e) {
                con.rollback();
                throw e;
            } finally {
                con.setAutoCommit(true);
            }
        }
    }

    public Integer findMyInstrumentForEvent(int fanfaronId, int evenementId) throws SQLException {
        // Recupere l'instrument choisi par un fanfaron pour un evenement.
        String sql = "SELECT id_instrument FROM participer WHERE id_fanfaron = ? AND id_evenement = ? LIMIT 1";
        try (Connection con = db.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, fanfaronId);
            ps.setInt(2, evenementId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt(1) : null;
            }
        }
    }

    public String findMyStatutForEvent(int fanfaronId, int evenementId) throws SQLException {
        // Recupere le statut (present/incertain/absent) du fanfaron.
        String sql = "SELECT statut FROM participer WHERE id_fanfaron = ? AND id_evenement = ? LIMIT 1";
        try (Connection con = db.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, fanfaronId);
            ps.setInt(2, evenementId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getString(1) : null;
            }
        }
    }

    public List<InscriptionEvenement> findInscriptionsByEvenement(int evenementId) throws SQLException {
        // Liste complete des participants d'un evenement.
        // Tri: pupitre puis statut (present->incertain->absent) puis nom fanfaron.
        String sql = "SELECT f.nom_fanfaron, p.nom AS pupitre, pa.statut " +
                "FROM participer pa " +
                "JOIN fanfaron f ON f.id = pa.id_fanfaron " +
                "JOIN pupitre p ON p.id = pa.id_instrument " +
                "WHERE pa.id_evenement = ? " +
                "ORDER BY p.nom, CASE pa.statut WHEN 'present' THEN 1 WHEN 'incertain' THEN 2 WHEN 'absent' THEN 3 ELSE 4 END, f.nom_fanfaron";
        List<InscriptionEvenement> list = new ArrayList<>();
        try (Connection con = db.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, evenementId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new InscriptionEvenement(
                            rs.getString("nom_fanfaron"),
                            rs.getString("pupitre"),
                            rs.getString("statut")
                    ));
                }
            }
        }
        return list;
    }

    public static Timestamp fromDateTimeLocal(String value) {
        // Conversion champ HTML datetime-local -> java.sql.Timestamp.
        if (value == null || value.isEmpty()) return null;
        // HTML envoie "yyyy-MM-ddTHH:mm", Timestamp attend espace + secondes.
        return Timestamp.valueOf(value.replace("T", " ") + ":00");
    }
}
