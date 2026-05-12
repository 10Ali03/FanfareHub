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
    private FanfaronDAO fanfaronDAO;
    private EvenementDAO evenementDAO;
    private PupitreDAO pupitreDAO;

    @Override
    public void init() {
        DbConnectionManager db = DbConnectionManager.getInstance();
        fanfaronDAO = new FanfaronDAO(db);
        evenementDAO = new EvenementDAO(db);
        pupitreDAO = new PupitreDAO(db);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        String login = session == null ? null : (String) session.getAttribute("login");
        if (login == null) {
            res.sendRedirect("connexion.jsp");
            return;
        }

        try {
            int idFanfaron = fanfaronDAO.findIdByNomFanfaron(login);
            boolean canManage = evenementDAO.isCommissionPrestationMember(idFanfaron);
            String action = req.getParameter("action");
            if (action == null) action = "list";

            if ("participation".equals(action)) {
                int idEvenement = Integer.parseInt(req.getParameter("id"));
                req.setAttribute("evenement", evenementDAO.findById(idEvenement));
                req.setAttribute("pupitres", pupitreDAO.findAll());
                req.setAttribute("myInstrumentId", evenementDAO.findMyInstrumentForEvent(idFanfaron, idEvenement));
                req.setAttribute("myStatut", evenementDAO.findMyStatutForEvent(idFanfaron, idEvenement));
                req.setAttribute("inscriptions", evenementDAO.findInscriptionsByEvenement(idEvenement));
                req.getRequestDispatcher("evenementParticipation.jsp").forward(req, res);
                return;
            }

            req.setAttribute("canManage", canManage);
            req.setAttribute("evenements", evenementDAO.findAll());
            if ("edit".equals(action) && canManage) {
                int id = Integer.parseInt(req.getParameter("id"));
                req.setAttribute("editingEvenement", evenementDAO.findById(id));
            }
            req.getRequestDispatcher("evenements.jsp").forward(req, res);
        } catch (Exception e) {
            e.printStackTrace();
            res.sendError(500, "Erreur serveur : " + e.getMessage());
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        HttpSession session = req.getSession(false);
        String login = session == null ? null : (String) session.getAttribute("login");
        if (login == null) {
            res.sendRedirect("connexion.jsp");
            return;
        }

        String action = req.getParameter("action");
        if (action == null) {
            res.sendError(400, "Action manquante");
            return;
        }

        try {
            int idFanfaron = fanfaronDAO.findIdByNomFanfaron(login);

            if ("saveParticipation".equals(action)) {
                int idEvenement = Integer.parseInt(req.getParameter("idEvenement"));
                int idInstrument = Integer.parseInt(req.getParameter("idInstrument"));
                String statut = req.getParameter("statut");
                evenementDAO.replaceParticipation(idFanfaron, idEvenement, idInstrument, statut);
                res.sendRedirect("Evenement?action=participation&id=" + idEvenement);
                return;
            }

            boolean canManage = evenementDAO.isCommissionPrestationMember(idFanfaron);
            if (!canManage) {
                res.sendError(403, "Acces reserve a la commission prestation");
                return;
            }

            if ("add".equals(action)) {
                Evenement e = readEvenementFromRequest(req, 0);
                evenementDAO.create(e, idFanfaron);
            } else if ("update".equals(action)) {
                int id = Integer.parseInt(req.getParameter("id"));
                Evenement e = readEvenementFromRequest(req, id);
                evenementDAO.update(e);
            } else if ("delete".equals(action)) {
                int id = Integer.parseInt(req.getParameter("id"));
                evenementDAO.delete(id);
            } else {
                res.sendError(404, "Action non supportée");
                return;
            }

            res.sendRedirect("Evenement?action=list");
        } catch (Exception e) {
            e.printStackTrace();
            res.sendError(500, "Erreur serveur : " + e.getMessage());
        }
    }

    private Evenement readEvenementFromRequest(HttpServletRequest req, int id) {
        String nom = req.getParameter("nom");
        String horodatage = req.getParameter("horodatage");
        int duree = Integer.parseInt(req.getParameter("duree"));
        String lieu = req.getParameter("lieu");
        String description = req.getParameter("description");
        Timestamp ts = EvenementDAO.fromDateTimeLocal(horodatage);
        return new Evenement(id, nom, ts, duree, lieu, description);
    }
}

