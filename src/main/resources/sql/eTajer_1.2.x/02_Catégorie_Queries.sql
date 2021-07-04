--~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~--
--================================ CREATION ==================================--
--~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~--
CREATE TABLE CATEGORIE   ( ID INT NOT NULL GENERATED ALWAYS AS IDENTITY,
                           DES  VARCHAR(50) NOT NULL UNIQUE,
                           ID_FAM INT DEFAULT NULL,
                           CONSTRAINT CAT_PK PRIMARY KEY(ID),
                           CONSTRAINT CAT_FAM_FK FOREIGN KEY(ID_FAM) REFERENCES FAMILLE ON DELETE RESTRICT);
--~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~--
--============================== ALTERATION ==================================--
--~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~--
--04/10/2013
ALTER TABLE CATEGORIE DROP CONSTRAINT CAT_FAM_FK;
ALTER TABLE CATEGORIE ADD CONSTRAINT CAT_FAM_FK FOREIGN KEY(ID_FAM) REFERENCES FAMILLE ON DELETE RESTRICT;
--01/05/2015
ALTER TABLE CATEGORIE DROP COLUMN ID_FAM;
ALTER TABLE CATEGORIE ADD COLUMN ID_FAM INT DEFAULT NULL;
ALTER TABLE CATEGORIE ADD CONSTRAINT CAT_FAM_FK FOREIGN KEY(ID_FAM) REFERENCES FAMILLE ON DELETE RESTRICT;
--
ALTER TABLE CATEGORIE ADD CONSTRAINT UNIQ_CATEG UNIQUE(DES);
--~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~--
--================================ VIEWS =====================================--
--~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~--
CREATE VIEW V_CATEGORIE (ID, ID_FAM, "Désignation") AS
SELECT ID, ID_FAM, DES FROM CATEGORIE;
--~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~--
--=============================== QUERIES ====================================--
--~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~--
SELECT * FROM V_CATEGORIE;
SELECT * FROM CATEGORIE WHERE ID = ?;
INSERT INTO CATEGORIE (DES, ID_FAM) VALUES (?, ?);
UPDATE CATEGORIE SET DES = ?, ID_FAM = ? WHERE ID = ?;
DELETE FROM CATEGORIE WHERE ID = ?;
--~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~--