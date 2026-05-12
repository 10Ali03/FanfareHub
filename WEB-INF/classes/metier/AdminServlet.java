package metier;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

import dao.DbConnectionManager;
import dao.FanfaronDAO;

@WebServlet("/Admin")
public class AdminServlet extends HttpServlet {
  private FanfaronDAO daoFanfaron;

  @Override
  public void init() {
    DbConnectionManager dbManager = DbConnectionManager.getInstance();
    daoFanfaron = new FanfaronDAO(dbManager);
  }

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
    if (!isAdmin(req.getSession(false))) {
      res.sendError(403, "Acces refuse");
      return;
    }
    String action = req.getParameter("action");
    if ("edit".equals(action)) {
      String nomFanfaron = req.getParameter("nomFanfaron");
      if (nomFanfaron != null && !nomFanfaron.isEmpty()) {
        req.setAttribute("editingFanfaron", daoFanfaron.findByNomFanfaron(nomFanfaron));
      }
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
          Fanfaron fanfaron = new Fanfaron(nomFanfaron, email, mdp, prenom, nom, genre, contraintesAlim, role, now, now);
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
        default:
          loadPage(req, res, "Action non supportée.");
      }
    } catch (Exception e) {
      e.printStackTrace();
      loadPage(req, res, "Erreur serveur: " + e.getMessage());
    }
  }

  private boolean isAdmin(HttpSession session) {
    if (session == null) return false;
    Object role = session.getAttribute("role");
    return role != null && "admin".equals(role.toString());
  }

  private void loadPage(HttpServletRequest req, HttpServletResponse res, String message) throws ServletException, IOException {
    try {
      req.setAttribute("fanfarons", daoFanfaron.findAllFanfarons());
      if (message != null) req.setAttribute("message", message);
      req.getRequestDispatcher("administration.jsp").forward(req, res);
    } catch (Exception e) {
      throw new ServletException(e);
    }
  }
}

