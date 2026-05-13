<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="metier.Fanfaron" %>
<%@ page import="metier.Pupitre" %>
<%@ page import="metier.Groupe" %>
<%@ page import="metier.Evenement" %>
<%! 
    // Fonction d'echappement HTML anti-XSS pour toutes les sorties dynamiques.
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
    // Ces listes sont alimentees par AdminServlet (request.setAttribute(...)).
    List<Fanfaron> fanfarons = (List<Fanfaron>) request.getAttribute("fanfarons");
    List<Pupitre> pupitres = (List<Pupitre>) request.getAttribute("pupitres");
    List<Groupe> groupes = (List<Groupe>) request.getAttribute("groupes");
    List<Evenement> evenements = (List<Evenement>) request.getAttribute("evenements");
    // Message applicatif (succès/erreur fonctionnelle) venant du controleur.
    String message = (String) request.getAttribute("message");
    // Objets "editing*" uniquement presents apres clic sur le bouton Modifier.
    Fanfaron editing = (Fanfaron) request.getAttribute("editingFanfaron");
    Pupitre editingPupitre = (Pupitre) request.getAttribute("editingPupitre");
    Groupe editingGroupe = (Groupe) request.getAttribute("editingGroupe");
    Evenement editingEvenement = (Evenement) request.getAttribute("editingEvenement");
    // Parametre technique pour revenir visuellement a la bonne section apres POST/GET.
    String sectionTarget = request.getParameter("sectionTarget");
    // Whitelist defensive: empêche toute valeur arbitraire injectée dans le script JS.
    if (!"fanfarons".equals(sectionTarget) && !"pupitres".equals(sectionTarget) && !"groupes".equals(sectionTarget) && !"evenements".equals(sectionTarget)) {
        sectionTarget = "";
    }
