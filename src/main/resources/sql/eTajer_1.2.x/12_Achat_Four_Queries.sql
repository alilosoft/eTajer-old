CREATE TABLE ACHAT_FOUR     (   ID INT NOT NULL GENERATED ALWAYS AS IDENTITY,
                                ID_FOUR INT  NOT NULL,
                                ID_ACHAT INT NOT NULL,
                                TOTAL_VERSS DECIMAL(10,2) NOT NULL DEFAULT 0,-- MAX: 999,999,999
                                CONSTRAINT ACH_FOUR_PK PRIMARY KEY (ID),
                                CONSTRAINT ACH_FOUR_FOUR_FK FOREIGN KEY (ID_FOUR) REFERENCES FOURNISSEUR ON UPDATE RESTRICT ON DELETE RESTRICT,
                                CONSTRAINT ACH_FOUR_ACH_FK FOREIGN KEY (ID_ACHAT) REFERENCES ACHAT ON UPDATE RESTRICT ON DELETE CASCADE);
--~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~--
-- TABLE ALTERATIONS
-- 19/03/2013 
ALTER TABLE ACHAT_FOUR DROP COLUMN REGLEE;
-- 04/10/2013
-- Constraint alteration (replace: ON DELETE CASCADE by ON DELETE RESTRICT)
ALTER TABLE ACHAT_FOUR DROP CONSTRAINT ACH_FOUR_FOUR_FK;
ALTER TABLE ACHAT_FOUR ADD CONSTRAINT ACH_FOUR_FOUR_FK FOREIGN KEY (ID_FOUR) REFERENCES FOURNISSEUR ON UPDATE RESTRICT ON DELETE RESTRICT;
--~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~--
-- Last modification: 12/11/2013
DROP VIEW V_ACHAT_FOUR_ONLY;
CREATE VIEW V_ACHAT_FOUR_ONLY AS
SELECT ACH.ID, ACH_FR.ID AS "ID_ACH_FOUR", FOUR.ID AS "ID_FOUR",ACH.NUM AS "N°", ACH."DATE" AS "Date.Achat", FOUR."NAME" AS "Fournisseur", 
    ACH.TOTAL AS "Total.(DA)", ACH_FR.TOTAL_VERSS AS "Montant.Versé(DA)", 
    ACH.VALIDE = 'Y' AS "Validé?", ((ACH.TOTAL <= ACH_FR.TOTAL_VERSS) OR (ACH.VALIDE = 'Y' AND FOUR."NAME" IS NULL)) AS "Réglé?"
FROM ACHAT ACH INNER JOIN (ACHAT_FOUR ACH_FR INNER JOIN FOURNISSEUR FOUR 
                                              ON ACH_FR.ID_FOUR = FOUR.ID)
                ON ACH.ID = ACH_FR.ID_ACHAT
ORDER BY ACH."DATE" DESC;
--~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~--
SELECT * FROM V_ACHAT_FOUR_ONLY;
SELECT * FROM ACHAT_FOUR WHERE ID = ?;
INSERT INTO ACHAT_FOUR (ID_FOUR, ID_ACHAT) VALUES (?, ?);
UPDATE ACHAT_FOUR SET ID_FOUR = ?, ID_ACHAT = ? WHERE ID = ?;
DELETE FROM ACHAT_FOUR WHERE ID = ?;
--~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~--
-- Get Achat_Four Of Achat --
SELECT * FROM ACHAT_FOUR WHERE ID_ACHAT = ?;
--~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~--
