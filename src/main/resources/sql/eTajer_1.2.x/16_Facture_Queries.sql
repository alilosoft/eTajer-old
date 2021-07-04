--~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~--
--================================ CREATION ==================================--
--~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~--
-- Last modification: 18/01/2015
DROP TABLE FACTURE;
CREATE TABLE FACTURE(ID INT NOT NULL GENERATED ALWAYS AS IDENTITY,
                     ID_VNT INT DEFAULT NULL,
                     NUM INT  NOT NULL,
                     "DATE" DATE DEFAULT CURRENT_DATE,
                     MONTANT DECIMAL(10,2) NOT NULL DEFAULT 0,
                     CONSTRAINT VNT_PK PRIMARY KEY (ID),
                     CONSTRAINT VNT_CL_FK FOREIGN KEY (ID_CL) REFERENCES CLIENT ON DELETE RESTRICT,
                     CONSTRAINT VNT_TYPE_FK FOREIGN KEY (ID_TYPE) REFERENCES TYPE_VNT ON DELETE RESTRICT);
-- Reset the ID:
ALTER TABLE VENTE ALTER COLUMN ID RESTART WITH 1;
--~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~--
--============================== ALTERATION ==================================--
--~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~--
-- Table alterations:
-- 11/11/2014
ALTER TABLE VENTE DROP COLUMN VALIDE;
ALTER TABLE VENTE ADD COLUMN VALIDEE BOOLEAN NOT NULL DEFAULT FALSE;
--~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~--
--================================ VIEWS =====================================--
--~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~--
-- Modified: 11/11/2014
DROP VIEW V_VENTE_ALL;
CREATE VIEW V_VENTE_ALL AS
SELECT VNT.ID, CL.ID AS "ID_CL", VNT.NUM AS "N°", VNT."DATE" AS "Date", VNT.HEURE AS "Heure", TVNT.DES AS "Mode", CL."NAME" AS "Client", 
    VNT.TOTAL AS "Total(DA)", VNT.VALIDEE AS "Validé?"
FROM  (VENTE VNT INNER JOIN TYPE_VNT TVNT ON VNT.ID_TYPE = TVNT.ID) LEFT JOIN CLIENT CL ON VNT.ID_CL = CL.ID 
ORDER BY VNT."DATE" DESC, VNT.HEURE DESC;
--=========================
DROP VIEW V_VENTE_CL;
CREATE VIEW V_VENTE_CL AS
SELECT VNT.ID, CL.ID AS "ID_CL", VNT.NUM AS "N°", VNT."DATE" AS "Date", VNT.HEURE AS "Heure", TVNT.DES AS "Mode", CL."NAME" AS "Client", 
    VNT.TOTAL AS "Total(DA)", VNT.VALIDEE AS "Validé?"
FROM  (VENTE VNT INNER JOIN TYPE_VNT TVNT ON VNT.ID_TYPE = TVNT.ID) INNER JOIN CLIENT CL ON VNT.ID_CL = CL.ID 
ORDER BY VNT."DATE" DESC, VNT.HEURE DESC;
--~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~--
--=============================== QUERIES ====================================--
--~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~--
SELECT * FROM V_VENTE_ALL;
SELECT * FROM VENTE WHERE ID = ?;
INSERT INTO VENTE (ID_CL, ID_TYPE, NUM, "DATE", HEURE) VALUES (?, ?, ?, ?, ?);
UPDATE VENTE SET ID_CL = ?, ID_TYPE = ?,  NUM = ?, "DATE" = ?, HEURE = ? WHERE ID = ?;
DELETE FROM VENTE WHERE ID = ?;
SELECT * FROM V_VENTE_ALL WHERE ID_CL IS NOT NULL;
-- Get new num de vente:
SELECT MAX (NUM) FROM VENTE WHERE "DATE" = CURRENT_DATE ;
SELECT MAX (NUM) FROM VENTE WHERE \"DATE\" >= ? AND \"DATE\" <= ?;
-- Valider une 'Vente'
UPDATE VENTE SET VALIDE = 'Y' WHERE ID = ?;
-- Invalider une Vente
UPDATE VENTE SET VALIDE = 'N' WHERE ID = ?;
-- Get total du vente
SELECT TOTAL FROM VENTE WHERE ID = ?;
-- Get total des ventes d'un jour
SELECT SUM(TOTAL) FROM VENTE WHERE VALIDE = 'Y' AND "DATE" >= ? AND "DATE" <=  ?;
-- Get total des ventes d'un client
SELECT TOTAL FROM VENTE WHERE ID_CL = ?;
--~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~--
--============================== TRIGGERS ====================================--
--~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~--
-- 11/11/2014
-- Update LIGNE_VNT.VALIDEE after update of VENTE.VALIDEE
DROP TRIGGER T_AFT_UPD_VALID_VNT_UPD_VALID_LVNT;
CREATE TRIGGER T_AFT_UPD_VALID_VNT_UPD_VALID_LVNT
AFTER UPDATE OF VALIDEE ON VENTE
REFERENCING NEW AS VNT
FOR EACH ROW
    UPDATE LIGNE_VNT LVNT 
    SET LVNT.VALIDEE = VNT.VALIDEE
    WHERE LVNT.ID_VNT = VNT.ID;
--~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~--
-- 15/11/2014
-- Delete REGLEMENT_CL after invalidate vente
DROP TRIGGER T_AFT_UPD_VALID_VNT_DEL_REGL;
CREATE TRIGGER T_AFT_UPD_VALID_VNT_DEL_REGL
AFTER UPDATE OF VALIDEE ON VENTE
REFERENCING NEW AS NEW_VNT
FOR EACH ROW
    DELETE FROM REGLEMENT_CL 
    WHERE NEW_VNT.VALIDEE = FALSE 
    AND ID_VNT = NEW_VNT.ID;
--~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~--
-- 15/11/2014
-- Delete CREDIT_CL after invalidate vente
DROP TRIGGER T_AFT_UPD_VALID_VNT_DEL_CREDIT;
CREATE TRIGGER T_AFT_UPD_VALID_VNT_DEL_CREDIT
AFTER UPDATE OF VALIDEE ON VENTE
REFERENCING NEW AS NEW_VNT
FOR EACH ROW
    DELETE FROM CREDIT_CL
    WHERE NEW_VNT.VALIDEE = FALSE 
    AND ID_VNT = NEW_VNT.ID;
--~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~--