--~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~--
--================================ CREATION ==================================--
--~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~--
-- Last modification: 21/01/2015
DROP TABLE REGLEMENT_FR;

CREATE TABLE REGLEMENT_FR  (    ID INT NOT NULL GENERATED ALWAYS AS IDENTITY,
                                ID_FR INT NOT NULL,
                                ID_ACH INT DEFAULT NULL,
                                "DATE" DATE NOT NULL DEFAULT CURRENT_DATE,
                                HEURE TIME NOT NULL DEFAULT CURRENT_TIME,
                                MONTANT DECIMAL(12,2) NOT NULL DEFAULT 0,-- MAX: 999,999,999
                                MODE_PAY INT NOT NULL,--MODE DE PAYEMENT, ESPECE, CHEQUE,...ETC.
                                COMMENT VARCHAR(200) DEFAULT NULL,
                                CONSTRAINT REGLFR_PK PRIMARY KEY (ID),
                                CONSTRAINT REGLFR_FR_FK FOREIGN KEY (ID_FR) REFERENCES FOURNISSEUR ON DELETE RESTRICT,
                                CONSTRAINT REGLFR_ACH_FK FOREIGN KEY (ID_ACH) REFERENCES ACHAT ON DELETE CASCADE,
                                CONSTRAINT REGLFR_MODE_FK FOREIGN KEY (MODE_PAY) REFERENCES MODE_PAYE ON DELETE RESTRICT);
--~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~--
--============================== ALTERATION ==================================--
--~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~--

--~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~--
--================================ VIEWS =====================================--
--~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~--
-- Last modification: 21/01/2015.
DROP VIEW V_REGLEMENT_FR;

CREATE VIEW V_REGLEMENT_FR AS
SELECT REG.ID, REG.ID_FR, REG.ID_ACH, FR.NOM AS "Fournisseur", REG.MONTANT AS "Montant(DA)", 
        REG."DATE" AS "Date", REG.HEURE AS "Heure", MP.DES AS "Mode", COMMENT AS "Commentaire"  
FROM (REGLEMENT_FR REG INNER JOIN FOURNISSEUR FR ON REG.MONTANT != 0 AND REG.ID_FR = FR.ID) 
    INNER JOIN MODE_PAYE MP ON REG.MODE_PAY = MP.ID
ORDER BY "Date" DESC, "Heure" DESC;
--~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~--
--=============================== QUERIES ====================================--
--~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~--
SELECT * FROM V_REGLEMENT_FR;
SELECT * FROM REGLEMENT_FR WHERE ID = ?;
INSERT INTO REGLEMENT_FR (ID_FR, ID_ACH, "DATE", HEURE, MONTANT, MODE_PAY, COMMENT) VALUES (?, ?, ?, ?, ?, ?, ?);
UPDATE REGLEMENT_FR SET ID_FR = ?, ID_ACH = ?, "DATE" = ?, HEURE = ?, MONTANT = ?, MODE_PAY = ?, COMMENT = ? WHERE ID = ?;
DELETE FROM REGLEMENT_FR WHERE ID = ?;
SELECT ID FROM REGLEMENT_FR WHERE ID_ACH = ?;
--~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~--
--============================== TRIGGERS ====================================--
--~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~--
-- 26/01/2015
-- Update FOURNISSEUR.DETTE after insert on REGLEMENT_FR
DROP TRIGGER T_AFT_INS_REGL_UPD_FR;

CREATE TRIGGER T_AFT_INS_REGL_UPD_FR
AFTER INSERT ON REGLEMENT_FR
REFERENCING NEW ROW AS NEW_REGL
FOR EACH ROW
    UPDATE FOURNISSEUR SET DETTE = DETTE - NEW_REGL.MONTANT
    WHERE  FOURNISSEUR.ID = NEW_REGL.ID_FR;
--~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~--
-- 27/01/2015
-- Update old FOURNISSEUR.DETTE after update REGLEMENT_FR
DROP TRIGGER T_AFT_UPD_REGL_UPD_OLD_FR;

CREATE TRIGGER T_AFT_UPD_REGL_UPD_OLD_FR
AFTER UPDATE ON REGLEMENT_FR
REFERENCING OLD ROW AS OLD_REGL
FOR EACH ROW
    UPDATE FOURNISSEUR SET DETTE = DETTE + OLD_REGL.MONTANT 
    WHERE ID = OLD_REGL.ID_FR;
-- Update new FOURNISSEUR.DETTE after update REGLEMENT_FR
DROP TRIGGER T_AFT_UPD_REGL_UPD_NEW_FR;

CREATE TRIGGER T_AFT_UPD_REGL_UPD_NEW_FR
AFTER UPDATE ON REGLEMENT_FR
REFERENCING NEW ROW AS NEW_REGL
FOR EACH ROW
    UPDATE FOURNISSEUR SET DETTE = DETTE - NEW_REGL.MONTANT
    WHERE ID = NEW_REGL.ID_FR;
--~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~--
-- 27/01/2015
-- Update FOURNISSEUR.DETTE after delete REGLEMENT_FR
DROP TRIGGER T_AFT_DEL_REGL_UPD_FR;

CREATE TRIGGER T_AFT_DEL_REGL_UPD_FR
AFTER DELETE ON REGLEMENT_FR
REFERENCING OLD ROW AS OLD_REGL
FOR EACH ROW
    UPDATE FOURNISSEUR SET DETTE = DETTE + OLD_REGL.MONTANT
    WHERE FOURNISSEUR.ID = OLD_REGL.ID_FR; 
--~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~--