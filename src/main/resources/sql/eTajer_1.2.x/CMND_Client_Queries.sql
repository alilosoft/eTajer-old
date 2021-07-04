CREATE TABLE CMND_CLIENT (  ID INT NOT NULL GENERATED ALWAYS AS IDENTITY,
                            NUM CHAR(3)NOT NULL,
                            "DATE" DATE NOT NULL DEFAULT CURRENT DATE,
                            TOTAL DECIMAL(10,2) NOT NULL DEFAULT 0,-- MAX: 999,999,999
                            VALIDEE CHAR(1) NOT NULL DEFAULT 'N',-- O POUR OUI, N POUR NON
                            ID_CL INT NOT NULL,
                            CONSTRAINT CMNDCL_PK PRIMARY KEY (ID),
                            CONSTRAINT CMNDCL_CLIENT_FK FOREIGN KEY (ID_CL) REFERENCES CLIENT ON UPDATE RESTRICT ON DELETE RESTRICT);
--~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~--
CREATE TABLE LIGNE_CMND_CL  (   ID INT NOT NULL GENERATED ALWAYS AS IDENTITY,
                                ID_CMND  INT NOT NULL,
                                ID_ART INT NOT NULL,
                                PU_VENTE DECIMAL(10,2) NOT NULL DEFAULT 0,-- MAX: 999,999,999
                                QTE_CMND INT NOT NULL DEFAULT 0,
                                UNITE_VNT INT NOT NULL,
                                CONSTRAINT LIGNECCL_PK PRIMARY KEY(ID),
                                CONSTRAINT LIGNECCL_CMNDCL_FK FOREIGN KEY (ID_CMND) REFERENCES CMND_CLIENT ON UPDATE RESTRICT ON DELETE CASCADE,
                                CONSTRAINT LIGNECCL_ART_FK FOREIGN KEY (ID_ART) REFERENCES ARTICLE ON UPDATE RESTRICT ON DELETE CASCADE,
                                CONSTRAINT LIGNECCL_EMB_FK FOREIGN KEY (UNITE_VNT) REFERENCES EMBALLAGE ON UPDATE RESTRICT ON DELETE CASCADE);
--~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~--