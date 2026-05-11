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
    HttpSession session = req.getSession(false);
    String login = session == null ? null : (String) session.getAttribute("login");
    if (login == null) {
      res.sendRedirect("connexion.jsp");
      return;
    }
    req.getRequestDispatcher("menu.jsp").forward(req, res);
  }
}
