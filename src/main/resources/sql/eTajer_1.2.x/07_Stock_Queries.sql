--~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~--
--================================ CREATION ==================================--
--~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~--
-- Last_Update: 21/12/2015
DROP TABLE EN_STOCK;

CREATE TABLE EN_STOCK   ( ID  INT NOT NULL GENERATED ALWAYS AS IDENTITY,
                          ID_PROD INT NOT NULL,
                          ID_DEPOT INT NOT NULL,
                          QTE DOUBLE NOT NULL DEFAULT 0,
                          DATE_EXP DATE NOT NULL DEFAULT DATE(2932897),
                          --DATE_ALERT DATE NOT NULL DEFAULT DATE(2932897),
                          COD_BAR VARCHAR(30) NOT NULL,
                          ACTIF BOOLEAN NOT NULL DEFAULT TRUE,
                          DATE_ENTR DATE NOT NULL DEFAULT CURRENT_DATE,
                          PU_ACH DECIMAL(10,2) NOT NULL DEFAULT 0,
                          PU_VNT_DT DECIMAL(10, 2) NOT NULL DEFAULT 0,
                          PU_VNT_GR DECIMAL(10, 2) NOT NULL DEFAULT 0,
                          PU_VNT_DGR DECIMAL(10, 2) NOT NULL DEFAULT 0,
                          PU_VNT_SGR DECIMAL(10, 2) NOT NULL DEFAULT 0,
                          CONSTRAINT LOT_PK PRIMARY KEY (ID),
                          CONSTRAINT LOT_PROD_FK FOREIGN KEY (ID_PROD) REFERENCES PRODUIT ON DELETE CASCADE,
                          CONSTRAINT LOT_DEPO_FK FOREIGN KEY (ID_DEPOT) REFERENCES DEPOT ON DELETE RESTRICT,
                          CONSTRAINT UNIQUE_LOT UNIQUE (ID_PROD, ID_DEPOT, PU_ACH, DATE_EXP),
                          CONSTRAINT CHECK_QTE CHECK (QTE >= 0));
--INSERT INTO LOT_EN_STOCK (ID_PROD, ID_DEPOT, QTE, DATE_EXP, DATE_ALERT, COD_BAR, ACTIF) 
--SELECT ID_PROD, ID_DEPOT, QTE, DATE_EXP, DATE_ALERT, COD_BAR, ACTIF FROM EN_STOCK; 
--~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~--
--============================== ALTERATION ==================================--
--~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~--
-- 24/12/2015
ALTER TABLE EN_STOCK DROP COLUMN DATE_ALERT;
ALTER TABLE EN_STOCK DROP CONSTRAINT UNIQUE_STK;
ALTER TABLE EN_STOCK ADD CONSTRAINT UNIQUE_LOT UNIQUE (ID_PROD, ID_DEPOT, PU_ACH, DATE_EXP);
-- 22/12/2015
ALTER TABLE EN_STOCK ADD COLUMN DATE_ENTR DATE NOT NULL DEFAULT CURRENT_DATE;
-- 21/12/2015
ALTER TABLE EN_STOCK ADD COLUMN PU_ACH DECIMAL(10,2) NOT NULL DEFAULT 0;
ALTER TABLE EN_STOCK ADD COLUMN PU_VNT_DT DECIMAL(10, 2) NOT NULL DEFAULT 0;
ALTER TABLE EN_STOCK ADD COLUMN PU_VNT_GR DECIMAL(10, 2) NOT NULL DEFAULT 0;
ALTER TABLE EN_STOCK ADD COLUMN PU_VNT_DGR DECIMAL(10, 2) NOT NULL DEFAULT 0;
ALTER TABLE EN_STOCK ADD COLUMN PU_VNT_SGR DECIMAL(10, 2) NOT NULL DEFAULT 0;
-- 18/05/2015;
ALTER TABLE EN_STOCK ADD CONSTRAINT CHECK_QTE_STK CHECK (QTE >= 0);
-- 15/05/2015;
ALTER TABLE EN_STOCK ADD CONSTRAINT UNIQUE_STK UNIQUE (ID_PROD, ID_DEPOT, DATE_EXP);
-- 10/05/2015;
ALTER TABLE EN_STOCK DROP COLUMN DATE_ENTRE;
-- 14/03/2015;
ALTER TABLE EN_STOCK DROP CONSTRAINT UNIQUE_EXP;
ALTER TABLE EN_STOCK ADD COLUMN DATE_ENTRE DATE NOT NULL DEFAULT CURRENT_DATE; --OK
-- 12/03/2015;
ALTER TABLE EN_STOCK DROP CONSTRAINT UNIQUE_CB; --OK
-- 10/03/2015;
ALTER TABLE EN_STOCK ADD COLUMN ACTIF BOOLEAN NOT NULL DEFAULT TRUE;
--~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~--
--================================ VIEWS =====================================--
--~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~--
-- Last_Update: 04/01/2016
DROP VIEW V_LOT_EN_STOCK;

