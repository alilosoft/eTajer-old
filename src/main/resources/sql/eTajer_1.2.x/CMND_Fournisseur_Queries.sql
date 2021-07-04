CREATE TABLE CMND_FOUR (    ID INT NOT NULL GENERATED ALWAYS AS IDENTITY,
                            NUM CHAR(3)NOT NULL,
                            "DATE" DATE NOT NULL DEFAULT CURRENT DATE,
                            TOTAL DECIMAL(10,2) NOT NULL DEFAULT 0,-- MAX: 999,999,999
                            VALIDEE CHAR(1) NOT NULL DEFAULT 'N',-- O POUR OUI, N POUR NON
                            ID_FOUR INT NOT NULL,
                            CONSTRAINT CMNDFR_PK PRIMARY KEY (ID),
                            CONSTRAINT CMNDFR_FOUR_FK FOREIGN KEY (ID_FOUR) REFERENCES FOURNISSEUR ON UPDATE RESTRICT ON DELETE RESTRICT);
--~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~--
CREATE TABLE LIGNE_CMND_FR  (   ID INT NOT NULL GENERATED ALWAYS AS IDENTITY,
                                ID_CMND  INT NOT NULL,
                                ID_PROD INT NOT NULL,
                                PU_ACHAT DECIMAL(10,2) NOT NULL DEFAULT 0,-- MAX: 999,999,999
                                QTE_CMND INT NOT NULL DEFAULT 0,
                                UNITE_ACHAT INT NOT NULL,
                                CONSTRAINT L_CMND_FR_PK PRIMARY KEY(ID),
                                CONSTRAINT L_CMND_FR_CMND_FR_FK FOREIGN KEY (ID_CMND) REFERENCES CMND_FOUR ON UPDATE RESTRICT ON DELETE CASCADE,
                                CONSTRAINT L_CMND_FR_PROD_FK FOREIGN KEY (ID_PROD) REFERENCES PRODUIT ON UPDATE RESTRICT ON DELETE CASCADE,
                                CONSTRAINT L_CMND_FR_EMB_FK FOREIGN KEY (UNITE_ACHAT) REFERENCES EMBALLAGE ON UPDATE RESTRICT ON DELETE CASCADE);
--~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~--
