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
  private FanfaronDAO daoFanfaron;
  private PupitreDAO pupitreDAO;
  private GroupeDAO groupeDAO;

  @Override
  public void init() {
    DbConnectionManager dbManager = DbConnectionManager.getInstance();
    daoFanfaron = new FanfaronDAO(dbManager);
    pupitreDAO = new PupitreDAO(dbManager);
    groupeDAO = new GroupeDAO(dbManager);
  }

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
    String action = req.getParameter("action");
    if (action == null || !"choixGroupesPupitres".equals(action)) {
      res.sendError(404, "Action non supportee");
      return;
    }

    HttpSession session = req.getSession(false);
    String login = session == null ? null : (String) session.getAttribute("login");
    if (login == null) {
      res.sendRedirect("connexion.jsp");
      return;
    }

    try {
      int idFanfaron = daoFanfaron.findIdByNomFanfaron(login);
      req.setAttribute("pupitres", pupitreDAO.findAll());
      req.setAttribute("groupes", groupeDAO.findAll());
      req.setAttribute("selectedPupitres", daoFanfaron.findPupitreIdsByFanfaron(idFanfaron));
      req.setAttribute("selectedGroupes", daoFanfaron.findGroupeIdsByFanfaron(idFanfaron));
      req.getRequestDispatcher("groupesPupitres.jsp").forward(req, res);
    } catch (Exception e) {
      e.printStackTrace();
      res.sendError(500, "Erreur serveur : " + e.getMessage());
    }
  }

  @Override
  protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
    String action = req.getParameter("action");
    if (action == null || !"saveChoixGroupesPupitres".equals(action)) {
      res.sendError(404, "Action non supportee");
      return;
    }

    HttpSession session = req.getSession(false);
    String login = session == null ? null : (String) session.getAttribute("login");
    if (login == null) {
      res.sendRedirect("connexion.jsp");
      return;
    }

    try {
      int idFanfaron = daoFanfaron.findIdByNomFanfaron(login);
      String[] pupitres = req.getParameterValues("pupitres");
      String[] groupes = req.getParameterValues("groupes");
      daoFanfaron.saveChoix(idFanfaron, pupitres, groupes);

      req.setAttribute("pupitres", pupitreDAO.findAll());
      req.setAttribute("groupes", groupeDAO.findAll());
      req.setAttribute("selectedPupitres", daoFanfaron.findPupitreIdsByFanfaron(idFanfaron));
      req.setAttribute("selectedGroupes", daoFanfaron.findGroupeIdsByFanfaron(idFanfaron));
      req.setAttribute("message", "Choix enregistres.");
      req.getRequestDispatcher("groupesPupitres.jsp").forward(req, res);
    } catch (Exception e) {
      e.printStackTrace();
      res.sendError(500, "Erreur serveur : " + e.getMessage());
    }
  }
}