CREATE VIEW V_LOT_EN_STOCK AS
SELECT S.ID, S.ID_PROD, S.ID_DEPOT, P.ID_FAM, P.ID_CATEG ,S.COD_BAR AS "Réf.Lot", P.COD_BAR AS "Réf.Produit", P.DES AS "Désignation", 
       S.QTE AS "Qte", D.ADR AS "Dépôt", S.DATE_ENTR AS "Date.Entrée", S.PU_ACH AS "PU.Achat", S.PU_VNT_DT AS "PU.Détail", 
       S.DATE_EXP AS "Date.Exp", S.ACTIF AS "Actif", S.QTE * S.PU_ACH AS "Total/Ach"
FROM (EN_STOCK S INNER JOIN DEPOT D ON  S.ID_DEPOT = D.ID) INNER JOIN PRODUIT P ON S.ID_PROD = P.ID 
ORDER BY S.DATE_ENTR ASC; -- S.ACTIF = TRUE AND
--~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~--
-- Last update: 04/01/2016;
DROP VIEW V_LOT_A_VENDRE;

CREATE VIEW V_LOT_A_VENDRE AS
SELECT S.ID, S.ID_PROD, S.ID_DEPOT, P.ID_CATEG, S.COD_BAR AS "Réf.Lot", P.COD_BAR AS "Réf.Produit", P.DES AS "Désignation", 
    S.QTE AS "Qte", D.ADR AS "Dépôt", S.DATE_ENTR AS "Date.Entrée", S.DATE_EXP AS "Date.Exp",   
    S.PU_VNT_DT AS "PU.Détail"
FROM (EN_STOCK S INNER JOIN DEPOT D ON S.ACTIF = TRUE AND S.QTE > 0 AND D.DE_VENTE = TRUE AND S.ID_DEPOT = D.ID) 
    INNER JOIN PRODUIT P ON S.ID_PROD = P.ID 
ORDER BY S.DATE_ENTR ASC;
--~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~--
--=============================== QUERIES ====================================--
--~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~--
SELECT * FROM V_LOT_EN_STOCK;
SELECT * FROM EN_STOCK WHERE ID = ?;
SELECT * FROM V_LOT_EN_STOCK WHERE ID_PROD = ?;
SELECT * FROM V_LOT_EN_STOCK WHERE ID_DEPOT = ?;

INSERT INTO EN_STOCK(ID_PROD, ID_DEPOT, QTE, DATE_EXP, DATE_ALERT, COD_BAR, ACTIF)  VALUES (?, ?, ?, ?, ?, ?, ?);
UPDATE EN_STOCK SET ID_PROD = ?, ID_DEPOT = ?, QTE = ?, DATE_EXP = ?, DATE_ALERT = ?, COD_BAR = ? WHERE ID = ?;
DELETE FROM EN_STOCK WHERE ID = ?;
--~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~--
--============================== TRIGGERS ====================================--
--~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~--
-- Last update: 12/03/2015
DROP TRIGGER T_AFT_INS_STK_UPD_PROD;

