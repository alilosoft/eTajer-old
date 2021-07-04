CREATE TABLE ECHEANCE_REGL_CL    (   ID INT NOT NULL GENERATED ALWAYS AS IDENTITY,
                                    DATE_REG DATE NOT NULL,
                                    MONTANT DECIMAL(10,2) NOT NULL DEFAULT 0,-- MAX: 999,999,999
                                    REGLEE CHAR(1) NOT NULL DEFAULT 'N',-- O SI OUI, N SINON
                                    ID_VNT_CL INT NOT NULL,
                                    CONSTRAINT ECH_REG_PK PRIMARY KEY (ID),
                                    CONSTRAINT ECH_REG_VNT_FK FOREIGN KEY (ID_VNT_CL) REFERENCES VENTE_CL ON UPDATE RESTRICT ON DELETE CASCADE);
--~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~--
CREATE VIEW V_ECHEANCE AS
SELECT ECH_CL.ID, ECH_CL.DATE_REG AS "Date.Réglement", CL."NAME" AS "Client", ECH_CL.MONTANT AS "Montant.Régl(DA)", ECH_CL.REGLEE AS "Réglée?"
FROM (ECHEANCE_REGL_CL ECH_CL INNER JOIN VENTE_CL VNT_CL ON ECH_CL.ID_VNT_CL = VNT_CL.ID )INNER JOIN CLIENT CL ON VNT_CL.ID_CL = CL.ID;
--~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~--