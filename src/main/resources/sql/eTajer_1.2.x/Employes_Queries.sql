--~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~--
--================================ CREATION ==================================--
--~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~--
-- Last_Update: 21/12/2014
DROP TABLE EMPLOYE;
CREATE TABLE EMPLOYE   (ID INT NOT NULL GENERATED ALWAYS AS IDENTITY, 
                        NOM VARCHAR(40) NOT NULL,
                        PRENOM VARCHAR(40) NOT NULL,
                        TEL VARCHAR(15),
                        EMAIL VARCHAR(50),
                        D_EMBAUCHE DATE NOT NULL,
                        SALAIRE DECIMAL(8,2),
                        CONSTRAINT EMP_PK PRIMARY KEY (ID));
--~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~--
--============================== ALTERATION ==================================--
--~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~--

--~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~--
--================================ VIEWS =====================================--
--~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~--DROP VIEW V_EMPLOYE;
-- Last_Update: 21/12/2014
DROP VIEW V_EMPLOYE;
CREATE VIEW V_EMPLOYE AS
SELECT E.ID, E.NOM AS "Nom", E.PRENOM AS "Prénom", E.TEL AS "Tél", E.EMAIL AS "E-Mail", SALAIRE AS "Salaire"
FROM EMPLOYE E;
--~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~--
--=============================== QUERIES ====================================--
--~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~--
SELECT * FROM V_EMPLOYE;
SELECT * FROM EMPLOYE WHERE ID = ?;
INSERT INTO EMPLOYE (NOM, PRENOM) VALUES (?, ?);
UPDATE EMPLOYE SET NOM = ?, PRENOM = ? WHERE ID = ?;
DELETE FROM EMPLOYE WHERE ID = ?;