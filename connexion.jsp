<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" language="java" %>

<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <title>Connexion - FanfareHub</title>
</head>
<body>
    <nav>
        <a href="connexion.jsp">Connexion</a> |
        <a href="inscription.jsp">Inscription</a>
    </nav>
    <h1>Connexion à FanfareHub</h1>
    <h1>Se connecter</h1>
    <form method="post" action="Auth">
        <input type='hidden' name='action' value='connexion'>
        Nom fanfaron : <input type="text" name="nomFanfaron" required><br><br>
        mot de passe : <input type="password" name="mdp" required><br><br>
        <input type="submit" value="Valider">
    </form>
    <p>
        Pas encore inscrit ?
        <a href="inscription.jsp">Créer un compte</a>
    </p>
</body>
</html>


