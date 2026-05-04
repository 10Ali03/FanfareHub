# 🏗️ Architecture Projet FanfareHub (DAO + MVC)

## 📌 Principe global

Le projet suit l’architecture **MVC + DAO** :

* **Model (metier)** → objets Java (POJO)
* **View (JSP)** → pages web (HTML + JSP)
* **Controller (Servlet)** → logique + gestion des requêtes
* **DAO** → accès base de données (JDBC)

---

## 📁 Structure complète du projet 
```text
FanfareHub/
│
├── META-INF/
│   └── context.xml
│
├── WEB-INF/
│   ├── classes/
│   │   ├── dao/
│   │   │   ├── ConnexionBD.java
│   │   │   ├── FanfaronDAO.java
│   │   │   ├── PupitreDAO.java
│   │   │   ├── GroupeDAO.java
│   │   │   ├── EvenementDAO.java
│   │   │   ├── ParticipationDAO.java
│   │   │   ├── AppartenirDAO.java
│   │   │   ├── ImpliquerDAO.java
│   │   │   └── ProposerDAO.java
│   │   │
│   │   ├── metier/
│   │   │   ├── Fanfaron.java
│   │   │   ├── Pupitre.java
│   │   │   ├── Groupe.java
│   │   │   ├── Evenement.java
│   │   │   └── Participation.java
│   │   │
│   │   └── servlet/
│   │       ├── InscriptionServlet.java
│   │       ├── ConnexionServlet.java
│   │       ├── DeconnexionServlet.java
│   │       ├── ProfilServlet.java
│   │       ├── ChoixGroupesPupitresServlet.java
│   │       ├── EvenementServlet.java
│   │       ├── ParticipationServlet.java
│   │       ├── AdminUtilisateurServlet.java
│   │       └── GestionEvenementServlet.java
│   │
│   └── lib/
│       └── postgresql-42.7.4.jar
│
├── index.jsp
├── inscription.jsp
├── connexion.jsp
├── accueil.jsp
├── profil.jsp
├── choixGroupesPupitres.jsp
├── listeEvenements.jsp
├── detailEvenement.jsp
├── adminUtilisateurs.jsp
├── formulaireUtilisateur.jsp
├── gestionEvenements.jsp
├── formulaireEvenement.jsp
├── erreur.jsp
│
└── docs/
    ├── creation_fanfarehub.sql
    ├── mld.txt
    └── architecture.md
```

---

## 🧩 Rôle de chaque couche

### 🔹 DAO

* Contient toutes les requêtes SQL
* Méthodes CRUD :

  * `insert()`
  * `find()`
  * `findAll()`
  * `update()`
  * `delete()`

---

### 🔹 METIER (Model)

* Classes simples (POJO)
* Correspondent aux tables SQL

Exemple :

```java
public class Fanfaron {
    private int id;
    private String nomFanfaron;
    private String email;
}
```

---

### 🔹 SERVLET (Controller)

* Reçoit les requêtes HTTP
* Récupère les paramètres (`request.getParameter`)
* Appelle le DAO
* Redirige vers une JSP

Exemple :

```java
String nom = request.getParameter("nom");
fanfaronDAO.insert(fanfaron);
request.getRequestDispatcher("accueil.jsp").forward(request, response);
```

---

### 🔹 JSP (View)

* Affiche les données
* Ne contient PAS de logique métier
* Utilise les attributs :

```jsp
<%= request.getAttribute("fanfaron") %>
```

---

## 🔐 Gestion de la session

Après connexion :

```java
HttpSession session = request.getSession();
session.setAttribute("user", fanfaron);
```

Protection des pages :

```java
if (session.getAttribute("user") == null) {
    response.sendRedirect("connexion.jsp");
}
```

---

## 🚀 Ordre de développement conseillé

```text
1. ConnexionBD.java
2. Fanfaron.java
3. FanfaronDAO.java
4. inscription.jsp
5. InscriptionServlet.java
6. connexion.jsp
7. ConnexionServlet.java
8. accueil.jsp
```

---

## 🎯 Objectif du TP

* Utiliser DAO pour accéder à la base
* Utiliser MVC pour structurer le projet
* Gérer :

  * inscription
  * connexion
  * groupes/pupitres
  * événements
  * participation

---

## ⚠️ Règles importantes

* Toujours utiliser **PreparedStatement**
* Ne jamais mettre SQL dans les JSP
* Ne jamais mettre HTML dans les DAO
* Toujours passer par les Servlets

---

## ✅ Résumé

```text
JSP → Servlet → DAO → BDD
```

---

## 💡 Astuce

Travaille en parallèle avec ta collègue :

* Toi → DAO + Servlet
* Elle → JSP
* Communication via GitHub (pull / push)

---
