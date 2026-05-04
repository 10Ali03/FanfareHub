<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <title>Connexion - FanfareHub</title>
</head>
<body>

<h1>Connexion à FanfareHub</h1>

<%
    String erreur = (String) request.getAttribute("erreur");
    if (erreur != null) {
%>
        <p style="color:red;"><%= erreur %></p>
<%
    }
%>

<form action="../connexion" method="post">

    <label for="nom_fanfaron">Nom de fanfaron :</label><br>
    <input type="text" id="nom_fanfaron" name="nom_fanfaron" required><br><br>

    <label for="mot_de_passe">Mot de passe :</label><br>
    <input type="password" id="mot_de_passe" name="mot_de_passe" required><br><br>

    <button type="submit">Se connecter</button>

</form>

<p>
    Pas encore inscrit ?
    <a href="inscription.jsp">Créer un compte</a>
</p>

</body>
</html>