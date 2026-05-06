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
        response.sendRedirect("login.html");
        return;
    } 
    if ("admin".equals(role)) { %>
        <a href="GestionFanfaron/action='pageAdmin'"> Voir les fanfarons</a>
    <% } %>
</body>
</html>