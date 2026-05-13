package metier;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

@WebServlet("/Profil")
public class ProfilServlet extends HttpServlet {
  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
    // On lit la session existante sans en creer une nouvelle.
    HttpSession session = req.getSession(false);
    // "login" est notre marqueur d'authentification.
    String login = session == null ? null : (String) session.getAttribute("login");
    // Si non connecte, retour vers le controleur d'authentification.
    if (login == null) {
      res.sendRedirect("Auth?action=showLogin");
      return;
    }
    // Si connecte, on affiche la vue menu via le controleur Profil.
    req.getRequestDispatcher("menu.jsp").forward(req, res);
  }
}
