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

@WebServlet("/Auth")
public class AuthServlet extends HttpServlet {
  private FanfaronDAO daoFanfaron;

  @Override
  public void init() {
    DbConnectionManager dbManager = DbConnectionManager.getInstance();
    daoFanfaron = new FanfaronDAO(dbManager);
  }

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
    String action = req.getParameter("action");
    if ("logout".equals(action)) {
      HttpSession session = req.getSession(false);
      if (session != null) {
        session.invalidate();
      }
      res.sendRedirect("connexion.jsp");
      return;
    }
    res.sendError(405, "Méthode non autorisée");
  }

  @Override
  protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
    req.setCharacterEncoding("UTF-8");
    String action = req.getParameter("action");
    if (action == null) {
      res.sendError(400, "Action manquante");
      return;
    }

    String vue = "menu.jsp";
    HttpSession session = req.getSession(true);

    try {
      switch (action) {
        case "connexion": {
          String nomFanfaron = req.getParameter("nomFanfaron");
          String mdp = req.getParameter("mdp");
          Fanfaron f = daoFanfaron.verifIdentif(nomFanfaron, mdp);

          if (f != null) {
            session.setAttribute("login", nomFanfaron);
            session.setAttribute("role", f.getRole());
            vue = "menu.jsp";
          } else {
            req.setAttribute("nomFanfaron", nomFanfaron);
            req.setAttribute("message", "Identifiants invalides.");
            vue = "connexion.jsp";
          }
          break;
        }

        case "inscription": {
          String nomFanfaron = req.getParameter("nomFanfaron");
          String email = req.getParameter("email");
          String mdp = req.getParameter("mdp");
          String mdpConfirm = req.getParameter("mdpConfirm");
          String prenom = req.getParameter("prenom");
          String nom = req.getParameter("nom");
          String genre = req.getParameter("genre");
          String contraintesAlim = req.getParameter("contraintesAlim");

          if (mdp == null || mdpConfirm == null || !mdp.equals(mdpConfirm)) {
            req.setAttribute("message", "Les mots de passe ne sont pas les mêmes !");
            vue = "inscription.jsp";
            break;
          }

          if (nomFanfaron == null || email == null || mdp == null || prenom == null
              || nom == null || genre == null || contraintesAlim == null) {
            res.sendError(400, "Paramètres manquants pour l'action créer");
            return;
          }

          java.sql.Timestamp now = new java.sql.Timestamp(System.currentTimeMillis());
          Fanfaron fanfaron = new Fanfaron(
              nomFanfaron, email, mdp, prenom, nom, genre, contraintesAlim, "utilisateur", now, now
          );
          boolean success = daoFanfaron.create(fanfaron);
          if (success) {
            session.setAttribute("login", nomFanfaron);
            session.setAttribute("role", "utilisateur");
            vue = "menu.jsp";
          } else {
            req.setAttribute("message", "Erreur lors de l'ajout du fanfaron !");
            vue = "inscription.jsp";
          }
          break;
        }

        default:
          res.sendError(404, "Action non supportée");
          return;
      }
    } catch (Exception e) {
      e.printStackTrace();
      res.sendError(500, "Erreur serveur : " + e.getMessage());
      return;
    }

    req.getRequestDispatcher(vue).forward(req, res);
  }
}

