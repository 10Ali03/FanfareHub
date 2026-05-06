package controleur;

import dao.DbConnectionManager;
import dao.FanfaronDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import metier.Fanfaron;

import java.io.IOException;

@WebServlet("/connexion")
public class ConnexionServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {

        req.setCharacterEncoding("UTF-8");

        String nomFanfaron = req.getParameter("nom_fanfaron");
        String motDePasse = req.getParameter("mot_de_passe");

        FanfaronDAO dao = new FanfaronDAO(DbConnectionManager.getInstance());

        Fanfaron fanfaron = dao.verifIdentif(nomFanfaron, motDePasse);

        if (fanfaron != null) {
            HttpSession session = req.getSession(true);
            session.setAttribute("fanfaron", fanfaron);
            res.sendRedirect("vue/accueil.jsp");
        } else {
            req.setAttribute("erreur", "Nom de fanfaron ou mot de passe incorrect.");
            req.getRequestDispatcher("vue/connexion.jsp").forward(req, res);
        }
    }
}