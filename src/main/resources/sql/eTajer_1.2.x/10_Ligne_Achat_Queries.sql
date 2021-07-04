--~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~--
--================================ CREATION ==================================--
--~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~--
-- 16/03/2015
DROP TABLE LIGNE_ACH;

CREATE TABLE LIGNE_ACH  (   ID INT NOT NULL GENERATED ALWAYS AS IDENTITY,
                            ID_ACH  INT NOT NULL,
                            ID_PROD INT NOT NULL,
                            ID_EN_STK INT NOT NULL,
                            PU_ACH DECIMAL(12,2) NOT NULL DEFAULT 0,-- MAX: 999,999,999
                            QTE INT NOT NULL,
                            UNITE INT NOT NULL,
                            VALIDEE BOOLEAN NOT NULL DEFAULT FALSE,
                            QTE_UNIT DOUBLE NOT NULL DEFAULT 0,-- Champs calculé (qte * unité), utiliser pour maj qte.stk sans re-calc pour optimiser la validation.
                            TOTAL_LACH DECIMAL(12,2) NOT NULL DEFAULT 0,
                            COMMAND BOOLEAN NOT NULL DEFAULT TRUE,
                            RECEPTION BOOLEAN NOT NULL DEFAULT FALSE,
                            CONSTRAINT LACH_PK PRIMARY KEY(ID),
                            CONSTRAINT LACH_ACH_FK FOREIGN KEY (ID_ACH) REFERENCES ACHAT ON DELETE CASCADE,
                            CONSTRAINT LACH_PROD_FK FOREIGN KEY (ID_PROD) REFERENCES PRODUIT ON DELETE RESTRICT,
                            CONSTRAINT LACH_UNITE_FK FOREIGN KEY (UNITE) REFERENCES UNITE ON DELETE RESTRICT,
                            CONSTRAINT LACH_ENSTK_FK FOREIGN KEY (ID_EN_STK) REFERENCES EN_STOCK ON DELETE RESTRICT);
--~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~--
--============================== ALTERATION ==================================--
--~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~--
-- 23/11/2015;
ALTER TABLE LIGNE_ACH ADD COLUMN RETOUR BOOLEAN NOT NULL DEFAULT FALSE;
--~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~--
--================================ VIEWS =====================================--
--~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~--
-- Last_Modified: 10/03/2015
DROP VIEW V_LIGNE_ACHAT; 

CREATE VIEW V_LIGNE_ACHAT AS
SELECT LACH.ID, LACH.ID_ACH, LACH.ID_PROD, PROD.COD_BAR AS "Réf/C.B", PROD.DES AS "Produit", LACH.PU_ACH AS "PU.Achat", 
        LACH.QTE AS "Qte", U.DES AS "Unité", LACH.TOTAL_LACH AS "S.Total(DA)"
FROM (LIGNE_ACH LACH INNER JOIN UNITE U ON LACH.UNITE = U.ID) INNER JOIN PRODUIT PROD ON LACH.ID_PROD = PROD.ID
ORDER BY LACH.ID;
--~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~--
--=============================== QUERIES ====================================--
--~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~--
SELECT * FROM V_LIGNE_ACHAT;
SELECT * FROM V_LIGNE_ACHAT WHERE ID_ACH = ?;
SELECT * FROM LIGNE_ACH WHERE ID = ?;
INSERT INTO LIGNE_ACH (ID_ACH, ID_PROD, ID_EN_STK, PU_ACH, QTE, UNITE, QTE_UNIT, TOTAL_LACH) VALUES (?, ?, ?, ?, ?, ?, ?, ?);
UPDATE LIGNE_ACH SET ID_ACH = ?, ID_PROD = ?, ID_EN_STK = ?, PU_ACH = ?, QTE = ?, UNITE = ?, QTE_UNIT = ?, TOTAL_LACH = ? WHERE ID = ?; 
DELETE FROM LIGNE_ACH WHERE ID = ?;
--~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~--
--============================== TRIGGERS ====================================--
--~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~--
-- 10/01/2015
-- This trigger updates the total achat  after INSERTING new 'Ligne.Achat'.
DROP TRIGGER T_AFT_INS_LACH_UPD_TOTALACH;

