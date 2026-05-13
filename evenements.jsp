<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="metier.Evenement" %>
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
    List<Evenement> evenements = (List<Evenement>) request.getAttribute("evenements");
    Evenement editing = (Evenement) request.getAttribute("editingEvenement");
    Boolean canManage = (Boolean) request.getAttribute("canManage");
    if (canManage == null) canManage = false;
%>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <title>Evenements</title>
</head>
<body>
    <nav>
        <a href="Profil">Menu</a> |
        <a href="Evenement?action=list">Evenements</a> |
        <a href="Choix?action=choixGroupesPupitres">Groupes/Pupitres</a> |
        <a href="Auth?action=logout">Deconnexion</a>
    </nav>
    <h1>Evenements</h1>
    <p><a href="Profil">Retour menu</a></p>

    <table border="1" cellpadding="6">
        <tr>
            <th>Nom</th>
            <th>Date</th>
            <th>Duree (min)</th>
            <th>Lieu</th>
            <th>Description</th>
            <th>Actions</th>
        </tr>
        <% if (evenements != null) for (Evenement e : evenements) { %>
        <tr>
            <td><%= h(e.getNom()) %></td>
            <td><%= e.getHorodatage() %></td>
            <td><%= e.getDuree() %></td>
            <td><%= h(e.getLieu()) %></td>
            <td><%= e.getDescription() == null ? "" : h(e.getDescription()) %></td>
            <td>
                <a href="Evenement?action=participation&id=<%= e.getId() %>">Voir / m'inscrire</a>
                <% if (canManage) { %>
                | <a href="Evenement?action=edit&id=<%= e.getId() %>">Modifier</a>
                | <form method="post" action="Evenement" style="display:inline;" onsubmit="return confirm('Supprimer cet evenement ?');">
                    <input type="hidden" name="action" value="delete">
                    <input type="hidden" name="id" value="<%= e.getId() %>">
                    <button type="submit">Supprimer</button>
                  </form>
                <% } %>
            </td>
        </tr>
        <% } %>
    </table>

    <% if (canManage) { %>
    <h2>Ajouter un evenement</h2>
    <form method="post" action="Evenement">
        <input type="hidden" name="action" value="add">
        Nom: <input type="text" name="nom" required><br>
        Horodatage: <input type="datetime-local" name="horodatage" required><br>
        Duree (minutes): <input type="number" name="duree" min="1" required><br>
        Lieu: <input type="text" name="lieu" required><br>
        Description: <textarea name="description"></textarea><br>
        <button type="submit">Ajouter</button>
    </form>

    <% if (editing != null) { %>
    <h2>Modifier un evenement</h2>
    <form method="post" action="Evenement">
        <input type="hidden" name="action" value="update">
        ID: <input type="number" name="id" value="<%= editing != null ? editing.getId() : "" %>" required><br>
        Nom: <input type="text" name="nom" value="<%= editing != null ? h(editing.getNom()) : "" %>" required><br>
        Horodatage: <input type="datetime-local" name="horodatage" value="<%= editing != null ? editing.getHorodatage().toString().replace(' ', 'T').substring(0,16) : "" %>" required><br>
        Duree (minutes): <input type="number" name="duree" min="1" value="<%= editing != null ? editing.getDuree() : "" %>" required><br>
        Lieu: <input type="text" name="lieu" value="<%= editing != null ? h(editing.getLieu()) : "" %>" required><br>
        Description: <textarea name="description"><%= editing != null && editing.getDescription() != null ? h(editing.getDescription()) : "" %></textarea><br>
        <button type="submit">Enregistrer modification</button>
    </form>
    <% } %>
    <% } %>
</body>
</html>

