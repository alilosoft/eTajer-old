CREATE TABLE VENTE_CL ( ID INT NOT NULL GENERATED ALWAYS AS IDENTITY,
                        ID_CL INT  NOT NULL,
                        ID_VNT INT NOT NULL,
                        TOTAL_VERSS DECIMAL(10,2) NOT NULL DEFAULT 0,-- MAX: 999,999,999
                        CONSTRAINT VNTCL_PK PRIMARY KEY (ID),
                        CONSTRAINT VNTCL_CL_FK FOREIGN KEY (ID_CL) REFERENCES CLIENT ON UPDATE RESTRICT ON DELETE RESTRICT,
                        CONSTRAINT VNTCL_VNT_FK FOREIGN KEY (ID_VNT) REFERENCES VENTE ON UPDATE RESTRICT ON DELETE CASCADE);
--~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~--
-- 17/03/2013 --
-- ALTER TABLE VENTE_CL DROP COLUMN REGLEE; -- REMOVE THIS COLUMN BECAUSE THIS INFO WILL be deducted from total vnt and total varsement
--~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~--
-- Last Modification: 27/10/2013
DROP VIEW V_VENTE_CL;
CREATE VIEW V_VENTE_CL AS
SELECT VNT.ID, VNT_CL.ID AS "ID_VNT_CL",  CL.ID AS "ID_CL", VNT.NUM AS "N°", VNT."DATE" AS "Date.Vente", VNT.HEURE AS "Heure", CL."NAME" AS "Client", VNT.TYPE_VNT AS "Mode", 
        VNT.TOTAL AS "Total(DA)", VNT_CL.TOTAL_VERSS AS "Versé(DA)",
        VNT.VALIDE = 'Y' AS "Validé?", (VNT.TOTAL <= VNT_CL.TOTAL_VERSS) AS "Réglé?"
FROM  (VENTE VNT INNER JOIN (VENTE_CL VNT_CL INNER JOIN CLIENT CL 
                                             ON VNT_CL.ID_CL = CL.ID )
                ON VNT.ID = VNT_CL.ID_VNT)
ORDER BY VNT."DATE" DESC;
--~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~--
SELECT * FROM V_VENTE_CL_ONLY;
SELECT * FROM VENTE_CL WHERE ID = ?;
INSERT INTO VENTE_CL (ID_CL, ID_VNT) VALUES (?, ?);
UPDATE VENTE_CL SET ID_CL = ?, ID_VNT = ? WHERE ID = ?;
DELETE FROM VENTE_CL WHERE ID = ?;
--~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~--
-- Get Vente_CL of Vente --
SELECT * FROM VENTE_CL WHERE ID_VNT = ?;
-- Get total crédit d'un joure
SELECT SUM(V.TOTAL - VCL.TOTAL_VERSS) FROM VENTE_CL VCL INNER JOIN VENTE V ON V.VALIDE = 'Y' AND VCL.ID_VNT = V.ID WHERE V."DATE" >= ? AND V."DATE" <= ?;