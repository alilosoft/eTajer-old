--~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~--
--================================ CREATION ==================================--
--~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~--
DROP TABLE APP_USER;
CREATE TABLE APP_USER   (ID INT NOT NULL GENERATED ALWAYS AS IDENTITY, 
                      LOGIN VARCHAR(25) NOT NULL UNIQUE,
                      PW VARCHAR(25) NOT NULL,
                      ID_GROUP INT NOT NULL, 
                      CONSTRAINT USER_PK PRIMARY KEY (ID),
                      CONSTRAINT USER_GP_FK FOREIGN KEY (ID_GROUP) REFERENCES USER_GP ON DELETE RESTRICT);
INSERT INTO APP_USER (LOGIN, PW, ID_GROUP) VALUES ('ADMIN', '123', 1);
--~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~--
--============================== ALTERATION ==================================--
--~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~--
-- 04/10/2013
-- ALTER TABLE UTILISATEUR ADD CONSTRAINT UNIQE_LOGIN UNIQUE(LOGIN);
--~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~--
--================================ VIEWS =====================================--
--~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~--
DROP VIEW V_USER;
CREATE VIEW V_USER AS
SELECT U.ID, LOGIN AS "Utilisateur", R.DES AS "Groupe"
FROM APP_USER U INNER JOIN USER_GP R ON U.ID_GROUP = R.ID;
--~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~--
--=============================== QUERIES ====================================--
--~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~--
SELECT * FROM V_USERS;
SELECT * FROM USERS WHERE ID = ?;
INSERT INTO USERS (LOGIN, PW, ID_GROUP) VALUES (?, ?, ?);
UPDATE USERS SET LOGIN = ?, PW = ?, ID_GROUP = ? WHERE ID = ?;
DELETE FROM USERS WHERE ID = ?;
-- Get Admins_Number
SELECT COUNT(*) FROM USERS WHERE ID_GROUP = 1;
