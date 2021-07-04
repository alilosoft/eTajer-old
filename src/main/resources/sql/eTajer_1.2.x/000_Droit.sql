--~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~--
--================================ CREATION ==================================--
--~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~--
DROP TABLE DROIT;
CREATE TABLE DROIT (ID INT NOT NULL GENERATED ALWAYS AS IDENTITY,
                    ID_GROUP INT NOT NULL,
                    OBJET VARCHAR(100) NOT NULL,
                    CAN_VIEW BOOLEAN NOT NULL DEFAULT FALSE,
                    CAN_INSERT BOOLEAN NOT NULL DEFAULT FALSE,
                    CAN_EDIT BOOLEAN NOT NULL DEFAULT FALSE,
                    CAN_DELETE BOOLEAN NOT NULL DEFAULT FALSE,
                    CONSTRAINT DROIT_PK PRIMARY KEY (ID),
                    CONSTRAINT DROIT_GP_FK FOREIGN KEY (ID_GROUP) REFERENCES USER_GP ON DELETE CASCADE
                    );
--~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~--
--============================== ALTERATION ==================================--
--~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~--

--~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~--
--================================ VIEWS =====================================--
--~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~--
DROP VIEW V_DROIT;
CREATE VIEW V_DROIT AS
SELECT D.ID, D.ID_GROUP, R.DES AS "Groupe", D.OBJET AS "Objet", D.CAN_VIEW AS "Voire", D.CAN_INSERT AS "Ajout", D.CAN_EDIT AS "Modif.", D.CAN_DELETE AS "Suppr."
FROM DROIT D INNER JOIN USER_GP R ON D.ID_GROUP = R.ID;
--~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~--
--=============================== QUERIES ====================================--
--~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~--
SELECT * FROM V_DROIT;
SELECT * FROM DROIT WHERE ID = ?;
INSERT INTO DROIT (ID_GROUP, OBJET, CAN_VIEW, CAN_INSERT, CAN_EDIT, CAN_DELETE) VALUES (?, ?, ?, ?, ?, ?);
UPDATE DROIT SET ID_GROUP = ?, OBJET = ?, CAN_VIEW = ?, CAN_INSERT = ?, CAN_EDIT = ?, CAN_DELETE = ? WHERE ID = ?;
DELETE FROM DROIT WHERE ID = ?;
