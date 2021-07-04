--~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~--
--================================ CREATION ==================================--
--~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~--
CREATE TABLE FAMILLE    (ID INT NOT NULL GENERATED ALWAYS AS IDENTITY, 
                         DES VARCHAR(50) NOT NULL UNIQUE, 
                         TVA SMALLINT DEFAULT 0,
                         SERVICE BOOLEAN NOT NULL DEFAULT FALSE,
                         CONSTRAINT FAM_PK PRIMARY KEY (ID));
--~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~--
--============================== ALTERATION ==================================--
--~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~--
-- 01/05/2015
ALTER TABLE FAMILLE ADD COLUMN SERVICE BOOLEAN NOT NULL DEFAULT FALSE;
ALTER TABLE FAMILLE ADD CONSTRAINT UNIQ_FAM UNIQUE(DES);
--~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~--
--================================ VIEWS =====================================--
--~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~--
--24-03-2017
DROP VIEW V_FAMILLE;
CREATE VIEW V_FAMILLE (ID, "Désignation", "TVA %") AS
SELECT ID, DES, TVA FROM FAMILLE ;
--~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~--
--=============================== QUERIES ====================================--
--~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~--
SELECT * FROM V_FAMILLE;
SELECT * FROM FAMILLE WHERE ID = ?;
INSERT INTO FAMILLE (DES, TVA, SERVICE) VALUES (?, ?, ?);
UPDATE FAMILLE SET DES = ?, TVA = ?, SERVICE = ? WHERE ID = ?;
DELETE FROM FAMILLE WHERE ID = ?;
--~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~--