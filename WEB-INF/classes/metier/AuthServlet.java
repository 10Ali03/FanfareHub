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
  // DAO d'acces aux comptes fanfaron (connexion/inscription).
  private FanfaronDAO daoFanfaron;

  @Override
  public void init() {
    // Initialisation du DAO avec le gestionnaire de connexion mutualise.
    DbConnectionManager dbManager = DbConnectionManager.getInstance();
    daoFanfaron = new FanfaronDAO(dbManager);
  }

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
    // L'action GET pilote l'affichage login/signup et le logout.
    String action = req.getParameter("action");
    if (action == null || action.isEmpty() || "showLogin".equals(action)) {
      // Affiche le formulaire de connexion.
      req.getRequestDispatcher("connexion.jsp").forward(req, res);
      return;
    }
    if ("showSignup".equals(action)) {
      // Affiche le formulaire d'inscription.
      req.getRequestDispatcher("inscription.jsp").forward(req, res);
      return;
    }
    if ("logout".equals(action)) {
      // Deconnexion: invalidation complete de session.
      HttpSession session = req.getSession(false);
      if (session != null) {
        session.invalidate();
      }
      // Redirige vers login via controleur.
      res.sendRedirect("Auth?action=showLogin");
      return;
    }
    res.sendError(405, "Methode non autorisee");
  }

  @Override
  protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
    // Encodage UTF-8 pour toutes les donnees formulaire.
    req.setCharacterEncoding("UTF-8");
    String action = req.getParameter("action");
    if (action == null) {
      res.sendError(400, "Action manquante");
      return;
    }

    // Vue cible par defaut (sera redirigee via Profil si succes).
    String vue = "menu.jsp";
    // Cree/recupere la session pour stocker les infos d'authentification.
    HttpSession session = req.getSession(true);

    try {
      switch (action) {
        case "connexion": {
          // Lecture des identifiants saisis.
          String nomFanfaron = req.getParameter("nomFanfaron");
          String mdp = req.getParameter("mdp");
          // Verification du couple login/mot de passe.
          Fanfaron f = daoFanfaron.verifIdentif(nomFanfaron, mdp);

          if (f != null) {
            // Mise a jour de la derniere connexion en base.
            daoFanfaron.updateDerniereConnexion(nomFanfaron, new java.sql.Timestamp(System.currentTimeMillis()));
            // Stockage du contexte utilisateur en session.
            session.setAttribute("login", nomFanfaron);
            session.setAttribute("role", f.getRole());
            vue = "menu.jsp";
          } else {
            // Reaffichage login avec message utilisateur.
            req.setAttribute("nomFanfaron", nomFanfaron);
            req.setAttribute("message", "Identifiants invalides.");
            vue = "connexion.jsp";
          }
          break;
        }

        case "inscription": {
          // Lecture des champs du formulaire d'inscription.
          String nomFanfaron = req.getParameter("nomFanfaron");
          String email = req.getParameter("email");
          String emailConfirm = req.getParameter("emailConfirm");
          String mdp = req.getParameter("mdp");
          String mdpConfirm = req.getParameter("mdpConfirm");
          String prenom = req.getParameter("prenom");
          String nom = req.getParameter("nom");
          String genre = req.getParameter("genre");
          String contraintesAlim = req.getParameter("contraintesAlim");

          // Validation metier: les deux emails doivent etre identiques.
          if (email == null || emailConfirm == null || !email.equals(emailConfirm)) {
            req.setAttribute("message", "Les adresses email ne sont pas les memes !");
            vue = "inscription.jsp";
            break;
          }

          // Validation metier: les deux mots de passe doivent etre identiques.
          if (mdp == null || mdpConfirm == null || !mdp.equals(mdpConfirm)) {
            req.setAttribute("message", "Les mots de passe ne sont pas les memes !");
            vue = "inscription.jsp";
            break;
          }

          // Verification de presence des champs obligatoires.
          if (nomFanfaron == null || email == null || mdp == null || prenom == null
              || nom == null || genre == null || contraintesAlim == null) {
            res.sendError(400, "Parametres manquants pour l'action creer");
            return;
          }

          // Controle d'unicite du nom fanfaron.
          if (daoFanfaron.existsByNomFanfaron(nomFanfaron)) {
            req.setAttribute("message", "Ce nom fanfaron est deja utilise.");
            vue = "inscription.jsp";
            break;
          }
          // Controle d'unicite de l'email.
          if (daoFanfaron.existsByEmail(email)) {
            req.setAttribute("message", "Cette adresse email est deja utilisee.");
            vue = "inscription.jsp";
            break;
          }

          // Construction de l'entite metier avant insertion.
          java.sql.Timestamp now = new java.sql.Timestamp(System.currentTimeMillis());
          Fanfaron fanfaron = new Fanfaron(
              nomFanfaron, email, mdp, prenom, nom, genre, contraintesAlim, "utilisateur", now, null
          );
          // Insertion en base (mot de passe hache dans le DAO).
          boolean success = daoFanfaron.create(fanfaron);
          if (success) {
            // Connexion automatique apres inscription reussie.
            session.setAttribute("login", nomFanfaron);
            session.setAttribute("role", "utilisateur");
            vue = "menu.jsp";
          } else {
            // Erreur fonctionnelle lors de l'insertion.
            req.setAttribute("message", "Erreur lors de l'ajout du fanfaron.");
            vue = "inscription.jsp";
          }
          break;
        }

        default:
          res.sendError(404, "Action non supportee");
          return;
      }
    } catch (Exception e) {
      e.printStackTrace();
      res.sendError(500, "Erreur serveur");
      return;
    }

    if ("menu.jsp".equals(vue)) {
      // On passe par le controleur Profil (et non un lien JSP direct).
      res.sendRedirect("Profil");
      return;
    }
    // Pour login/signup en echec: rendu direct de la JSP cible.
    req.getRequestDispatcher(vue).forward(req, res);
  }
}
