--~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~--
--================================ CREATION ==================================--
--~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~--
DROP TABLE UNITE;
CREATE TABLE UNITE      (   ID  INT NOT NULL GENERATED ALWAYS AS IDENTITY,
                            DES VARCHAR(30) NOT NULL UNIQUE,
                            QTE DOUBLE NOT NULL DEFAULT 0,
                            CONSTRAINT UNITE_PK PRIMARY KEY (ID));
--~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~--
--================================ VIEWS =====================================--
--~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~--
DROP VIEW V_UNITE;
CREATE VIEW V_UNITE AS
SELECT ID, DES AS "Désignation", QTE AS "Quantité"
FROM UNITE;
--~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~--
--=============================== QUERIES ====================================--
--~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~--
SELECT * FROM V_UNITE; -- getAll
SELECT * FROM UNITE WHERE ID = ?;

INSERT INTO UNITE(DES, QTE)  VALUES (?, ?);
UPDATE UNITE SET DES = ?, QTE = ? WHERE ID = ?;
DELETE FROM UNITE WHERE ID = ?;
--~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~--
--============================== TRIGGERS ====================================--
--~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~--

--~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~--
--============================== ALTERATION ==================================--
--~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~--
-- 07/04/2016
ALTER TABLE UNITE ADD COLUMN QTE DOUBLE NOT NULL DEFAULT 0;
UPDATE UNITE SET QTE = QTE_COLIS/QTE_MORCE;
ALTER TABLE UNITE DROP COLUMN QTE_COLIS;
ALTER TABLE UNITE DROP COLUMN QTE_MORCE;
-- 20/11/2015
ALTER TABLE UNITE ADD COLUMN QTE_COLIS_TMP DOUBLE;
UPDATE UNITE SET QTE_COLIS_TMP = QTE_COLIS;
ALTER TABLE UNITE DROP COLUMN QTE_COLIS;
ALTER TABLE UNITE ADD COLUMN QTE_COLIS DOUBLE NOT NULL DEFAULT 0;
UPDATE UNITE SET QTE_COLIS = QTE_COLIS_TMP;
ALTER TABLE UNITE DROP COLUMN QTE_COLIS_TMP;