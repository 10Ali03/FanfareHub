========================
MLD - FanfareHub 
========================

FANFARON(
  id_fanfaron PK,
  nom_fanfaron UNIQUE,
  email UNIQUE,
  mot_de_passe,
  prenom,
  nom,
  genre,
  contraintes_alimentaires,
  date_creation,
  derniere_connexion
  role
)

PUPITRE(
  id_instrument PK,
  nom UNIQUE
)

GROUPE(
  id_groupe PK,
  nom UNIQUE
)

EVENEMENT(
  id_evenement PK,
  nom,
  horodatage,
  duree,
  lieu,
  description
)

----------------------------------
ASSOCIATIONS
----------------------------------

IMPLIQUER(
  id_fanfaron PK, FK → FANFARON(id_fanfaron),
  id_groupe PK, FK → GROUPE(id_groupe)
)

APPARTENIR(
  id_fanfaron PK, FK → FANFARON(id_fanfaron),
  id_instrument PK, FK → PUPITRE(id_instrument)
)

PROPOSER(
  id_evenement PK, FK → EVENEMENT(id_evenement),
  id_fanfaron FK → FANFARON(id_fanfaron)
)

PARTICIPER(
  id_fanfaron PK, FK → FANFARON(id_fanfaron),
  id_evenement PK, FK → EVENEMENT(id_evenement),
  id_instrument PK, FK → PUPITRE(id_instrument),
  statut
)