CREATE TRIGGER T_AFT_INS_STK_UPD_PROD
AFTER INSERT ON EN_STOCK
REFERENCING NEW ROW AS NEW_STK
FOR EACH ROW
    UPDATE PRODUIT SET 
        QTE_GLOBAL = QTE_GLOBAL + NEW_STK.QTE 
    WHERE NEW_STK.ACTIF = TRUE AND ID = NEW_STK.ID_PROD;
--~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~--
-- 17/03/2015
DROP TRIGGER T_AFT_UPD_STK_UPD_PROD;

CREATE TRIGGER T_AFT_UPD_STK_UPD_PROD
AFTER UPDATE OF QTE ON EN_STOCK
REFERENCING OLD AS OLD_STK NEW AS NEW_STK 
FOR EACH ROW
    UPDATE PRODUIT SET 
        QTE_GLOBAL = QTE_GLOBAL + NEW_STK.QTE -  OLD_STK.QTE 
    WHERE ID = NEW_STK.ID_PROD;
--~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~--
-- 12/03/2015
DROP TRIGGER T_AFT_DEL_STK_UPD_PROD;

CREATE TRIGGER T_AFT_DEL_STK_UPD_PROD
AFTER DELETE ON EN_STOCK
REFERENCING OLD ROW AS OLD_STK
FOR EACH ROW
    UPDATE PRODUIT SET 
        QTE_GLOBAL = QTE_GLOBAL -  OLD_STK.QTE 
    WHERE OLD_STK.ACTIF = TRUE AND ID = OLD_STK.ID_PROD;
--~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~--
-- 04/01/2016 ==> .
DROP TRIGGER T_AFT_UPD_QTE_UPD_ACTIF;

CREATE TRIGGER T_AFT_UPD_QTE_UPD_ACTIF
AFTER UPDATE OF QTE ON EN_STOCK
REFERENCING NEW ROW AS NEW_STK
FOR EACH ROW
    UPDATE  EN_STOCK SET ACTIF = TRUE 
    WHERE NEW_STK.QTE > 0 AND ID = NEW_STK.ID;
--~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~--
-- EN_STOCK referenced from LINGE_VNT or LINGE_ACH must be keept even if the Qte <= 0. 
-- 10/11/2015; 
DROP TRIGGER T_AFT_UPD_QTE_DEL_STK;

CREATE TRIGGER T_AFT_UPD_QTE_DEL_STK
AFTER UPDATE OF QTE ON EN_STOCK
REFERENCING NEW ROW AS NEW_STK
FOR EACH ROW
    DELETE FROM  EN_STOCK
    WHERE NEW_STK.QTE <= 0 AND ID = NEW_STK.ID
    AND ID NOT IN (SELECT ID_EN_STK FROM LIGNE_VNT UNION SELECT ID_EN_STK FROM LIGNE_ACH);
--~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~--
-- 27/12/2016
-- UPDATE PRIX-VENTE DE TOUT LES LOTS A LA MODIFICATION D'UN
DROP TRIGGER T_AFT_UPD_PUVNT_UPD_PUVNT;

CREATE TRIGGER T_AFT_UPD_PUVNT_UPD_PUVNT
AFTER UPDATE OF PU_VNT_DT ON EN_STOCK
REFERENCING NEW ROW AS NEW_STK
FOR EACH ROW
    UPDATE  EN_STOCK SET PU_VNT_DT = NEW_STK.PU_VNT_DT 
    WHERE EN_STOCK.ID != NEW_STK.ID
    AND EN_STOCK.PU_VNT_DT !=  NEW_STK.PU_VNT_DT
    AND EN_STOCK.ID_PROD = NEW_STK.ID_PROD;