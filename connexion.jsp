<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <title>Connexion - FanfareHub</title>
</head>
<body>
    <h1>Connexion à FanfareHub</h1>
    <h1>Se connecter</h1>
    <form method="get" action="GestionFanfaron">
        <input type='hidden' name='action' value='connexion'>
        Nom fanfaron : <input type="text" name="nomFanfaron" required><br><br>
        mot de passe : <input type="text" name="mdp" required><br><br>
        <input type="submit" value="Valider">
    </form>
    <p>
        Pas encore inscrit ?
        <a href="inscription.jsp">Créer un compte</a>
    </p>
</body>
</html>