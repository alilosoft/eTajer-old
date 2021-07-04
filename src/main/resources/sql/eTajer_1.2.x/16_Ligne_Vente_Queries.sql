--~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~--
--================================ CREATION ==================================--
--~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~--
-- 20/11/2015
DROP TABLE LIGNE_VNT;

CREATE TABLE LIGNE_VNT  (   ID INT NOT NULL GENERATED ALWAYS AS IDENTITY,
                            ID_VNT  INT NOT NULL,
                            ID_EN_STK INT NOT NULL,
                            PU_VNT DECIMAL(12,2) NOT NULL DEFAULT 0,-- MAX: 999,999,999
                            PU_ACH DECIMAL(12,2) NOT NULL DEFAULT 0,-- pour calc bénifice
                            QTE DOUBLE NOT NULL DEFAULT 1,
                            UNITE_VNT INT NOT NULL,
                            VALIDEE BOOLEAN NOT NULL DEFAULT FALSE,
                            QTE_UNIT DOUBLE NOT NULL DEFAULT 0,-- Champs calculé (qte * unité), utiliser pour optimiser la validation.
                            TOTAL_LVNT DECIMAL(12,2) NOT NULL DEFAULT 0,
                            COMMAND BOOLEAN NOT NULL DEFAULT TRUE,
                            RESERV BOOLEAN NOT NULL DEFAULT FALSE,
                            LIVRAIS BOOLEAN NOT NULL DEFAULT FALSE,
                            RETOUR BOOLEAN NOT NULL DEFAULT FALSE,
                            CONSTRAINT LIGNEVNT_PK PRIMARY KEY(ID),
                            CONSTRAINT LIGNEVNT_VNT_FK FOREIGN KEY (ID_VNT) REFERENCES VENTE ON DELETE CASCADE,
                            CONSTRAINT LIGNEVNT_ENSTK_FK FOREIGN KEY (ID_EN_STK) REFERENCES EN_STOCK ON DELETE RESTRICT,
                            CONSTRAINT LIGNEVNT_UNITE_FK FOREIGN KEY (UNITE_VNT) REFERENCES UNITE ON DELETE RESTRICT);
--~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~--
--============================== ALTERATION ==================================--
--~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~--
-- 23/11/2015;
ALTER TABLE LIGNE_VNT ADD COLUMN RETOUR BOOLEAN NOT NULL DEFAULT FALSE;
-- 13/05/2015
ALTER TABLE LIGNE_VNT ALTER COLUMN COMMAND SET DEFAULT TRUE;
-- 09/02/2015
ALTER TABLE LIGNE_VNT ADD COLUMN ID_DEPOT INT NOT NULL;
ALTER TABLE LIGNE_VNT ADD CONSTRAINT LIGNEVNT_DEPOT_FK FOREIGN KEY (ID_DEPOT) REFERENCES DEPOT ON DELETE RESTRICT;
-- 20/11/2014
ALTER TABLE LIGNE_VNT DROP COLUMN QTE_UNITAIR_VENDU;
ALTER TABLE LIGNE_VNT ADD COLUMN QTE_UNITAIR_VENDU DOUBLE NOT NULL DEFAULT 0;
--
ALTER TABLE LIGNE_VNT DROP COLUMN VALIDEE;
ALTER TABLE LIGNE_VNT ADD COLUMN VALIDEE BOOLEAN NOT NULL DEFAULT FALSE;
-- 11/11/2014
ALTER TABLE LIGNE_VNT DROP COLUMN QTE_ENVRAC_VENDU;
ALTER TABLE LIGNE_VNT ADD COLUMN QTE_ENVRAC_VENDU DOUBLE NOT NULL DEFAULT 0;
--~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~--
--================================ VIEWS =====================================--
--~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~--
-- Last_Modified: 10/03/2015
DROP VIEW V_LIGNE_VENTE; 

CREATE VIEW V_LIGNE_VENTE AS
SELECT LVNT.ID, LVNT.ID_VNT, S.ID_PROD, S.ID_DEPOT, LVNT.QTE_UNIT <= S.QTE AS "Dispo", 
    S.COD_BAR AS "Réf/C.B", PROD.DES AS "Produit", LVNT.PU_VNT AS "PU.Vente", 
    LVNT.QTE AS "Qte.Vendue", U.DES AS "Unité.Vnt", LVNT.TOTAL_LVNT AS "S.Total(DA)"
FROM LIGNE_VNT LVNT INNER JOIN UNITE U ON LVNT.UNITE_VNT = U.ID 
        INNER JOIN EN_STOCK S ON LVNT.ID_EN_STK = S.ID 
        INNER JOIN  PRODUIT PROD ON S.ID_PROD = PROD.ID
ORDER BY LVNT.ID;

-- 20/11/2015;
DROP VIEW JOURNAL_VNT; 

CREATE VIEW JOURNAL_VNT AS
SELECT VNT.ID_USER, PROD.DES AS "Produit", LVNT.QTE_UNIT AS "Qte.Vendue", LVNT.PU_VNT AS "PU.Vente",  VNT."DATE" AS "Date", U.LOGIN AS "Vendeur"
FROM    VENTE VNT INNER JOIN APP_USER U ON VNT.ID_USER = U.ID
        INNER JOIN LIGNE_VNT LVNT ON VNT.ID = LVNT.ID_VNT 
        INNER JOIN EN_STOCK S ON LVNT.ID_EN_STK = S.ID 
        INNER JOIN  PRODUIT PROD ON S.ID_PROD = PROD.ID
