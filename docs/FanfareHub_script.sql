-- =========================
-- RESET
-- =========================
DROP TABLE IF EXISTS participer CASCADE;
DROP TABLE IF EXISTS proposer CASCADE;
DROP TABLE IF EXISTS impliquer CASCADE;
DROP TABLE IF EXISTS appartenir CASCADE;
DROP TABLE IF EXISTS evenement CASCADE;
DROP TABLE IF EXISTS groupe CASCADE;
DROP TABLE IF EXISTS pupitre CASCADE;
DROP TABLE IF EXISTS fanfaron CASCADE;

-- =========================
-- EXTENSION POUR HASH SHA-256
-- =========================
CREATE EXTENSION IF NOT EXISTS pgcrypto;

-- =========================
-- TABLES DE REFERENCE
-- =========================
CREATE TABLE pupitre (
    id SERIAL PRIMARY KEY,
    nom VARCHAR(50) NOT NULL UNIQUE
);

CREATE TABLE groupe (
    id SERIAL PRIMARY KEY,
    nom VARCHAR(100) NOT NULL UNIQUE
);

-- =========================
-- FANFARON
-- =========================
CREATE TABLE fanfaron (
    id SERIAL PRIMARY KEY,
    nom_fanfaron VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(100) NOT NULL UNIQUE,
    mot_de_passe BYTEA NOT NULL,
    prenom VARCHAR(50) NOT NULL,
    nom VARCHAR(50) NOT NULL,
    genre VARCHAR(10) CHECK (genre IN ('homme', 'femme', 'autre')),
    contraintes_alimentaires VARCHAR(20)
        CHECK (contraintes_alimentaires IN ('aucune', 'vegetarien', 'vegan', 'sans porc')),
    role VARCHAR(20) DEFAULT 'utilisateur' CHECK (role IN ('utilisateur', 'admin')),
    date_creation TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    derniere_connexion TIMESTAMP NULL
);

-- =========================
-- ASSOCIATION FANFARON <-> PUPITRE
-- =========================
CREATE TABLE appartenir (
    id_fanfaron INTEGER NOT NULL,
    id_instrument INTEGER NOT NULL,
    PRIMARY KEY (id_fanfaron, id_instrument),
    CONSTRAINT fk_app_fanfaron
        FOREIGN KEY (id_fanfaron)
        REFERENCES fanfaron(id)
        ON DELETE CASCADE,
    CONSTRAINT fk_app_pupitre
        FOREIGN KEY (id_instrument)
        REFERENCES pupitre(id)
        ON DELETE CASCADE
);

-- =========================
-- ASSOCIATION FANFARON <-> GROUPE
-- =========================
CREATE TABLE impliquer (
    id_fanfaron INTEGER NOT NULL,
    id_groupe INTEGER NOT NULL,
    PRIMARY KEY (id_fanfaron, id_groupe),
    CONSTRAINT fk_imp_fanfaron
        FOREIGN KEY (id_fanfaron)
        REFERENCES fanfaron(id)
        ON DELETE CASCADE,
    CONSTRAINT fk_imp_groupe
        FOREIGN KEY (id_groupe)
        REFERENCES groupe(id)
        ON DELETE CASCADE
);

-- =========================
-- EVENEMENT
-- =========================
CREATE TABLE evenement (
    id SERIAL PRIMARY KEY,
    nom VARCHAR(100) NOT NULL,
    horodatage TIMESTAMP NOT NULL,
    duree INTEGER NOT NULL CHECK (duree > 0),
    lieu VARCHAR(150) NOT NULL,
    description TEXT
);

-- =========================
-- ASSOCIATION PROPOSER (1,1 côté événement)
-- =========================
CREATE TABLE proposer (
    id_evenement INTEGER PRIMARY KEY,
    id_fanfaron INTEGER NOT NULL,
    CONSTRAINT fk_prop_evenement
        FOREIGN KEY (id_evenement)
        REFERENCES evenement(id)
        ON DELETE CASCADE,
    CONSTRAINT fk_prop_fanfaron
        FOREIGN KEY (id_fanfaron)
        REFERENCES fanfaron(id)
        ON DELETE CASCADE
);

-- =========================
-- ASSOCIATION PARTICIPER (ternaire)
-- =========================
CREATE TABLE participer (
    id_fanfaron INTEGER NOT NULL,
    id_evenement INTEGER NOT NULL,
    id_instrument INTEGER NOT NULL,
    statut VARCHAR(10) NOT NULL
        CHECK (statut IN ('present', 'absent', 'incertain')),
    PRIMARY KEY (id_fanfaron, id_evenement, id_instrument),
    CONSTRAINT fk_part_fanfaron
        FOREIGN KEY (id_fanfaron)
        REFERENCES fanfaron(id)
        ON DELETE CASCADE,
    CONSTRAINT fk_part_evenement
        FOREIGN KEY (id_evenement)
        REFERENCES evenement(id)
        ON DELETE CASCADE,
    CONSTRAINT fk_part_pupitre
        FOREIGN KEY (id_instrument)
        REFERENCES pupitre(id)
        ON DELETE RESTRICT
);

-- =========================
-- DONNEES INITIALES
-- =========================
INSERT INTO pupitre (nom) VALUES
('clarinette'),
('saxophone alto'),
('euphonium'),
('percussion'),
('basse'),
('trompette'),
('saxophone baryton'),
('trombone');

INSERT INTO groupe (nom) VALUES
('commission prestation'),
('commission artistique'),
('commission logistique'),
('commission communication interne');