<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" language="java" %>
<%!
    // Echapement HTML minimal pour les messages/valeurs venant du serveur.
    private static String h(String s) {
        if (s == null) return "";
        return s.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#x27;");
    }
%>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <title>Connexion - FanfareHub</title>
</head>
<body>
    <nav>
        <!-- Navigation vers les ecrans publics via controleur Auth -->
        <a href="Auth?action=showLogin">Connexion</a> |
        <a href="Auth?action=showSignup">Inscription</a>
    </nav>
    <h1>Connexion a FanfareHub</h1>
    <h1>Se connecter</h1>
    <!-- Message d'erreur de connexion (si present) -->
    <p><%= request.getAttribute("message") != null ? h((String) request.getAttribute("message")) : "" %></p>
    <form method="post" action="Auth">
        <!-- Action explicite interpretee par AuthServlet#doPost -->
        <input type='hidden' name='action' value='connexion'>
        <!-- Le login est conserve en cas d'echec pour eviter une ressaisie -->
        Nom fanfaron : <input type="text" name="nomFanfaron" value='<%= request.getAttribute("nomFanfaron") != null ? h((String) request.getAttribute("nomFanfaron")) : "" %>' required><br><br>
        <!-- Mot de passe non pre-rempli volontairement -->
        mot de passe : <input type="password" name="mdp" required><br><br>
        <input type="submit" value="Valider">
    </form>
    <p>
        Pas encore inscrit ?
        <a href="Auth?action=showSignup">Creer un compte</a>
    </p>
</body>
</html>