WHERE LVNT.VALIDEE = TRUE and VNT."DATE" = current_date
ORDER BY LVNT.ID  DESC;

-- backup
CREATE VIEW JOURNAL_VNT AS
SELECT LVNT.ID, LVNT.ID_VNT, S.ID_PROD, S.ID_DEPOT, VNT.ID_USER, PROD.DES AS "Produit", LVNT.QTE_UNIT AS "Qte.Vendue", 
        LVNT.PU_VNT AS "PU.Vente", LVNT.PU_ACH AS "PU.Achat", LVNT.TOTAL_LVNT AS "Total.Vente", 
        LVNT.QTE_UNIT * (LVNT.PU_VNT - LVNT.PU_ACH) AS "Bénifice", VNT."DATE" AS "Date", U.LOGIN AS "Vendeur"
FROM    VENTE VNT INNER JOIN APP_USER U ON VNT.ID_USER = U.ID
        INNER JOIN LIGNE_VNT LVNT ON VNT.ID = LVNT.ID_VNT 
        INNER JOIN EN_STOCK S ON LVNT.ID_EN_STK = S.ID 
        INNER JOIN  PRODUIT PROD ON S.ID_PROD = PROD.ID
WHERE LVNT.VALIDEE = TRUE
ORDER BY LVNT.ID  DESC;
--~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~--
--=============================== QUERIES ====================================--
--~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~--
SELECT * FROM V_LIGNE_VENTE;
SELECT * FROM V_LIGNE_VENTE WHERE ID_VNT = ?;
SELECT * FROM LIGNE_VNT WHERE ID = ?;
INSERT INTO LIGNE_VNT (ID_VNT, ID_EN_STK, PU_VNT, PU_ACH, QTE, UNITE_VNT, QTE_UNIT, TOTAL_LVNT) VALUES (?, ?, ?, ?, ?, ?, ?, ?);
UPDATE LIGNE_VNT SET ID_VNT = ?, ID_EN_STK = ?, PU_VNT = ?, PU_ACH = ?, QTE = ?, UNITE_VNT = ?, QTE_UNIT = ?, TOTAL_LVNT = ? WHERE ID = ?; 
DELETE FROM LIGNE_VNT WHERE ID = ?;
--~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~--
--============================== TRIGGERS ====================================--
--~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~--
-- 06/11/2014
-- This trigger sets the total vente  after INSERTING a 'Ligne.Vente'.
DROP TRIGGER T_AFT_INS_LVNT_UPD_TOTALVNT;

CREATE TRIGGER T_AFT_INS_LVNT_UPD_TOTALVNT
AFTER INSERT ON LIGNE_VNT
REFERENCING NEW ROW AS NEW_LVNT
FOR EACH ROW
    UPDATE VENTE SET 
        TOTAL = TOTAL + NEW_LVNT.TOTAL_LVNT  
    WHERE ID = NEW_LVNT.ID_VNT;
--~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~--
-- 06/11/2014
-- This trigger update total vent after updating ligne vente
DROP TRIGGER T_AFT_UPD_LVNT_UPD_TOTALVNT;

CREATE TRIGGER T_AFT_UPD_LVNT_UPD_TOTALVNT
AFTER UPDATE ON LIGNE_VNT
REFERENCING OLD ROW AS OLD_LVNT NEW ROW AS NEW_LVNT
FOR EACH ROW
    UPDATE VENTE SET 
        TOTAL = TOTAL - OLD_LVNT.TOTAL_LVNT + NEW_LVNT.TOTAL_LVNT
    WHERE ID = NEW_LVNT.ID_VNT;
--~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~--
-- 06/11/2014
-- This trigger update total vent after deleting ligne vente  
DROP TRIGGER T_AFT_DEL_LVNT_UPD_TOTALVNT;

CREATE TRIGGER T_AFT_DEL_LVNT_UPD_TOTALVNT
AFTER DELETE ON LIGNE_VNT
REFERENCING OLD ROW AS DEL_LVNT
FOR EACH ROW
    UPDATE VENTE SET 
        TOTAL = TOTAL - DEL_LVNT.TOTAL_LVNT
    WHERE ID = DEL_LVNT.ID_VNT;
--~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~--
-- 02/01/2016
-- UPDATE the produit quantities after validating/invalidating a 'Ligne.Vente'. 
DROP TRIGGER T_AFT_VALID_LVNT_UPD_STK;
CREATE TRIGGER T_AFT_VALID_LVNT_UPD_STK
AFTER UPDATE OF VALIDEE ON LIGNE_VNT
REFERENCING NEW AS LVNT
FOR EACH ROW 
    UPDATE EN_STOCK SET QTE =  QTE - LVNT.QTE_UNIT
    WHERE LVNT.VALIDEE = TRUE AND ID = LVNT.ID_EN_STK;

DROP TRIGGER T_AFT_INVALID_LVNT_UPD_STK;
CREATE TRIGGER T_AFT_INVALID_LVNT_UPD_STK
AFTER UPDATE OF VALIDEE ON LIGNE_VNT
REFERENCING NEW AS LVNT
FOR EACH ROW 
    UPDATE EN_STOCK SET QTE =  QTE + LVNT.QTE_UNIT
    WHERE LVNT.VALIDEE = FALSE AND ID = LVNT.ID_EN_STK;
--~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~--