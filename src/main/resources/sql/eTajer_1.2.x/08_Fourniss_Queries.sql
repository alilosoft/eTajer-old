--~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~--
--================================ CREATION ==================================--
--~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~--
-- 19/12/2014
DROP TABLE FOURNISSEUR;

CREATE TABLE FOURNISSEUR(   ID INT NOT NULL GENERATED ALWAYS AS IDENTITY,
                            CODE VARCHAR(5) NOT NULL UNIQUE,
                            NOM VARCHAR(40) NOT NULL, 
                            ADR VARCHAR(50) DEFAULT NULL,
                            TEL VARCHAR(20) DEFAULT NULL,
                            MOBILE VARCHAR(21) DEFAULT NULL,
                            EMAIL VARCHAR(50) DEFAULT NULL,
                            NUM_RC VARCHAR(15) DEFAULT NULL,
                            NUM_FISC VARCHAR(20) DEFAULT NULL,
                            NUM_ART  VARCHAR(15) DEFAULT NULL,
                            DETTE DECIMAL(12,2) NOT NULL DEFAULT 0,
                            CONSTRAINT FOURN_PK PRIMARY KEY (ID));
--~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~--
--============================== ALTERATION ==================================--
--~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~--
--12/05/2015
ALTER TABLE FOURNISSEUR ALTER COLUMN TEL SET DATA TYPE VARCHAR(20);
ALTER TABLE FOURNISSEUR ALTER COLUMN MOBILE SET DATA TYPE VARCHAR(21);
--~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~--
--================================ VIEWS =====================================--
--~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~--
-- 27/09/2013
DROP VIEW V_FOURNISSEUR;

CREATE VIEW V_FOURNISSEUR AS
SELECT FR.ID, CODE AS "Code", NOM AS "Fournisseur", ADR AS "Adresse", TEL AS "N° Tél/Fax", MOBILE AS "Tél.Portable", EMAIL AS "E-Mail", DETTE AS "Crédit(DA)"
FROM FOURNISSEUR FR;
--~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~--
--=============================== QUERIES ====================================--
--~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~--
SELECT * FROM V_FOURNISSEUR;
SELECT * FROM FOURNISSEUR WHERE ID = ?;
INSERT INTO FOURNISS (CODE, NOM, ADR, TEL, MOBILE, EMAIL, NUM_RC, NUM_FISC, NUM_ART) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?);
UPDATE FOURNISSEUR SET CODE = ?, NOM = ?, ADR = ?, TEL = ?, MOBILE = ?, EMAIL = ?, NUM_RC = ?, NUM_FISC = ?, NUM_ART = ? WHERE ID = ?;
DELETE FROM FOURNISSEUR WHERE ID = ?;
--~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~--