CREATE TRIGGER T_AFT_INS_LACH_UPD_TOTALACH
AFTER INSERT ON LIGNE_ACH
REFERENCING NEW ROW AS NEW_LACH
FOR EACH ROW
    UPDATE ACHAT SET 
        TOTAL = TOTAL + NEW_LACH.TOTAL_LACH  
    WHERE ID = NEW_LACH.ID_ACH;
--~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~--
-- 20/01/2016
-- This trigger update total achat after UPDATING total ligne achat
DROP TRIGGER T_AFT_UPD_LACH_UPD_TOTALACH;

CREATE TRIGGER T_AFT_UPD_LACH_UPD_TOTALACH
AFTER UPDATE OF TOTAL_LACH ON LIGNE_ACH
REFERENCING OLD ROW AS OLD_LACH NEW ROW AS NEW_LACH
FOR EACH ROW
    UPDATE ACHAT SET 
        TOTAL = TOTAL - OLD_LACH.TOTAL_LACH + NEW_LACH.TOTAL_LACH
    WHERE ID = NEW_LACH.ID_ACH;
--~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~--
-- 10/01/2015
-- This trigger update total achat after DELETING ligne vente  
DROP TRIGGER T_AFT_DEL_LACH_UPD_TOTALACH;

CREATE TRIGGER T_AFT_DEL_LACH_UPD_TOTALACH
AFTER DELETE ON LIGNE_ACH
REFERENCING OLD ROW AS DEL_LACH
FOR EACH ROW
    UPDATE ACHAT SET 
        TOTAL = TOTAL - DEL_LACH.TOTAL_LACH
    WHERE ID = DEL_LACH.ID_ACH;
--~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~--
-- 17/01/2015
-- This trigger delete 'EnStock' row related to the deleted 'LigneAchat'     
DROP TRIGGER T_AFT_DEL_LACH_DEL_ENSTK;

CREATE TRIGGER T_AFT_DEL_LACH_DEL_ENSTK
AFTER DELETE ON LIGNE_ACH
REFERENCING OLD ROW AS DEL_LACH
FOR EACH ROW
    DELETE FROM EN_STOCK WHERE QTE = 0 AND ID = DEL_LACH.ID_EN_STK 
    AND ID NOT IN (SELECT ID_EN_STK FROM LIGNE_VNT UNION SELECT ID_EN_STK FROM LIGNE_ACH);
--~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~--
-- 21/01/2016
-- This trigger delete 'EnStock' that was related to an updated 'LigneAchat' and become "Unused".     
DROP TRIGGER T_AFT_UPD_LACH_DEL_ENSTK;

CREATE TRIGGER T_AFT_UPD_LACH_DEL_ENSTK
AFTER UPDATE OF ID_EN_STK ON LIGNE_ACH
REFERENCING OLD ROW AS OLD_LACH NEW ROW AS NEW_LACH
FOR EACH ROW
    DELETE FROM EN_STOCK 
    WHERE OLD_LACH.ID_EN_STK != NEW_LACH.ID_EN_STK
    AND ID = OLD_LACH.ID_EN_STK AND ACTIF = FALSE 
    AND ID NOT IN (SELECT ID_EN_STK FROM LIGNE_VNT UNION SELECT ID_EN_STK FROM LIGNE_ACH);
--~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~--
-- 03/2015
-- UPDATE the  related EnStock quantities after validating/invalidating a 'Ligne.achat'. 
DROP TRIGGER T_AFT_VALID_LACH_UPD_ENSTK;

CREATE TRIGGER T_AFT_VALID_LACH_UPD_ENSTK
AFTER UPDATE OF VALIDEE ON LIGNE_ACH
REFERENCING NEW AS LACH
FOR EACH ROW 
    UPDATE EN_STOCK SET 
        QTE =  QTE + LACH.QTE_UNIT 
    WHERE LACH.VALIDEE = TRUE AND ID = LACH.ID_EN_STK; 

DROP TRIGGER T_AFT_INVALID_LACH_UPD_ENSTK;

CREATE TRIGGER T_AFT_INVALID_LACH_UPD_ENSTK
AFTER UPDATE OF VALIDEE ON LIGNE_ACH
REFERENCING NEW AS LACH
FOR EACH ROW 
    UPDATE EN_STOCK SET 
        QTE =  QTE - LACH.QTE_UNIT
    WHERE LACH.VALIDEE = FALSE AND ID = LACH.ID_EN_STK; 
--~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~--