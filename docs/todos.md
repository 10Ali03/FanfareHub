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
├── docs/
├── META-INF/
├── WEB-INF/
│   ├── classes/
│   │   ├── controleur/
│   │   │   ├── InscriptionServlet.java
│   │   │   ├── ConnexionServlet.java
│   │   │   └── DeconnexionServlet.java
│   │   │
│   │   └── dao/
│   │       ├── ConnexionBD.java
│   │       ├── Fanfaron.java
│   │       ├── FanfaronDAO.java
│   │       ├── FanfaronJDBCDAO.java
│   │       └── DAOFactory.java
│   │
│   └── lib/
│       └── postgresql-42.7.4.jar
│
└── vue/
    ├── index.jsp
    ├── inscription.jsp
    ├── connexion.jsp
    └── accueil.jsp

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
## Dernieres modifications (auth, routing, securite, UX admin)

### 1) Routage MVC (plus de navigation directe vers les JSP)
- Ajout de routes GET dans `AuthServlet` :
  - `Auth?action=showLogin` -> affiche `connexion.jsp`
  - `Auth?action=showSignup` -> affiche `inscription.jsp`
- Remplacement des liens directs vers `.jsp` dans les vues par des routes controleur (`Auth`, `Profil`, `Evenement`, `Choix`, `Admin`).
- Ajout d'un point d'entree global `index.html` qui redirige vers `Auth?action=showLogin`.

### 2) Controle d'acces global
- Ajout de `AuthFilter` (filtre `/*`) :
  - redirige vers login si l'utilisateur n'est pas authentifie.
  - bloque l'acces direct aux JSP (oblige passage via controleur).
  - redirige un utilisateur deja connecte vers `Profil` s'il tente de revenir sur `Auth` (hors logout).

### 3) UX admin: formulaires de modification affiches a la demande
- Les formulaires de modification ne sont plus affiches par defaut.
- Ils s'affichent seulement apres clic sur "Modifier" (objet `editing...` present dans la requete).
- Applique a:
  - fanfarons
  - pupitres
  - groupes
  - evenements

### 4) Securite XSS
- Ajout d'une fonction d'echappement HTML `h(...)` dans les JSP principales.
- Echappement des valeurs dynamiques affiches dans:
  - `administration.jsp`
  - `evenements.jsp`
  - `evenementParticipation.jsp`
  - `groupesPupitres.jsp`
  - `connexion.jsp`
  - `inscription.jsp`
- La variable JS de scroll admin (`sectionTarget`) est maintenant whitelistée pour eviter l'injection.

### 5) Securite SQL
- Verification: les DAO utilisent des `PreparedStatement` (pas de concatenation SQL utilisateur).
- Pas de faille SQL injection evidente detectee sur le code audite.

### 6) Inscription / connexion (coherence TP)
- Inscription:
  - confirmation email (`emailConfirm`)
  - confirmation mot de passe
  - verif unicite explicite (`nom_fanfaron`, `email`)
  - `derniere_connexion` initialisee a `null`
- Connexion:
  - mise a jour de `derniere_connexion` apres succes.

### 7) Evenements
- Autorisation commission prestation rendue plus robuste (`LOWER(TRIM(nom))`).
- Creation evenement compatible avec deux schemas:
  - schema avec `proposer`
  - schema avec colonne `evenement.id_fanfaron`
