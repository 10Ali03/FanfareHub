package metier;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import dao.*;

import java.util.Date;

@WebServlet("/GestionFanfaron")
public class GestionFanfaronServlet extends HttpServlet {
  private FanfaronDAO daoFanfaron;
  @Override
  public void init() {
    DbConnectionManager dbManager = DbConnectionManager.getInstance();
    daoFanfaron = new FanfaronDAO(dbManager);
  }

  @Override
  protected void service(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
    String action = req.getParameter("action");
    String vue = "menu.jsp";
    HttpSession session = req.getSession(true);

    try {
      switch (action) {
        case "connexion": {
          String nomFanfaron = req.getParameter("nom_fanfaron");
          String mdp = req.getParameter("mdp");

          Fanfaron f = daoFanfaron.verifIdentif(nomFanfaron, mdp);
          
          if (f != null) {
            session.setAttribute("login",nomFanfaron);
            session.setAttribute("role",f.getRole());
            vue = "menu.jsp";
          } else {
            req.setAttribute("nomFanfaron", nomFanfaron);
            req.setAttribute("mdp", mdp);
            vue = "inscription.jsp";
          }
            
          break;
        }

        case "inscription": {
          
          String nomFanfaron = req.getParameter("nom_fanfaron");
          String email = req.getParameter("email");
          String mdp = req.getParameter("mdp");
          String prenom = req.getParameter("prenom");
          String nom = req.getParameter("nom");
          String genre = req.getParameter("genre");
          String contraintesAlim = req.getParameter("contraintes_alimentaires");
          
          java.sql.Timestamp now = new java.sql.Timestamp(System.currentTimeMillis());

          if (nomFanfaron == null || email == null || mdp == null || prenom == null || nom == null || genre == null || contraintesAlim == null ) {
            res.sendError(400, "Paramètres manquants pour l'action créer");
            return;
          }

          try {
            Fanfaron fanfaron = new Fanfaron(nomFanfaron, email, mdp, prenom, nom, genre, contraintesAlim, "utilisateur", now, now);
            boolean success = daoFanfaron.create(fanfaron);
            if (success) {
              session.setAttribute("login",nomFanfaron);
              session.setAttribute("role","utilisateur");
              vue = "menu.jsp";
            }
            else {
              vue = "inscription.jsp";
            }
            
          } catch (Exception e) {
            e.printStackTrace();
            return;
          }

          break;
        }
        case "addUser": {
          break;
        }
        case "updateUser": {
          break;
        }
        case "modifierUser" :{
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