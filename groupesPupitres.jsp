<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="metier.Pupitre, metier.Groupe" %>
<%!
    private static String h(String s) {
        if (s == null) return "";
        return s.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#x27;");
    }
%>
<%
    // Listes de reference chargees par ChoixServlet.
    List<Pupitre> pupitres = (List<Pupitre>) request.getAttribute("pupitres");
    List<Groupe> groupes = (List<Groupe>) request.getAttribute("groupes");
    // Listes des IDs deja selectionnes par l'utilisateur courant.
    List<Integer> selectedPupitres = (List<Integer>) request.getAttribute("selectedPupitres");
    List<Integer> selectedGroupes = (List<Integer>) request.getAttribute("selectedGroupes");
    // Message de confirmation apres sauvegarde.
    String message = (String) request.getAttribute("message");

    // Fallback defensif pour eviter les NullPointerException dans contains(...).
    if (selectedPupitres == null) selectedPupitres = new java.util.ArrayList<>();
    if (selectedGroupes == null) selectedGroupes = new java.util.ArrayList<>();
%>

<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <title>Choix Groupes et Pupitres</title>
</head>
<body>
    <nav>
        <a href="Profil">Menu</a> |
        <a href="Evenement?action=list">Evenements</a> |
        <a href="Choix?action=choixGroupesPupitres">Groupes/Pupitres</a> |
        <a href="Auth?action=logout">Deconnexion</a>
    </nav>
<h1>Mes choix</h1>
<p><a href="Profil">Retour au menu</a></p>

<% if (message != null) { %>
    <p><%= h(message) %></p>
<% } %>

<form method="post" action="Choix">
    <!-- Action explicite pour que le controleur sache quel traitement executer -->
    <input type="hidden" name="action" value="saveChoixGroupesPupitres"/>

    <h3>Pupitres</h3>
    <% for (Pupitre p : pupitres) { %>
        <label>
            <!-- Chaque case cochee envoie l'ID du pupitre -->
            <input type="checkbox" name="pupitres" value="<%= p.getId() %>"
                <%= selectedPupitres.contains(p.getId()) ? "checked" : "" %> />
            <%= h(p.getNom()) %>
        </label><br/>
    <% } %>

    <h3>Groupes</h3>
    <% for (Groupe g : groupes) { %>
        <label>
            <!-- Chaque case cochee envoie l'ID du groupe -->
            <input type="checkbox" name="groupes" value="<%= g.getId() %>"
                <%= selectedGroupes.contains(g.getId()) ? "checked" : "" %> />
            <%= h(g.getNom()) %>
        </label><br/>
    <% } %>

    <br/>
    <button type="submit">Enregistrer</button>
</form>
</body>
</html>
