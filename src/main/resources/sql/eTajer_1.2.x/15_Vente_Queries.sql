
--~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~--
--================================ CREATION ==================================--
--~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~--
-- Last modification: 11/11/2014
DROP TABLE VENTE;

CREATE TABLE VENTE  (ID INT NOT NULL GENERATED ALWAYS AS IDENTITY,
                     ID_CL INT DEFAULT NULL,
                     ID_TYPE INT NOT NULL, 
                     NUM INT  NOT NULL,
                     "DATE" DATE DEFAULT CURRENT_DATE,
                     HEURE TIME NOT NULL DEFAULT CURRENT_TIME,
                     VALIDEE BOOLEAN NOT NULL DEFAULT FALSE,-- 'Y' si la vente est validé, 'N' sinon.
                     TOTAL DECIMAL(12,2) NOT NULL DEFAULT 0,-- MAX: 999,999,999
                     ID_USER INT NOT NULL,
                     COMMAND BOOLEAN NOT NULL DEFAULT TRUE,
                     RESERV BOOLEAN NOT NULL DEFAULT FALSE,
                     LIVRAIS BOOLEAN NOT NULL DEFAULT FALSE,
                     RETOUR BOOLEAN NOT NULL DEFAULT FALSE,
                     CONSTRAINT VNT_PK PRIMARY KEY (ID),
                     CONSTRAINT VNT_CL_FK FOREIGN KEY (ID_CL) REFERENCES CLIENT ON DELETE RESTRICT,
                     CONSTRAINT VNT_TYPE_FK FOREIGN KEY (ID_TYPE) REFERENCES TYPE_VNT ON DELETE RESTRICT,
                     CONSTRAINT VNT_USER_FK FOREIGN KEY (ID_USER) REFERENCES APP_USER ON DELETE RESTRICT);
-- Reset the ID:
ALTER TABLE VENTE ALTER COLUMN ID RESTART WITH 1;
--~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~--
--============================== ALTERATION ==================================--
--~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~--
-- 23/11/2015;
ALTER TABLE VENTE ADD COLUMN RETOUR BOOLEAN NOT NULL DEFAULT FALSE;
-- 16/11/2015
ALTER TABLE VENTE ADD COLUMN ID_USER INT NOT NULL DEFAULT 1; 
ALTER TABLE VENTE ADD CONSTRAINT VNT_USER_FK FOREIGN KEY (ID_USER) REFERENCES APP_USER ON DELETE RESTRICT;
-- 13/05/2015
ALTER TABLE VENTE ALTER COLUMN COMMAND SET DEFAULT TRUE;
--
ALTER TABLE VENTE ADD COLUMN COMMAND BOOLEAN NOT NULL DEFAULT TRUE;
ALTER TABLE VENTE ADD COLUMN RESERV BOOLEAN NOT NULL DEFAULT FALSE;
ALTER TABLE VENTE ADD COLUMN LIVRAIS BOOLEAN NOT NULL DEFAULT FALSE;
-- 11/11/2014
ALTER TABLE VENTE DROP COLUMN VALIDE;
ALTER TABLE VENTE ADD COLUMN VALIDEE BOOLEAN NOT NULL DEFAULT FALSE;
--~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~--
--================================ VIEWS =====================================--
--~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~--
-- 10/05/2016;
DROP VIEW V_VENTE_ALL;

CREATE VIEW V_VENTE_ALL AS
SELECT VNT.ID, VNT.NUM AS "N°", VNT."DATE" AS "Date", VNT.HEURE AS "Heure", VNT.TOTAL AS "Total(DA)", VNT.VALIDEE AS "Livrée?"
FROM  VENTE VNT
WHERE VNT."DATE" = current_date
ORDER BY VNT."DATE" DESC, VNT.HEURE DESC;

-- backup
SELECT VNT.ID, CL.ID AS "ID_CL", VNT.ID_USER, VNT.NUM AS "N°", VNT."DATE" AS "Date", VNT.HEURE AS "Heure", CL.NOM AS "Client", 
    VNT.TOTAL AS "Total(DA)", VNT.VALIDEE AS "Livrée?", U.LOGIN AS "Vendeur"
FROM  VENTE VNT LEFT JOIN CLIENT CL ON VNT.ID_CL = CL.ID
                INNER JOIN APP_USER U ON VNT.ID_USER = U.ID
WHERE VNT."DATE" = current_date
ORDER BY VNT."DATE" DESC, VNT.HEURE DESC;
OFFSET 100 ROWS 
FETCH NEXT 100 ROWS ONLY;
--=========================
DROP VIEW V_VENTE_CL;

CREATE VIEW V_VENTE_CL AS
SELECT * FROM V_VENTE_ALL WHERE ID_CL IS NOT NULL;
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
REFERENCING NEW AS NEW_VNT
FOR EACH ROW
    UPDATE LIGNE_VNT LVNT 
    SET LVNT.VALIDEE = NEW_VNT.VALIDEE
    WHERE LVNT.ID_VNT = NEW_VNT.ID;
--~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~--
-- 13/05/2015
-- Update Client.Dette after validating 'Vente'
DROP TRIGGER T_AFT_VALIDATE_VNT_UPD_CREDIT_CL;

CREATE TRIGGER T_AFT_VALIDATE_VNT_UPD_CREDIT_CL
AFTER UPDATE OF VALIDEE ON VENTE
REFERENCING NEW AS NEW_VNT
FOR EACH ROW
    UPDATE CLIENT SET DETTE = DETTE + NEW_VNT.TOTAL
    WHERE NEW_VNT.VALIDEE = TRUE AND CLIENT.ID = NEW_VNT.ID_CL;
--~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~--
-- 13/05/2015
-- Update Client.Dette after in-validating 'Vente'
DROP TRIGGER T_AFT_INVALIDATE_VNT_UPD_CREDIT_CL;

CREATE TRIGGER T_AFT_INVALIDATE_VNT_UPD_CREDIT_CL
AFTER UPDATE OF VALIDEE ON VENTE
REFERENCING NEW AS NEW_NEW_VNT
FOR EACH ROW
    UPDATE CLIENT SET DETTE = DETTE - NEW_VNT.TOTAL
    WHERE NEW_VNT.VALIDEE = FALSE AND CLIENT.ID = NEW_VNT.ID_CL;
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
-- 13/05/2015 => droped!
-- Delete CREDIT_CL after invalidate vente
DROP TRIGGER T_AFT_UPD_VALID_VNT_DEL_CREDIT;
-- Droped!
CREATE TRIGGER T_AFT_UPD_VALID_VNT_DEL_CREDIT
AFTER UPDATE OF VALIDEE ON VENTE
REFERENCING NEW AS NEW_VNT
FOR EACH ROW
    DELETE FROM CREDIT_CL
    WHERE NEW_VNT.VALIDEE = FALSE 
    AND ID_VNT = NEW_VNT.ID;
--~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~--
-- Maintenance
select * from VENTE  
where 
     --id >= 120697 and 
"DATE" = '2015-11-20'
order by id;

select * from VENTE  
where 
     --id >= 120697 and 
"DATE" = '2016-09-30'
order by id;

update VENTE set "DATE" = '2016-09-28' where id > 356 and "DATE" = '2015-11-21';
update VENTE set "DATE" = '2016-09-29' where "DATE" = '2015-11-19';
update VENTE set "DATE" = '2016-10-01' where id >= 120697 and "DATE" = '2015-11-20';
update VENTE set "DATE" = '2016-09-30' where id > 57  and "DATE" = '2015-11-20';



