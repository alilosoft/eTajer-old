--~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~--
--================================ CREATION ==================================--
--~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~--
-- Last_Update: 25/12/2015
CREATE TABLE ALERTE_EXP ( ID  INT NOT NULL GENERATED ALWAYS AS IDENTITY,
                          ID_LOT INT NOT NULL,
                          DATE_ALERT DATE NOT NULL DEFAULT DATE(2932897),
                          CONSTRAINT ALERT_EXP_PK PRIMARY KEY (ID),
                          CONSTRAINT ALERT_LOT_FK FOREIGN KEY (ID_LOT) REFERENCES EN_STOCK ON DELETE CASCADE);
--~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~--
--============================== ALTERATION ==================================--
--~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~--

--~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~--
--================================ VIEWS =====================================--
--~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~--
-- Last_Update: 25/11/2015
DROP VIEW V_ALERT_EXP;

CREATE VIEW V_ALERT_EXP AS
SELECT A.ID, A.ID_LOT, L.ID_PROD, P.DES AS "Produit", A.DATE_ALERT AS "Date.Alert", L.DATE_EXP AS "Date.Exp"  
FROM EN_STOCK L INNER JOIN ALERTE_EXP A ON A.ID_LOT = L.ID INNER JOIN PRODUIT P ON L.ID_PROD = P.ID;
--~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~--
--=============================== QUERIES ====================================--
--~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~--
SELECT * FROM V_ALERT_EXP;
SELECT * FROM ALERTE_EXP WHERE ID = ?;

INSERT INTO ALERTE_EXP(ID_LOT, DATE_ALERT)  VALUES (?, ?);
UPDATE ALERTE_EXP SET ID_LOT = ?, DATE_ALERT = ? WHERE ID = ?;
DELETE FROM ALERTE_EXP WHERE ID = ?;
--~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~--
--============================== TRIGGERS ====================================--
--~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~--

--~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~--