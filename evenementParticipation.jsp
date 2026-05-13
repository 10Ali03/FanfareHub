<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="metier.Evenement, metier.Pupitre, metier.InscriptionEvenement" %>
<%!
    // Echapement HTML centralise pour eviter les XSS dans les sorties JSP.
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
    // Donnees injectees par EvenementServlet (request.setAttribute).
    Evenement e = (Evenement) request.getAttribute("evenement");
    List<Pupitre> pupitres = (List<Pupitre>) request.getAttribute("pupitres");
    Integer myInstrumentId = (Integer) request.getAttribute("myInstrumentId");
    String myStatut = (String) request.getAttribute("myStatut");
    List<InscriptionEvenement> inscriptions = (List<InscriptionEvenement>) request.getAttribute("inscriptions");
%>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <title>Participation Evenement</title>
</head>
<body>
    <nav>
        <a href="Profil">Menu</a> |
        <a href="Evenement?action=list">Evenements</a> |
        <a href="Choix?action=choixGroupesPupitres">Groupes/Pupitres</a> |
        <a href="Auth?action=logout">Deconnexion</a>
    </nav>
    <p><a href="Evenement?action=list">Retour evenements</a></p>
    <h1><%= h(e.getNom()) %></h1>
    <p><b>Date:</b> <%= e.getHorodatage() %></p>
    <p><b>Duree:</b> <%= e.getDuree() %> min</p>
    <p><b>Lieu:</b> <%= h(e.getLieu()) %></p>
    <p><b>Description:</b> <%= e.getDescription() == null ? "" : h(e.getDescription()) %></p>

    <h2>Mon inscription</h2>
    <form method="post" action="Evenement">
        <!-- Action de persistance interpretee par le controleur EvenementServlet -->
        <input type="hidden" name="action" value="saveParticipation">
        <!-- ID evenement technique pour cibler la bonne ligne en base -->
        <input type="hidden" name="idEvenement" value="<%= e.getId() %>">
        Instrument:
        <select name="idInstrument" required>
            <% for (Pupitre p : pupitres) { %>
            <!-- Option pre-selectionnee si l'utilisateur avait deja choisi un instrument -->
            <option value="<%= p.getId() %>" <%= myInstrumentId != null && myInstrumentId == p.getId() ? "selected" : "" %>>
                <%= h(p.getNom()) %>
            </option>
            <% } %>
        </select><br>
        Statut:
        <select name="statut" required>
            <!-- Statuts autorises (alignes sur la contrainte CHECK SQL) -->
            <option value="present" <%= "present".equals(myStatut) ? "selected" : "" %>>present</option>
            <option value="incertain" <%= "incertain".equals(myStatut) ? "selected" : "" %>>incertain</option>
            <option value="absent" <%= "absent".equals(myStatut) ? "selected" : "" %>>absent</option>
        </select><br>
        <button type="submit">Enregistrer mon choix</button>
    </form>

    <h2>Inscriptions (par instrument puis statut)</h2>
    <table border="1" cellpadding="6">
        <tr>
            <th>Instrument</th>
            <th>Fanfaron</th>
            <th>Statut</th>
        </tr>
        <% if (inscriptions != null) for (InscriptionEvenement ins : inscriptions) { %>
        <tr>
            <td><%= h(ins.getPupitre()) %></td>
            <td><%= h(ins.getNomFanfaron()) %></td>
            <!-- Couleur visuelle du statut pour lecture rapide -->
            <td style="<%= "present".equals(ins.getStatut()) ? "color:green;" : ("absent".equals(ins.getStatut()) ? "color:red;" : "color:orange;") %>">
                <%= h(ins.getStatut()) %>
            </td>
        </tr>
        <% } %>
    </table>
</body>
</html>
