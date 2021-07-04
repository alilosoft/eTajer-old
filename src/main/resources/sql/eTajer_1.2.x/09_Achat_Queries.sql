--~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~--
--================================ CREATION ==================================--
--~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~--
-- Last modification: 19/03/2015
DROP TABLE ACHAT;

CREATE TABLE ACHAT  (ID INT NOT NULL GENERATED ALWAYS AS IDENTITY,
                     ID_FR INT DEFAULT NULL,
                     NUM INT  NOT NULL,
                     "DATE" DATE DEFAULT CURRENT_DATE,
                     HEURE TIME NOT NULL DEFAULT CURRENT_TIME,
                     VALIDE BOOLEAN NOT NULL DEFAULT FALSE,
                     TOTAL DECIMAL(12,2) NOT NULL DEFAULT 0,-- MAX: 999,999,999
                     COMMAND BOOLEAN NOT NULL DEFAULT TRUE,
                     RECEPTION BOOLEAN NOT NULL DEFAULT FALSE,
                     RETOUR BOOLEAN NOT NULL DEFAULT FALSE,
                     CONSTRAINT ACH_PK PRIMARY KEY (ID),
                     CONSTRAINT ACH_FR_FK FOREIGN KEY (ID_FR) REFERENCES FOURNISSEUR ON DELETE RESTRICT);
-- Reset the ID:
ALTER TABLE ACHAT ALTER COLUMN ID RESTART WITH 1;
--~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~--
--============================== ALTERATION ==================================--
--~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~--
-- 23/11/2015;
ALTER TABLE ACHAT ADD COLUMN RETOUR BOOLEAN NOT NULL DEFAULT FALSE;
--~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~--
--================================ VIEWS =====================================--
--~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~--
-- Modified: 03/01/2015
DROP VIEW V_ACHAT_ALL;

CREATE VIEW V_ACHAT_ALL AS
SELECT ACH.ID, ACH.ID_FR, ACH.NUM AS "N°", ACH."DATE" AS "Date", ACH.HEURE AS "Heure", FR.NOM AS "Fournisseur", 
    ACH.TOTAL AS "Total(DA)", ACH.VALIDE AS "Validé?"
FROM  ACHAT ACH LEFT JOIN FOURNISSEUR FR ON ACH.ID_FR = FR.ID 
ORDER BY ACH."DATE" DESC, ACH.HEURE DESC;
--=========================
DROP VIEW V_ACHAT_FOUR;

CREATE VIEW V_ACHAT_FOUR AS
SELECT * FROM V_ACHAT_ALL WHERE ID_FR IS NOT NULL;
--~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~--
--=============================== QUERIES ====================================--
--~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~--
SELECT * FROM V_ACHAT_ALL;
SELECT * FROM ACHAT WHERE ID = ?;
INSERT INTO ACHAT (ID_FR, NUM, "DATE", HEURE) VALUES (?, ?, ?, ?);
UPDATE ACHAT SET ID_FR = ?, NUM = ?, "DATE" = ?, HEURE = ? WHERE ID = ?;
DELETE FROM ACHAT WHERE ID = ?;
-- Get new N° d'achat:
SELECT MAX (NUM) FROM ACHAT WHERE "DATE" = CURRENT_DATE ;
SELECT MAX (NUM) FROM ACHAT WHERE "DATE" >= ? AND "DATE" <= ?;
-- Valider un 'Achat'
UPDATE ACHAT SET VALIDE = TRUE WHERE ID = ?;
-- Invalider un Achat
UPDATE ACHAT SET VALIDE = FALSE WHERE ID = ?;
-- Get total d'achat
SELECT TOTAL FROM ACHAT WHERE ID = ?;
-- Get total des achat d'un fournisseur
SELECT TOTAL FROM ACHAT WHERE ID_FOUR = ?;
--~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~--
--============================== TRIGGERS ====================================--
--~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~--
-- 10/01/2015
-- Update LIGNE_ACH.VALIDEE after update of ACHAT.VALIDEE
DROP TRIGGER T_AFT_UPD_VALID_ACH_UPD_VALID_LACH;
CREATE TRIGGER T_AFT_UPD_VALID_ACH_UPD_VALID_LACH
AFTER UPDATE OF VALIDE ON ACHAT
REFERENCING NEW AS ACH
FOR EACH ROW
    UPDATE LIGNE_ACH LACH 
    SET LACH.VALIDEE = ACH.VALIDE
    WHERE LACH.ID_ACH = ACH.ID;
--~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~--
-- 13/05/2015
-- Update Client.Dette after validating 'Vente'
DROP TRIGGER T_AFT_VALIDATE_ACH_UPD_CREDIT_FR;

CREATE TRIGGER T_AFT_VALIDATE_ACH_UPD_CREDIT_FR
AFTER UPDATE OF VALIDE ON ACHAT
REFERENCING NEW AS NEW_ACH
FOR EACH ROW
    UPDATE FOURNISSEUR SET DETTE = DETTE + NEW_ACH.TOTAL
    WHERE NEW_ACH.VALIDE = TRUE AND FOURNISSEUR.ID = NEW_ACH.ID_FR;
--~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~--
-- 13/05/2015
-- Update Client.Dette after in-validating 'Vente'
DROP TRIGGER T_AFT_INVALIDATE_ACH_UPD_CREDIT_FR;

CREATE TRIGGER T_AFT_INVALIDATE_ACH_UPD_CREDIT_FR
AFTER UPDATE OF VALIDE ON ACHAT
REFERENCING NEW AS NEW_ACH
FOR EACH ROW
    UPDATE FOURNISSEUR SET DETTE = DETTE - NEW_ACH.TOTAL
    WHERE NEW_ACH.VALIDE = FALSE AND FOURNISSEUR.ID = NEW_ACH.ID_FR;
--~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~--
-- 27/01/2015
-- Delete REGLEMENT_FR after invalidate vente
DROP TRIGGER T_AFT_UPD_VALID_ACH_DEL_REGL;

CREATE TRIGGER T_AFT_UPD_VALID_ACH_DEL_REGL
AFTER UPDATE OF VALIDE ON ACHAT
REFERENCING NEW AS NEW_ACH
FOR EACH ROW
    DELETE FROM REGLEMENT_FR 
    WHERE NEW_ACH.VALIDE = FALSE 
    AND ID_ACH = NEW_ACH.ID;
--~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~--
-- 13/05/2015 => droped!
-- Delete CREDIT_FR after invalidate vente
DROP TRIGGER T_AFT_UPD_VALID_ACH_DEL_CREDIT;
-- Droped!
CREATE TRIGGER T_AFT_UPD_VALID_ACH_DEL_CREDIT
AFTER UPDATE OF VALIDE ON ACHAT
REFERENCING NEW AS NEW_ACH
FOR EACH ROW
    DELETE FROM CREDIT_FR
    WHERE NEW_ACH.VALIDE = FALSE 
    AND ID_ACH = NEW_ACH.ID;
--~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~--
