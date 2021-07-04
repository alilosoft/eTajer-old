--~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~--
--================================ CREATION ==================================--
--~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~--
-- Last modification: 19/01/2015.
DROP TABLE CREDIT_FR;

CREATE TABLE CREDIT_FR     (ID INT NOT NULL GENERATED ALWAYS AS IDENTITY,
                            ID_FR INT NOT NULL,
                            ID_ACH INT DEFAULT NULL,
                            "DATE" DATE DEFAULT CURRENT_DATE,
                            HEURE TIME NOT NULL DEFAULT CURRENT_TIME,
                            MONTANT DECIMAL(12,2) NOT NULL DEFAULT 0,-- MAX: 999,999,999
                            COMMENT VARCHAR(200),
                            INITIAL BOOLEAN NOT NULL DEFAULT FALSE,
                            CONSTRAINT CREDITFR_PK PRIMARY KEY (ID),
                            CONSTRAINT CREDIT_FOURNISS_FK FOREIGN KEY (ID_FR) REFERENCES FOURNISSEUR ON DELETE RESTRICT,
                            CONSTRAINT CREDIT_ACH_FK FOREIGN KEY (ID_ACH) REFERENCES ACHAT ON DELETE CASCADE);
--~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~--
--============================== ALTERATION ==================================--
--~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~--

--~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~--
--================================ VIEWS =====================================--
--~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~--
-- 13/05/2015;
DROP VIEW V_CREDIT_FR;

CREATE VIEW V_CREDIT_FR AS
SELECT CRED.ID, CRED.ID_FR, CRED.ID_ACH, FR.NOM AS "Fournisseur", CRED.MONTANT AS "Montant(DA)", 
        CRED."DATE" AS "Date", CRED.HEURE AS "Heure", COMMENT AS "Commentaire"  
FROM CREDIT_FR CRED INNER JOIN FOURNISSEUR FR ON CRED.ID_FR = FR.ID
ORDER BY "Date" DESC, "Heure" DESC;
--~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~--
--=============================== QUERIES ====================================--
--~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~--
SELECT * FROM V_CREDIT_FR;
SELECT * FROM CREDIT_FR WHERE ID = ?;
INSERT INTO CREDIT_FR (ID_FR, ID_ACH, "DATE", HEURE, MONTANT, COMMENT, INITIAL) VALUES (?, ?, ?, ?, ?, ?, ?);
UPDATE CREDIT_FR SET ID_FR = ?, ID_ACH = ?, "DATE" = ?, HEURE = ?, MONTANT = ?, COMMENT = ? WHERE ID = ?;
DELETE FROM CREDIT_FR WHERE ID = ?;
--~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~--
--============================== TRIGGERS ====================================--
--~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~--
-- 19/01/2015
-- Update FOURN.DETTE after insert new CREDIT_FR
DROP TRIGGER T_AFT_INS_CREDIT_UPD_FR;

CREATE TRIGGER T_AFT_INS_CREDIT_UPD_FR
AFTER INSERT ON CREDIT_FR
REFERENCING NEW ROW AS NEW_CREDIT
FOR EACH ROW
    UPDATE FOURNISSEUR SET DETTE = DETTE + NEW_CREDIT.MONTANT
    WHERE FOURNISSEUR.ID = NEW_CREDIT.ID_FR;
--~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~--
-- 19/01/2015
-- Update old FOURN.DETTE after update CREDIT_FR
DROP TRIGGER T_AFT_UPD_CREDIT_UPD_OLD_FR;

CREATE TRIGGER T_AFT_UPD_CREDIT_UPD_OLD_FR
AFTER UPDATE ON CREDIT_FR
REFERENCING OLD ROW AS OLD_CREDIT
FOR EACH ROW
    UPDATE FOURNISSEUR SET DETTE = DETTE - OLD_CREDIT.MONTANT 
    WHERE ID = OLD_CREDIT.ID_FR;
-- Update new FOURN.DETTE after update CREDIT_FR
DROP TRIGGER T_AFT_UPD_CREDIT_UPD_NEW_FR;

CREATE TRIGGER T_AFT_UPD_CREDIT_UPD_NEW_FR
AFTER UPDATE ON CREDIT_FR
REFERENCING NEW ROW AS NEW_CREDIT
FOR EACH ROW
    UPDATE FOURNISSEUR SET DETTE = DETTE + NEW_CREDIT.MONTANT
    WHERE ID = NEW_CREDIT.ID_FR;
--~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~--
-- 19/01/2015
-- Update FOURN.DETTE after delete CREDIT_FR
DROP TRIGGER T_AFT_DEL_CREDIT_UPD_FR;

CREATE TRIGGER T_AFT_DEL_CREDIT_UPD_FR
AFTER DELETE ON CREDIT_FR
REFERENCING OLD ROW AS OLD_CREDIT
FOR EACH ROW
    UPDATE FOURNISSEUR SET DETTE = DETTE - OLD_CREDIT.MONTANT
    WHERE ID = OLD_CREDIT.ID_FR;
--~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~--    