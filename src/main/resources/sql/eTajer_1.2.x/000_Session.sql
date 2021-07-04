--~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~--
--================================ CREATION ==================================--
--~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~--
-- Last modification: 17/11/2015
DROP TABLE SESSION; 

CREATE TABLE SESSION    (ID INT NOT NULL GENERATED ALWAYS AS IDENTITY,
                         ID_USER INT DEFAULT NULL,
                         HOST_NAME VARCHAR(30) NOT NULL,
                         "DATE" DATE DEFAULT CURRENT_DATE,
                         HEURE TIME NOT NULL DEFAULT CURRENT_TIME,
                         OPENED BOOLEAN NOT NULL DEFAULT TRUE,
                         LOCKED BOOLEAN NOT NULL DEFAULT FALSE,
                        CONSTRAINT SESS_PK PRIMARY KEY (ID),
                        CONSTRAINT SESS_USR_FK FOREIGN KEY (ID_USER) REFERENCES APP_USER ON DELETE RESTRICT);
-- Reset the ID:
ALTER TABLE SESSION ALTER COLUMN ID RESTART WITH 1;
--~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~--
--============================== ALTERATION ==================================--
--~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~--

--~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~--
--================================ VIEWS =====================================--
--~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~--
-- Modified: 17/11/2015
DROP VIEW V_SESSION;

CREATE VIEW V_SESSION AS
SELECT S.ID, ID_USER, LOGIN AS "Utilisateur", HOST_NAME AS "Poste", "DATE" AS "Date", HEURE AS "Heure", OPENED AS "Ouverte?", LOCKED AS "Verouillé"
FROM  "SESSION" S INNER JOIN APP_USER U ON S.ID_USER = U.ID 
ORDER BY "DATE", HEURE  DESC;
--~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~--
--=============================== QUERIES ====================================--
--~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~--
SELECT * FROM V_SESSION;
SELECT * FROM "SESSION" WHERE ID = ?;
INSERT INTO "SESSION" (ID_USER, HOST_NAME, "DATE", HEURE) VALUES (?, ?, ?, ?);
UPDATE "SESSION" SET OPENED = ?;
UPDATE "SESSION" SET LOCKED = ?;
DELETE FROM "SESSION" WHERE ID = ?;
--~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~--
--============================== TRIGGERS ====================================--
--~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~--

--~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~--