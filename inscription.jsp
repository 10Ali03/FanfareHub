<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <title>Inscription</title>
</head>
<body>
    <h1>S'inscrire</h1>
    <form method="get" action="GestionFanfaron">
        <input type='hidden' name='action' value='inscription'>
        Nom fanfaron : <input type="text" name="nom_fanfaron" value='<%= request.getParameter("nom_fanfaron") != null ? request.getParameter("nom_fanfaron") : "" %>' required><br><br>
        email : <input type="email" name="email" required><br><br>
        mot de passe : <input type="text" name="mdp" value='<%= request.getParameter("mdp") != null ? request.getParameter("mdp") : "" %>' required><br><br>
        prenom : <input type="text" name="prenom" required><br><br>
        nom : <input type="text" name="nom" required><br><br>
        genre :
        <select id="genre" name="genre" required>
            <option value="homme">Homme</option>
            <option value="femme">Femme</option>
            <option value="autre">Autre</option>
        </select><br><br>
        contraintes alimentaires :
        <select id="contraintesAlim" name="contraintes_alimentaires" required>
            <option value="aucune">Aucune</option>
            <option value="vegetarien">Vegetarien</option>
            <option value="vegan">Vegan</option>
            <option value="sans porc">Sans porc</option>
        </select><br><br>
        <input type="submit" value="Valider">
    </form>
    <p>
        Déjà inscrit ?
        <a href="connexion.html">Se connecter</a>
    </p>

</body>
</html>