<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="metier.Pupitre, metier.Groupe" %>

<%
    List<Pupitre> pupitres = (List<Pupitre>) request.getAttribute("pupitres");
    List<Groupe> groupes = (List<Groupe>) request.getAttribute("groupes");
    List<Integer> selectedPupitres = (List<Integer>) request.getAttribute("selectedPupitres");
    List<Integer> selectedGroupes = (List<Integer>) request.getAttribute("selectedGroupes");
    String message = (String) request.getAttribute("message");

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
<h1>Mes choix</h1>

<% if (message != null) { %>
    <p><%= message %></p>
<% } %>

<form method="post" action="GestionFanfaron">
    <input type="hidden" name="action" value="saveChoixGroupesPupitres"/>

    <h3>Pupitres</h3>
    <% for (Pupitre p : pupitres) { %>
        <label>
            <input type="checkbox" name="pupitres" value="<%= p.getId() %>"
                <%= selectedPupitres.contains(p.getId()) ? "checked" : "" %> />
            <%= p.getNom() %>
        </label><br/>
    <% } %>

    <h3>Groupes</h3>
    <% for (Groupe g : groupes) { %>
        <label>
            <input type="checkbox" name="groupes" value="<%= g.getId() %>"
                <%= selectedGroupes.contains(g.getId()) ? "checked" : "" %> />
            <%= g.getNom() %>
        </label><br/>
    <% } %>

    <br/>
    <button type="submit">Enregistrer</button>
</form>
</body>
</html>
