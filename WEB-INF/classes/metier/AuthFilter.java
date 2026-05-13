package metier;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

@WebFilter("/*")
public class AuthFilter extends HttpFilter implements Filter {
    @Override
    protected void doFilter(HttpServletRequest req, HttpServletResponse res, FilterChain chain) throws IOException, ServletException {
        // Le contextPath permet de rendre les comparaisons robustes, meme si l'appli change de nom.
        String contextPath = req.getContextPath();
        String uri = req.getRequestURI();
        // On retire le prefixe contextPath pour manipuler un chemin "interne" propre.
        String path = uri.substring(contextPath.length());

        // Route du controleur d'authentification.
        boolean isAuthRoute = path.startsWith("/Auth");
        // Ressources publiques autorisees sans session.
        boolean isPublicAsset = path.equals("/") || path.equals("/index.html") || path.endsWith(".css") || path.endsWith(".js") || path.endsWith(".png") || path.endsWith(".jpg") || path.endsWith(".jpeg") || path.endsWith(".gif") || path.endsWith(".ico");

        // Session lue en mode non-creatif pour eviter une session fantome.
        HttpSession session = req.getSession(false);
        // Un utilisateur est considere authentifie si "login" est present en session.
        boolean authenticated = session != null && session.getAttribute("login") != null;

        // Interdiction d'acceder directement aux JSP: on impose le passage par les controleurs.
        if (path.endsWith(".jsp")) {
            // Si deja connecte, on renvoie vers la page applicative.
            if (authenticated) {
                res.sendRedirect("Profil");
            } else {
                // Sinon, on renvoie vers la connexion.
                res.sendRedirect("Auth?action=showLogin");
            }
            return;
        }

        // Un utilisateur non connecte n'accede qu'aux routes publiques/auth.
        if (!authenticated && !isAuthRoute && !isPublicAsset) {
            res.sendRedirect("Auth?action=showLogin");
            return;
        }

        // Un utilisateur connecte ne doit pas revisiter login/signup.
        if (authenticated && isAuthRoute) {
            String action = req.getParameter("action");
            // Exception: logout reste autorise.
            if (!"logout".equals(action)) {
                res.sendRedirect("Profil");
                return;
            }
        }

        // Si rien ne bloque, on poursuit la chaine normale des filtres/servlets.
        chain.doFilter(req, res);
    }
}
