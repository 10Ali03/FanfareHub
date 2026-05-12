<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <title>Menu - FanfareHub</title>
</head>
<body>
    <h1>Menu</h1>
    <% 
    String login = (String)session.getAttribute( "login" );
    String role = (String)session.getAttribute( "role" );
    if (login == null){
        response.sendRedirect("connexion.jsp");
        return;
    } 
    if ("admin".equals(role)) { %>
        <p><a href="Admin">Administration des fanfarons</a></p>
    <% } %>
    <p><a href="Evenement?action=list">Evenements</a></p>
    <p><a href="Choix?action=choixGroupesPupitres">Gerer mes groupes et pupitres</a></p>
    <p><a href="Auth?action=logout">Se déconnecter</a></p>
</body>
</html>

