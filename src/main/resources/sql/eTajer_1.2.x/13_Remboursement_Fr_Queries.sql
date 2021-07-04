--~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~--
--================================ CREATION ==================================--
--~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~--
-- 23/11/2015;
DROP TABLE REMBOURSE_FR;

CREATE TABLE REMBOURSE_FR  (    ID INT NOT NULL GENERATED ALWAYS AS IDENTITY,
                                ID_FR INT NOT NULL,
                                ID_RETOUR INT DEFAULT NULL,
                                "DATE" DATE NOT NULL DEFAULT CURRENT_DATE,
                                HEURE TIME NOT NULL DEFAULT CURRENT_TIME,
                                MONTANT DECIMAL(12,2) NOT NULL DEFAULT 0,-- MAX: 999,999,999
                                MODE_PAY INT NOT NULL,--MODE DE PAYEMENT, ESPECE, CHEQUE,...ETC.
                                COMMENT VARCHAR(200) DEFAULT NULL,
                                CONSTRAINT REMB_PK PRIMARY KEY (ID),
                                CONSTRAINT REMB_FR_FK FOREIGN KEY (ID_FR) REFERENCES FOURNISSEUR ON DELETE RESTRICT,
                                CONSTRAINT REMB_ACH_FK FOREIGN KEY (ID_ACH) REFERENCES ACHAT ON DELETE CASCADE,
                                CONSTRAINT REMB_MODE_FK FOREIGN KEY (MODE_PAY) REFERENCES MODE_PAYE ON DELETE RESTRICT);
