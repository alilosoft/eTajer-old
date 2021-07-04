--~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~--
--================================ CREATION ==================================--
--~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~--
-- Last_Update: 07/10/2014
DROP TABLE QUANTIFIER;
CREATE TABLE QUANTIFIER  (ID  INT NOT NULL GENERATED ALWAYS AS IDENTITY,
                          ID_PROD INT NOT NULL,
                          ID_UNITE INT NOT NULL,
                          UNITE_DT BOOLEAN NOT NULL DEFAULT FALSE,
                          UNITE_GR BOOLEAN NOT NULL DEFAULT FALSE,
                          UNITE_DGR BOOLEAN NOT NULL DEFAULT FALSE,
                          UNITE_DGR BOOLEAN NOT NULL DEFAULT FALSE,
                          CONSTRAINT QUANTIF_PK PRIMARY KEY (ID),
                          CONSTRAINT QUANTIF_PROD_FK FOREIGN KEY (ID_PROD) REFERENCES PRODUIT ON DELETE CASCADE,
                          CONSTRAINT QUANTIF_UNIT_FK FOREIGN KEY (ID_UNITE) REFERENCES UNITE ON DELETE RESTRICT);
--~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~--
--============================== ALTERATION ==================================--
--~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~--
-- 08/11/2015;
ALTER TABLE QUANTIFIER ADD COLUMN UNITE_DT BOOLEAN NOT NULL DEFAULT FALSE;
ALTER TABLE QUANTIFIER ADD COLUMN UNITE_GR BOOLEAN NOT NULL DEFAULT FALSE;
ALTER TABLE QUANTIFIER ADD COLUMN UNITE_DGR BOOLEAN NOT NULL DEFAULT FALSE;
ALTER TABLE QUANTIFIER ADD COLUMN UNITE_SGR BOOLEAN NOT NULL DEFAULT FALSE;
-- 26/05/2015;
ALTER TABLE QUANTIFIER DROP COLUMN UNITAIRE;
ALTER TABLE QUANTIFIER DROP COLUMN COLISAGE;
ALTER TABLE QUANTIFIER DROP COLUMN MORCELLEMENT;
-- 04/10/2013
--ALTER TABLE QUANTIFIER DROP CONSTRAINT PACKED_PROD_FK;
--ALTER TABLE QUANTIFIER DROP CONSTRAINT QUANT_EMB_FK;
--ALTER TABLE QUANTIFIER ADD CONSTRAINT QUANTIF_PROD_FK FOREIGN KEY (ID_PROD) REFERENCES PRODUIT ON UPDATE RESTRICT ON DELETE CASCADE;
--ALTER TABLE QUANTIFIER ADD CONSTRAINT QUANTIF_EMB_FK FOREIGN KEY (ID_EMB) REFERENCES EMBALLAGE ON UPDATE RESTRICT ON DELETE RESTRICT;
--~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~--
--================================ VIEWS =====================================--
--~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~--
DROP VIEW V_QUANTIFIER;
CREATE VIEW V_QUANTIFIER AS
SELECT Q.ID, Q.ID_PROD, Q.ID_UNITE, P.DES AS "Produit", U.DES AS "Unité", U.QTE_COLIS AS "Qte.Colis", U.QTE_MORCE AS "Morceaux.Par.U"
FROM (QUANTIFIER Q INNER JOIN PRODUIT P ON P.ID = Q.ID_PROD) INNER JOIN UNITE U ON U.ID = Q.ID_UNITE;
--~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~--
--=============================== QUERIES ====================================--
--~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~--
SELECT * FROM V_QUANTIFIER;
SELECT * FROM QUANTIFIER WHERE ID = ?;
SELECT * FROM QUANTIFIER WHERE ID_PROD = ?;
INSERT INTO QUANTIFIER(ID_PROD, ID_UNITE)  VALUES (?, ?);
UPDATE QUANTIFIER SET ID_PROD = ?, ID_UNITE = ? WHERE ID = ?;
DELETE FROM QUANTIFIER WHERE ID = ?;
DELETE FROM QUANTIFIER WHERE ID_PROD = ?;
--~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~--
--============================== TRIGGERS ====================================--
--~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~--
