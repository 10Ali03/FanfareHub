-- =========================
-- DONNEES FICTIVES FANFAREHUB
-- =========================

-- FANFARONS
INSERT INTO fanfaron
(nom_fanfaron, email, mot_de_passe, prenom, nom, genre, contraintes_alimentaires, role)
VALUES
('ali_trompette', 'ali@example.com', 'azerty123', 'Ali', 'El Bouazzaoui', 'homme', 'sans porc', 'admin'),
('lea_clari', 'lea@example.com', 'azerty123', 'Léa', 'Martin', 'femme', 'vegetarien', 'utilisateur'),
('mehdi_saxo', 'mehdi@example.com', 'azerty123', 'Mehdi', 'Benali', 'homme', 'aucune', 'utilisateur'),
('emma_percu', 'emma@example.com', 'azerty123', 'Emma', 'Durand', 'femme', 'vegan', 'utilisateur'),
('luc_basse', 'luc@example.com', 'azerty123', 'Luc', 'Petit', 'homme', 'aucune', 'utilisateur'),
('nina_trombone', 'nina@example.com', 'azerty123', 'Nina', 'Robert', 'femme', 'sans porc', 'utilisateur')
ON CONFLICT (nom_fanfaron) DO NOTHING;

-- APPARTENANCE AUX PUPITRES
INSERT INTO appartenir (id_fanfaron, id_instrument)
SELECT f.id, p.id
FROM fanfaron f, pupitre p
WHERE f.nom_fanfaron = 'ali_trompette' AND p.nom = 'trompette'
ON CONFLICT DO NOTHING;

INSERT INTO appartenir (id_fanfaron, id_instrument)
SELECT f.id, p.id
FROM fanfaron f, pupitre p
WHERE f.nom_fanfaron = 'lea_clari' AND p.nom = 'clarinette'
ON CONFLICT DO NOTHING;

INSERT INTO appartenir (id_fanfaron, id_instrument)
SELECT f.id, p.id
FROM fanfaron f, pupitre p
WHERE f.nom_fanfaron = 'mehdi_saxo' AND p.nom = 'saxophone alto'
ON CONFLICT DO NOTHING;

INSERT INTO appartenir (id_fanfaron, id_instrument)
SELECT f.id, p.id
FROM fanfaron f, pupitre p
WHERE f.nom_fanfaron = 'emma_percu' AND p.nom = 'percussion'
ON CONFLICT DO NOTHING;

INSERT INTO appartenir (id_fanfaron, id_instrument)
SELECT f.id, p.id
FROM fanfaron f, pupitre p
WHERE f.nom_fanfaron = 'luc_basse' AND p.nom = 'basse'
ON CONFLICT DO NOTHING;

INSERT INTO appartenir (id_fanfaron, id_instrument)
SELECT f.id, p.id
FROM fanfaron f, pupitre p
WHERE f.nom_fanfaron = 'nina_trombone' AND p.nom = 'trombone'
ON CONFLICT DO NOTHING;

-- IMPLICATION DANS LES GROUPES
INSERT INTO impliquer (id_fanfaron, id_groupe)
SELECT f.id, g.id
FROM fanfaron f, groupe g
WHERE f.nom_fanfaron = 'ali_trompette' AND g.nom = 'commission prestation'
ON CONFLICT DO NOTHING;

INSERT INTO impliquer (id_fanfaron, id_groupe)
SELECT f.id, g.id
FROM fanfaron f, groupe g
WHERE f.nom_fanfaron = 'lea_clari' AND g.nom = 'commission artistique'
ON CONFLICT DO NOTHING;

INSERT INTO impliquer (id_fanfaron, id_groupe)
SELECT f.id, g.id
FROM fanfaron f, groupe g
WHERE f.nom_fanfaron = 'mehdi_saxo' AND g.nom = 'commission prestation'
ON CONFLICT DO NOTHING;

