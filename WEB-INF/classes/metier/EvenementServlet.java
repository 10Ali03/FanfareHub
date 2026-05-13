package metier;

import dao.DbConnectionManager;
import dao.EvenementDAO;
import dao.FanfaronDAO;
import dao.PupitreDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.sql.Timestamp;

@WebServlet("/Evenement")
public class EvenementServlet extends HttpServlet {
    // DAO pour retrouver l'ID du fanfaron courant.
    private FanfaronDAO fanfaronDAO;
    // DAO metier evenement (CRUD + participation).
    private EvenementDAO evenementDAO;
    // DAO de reference pupitres pour les inscriptions evenement.
    private PupitreDAO pupitreDAO;

    @Override
    public void init() {
        // Recuperation du gestionnaire de connexion unique.
        DbConnectionManager db = DbConnectionManager.getInstance();
        // Initialisation des DAO utilises par le controleur.
        fanfaronDAO = new FanfaronDAO(db);
        evenementDAO = new EvenementDAO(db);
        pupitreDAO = new PupitreDAO(db);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        // Session lue sans creation automatique.
        HttpSession session = req.getSession(false);
        // "login" est l'identite fonctionnelle stockee en session.
        String login = session == null ? null : (String) session.getAttribute("login");
        // Si non authentifie, redirection vers login via controleur.
        if (login == null) {
            res.sendRedirect("Auth?action=showLogin");
            return;
        }

        try {
            // Traduction login -> id technique.
            int idFanfaron = fanfaronDAO.findIdByNomFanfaron(login);
            // Droit de gestion selon appartenance commission prestation.
            boolean canManage = evenementDAO.isCommissionPrestationMember(idFanfaron);
            // Action lue dans l'URL.
            String action = req.getParameter("action");
            // Action par defaut: liste des evenements.
            if (action == null) action = "list";

            // Cas special: page detail participation d'un evenement.
            if ("participation".equals(action)) {
                // ID evenement depuis l'URL.
                int idEvenement = Integer.parseInt(req.getParameter("id"));
                // Charge les donnees du detail evenement.
                req.setAttribute("evenement", evenementDAO.findById(idEvenement));
                // Charge la liste des pupitres selectionnables.
                req.setAttribute("pupitres", pupitreDAO.findAll());
                // Charge instrument deja choisi par l'utilisateur.
                req.setAttribute("myInstrumentId", evenementDAO.findMyInstrumentForEvent(idFanfaron, idEvenement));
                // Charge statut deja choisi par l'utilisateur.
                req.setAttribute("myStatut", evenementDAO.findMyStatutForEvent(idFanfaron, idEvenement));
                // Charge la liste complete des inscriptions pour affichage.
                req.setAttribute("inscriptions", evenementDAO.findInscriptionsByEvenement(idEvenement));
                // Rendu de la JSP detail.
                req.getRequestDispatcher("evenementParticipation.jsp").forward(req, res);
                return;
            }

            // Attribut utilises par la vue liste.
            req.setAttribute("canManage", canManage);
            req.setAttribute("evenements", evenementDAO.findAll());
            // Si action edit et droit OK, on precharge l'evenement a modifier.
            if ("edit".equals(action) && canManage) {
                int id = Integer.parseInt(req.getParameter("id"));
                req.setAttribute("editingEvenement", evenementDAO.findById(id));
            }
            // Affichage de la vue liste.
            req.getRequestDispatcher("evenements.jsp").forward(req, res);
        } catch (Exception e) {
            // Log serveur + message neutre utilisateur.
            e.printStackTrace();
            res.sendError(500, "Erreur serveur");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        // Encodage pour les champs texte.
        req.setCharacterEncoding("UTF-8");
        // Session utilisateur.
        HttpSession session = req.getSession(false);
        // Login en session.
        String login = session == null ? null : (String) session.getAttribute("login");
        // Blocage si non connecte.
        if (login == null) {
            res.sendRedirect("Auth?action=showLogin");
            return;
        }

        // Action de formulaire.
        String action = req.getParameter("action");
        // Validation minimale presence action.
        if (action == null) {
            res.sendError(400, "Action manquante");
            return;
        }

        try {
            // ID technique du fanfaron courant.
            int idFanfaron = fanfaronDAO.findIdByNomFanfaron(login);

            // Cas special: sauvegarde participation (accessible a tous connectes).
            if ("saveParticipation".equals(action)) {
                // Lecture des champs du formulaire participation.
                int idEvenement = Integer.parseInt(req.getParameter("idEvenement"));
                int idInstrument = Integer.parseInt(req.getParameter("idInstrument"));
                String statut = req.getParameter("statut");
                // Ecriture "replace" de la participation en base.
                evenementDAO.replaceParticipation(idFanfaron, idEvenement, idInstrument, statut);
                // Redirection PRG vers la meme page detail.
                res.sendRedirect("Evenement?action=participation&id=" + idEvenement);
                return;
            }

            // Controle droit gestion evenement.
            boolean canManage = evenementDAO.isCommissionPrestationMember(idFanfaron);
            if (!canManage) {
                res.sendError(403, "Acces reserve a la commission prestation");
                return;
            }

            // CRUD evenement via action.
            if ("add".equals(action)) {
                // Construction de l'objet depuis le formulaire.
                Evenement e = readEvenementFromRequest(req, 0);
                // Creation + lien auteur/proposeur selon schema.
                evenementDAO.create(e, idFanfaron);
            } else if ("update".equals(action)) {
                int id = Integer.parseInt(req.getParameter("id"));
                Evenement e = readEvenementFromRequest(req, id);
                evenementDAO.update(e);
            } else if ("delete".equals(action)) {
                int id = Integer.parseInt(req.getParameter("id"));
                evenementDAO.delete(id);
            } else {
                // Action inconnue.
                res.sendError(404, "Action non supportee");
                return;
            }

            // Redirection PRG vers la liste pour eviter resoumission.
            res.sendRedirect("Evenement?action=list");
        } catch (Exception e) {
            // Log serveur + message neutre cote client.
            e.printStackTrace();
            res.sendError(500, "Erreur serveur");
        }
    }

    private Evenement readEvenementFromRequest(HttpServletRequest req, int id) {
        // Lecture des champs du formulaire evenement.
        String nom = req.getParameter("nom");
        String horodatage = req.getParameter("horodatage");
        int duree = Integer.parseInt(req.getParameter("duree"));
        String lieu = req.getParameter("lieu");
        String description = req.getParameter("description");
        // Conversion HTML datetime-local -> Timestamp SQL.
        Timestamp ts = EvenementDAO.fromDateTimeLocal(horodatage);
        // Construction objet metier.
        return new Evenement(id, nom, ts, duree, lieu, description);
    }
}
