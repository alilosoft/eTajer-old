--~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~--
--================================ CREATION ==================================--
--~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~--
-- Last modification: 15/11/2014.
DROP TABLE REGLEMENT_CL;

CREATE TABLE REGLEMENT_CL  (    ID INT NOT NULL GENERATED ALWAYS AS IDENTITY,
                                ID_CL INT NOT NULL,
                                ID_VNT INT DEFAULT NULL,
                                "DATE" DATE NOT NULL DEFAULT CURRENT_DATE,
                                HEURE TIME NOT NULL DEFAULT CURRENT_TIME,
                                MONTANT DECIMAL(12,2) NOT NULL DEFAULT 0,-- MAX: 999,999,999
                                MODE_PAY INT NOT NULL,--MODE DE PAYEMENT, ESPECE, CHEQUE,...ETC.
                                COMMENT VARCHAR(200) DEFAULT NULL,
                                CONSTRAINT REGLCL_PK PRIMARY KEY (ID),
                                CONSTRAINT REGLCL_CL_FK FOREIGN KEY (ID_CL) REFERENCES CLIENT ON DELETE RESTRICT,
                                CONSTRAINT REGLCL_VNT_FK FOREIGN KEY (ID_VNT) REFERENCES VENTE ON DELETE CASCADE,
                                CONSTRAINT REGLCL_MODE_FK FOREIGN KEY (MODE_PAY) REFERENCES MODE_PAYE ON DELETE RESTRICT);
--~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~--
--============================== ALTERATION ==================================--
--~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~--
ALTER TABLE REGLEMENT_CL DROP COLUMN MODE_PAY;
ALTER TABLE REGLEMENT_CL ADD COLUMN MODE_PAY INT NOT NULL DEFAULT 1;
ALTER TABLE REGLEMENT_CL ADD CONSTRAINT REGLCL_MODE_FK FOREIGN KEY (MODE_PAY) REFERENCES MODE_PAYE ON DELETE RESTRICT;
--~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~--
--================================ VIEWS =====================================--
--~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~--
-- Last modification: 12/05/2015
DROP VIEW V_REGLEMENT_CL;

CREATE VIEW V_REGLEMENT_CL AS
SELECT REG.ID, REG.ID_CL, REG.ID_VNT, CL.NOM AS "Client", REG.MONTANT AS "Montant(DA)", 
        REG."DATE" AS "Date", REG.HEURE AS "Heure", MP.DES AS "Mode", COMMENT AS "Commentaire"  
FROM (REGLEMENT_CL REG INNER JOIN CLIENT CL ON REG.MONTANT != 0 AND REG.ID_CL = CL.ID) INNER JOIN MODE_PAYE MP ON REG.MODE_PAY = MP.ID
ORDER BY "Date" DESC, "Heure" DESC;
--~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~--
--=============================== QUERIES ====================================--
--~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~--
SELECT * FROM V_REGLEMENT_CL;
SELECT * FROM REGLEMENT_CL WHERE ID = ?;
INSERT INTO REGLEMENT_CL (ID_CL, ID_VNT, "DATE", HEURE, MONTANT, MODE_PAY, COMMENT) VALUES (?, ?, ?, ?, ?, ?, ?);
UPDATE REGLEMENT_CL SET ID_CL = ?, ID_VNT = ?, "DATE" = ?, HEURE = ?, MONTANT = ?, MODE_PAY = ?, COMMENT = ? WHERE ID = ?;
DELETE FROM REGLEMENT_CL WHERE ID = ?;
SELECT ID FROM REGLEMENT_CL WHERE ID_VNT = ?;
--~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~--
--============================== TRIGGERS ====================================--
--~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~--
-- 15/11/2014
-- Update CLIENT.DETTE after insert on REGLEMENT_CL
DROP TRIGGER T_AFT_INS_REGL_UPD_CLIENT;

CREATE TRIGGER T_AFT_INS_REGL_UPD_CLIENT
AFTER INSERT ON REGLEMENT_CL
REFERENCING NEW ROW AS NEW_REGL
FOR EACH ROW
    UPDATE CLIENT SET DETTE = DETTE - NEW_REGL.MONTANT
    WHERE  CLIENT.ID = NEW_REGL.ID_CL;
    -- NEW_REGL.ID_VNT IS NULL AND  
--~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~--
-- 15/11/2014
-- Update old CLIENT.DETTE after update REGLEMENT_CL
DROP TRIGGER T_AFT_UPD_REGL_UPD_OLD_CLIENT;

CREATE TRIGGER T_AFT_UPD_REGL_UPD_OLD_CLIENT
AFTER UPDATE ON REGLEMENT_CL
REFERENCING OLD ROW AS OLD_REGL
FOR EACH ROW
    UPDATE CLIENT SET DETTE = DETTE + OLD_REGL.MONTANT 
    WHERE ID = OLD_REGL.ID_CL;
-- Update new CLIENT.DETTE after update REGLEMENT_CL
DROP TRIGGER T_AFT_UPD_REGL_UPD_NEW_CLIENT;

CREATE TRIGGER T_AFT_UPD_REGL_UPD_NEW_CLIENT
AFTER UPDATE ON REGLEMENT_CL
REFERENCING NEW ROW AS NEW_REGL
FOR EACH ROW
    UPDATE CLIENT SET DETTE = DETTE - NEW_REGL.MONTANT
    WHERE ID = NEW_REGL.ID_CL;
--~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~--
-- 15/11/2014
-- Update CLIENT.DETTE after delete REGLEMENT_CL
DROP TRIGGER T_AFT_DEL_REGL_UPD_CLIENT;

CREATE TRIGGER T_AFT_DEL_REGL_UPD_CLIENT
AFTER DELETE ON REGLEMENT_CL
REFERENCING OLD ROW AS OLD_REGL
FOR EACH ROW
    UPDATE CLIENT SET DETTE = DETTE + OLD_REGL.MONTANT
    WHERE CLIENT.ID = OLD_REGL.ID_CL; 
--~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~--