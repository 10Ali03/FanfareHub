<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="metier.Fanfaron" %>
<%
    List<Fanfaron> fanfarons = (List<Fanfaron>) request.getAttribute("fanfarons");
    String message = (String) request.getAttribute("message");
    Fanfaron editing = (Fanfaron) request.getAttribute("editingFanfaron");
%>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <title>Administration</title>
</head>
<body>
    <nav>
        <a href="menu.jsp">Menu</a> |
        <a href="Evenement?action=list">Evenements</a> |
        <a href="Choix?action=choixGroupesPupitres">Groupes/Pupitres</a> |
        <a href="Auth?action=logout">Deconnexion</a>
    </nav>
    <h1>Administration des fanfarons</h1>
    <% if (message != null) { %><p><%= message %></p><% } %>

    <h2>Liste</h2>
    <table border="1" cellpadding="6">
        <tr>
            <th>Nom fanfaron</th>
            <th>Email</th>
            <th>Prenom</th>
            <th>Nom</th>
            <th>Genre</th>
            <th>Contraintes</th>
            <th>Role</th>
            <th>Actions</th>
        </tr>
        <% if (fanfarons != null) for (Fanfaron f : fanfarons) { %>
        <tr>
            <td><%= f.getNomFanfaron() %></td>
            <td><%= f.getEmail() %></td>
            <td><%= f.getPrenom() %></td>
            <td><%= f.getNom() %></td>
            <td><%= f.getGenre() %></td>
            <td><%= f.getContraintesAlim() %></td>
            <td><%= f.getRole() %></td>
            <td>
                <form method="get" action="Admin" style="display:inline;">
                    <input type="hidden" name="action" value="edit">
                    <input type="hidden" name="nomFanfaron" value="<%= f.getNomFanfaron() %>">
                    <button type="submit">Modifier</button>
                </form>
                <form method="post" action="Admin" style="display:inline;" onsubmit="return confirm('Supprimer <%= f.getNomFanfaron() %> ?');">
                    <input type="hidden" name="action" value="delete">
                    <input type="hidden" name="nomFanfaron" value="<%= f.getNomFanfaron() %>">
                    <button type="submit">Supprimer</button>
                </form>
            </td>
        </tr>
        <% } %>
    </table>

    <h2>Ajouter</h2>
    <form method="post" action="Admin">
        <input type="hidden" name="action" value="add">
        Nom fanfaron: <input type="text" name="nomFanfaron" required><br>
        Email: <input type="email" name="email" required><br>
        Mot de passe: <input type="password" name="mdp" required><br>
        Prenom: <input type="text" name="prenom" required><br>
        Nom: <input type="text" name="nom" required><br>
        Genre: <select name="genre"><option value="homme">homme</option><option value="femme">femme</option><option value="autre">autre</option></select><br>
        Contraintes: <select name="contraintesAlim"><option value="aucune">aucune</option><option value="vegetarien">vegetarien</option><option value="vegan">vegan</option><option value="sans porc">sans porc</option></select><br>
        Role: <select name="role"><option value="utilisateur">utilisateur</option><option value="admin">admin</option></select><br>
        <button type="submit">Ajouter</button>
    </form>

    <h2>Modifier</h2>
    <form method="post" action="Admin">
        <input type="hidden" name="action" value="update">
        Ancien nom fanfaron: <input type="text" name="ancienNomFanfaron" value="<%= editing != null ? editing.getNomFanfaron() : "" %>" required readonly><br>
        Nouveau nom fanfaron: <input type="text" name="nomFanfaron" value="<%= editing != null ? editing.getNomFanfaron() : "" %>" required><br>
        Email: <input type="email" name="email" value="<%= editing != null ? editing.getEmail() : "" %>" required><br>
        Nouveau mot de passe (laisser vide pour ne pas changer): <input type="password" name="mdp"><br>
        Prenom: <input type="text" name="prenom" value="<%= editing != null ? editing.getPrenom() : "" %>" required><br>
        Nom: <input type="text" name="nom" value="<%= editing != null ? editing.getNom() : "" %>" required><br>
        Genre:
        <select name="genre">
            <option value="homme" <%= editing != null && "homme".equals(editing.getGenre()) ? "selected" : "" %>>homme</option>
            <option value="femme" <%= editing != null && "femme".equals(editing.getGenre()) ? "selected" : "" %>>femme</option>
            <option value="autre" <%= editing != null && "autre".equals(editing.getGenre()) ? "selected" : "" %>>autre</option>
        </select><br>
        Contraintes:
        <select name="contraintesAlim">
            <option value="aucune" <%= editing != null && "aucune".equals(editing.getContraintesAlim()) ? "selected" : "" %>>aucune</option>
            <option value="vegetarien" <%= editing != null && "vegetarien".equals(editing.getContraintesAlim()) ? "selected" : "" %>>vegetarien</option>
            <option value="vegan" <%= editing != null && "vegan".equals(editing.getContraintesAlim()) ? "selected" : "" %>>vegan</option>
            <option value="sans porc" <%= editing != null && "sans porc".equals(editing.getContraintesAlim()) ? "selected" : "" %>>sans porc</option>
        </select><br>
        Role:
        <select name="role">
            <option value="utilisateur" <%= editing != null && "utilisateur".equals(editing.getRole()) ? "selected" : "" %>>utilisateur</option>
            <option value="admin" <%= editing != null && "admin".equals(editing.getRole()) ? "selected" : "" %>>admin</option>
        </select><br>
        <button type="submit">Modifier</button>
    </form>

    <p><a href="menu.jsp">Retour menu</a></p>
</body>
</html>

