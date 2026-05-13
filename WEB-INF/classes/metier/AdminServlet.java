package metier;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.sql.Timestamp;

import dao.DbConnectionManager;
import dao.EvenementDAO;
import dao.FanfaronDAO;
import dao.GroupeDAO;
import dao.PupitreDAO;

@WebServlet("/Admin")
public class AdminServlet extends HttpServlet {
  private FanfaronDAO daoFanfaron;
  private PupitreDAO pupitreDAO;
  private GroupeDAO groupeDAO;
  private EvenementDAO evenementDAO;

  @Override
  public void init() {
    DbConnectionManager dbManager = DbConnectionManager.getInstance();
    daoFanfaron = new FanfaronDAO(dbManager);
    pupitreDAO = new PupitreDAO(dbManager);
    groupeDAO = new GroupeDAO(dbManager);
    evenementDAO = new EvenementDAO(dbManager);
  }

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
    if (!isAdmin(req.getSession(false))) {
      res.sendError(403, "Acces refuse");
      return;
    }
    String action = req.getParameter("action");
    try {
      if ("edit".equals(action)) {
        String nomFanfaron = req.getParameter("nomFanfaron");
        if (nomFanfaron != null && !nomFanfaron.isEmpty()) {
          req.setAttribute("editingFanfaron", daoFanfaron.findByNomFanfaron(nomFanfaron));
        }
      } else if ("editPupitre".equals(action)) {
        int id = parseInt(req.getParameter("id"));
        if (id > 0) req.setAttribute("editingPupitre", pupitreDAO.findById(id));
      } else if ("editGroupe".equals(action)) {
        int id = parseInt(req.getParameter("id"));
        if (id > 0) req.setAttribute("editingGroupe", groupeDAO.findById(id));
      } else if ("editEvenement".equals(action)) {
        int id = parseInt(req.getParameter("id"));
        if (id > 0) req.setAttribute("editingEvenement", evenementDAO.findById(id));
      }
    } catch (Exception e) {
      throw new ServletException(e);
    }
    loadPage(req, res, null);
  }

  @Override
  protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
    req.setCharacterEncoding("UTF-8");
    if (!isAdmin(req.getSession(false))) {
      res.sendError(403, "Acces refuse");
      return;
    }

    String action = req.getParameter("action");
    if (action == null) {
      loadPage(req, res, "Action manquante.");
      return;
    }

    try {
      switch (action) {
        case "add": {
          String nomFanfaron = req.getParameter("nomFanfaron");
          String email = req.getParameter("email");
          String mdp = req.getParameter("mdp");
          String prenom = req.getParameter("prenom");
          String nom = req.getParameter("nom");
          String genre = req.getParameter("genre");
          String contraintesAlim = req.getParameter("contraintesAlim");
          String role = req.getParameter("role");

          java.sql.Timestamp now = new java.sql.Timestamp(System.currentTimeMillis());
          Fanfaron fanfaron = new Fanfaron(nomFanfaron, email, mdp, prenom, nom, genre, contraintesAlim, role, now, null);
          boolean ok = daoFanfaron.create(fanfaron);
          loadPage(req, res, ok ? "Fanfaron ajoute." : "Echec de l'ajout.");
          return;
        }
        case "update": {
          String ancienNom = req.getParameter("ancienNomFanfaron");
          String nomFanfaron = req.getParameter("nomFanfaron");
          String email = req.getParameter("email");
          String newMdp = req.getParameter("mdp");
          String prenom = req.getParameter("prenom");
          String nom = req.getParameter("nom");
          String genre = req.getParameter("genre");
          String contraintesAlim = req.getParameter("contraintesAlim");
          String role = req.getParameter("role");

          Fanfaron fanfaron = new Fanfaron(nomFanfaron, email, "", prenom, nom, genre, contraintesAlim, role, null, null);
          boolean ok = daoFanfaron.updateByNomFanfaron(ancienNom, fanfaron, newMdp);
          loadPage(req, res, ok ? "Fanfaron modifie." : "Echec de la modification.");
          return;
        }
        case "delete": {
          String nomFanfaron = req.getParameter("nomFanfaron");
          boolean ok = daoFanfaron.deleteByNomFanfaron(nomFanfaron);
          loadPage(req, res, ok ? "Fanfaron supprime." : "Echec de la suppression.");
          return;
        }
        case "addPupitre": {
          String nom = trimToNull(req.getParameter("nomPupitre"));
          if (nom == null) {
            loadPage(req, res, "Nom du pupitre obligatoire.");
            return;
          }
          boolean ok = pupitreDAO.create(nom);
          loadPage(req, res, ok ? "Pupitre ajoute." : "Echec de l'ajout du pupitre.");
          return;
        }
        case "updatePupitre": {
          int id = parseInt(req.getParameter("idPupitre"));
          String nom = trimToNull(req.getParameter("nomPupitre"));
          if (id <= 0 || nom == null) {
            loadPage(req, res, "Parametres invalides pour la modification du pupitre.");
            return;
          }
          boolean ok = pupitreDAO.update(id, nom);
          loadPage(req, res, ok ? "Pupitre modifie." : "Echec de la modification du pupitre.");
          return;
        }
        case "deletePupitre": {
          int id = parseInt(req.getParameter("idPupitre"));
          boolean ok = id > 0 && pupitreDAO.delete(id);
          loadPage(req, res, ok ? "Pupitre supprime." : "Echec de la suppression du pupitre.");
          return;
        }
        case "addGroupe": {
          String nom = trimToNull(req.getParameter("nomGroupe"));
          if (nom == null) {
            loadPage(req, res, "Nom du groupe obligatoire.");
            return;
          }
          boolean ok = groupeDAO.create(nom);
          loadPage(req, res, ok ? "Groupe ajoute." : "Echec de l'ajout du groupe.");
          return;
        }
        case "updateGroupe": {
          int id = parseInt(req.getParameter("idGroupe"));
          String nom = trimToNull(req.getParameter("nomGroupe"));
          if (id <= 0 || nom == null) {
            loadPage(req, res, "Parametres invalides pour la modification du groupe.");
            return;
          }
          boolean ok = groupeDAO.update(id, nom);
          loadPage(req, res, ok ? "Groupe modifie." : "Echec de la modification du groupe.");
          return;
        }
        case "deleteGroupe": {
          int id = parseInt(req.getParameter("idGroupe"));
          boolean ok = id > 0 && groupeDAO.delete(id);
          loadPage(req, res, ok ? "Groupe supprime." : "Echec de la suppression du groupe.");
          return;
        }
        case "addEvenement": {
          Evenement e = readEvenementFromRequest(req, 0);
          String login = (String) req.getSession(false).getAttribute("login");
          int idProposer = daoFanfaron.findIdByNomFanfaron(login);
          boolean ok = evenementDAO.create(e, idProposer);
          loadPage(req, res, ok ? "Evenement ajoute." : "Echec de l'ajout de l'evenement.");
          return;
        }
        case "updateEvenement": {
          int id = parseInt(req.getParameter("idEvenement"));
          if (id <= 0) {
            loadPage(req, res, "ID evenement invalide.");
            return;
          }
          Evenement e = readEvenementFromRequest(req, id);
          boolean ok = evenementDAO.update(e);
          loadPage(req, res, ok ? "Evenement modifie." : "Echec de la modification de l'evenement.");
          return;
        }
        case "deleteEvenement": {
          int id = parseInt(req.getParameter("idEvenement"));
          boolean ok = id > 0 && evenementDAO.delete(id);
          loadPage(req, res, ok ? "Evenement supprime." : "Echec de la suppression de l'evenement.");
          return;
        }
        default:
          loadPage(req, res, "Action non supportee.");
      }
    } catch (Exception e) {
      e.printStackTrace();
      loadPage(req, res, "Erreur serveur.");
    }
  }

  private Evenement readEvenementFromRequest(HttpServletRequest req, int id) {
    String nom = req.getParameter("nomEvenement");
    String horodatage = req.getParameter("horodatageEvenement");
    int duree = parseInt(req.getParameter("dureeEvenement"));
    String lieu = req.getParameter("lieuEvenement");
    String description = req.getParameter("descriptionEvenement");
    Timestamp ts = EvenementDAO.fromDateTimeLocal(horodatage);
    return new Evenement(id, nom, ts, duree, lieu, description);
  }

  private boolean isAdmin(HttpSession session) {
    if (session == null) return false;
    Object role = session.getAttribute("role");
    return role != null && "admin".equals(role.toString());
  }

  private void loadPage(HttpServletRequest req, HttpServletResponse res, String message) throws ServletException, IOException {
    try {
      req.setAttribute("fanfarons", daoFanfaron.findAllFanfarons());
      req.setAttribute("pupitres", pupitreDAO.findAll());
      req.setAttribute("groupes", groupeDAO.findAll());
      req.setAttribute("evenements", evenementDAO.findAll());
      if (message != null) req.setAttribute("message", message);
      req.getRequestDispatcher("administration.jsp").forward(req, res);
    } catch (Exception e) {
      throw new ServletException(e);
    }
  }

  private int parseInt(String value) {
    try {
      return Integer.parseInt(value);
    } catch (Exception e) {
      return -1;
    }
  }

  private String trimToNull(String value) {
    if (value == null) return null;
    String trimmed = value.trim();
    return trimmed.isEmpty() ? null : trimmed;
  }
}
