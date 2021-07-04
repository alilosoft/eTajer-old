--~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~--
--================================ CREATION ==================================--
--~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~--
-- Last modification: 15/11/2014.
DROP TABLE CREDIT_CL;

CREATE TABLE CREDIT_CL     (ID INT NOT NULL GENERATED ALWAYS AS IDENTITY,
                            ID_CL INT NOT NULL,
                            ID_VNT INT DEFAULT NULL,
                            "DATE" DATE DEFAULT CURRENT_DATE,
                            HEURE TIME NOT NULL DEFAULT CURRENT_TIME,
                            MONTANT DECIMAL(12,2) NOT NULL DEFAULT 0,-- MAX: 999,999,999
                            COMMENT VARCHAR(200),
                            INITIAL BOOLEAN NOT NULL DEFAULT FALSE,
                            CONSTRAINT CREDITCL_PK PRIMARY KEY (ID),
                            CONSTRAINT CREDIT_CL_FK FOREIGN KEY (ID_CL) REFERENCES CLIENT ON DELETE RESTRICT,
                            CONSTRAINT CREDIT_VNT_FK FOREIGN KEY (ID_VNT) REFERENCES VENTE ON DELETE CASCADE);
--~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~--
--============================== ALTERATION ==================================--
--~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~--
-- 02/01/2015
ALTER TABLE CREDIT_CL ADD COLUMN INITIAL BOOLEAN NOT NULL DEFAULT FALSE;
--~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~--
--================================ VIEWS =====================================--
--~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~--
-- Last modification: 12/05/2015.
DROP VIEW V_CREDIT_CL;

CREATE VIEW V_CREDIT_CL AS
SELECT CRED.ID, CRED.ID_CL, CRED.ID_VNT, CL.NOM AS "Client", CRED.MONTANT AS "Montant(DA)", 
        CRED."DATE" AS "Date", CRED.HEURE AS "Heure", COMMENT AS "Commentaire"  
FROM CREDIT_CL CRED INNER JOIN CLIENT CL ON CRED.ID_VNT IS NULL AND CRED.ID_CL = CL.ID 
ORDER BY "Date" DESC, "Heure" DESC;
--~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~--
--=============================== QUERIES ====================================--
--~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~--
SELECT * FROM V_CREDIT_CL;
SELECT * FROM CREDIT_CL WHERE ID = ?;
INSERT INTO CREDIT_CL (ID_CL, ID_VNT, "DATE", HEURE, MONTANT, COMMENT, INITIAL) VALUES (?, ?, ?, ?, ?, ?, ?);
UPDATE CREDIT_CL SET ID_CL = ?, ID_VNT = ?, "DATE" = ?, HEURE = ?, MONTANT = ?, COMMENT = ? WHERE ID = ?;
DELETE FROM CREDIT_CL WHERE ID = ?;
--~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~--
--============================== TRIGGERS ====================================--
--~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~--
-- 15/11/2014
-- Update CLIENT.DETTE after insert new CREDIT_CL
DROP TRIGGER T_AFT_INS_CREDIT_UPD_CLIENT;

CREATE TRIGGER T_AFT_INS_CREDIT_UPD_CLIENT
AFTER INSERT ON CREDIT_CL
REFERENCING NEW ROW AS NEW_CREDIT
FOR EACH ROW
    UPDATE CLIENT SET DETTE = DETTE + NEW_CREDIT.MONTANT
    WHERE CLIENT.ID = NEW_CREDIT.ID_CL;
--~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~--
-- 15/11/2014
-- Update old CLIENT.DETTE after update CREDIT_CL
DROP TRIGGER T_AFT_UPD_CREDIT_UPD_OLD_CLIENT;

CREATE TRIGGER T_AFT_UPD_CREDIT_UPD_OLD_CLIENT
AFTER UPDATE ON CREDIT_CL
REFERENCING OLD ROW AS OLD_CREDIT
FOR EACH ROW
    UPDATE CLIENT SET DETTE = DETTE - OLD_CREDIT.MONTANT 
    WHERE ID = OLD_CREDIT.ID_CL;
-- Update new CLIENT.DETTE after update CREDIT_CL
DROP TRIGGER T_AFT_UPD_CREDIT_UPD_NEW_CLIENT;

CREATE TRIGGER T_AFT_UPD_CREDIT_UPD_NEW_CLIENT
AFTER UPDATE ON CREDIT_CL
REFERENCING NEW ROW AS NEW_CREDIT
FOR EACH ROW
    UPDATE CLIENT SET DETTE = DETTE + NEW_CREDIT.MONTANT
    WHERE ID = NEW_CREDIT.ID_CL;
--~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~--
-- 15/11/2014
-- Update CLIENT.DETTE after delete CREDIT_CL
DROP TRIGGER T_AFT_DEL_CREDIT_UPD_CLIENT;

CREATE TRIGGER T_AFT_DEL_CREDIT_UPD_CLIENT
AFTER DELETE ON CREDIT_CL
REFERENCING OLD ROW AS OLD_CREDIT
FOR EACH ROW
    UPDATE CLIENT SET DETTE = DETTE - OLD_CREDIT.MONTANT
    WHERE CLIENT.ID = OLD_CREDIT.ID_CL;
--~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~--    