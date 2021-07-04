--~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~--
--================================ CREATION ==================================--
--~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~--
-- Last modif: 01/02/2015 
DROP TABLE DEPOT;
CREATE TABLE DEPOT (ID INTEGER NOT NULL GENERATED ALWAYS AS IDENTITY,
                    ADR VARCHAR(50) NOT NULL UNIQUE,
                    DE_VENTE BOOLEAN NOT NULL DEFAULT TRUE,
                    DE_RESERVE BOOLEAN NOT NULL DEFAULT FALSE,
                    DE_STOCKAGE BOOLEAN NOT NULL DEFAULT FALSE,
                    DE_PERTES BOOLEAN NOT NULL DEFAULT FALSE,
                    DE_CMND_CL BOOLEAN NOT NULL DEFAULT FALSE,
                    DE_CMND_FR BOOLEAN NOT NULL DEFAULT FALSE,
                    PRIMARY KEY (ID));
INSERT INTO DEPOT (ADR) VALUES ('EL-ABADIA');
--~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~--
--============================== ALTERATION ==================================--
--~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~--
--23/05/2015
ALTER TABLE DEPOT ADD CONSTRAINT UNIQUE_ADR UNIQUE(ADR);
--~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~--
--================================ VIEWS =====================================--
--~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~--
-- Last modif: 31/01/2015
CREATE VIEW V_DEPOT (ID, "Adresse", "De.Vente", "De.Réserve", "De.Stockage") AS
SELECT ID, ADR, DE_VENTE, DE_RESERVE, DE_STOCKAGE FROM DEPOT
WHERE DE_PERTES = FALSE AND DE_CMND_CL = FALSE AND DE_CMND_FR = FALSE;
--~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~--
--=============================== QUERIES ====================================--
--~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~--
SELECT * FROM V_DEPOT;
SELECT * FROM DEPOT WHERE ID = ?;
SELECT * FROM DEPOT WHERE DE_VENTE = TRUE;
SELECT * FROM DEPOT WHERE DE_RESERVE = TRUE;
SELECT * FROM DEPOT WHERE DE_COMMANDE = TRUE;

INSERT INTO DEPOT (ADR, DE_VENTE, DE_RESERVE, DE_COMMANDE) VALUES (?, ?, ?, ?);
UPDATE  DEPOT SET ADR = ?, DE_VENTE = ?, DE_RESERVE = ?, DE_COMMANDE = ?  WHERE ID = ?;
DELETE FROM DEPOT WHERE ID = ?;
/--~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~--