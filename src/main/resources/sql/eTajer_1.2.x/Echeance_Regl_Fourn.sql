CREATE TABLE ECHEANCE_REGL_FOUR    (   ID INT NOT NULL GENERATED ALWAYS AS IDENTITY,
                                    DATE_REG DATE NOT NULL,
                                    MONTANT DECIMAL(10,2) NOT NULL DEFAULT 0,-- MAX: 999,999,999
                                    REGLEE CHAR(1) NOT NULL DEFAULT 'N',-- O SI OUI, N SINON
                                    ID_ACH_FR INT NOT NULL,
                                    CONSTRAINT ECH_PK PRIMARY KEY (ID),
                                    CONSTRAINT ECH_REG_ACH_FK FOREIGN KEY (ID_ACH_FR) REFERENCES ACHAT_FOUR ON UPDATE RESTRICT ON DELETE CASCADE);
--~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~--