--~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~--
--================================ CREATION ==================================--
--~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~--
-- last modif: 13/02/2015
DROP TABLE EXPIRATION;
CREATE TABLE EXPIRATION (ID INT NOT NULL GENERATED ALWAYS AS IDENTITY,
                         DATE_EXP DATE NOT NULL,
                         DATE_ALERT DATE NOT NULL,
                         CONSTRAINT EXP_PK PRIMARY KEY(ID));
--~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~--
--============================== ALTERATION ==================================--
--~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~--

--~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~--
--================================ VIEWS =====================================--
--~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~--
-- last modif: 13/02/2015
DROP VIEW V_EXPIRE;
CREATE VIEW V_EXPIRE AS
SELECT E.ID, E.DATE_EXP AS "Date.Expir", E.DATE_ALERT AS "Date.Alert"
FROM EXPIRATION E;
--~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~--
--=============================== QUERIES ====================================--
--~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~--
SELECT * FROM V_EXPIRE;
SELECT * FROM EXPIRE WHERE ID = ?;
INSERT INTO EXPIRATION (DATE_EXP, DATE_ALERT) VALUES (?, ?);
UPDATE EXPIRATION SET DATE_EXP = ?, DATE_ALERT = ? WHERE ID = ?;
DELETE FROM EXPIRATION WHERE ID = ?;
--
SELECT ID FROM EXPIRATION WHERE DATE_EXP = ? AND DATE_ALERT = ?;
--~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~--