%>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <title>Administration</title>
</head>
<body>
    <nav>
        <a href="Profil">Menu</a> |
        <a href="Evenement?action=list">Evenements</a> |
        <a href="Choix?action=choixGroupesPupitres">Groupes/Pupitres</a> |
        <a href="Auth?action=logout">Deconnexion</a>
    </nav>
    <h1>Administration des fanfarons</h1>
    <% if (message != null) { %><p><%= h(message) %></p><% } %>

    <h2 id="fanfarons">Liste</h2>
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
            <td><%= h(f.getNomFanfaron()) %></td>
            <td><%= h(f.getEmail()) %></td>
            <td><%= h(f.getPrenom()) %></td>
            <td><%= h(f.getNom()) %></td>
            <td><%= h(f.getGenre()) %></td>
            <td><%= h(f.getContraintesAlim()) %></td>
            <td><%= h(f.getRole()) %></td>
            <td>
                <form method="get" action="Admin" style="display:inline;">
                    <!-- action=edit: demande au controleur de charger l'objet a modifier -->
                    <input type="hidden" name="action" value="edit">
                    <!-- identifiant fonctionnel de la ligne selectionnee -->
                    <input type="hidden" name="nomFanfaron" value="<%= h(f.getNomFanfaron()) %>">
                    <!-- sectionTarget: permet le scroll auto au bon endroit -->
                    <input type="hidden" name="sectionTarget" value="fanfarons">
                    <button type="submit">Modifier</button>
                </form>
                <form method="post" action="Admin" style="display:inline;" onsubmit="return confirm('Supprimer <%= h(f.getNomFanfaron()) %> ?');">
                    <!-- POST delete: suppression effectuee par le controleur -->
                    <input type="hidden" name="action" value="delete">
                    <input type="hidden" name="nomFanfaron" value="<%= h(f.getNomFanfaron()) %>">
                    <input type="hidden" name="sectionTarget" value="fanfarons">
                    <button type="submit">Supprimer</button>
                </form>
            </td>
        </tr>
        <% } %>
    </table>

    <h2>Ajouter</h2>
    <form method="post" action="Admin">
        <!-- action=add: route serveur pour creer un compte -->
        <input type="hidden" name="action" value="add">
        <input type="hidden" name="sectionTarget" value="fanfarons">
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

    <% if (editing != null) { %>
    <h2>Modifier</h2>
    <form method="post" action="Admin">
        <!-- action=update: route serveur pour modifier un compte -->
        <input type="hidden" name="action" value="update">
        <input type="hidden" name="sectionTarget" value="fanfarons">
        Ancien nom fanfaron: <input type="text" name="ancienNomFanfaron" value="<%= editing != null ? h(editing.getNomFanfaron()) : "" %>" required readonly><br>
        Nouveau nom fanfaron: <input type="text" name="nomFanfaron" value="<%= editing != null ? h(editing.getNomFanfaron()) : "" %>" required><br>
        Email: <input type="email" name="email" value="<%= editing != null ? h(editing.getEmail()) : "" %>" required><br>
        Nouveau mot de passe (laisser vide pour ne pas changer): <input type="password" name="mdp"><br>
        Prenom: <input type="text" name="prenom" value="<%= editing != null ? h(editing.getPrenom()) : "" %>" required><br>
        Nom: <input type="text" name="nom" value="<%= editing != null ? h(editing.getNom()) : "" %>" required><br>
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
    <% } %>

    <hr>
    <h1 id="pupitres">Administration des pupitres</h1>
    <h2>Liste des pupitres</h2>
    <table border="1" cellpadding="6">
        <tr>
            <th>ID</th>
            <th>Nom</th>
            <th>Actions</th>
        </tr>
        <% if (pupitres != null) for (Pupitre p : pupitres) { %>
        <tr>
            <td><%= p.getId() %></td>
            <td><%= h(p.getNom()) %></td>
            <td>
                <form method="get" action="Admin" style="display:inline;">
                    <!-- editPupitre: precharge le pupitre dans editingPupitre -->
                    <input type="hidden" name="action" value="editPupitre">
                    <input type="hidden" name="id" value="<%= p.getId() %>">
                    <input type="hidden" name="sectionTarget" value="pupitres">
                    <button type="submit">Modifier</button>
                </form>
                <form method="post" action="Admin" style="display:inline;" onsubmit="return confirm('Supprimer le pupitre <%= h(p.getNom()) %> ?');">
                    <!-- deletePupitre: suppression + refresh liste -->
                    <input type="hidden" name="action" value="deletePupitre">
                    <input type="hidden" name="idPupitre" value="<%= p.getId() %>">
                    <input type="hidden" name="sectionTarget" value="pupitres">
                    <button type="submit">Supprimer</button>
                </form>
            </td>
        </tr>
        <% } %>
    </table>

    <h2>Ajouter un pupitre</h2>
    <form method="post" action="Admin">
        <!-- addPupitre: creation pupitre via controleur -->
        <input type="hidden" name="action" value="addPupitre">
        <input type="hidden" name="sectionTarget" value="pupitres">
        Nom du pupitre:
        <input type="text" name="nomPupitre" list="listePupitres" required>
        <datalist id="listePupitres">
            <% if (pupitres != null) for (Pupitre p : pupitres) { %>
                <option value="<%= h(p.getNom()) %>"></option>
            <% } %>
        </datalist>
        <button type="submit">Ajouter</button>
    </form>

    <% if (editingPupitre != null) { %>
    <h2>Modifier un pupitre</h2>
    <form method="post" action="Admin">
        <!-- updatePupitre: modification pupitre existant -->
        <input type="hidden" name="action" value="updatePupitre">
        <input type="hidden" name="sectionTarget" value="pupitres">
        ID: <input type="number" name="idPupitre" value="<%= editingPupitre != null ? editingPupitre.getId() : "" %>" required readonly><br>
        Nom: <input type="text" name="nomPupitre" value="<%= editingPupitre != null ? h(editingPupitre.getNom()) : "" %>" required><br>
        <button type="submit">Modifier</button>
    </form>
    <% } %>

    <hr>
    <h1 id="groupes">Administration des groupes</h1>
    <h2>Liste des groupes</h2>
    <table border="1" cellpadding="6">
        <tr>
            <th>ID</th>
            <th>Nom</th>
            <th>Actions</th>
        </tr>
        <% if (groupes != null) for (Groupe g : groupes) { %>
        <tr>
            <td><%= g.getId() %></td>
            <td><%= h(g.getNom()) %></td>
            <td>
                <form method="get" action="Admin" style="display:inline;">
                    <!-- editGroupe: precharge le groupe dans editingGroupe -->
                    <input type="hidden" name="action" value="editGroupe">
                    <input type="hidden" name="id" value="<%= g.getId() %>">
                    <input type="hidden" name="sectionTarget" value="groupes">
                    <button type="submit">Modifier</button>
                </form>
                <form method="post" action="Admin" style="display:inline;" onsubmit="return confirm('Supprimer le groupe <%= h(g.getNom()) %> ?');">
                    <!-- deleteGroupe: suppression groupe -->
                    <input type="hidden" name="action" value="deleteGroupe">
                    <input type="hidden" name="idGroupe" value="<%= g.getId() %>">
                    <input type="hidden" name="sectionTarget" value="groupes">
                    <button type="submit">Supprimer</button>
                </form>
            </td>
        </tr>
        <% } %>
    </table>

    <h2>Ajouter un groupe</h2>
    <form method="post" action="Admin">
        <!-- addGroupe: creation groupe -->
        <input type="hidden" name="action" value="addGroupe">
        <input type="hidden" name="sectionTarget" value="groupes">
        Nom du groupe:
        <input type="text" name="nomGroupe" list="listeGroupes" required>
        <datalist id="listeGroupes">
            <% if (groupes != null) for (Groupe g : groupes) { %>
                <option value="<%= h(g.getNom()) %>"></option>
            <% } %>
        </datalist>
        <button type="submit">Ajouter</button>
    </form>

    <% if (editingGroupe != null) { %>
    <h2>Modifier un groupe</h2>
    <form method="post" action="Admin">
        <!-- updateGroupe: modification groupe -->
        <input type="hidden" name="action" value="updateGroupe">
        <input type="hidden" name="sectionTarget" value="groupes">
        ID: <input type="number" name="idGroupe" value="<%= editingGroupe != null ? editingGroupe.getId() : "" %>" required readonly><br>
        Nom: <input type="text" name="nomGroupe" value="<%= editingGroupe != null ? h(editingGroupe.getNom()) : "" %>" required><br>
        <button type="submit">Modifier</button>
    </form>
    <% } %>

    <hr>
    <h1 id="evenements">Administration des evenements</h1>
    <h2>Liste des evenements</h2>
    <table border="1" cellpadding="6">
        <tr>
            <th>ID</th>
            <th>Nom</th>
            <th>Date</th>
            <th>Duree</th>
            <th>Lieu</th>
            <th>Description</th>
            <th>Actions</th>
        </tr>
        <% if (evenements != null) for (Evenement e : evenements) { %>
        <tr>
            <td><%= e.getId() %></td>
            <td><%= h(e.getNom()) %></td>
            <td><%= e.getHorodatage() %></td>
            <td><%= e.getDuree() %></td>
            <td><%= h(e.getLieu()) %></td>
            <td><%= e.getDescription() == null ? "" : h(e.getDescription()) %></td>
            <td>
                <form method="get" action="Admin" style="display:inline;">
                    <!-- editEvenement: charge editingEvenement pour afficher le formulaire -->
                    <input type="hidden" name="action" value="editEvenement">
                    <input type="hidden" name="id" value="<%= e.getId() %>">
                    <input type="hidden" name="sectionTarget" value="evenements">
                    <button type="submit">Modifier</button>
                </form>
                <form method="post" action="Admin" style="display:inline;" onsubmit="return confirm('Supprimer cet evenement ?');">
                    <!-- deleteEvenement: suppression evenement -->
                    <input type="hidden" name="action" value="deleteEvenement">
                    <input type="hidden" name="idEvenement" value="<%= e.getId() %>">
                    <input type="hidden" name="sectionTarget" value="evenements">
                    <button type="submit">Supprimer</button>
                </form>
            </td>
        </tr>
        <% } %>
    </table>

    <h2>Ajouter un evenement</h2>
    <form method="post" action="Admin">
        <!-- addEvenement: creation evenement depuis la section admin -->
        <input type="hidden" name="action" value="addEvenement">
        <input type="hidden" name="sectionTarget" value="evenements">
        Nom: <input type="text" name="nomEvenement" required><br>
        Horodatage: <input type="datetime-local" name="horodatageEvenement" required><br>
        Duree (minutes): <input type="number" name="dureeEvenement" min="1" required><br>
        Lieu: <input type="text" name="lieuEvenement" required><br>
        Description: <textarea name="descriptionEvenement"></textarea><br>
        <button type="submit">Ajouter</button>
    </form>

    <% if (editingEvenement != null) { %>
    <h2>Modifier un evenement</h2>
    <form method="post" action="Admin">
        <!-- updateEvenement: modification evenement -->
        <input type="hidden" name="action" value="updateEvenement">
        <input type="hidden" name="sectionTarget" value="evenements">
        ID: <input type="number" name="idEvenement" value="<%= editingEvenement != null ? editingEvenement.getId() : "" %>" required readonly><br>
        Nom: <input type="text" name="nomEvenement" value="<%= editingEvenement != null ? h(editingEvenement.getNom()) : "" %>" required><br>
        Horodatage: <input type="datetime-local" name="horodatageEvenement" value="<%= editingEvenement != null ? editingEvenement.getHorodatage().toString().replace(' ', 'T').substring(0,16) : "" %>" required><br>
        Duree (minutes): <input type="number" name="dureeEvenement" min="1" value="<%= editingEvenement != null ? editingEvenement.getDuree() : "" %>" required><br>
        Lieu: <input type="text" name="lieuEvenement" value="<%= editingEvenement != null ? h(editingEvenement.getLieu()) : "" %>" required><br>
        Description: <textarea name="descriptionEvenement"><%= editingEvenement != null && editingEvenement.getDescription() != null ? h(editingEvenement.getDescription()) : "" %></textarea><br>
        <button type="submit">Modifier</button>
    </form>
    <% } %>

    <p><a href="Profil">Retour menu</a></p>
    <script>
        (function () {
            // sectionTarget vient du serveur (valeur whitelistee plus haut).
            var target = "<%= sectionTarget %>";
            // Si vide, aucun scroll necessaire.
            if (!target) return;
            // Recherche de la section cible par son id HTML.
            var el = document.getElementById(target);
            if (el) {
                // Scroll doux pour garder le contexte utilisateur apres submit.
                el.scrollIntoView({ behavior: "smooth", block: "start" });
            }
        })();
    </script>
</body>
</html>

