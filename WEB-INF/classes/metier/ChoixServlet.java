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
import dao.GroupeDAO;
import dao.PupitreDAO;

@WebServlet("/Choix")
public class ChoixServlet extends HttpServlet {
  // DAO principal pour retrouver l'utilisateur courant et ecrire ses choix.
  private FanfaronDAO daoFanfaron;
  // DAO de reference des pupitres affiches dans la vue.
  private PupitreDAO pupitreDAO;
  // DAO de reference des groupes affiches dans la vue.
  private GroupeDAO groupeDAO;

  @Override
  public void init() {
    // Recuperation du gestionnaire de connexion commun (singleton).
    DbConnectionManager dbManager = DbConnectionManager.getInstance();
    // Initialisation des DAO utilises par ce controleur.
    daoFanfaron = new FanfaronDAO(dbManager);
    pupitreDAO = new PupitreDAO(dbManager);
    groupeDAO = new GroupeDAO(dbManager);
  }

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
    // Cette page est pilotee par l'action explicite "choixGroupesPupitres".
    String action = req.getParameter("action");
    if (action == null || !"choixGroupesPupitres".equals(action)) {
      res.sendError(404, "Action non supportee");
      return;
    }

    // Verification session utilisateur.
    HttpSession session = req.getSession(false);
    String login = session == null ? null : (String) session.getAttribute("login");
    if (login == null) {
      res.sendRedirect("Auth?action=showLogin");
      return;
    }

    try {
      // Resolution du login fonctionnel vers l'ID technique.
      int idFanfaron = daoFanfaron.findIdByNomFanfaron(login);
      // Chargement des listes de reference.
      req.setAttribute("pupitres", pupitreDAO.findAll());
      req.setAttribute("groupes", groupeDAO.findAll());
      // Chargement des choix deja enregistres pour pre-cocher la vue.
      req.setAttribute("selectedPupitres", daoFanfaron.findPupitreIdsByFanfaron(idFanfaron));
      req.setAttribute("selectedGroupes", daoFanfaron.findGroupeIdsByFanfaron(idFanfaron));
      // Rendu de la vue.
      req.getRequestDispatcher("groupesPupitres.jsp").forward(req, res);
    } catch (Exception e) {
      // Traces serveur pour debug + message neutre pour l'utilisateur.
      e.printStackTrace();
      res.sendError(500, "Erreur serveur");
    }
  }

  @Override
  protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
    // Encodage pour conserver correctement les caracteres accentues.
    req.setCharacterEncoding("UTF-8");
    // Action attendue lors de la soumission du formulaire.
    String action = req.getParameter("action");
    if (action == null || !"saveChoixGroupesPupitres".equals(action)) {
      res.sendError(404, "Action non supportee");
      return;
    }

    // Verification session utilisateur.
    HttpSession session = req.getSession(false);
    String login = session == null ? null : (String) session.getAttribute("login");
    if (login == null) {
      res.sendRedirect("Auth?action=showLogin");
      return;
    }

    try {
      // Identification du fanfaron courant.
      int idFanfaron = daoFanfaron.findIdByNomFanfaron(login);
      // Valeurs cochees dans le formulaire (peuvent etre null si rien coche).
      String[] pupitres = req.getParameterValues("pupitres");
      String[] groupes = req.getParameterValues("groupes");
      // Ecriture transactionnelle des choix en base.
      daoFanfaron.saveChoix(idFanfaron, pupitres, groupes);

      // Rechargement complet pour reaffichage a jour.
      req.setAttribute("pupitres", pupitreDAO.findAll());
      req.setAttribute("groupes", groupeDAO.findAll());
      req.setAttribute("selectedPupitres", daoFanfaron.findPupitreIdsByFanfaron(idFanfaron));
      req.setAttribute("selectedGroupes", daoFanfaron.findGroupeIdsByFanfaron(idFanfaron));
      req.setAttribute("message", "Choix enregistres.");
      req.getRequestDispatcher("groupesPupitres.jsp").forward(req, res);
    } catch (Exception e) {
      // Traces serveur pour debug + message neutre pour l'utilisateur.
      e.printStackTrace();
      res.sendError(500, "Erreur serveur");
    }
  }
}