INSERT INTO impliquer (id_fanfaron, id_groupe)
SELECT f.id, g.id
FROM fanfaron f, groupe g
WHERE f.nom_fanfaron = 'emma_percu' AND g.nom = 'commission logistique'
ON CONFLICT DO NOTHING;

INSERT INTO impliquer (id_fanfaron, id_groupe)
SELECT f.id, g.id
FROM fanfaron f, groupe g
WHERE f.nom_fanfaron = 'nina_trombone' AND g.nom = 'commission communication interne'
ON CONFLICT DO NOTHING;

-- EVENEMENTS
INSERT INTO evenement
(nom, horodatage, duree, lieu, description, id_fanfaron)
SELECT 'Répétition générale', '2026-05-15 18:30:00', 120, 'Salle Polytech Lyon',
       'Répétition complète avant prestation.',
       f.id
FROM fanfaron f
WHERE f.nom_fanfaron = 'ali_trompette';

INSERT INTO evenement
(nom, horodatage, duree, lieu, description, id_fanfaron)
SELECT 'Atelier rythme', '2026-05-20 17:00:00', 90, 'Local musique',
       'Atelier autour du rythme et de la mise en place.',
       f.id
FROM fanfaron f
WHERE f.nom_fanfaron = 'mehdi_saxo';

INSERT INTO evenement
(nom, horodatage, duree, lieu, description, id_fanfaron)
SELECT 'Prestation campus', '2026-06-02 19:00:00', 60, 'Campus LyonTech',
       'Prestation musicale ouverte aux étudiants.',
       f.id
FROM fanfaron f
WHERE f.nom_fanfaron = 'ali_trompette';

-- PARTICIPATIONS
INSERT INTO participer (id_fanfaron, id_evenement, id_instrument, statut)
SELECT f.id, e.id, p.id, 'present'
FROM fanfaron f, evenement e, pupitre p
WHERE f.nom_fanfaron = 'ali_trompette'
AND e.nom = 'Répétition générale'
AND p.nom = 'trompette'
ON CONFLICT DO NOTHING;

INSERT INTO participer (id_fanfaron, id_evenement, id_instrument, statut)
SELECT f.id, e.id, p.id, 'present'
FROM fanfaron f, evenement e, pupitre p
WHERE f.nom_fanfaron = 'lea_clari'
AND e.nom = 'Répétition générale'
AND p.nom = 'clarinette'
ON CONFLICT DO NOTHING;

INSERT INTO participer (id_fanfaron, id_evenement, id_instrument, statut)
SELECT f.id, e.id, p.id, 'incertain'
FROM fanfaron f, evenement e, pupitre p
WHERE f.nom_fanfaron = 'mehdi_saxo'
AND e.nom = 'Répétition générale'
AND p.nom = 'saxophone alto'
ON CONFLICT DO NOTHING;

INSERT INTO participer (id_fanfaron, id_evenement, id_instrument, statut)
SELECT f.id, e.id, p.id, 'absent'
FROM fanfaron f, evenement e, pupitre p
WHERE f.nom_fanfaron = 'emma_percu'
AND e.nom = 'Répétition générale'
AND p.nom = 'percussion'
ON CONFLICT DO NOTHING;

INSERT INTO participer (id_fanfaron, id_evenement, id_instrument, statut)
SELECT f.id, e.id, p.id, 'present'
FROM fanfaron f, evenement e, pupitre p
WHERE f.nom_fanfaron = 'luc_basse'
AND e.nom = 'Prestation campus'
AND p.nom = 'basse'
ON CONFLICT DO NOTHING;

INSERT INTO participer (id_fanfaron, id_evenement, id_instrument, statut)
SELECT f.id, e.id, p.id, 'incertain'
FROM fanfaron f, evenement e, pupitre p
WHERE f.nom_fanfaron = 'nina_trombone'
AND e.nom = 'Prestation campus'
AND p.nom = 'trombone'
ON CONFLICT DO NOTHING;