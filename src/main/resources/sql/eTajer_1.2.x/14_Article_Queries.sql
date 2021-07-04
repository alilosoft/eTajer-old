CREATE TABLE ARTICLE    (   ID INT NOT NULL GENERATED ALWAYS AS IDENTITY,
                            PU_VENTE_GR DECIMAL(10, 2) DEFAULT 0,
                            PU_VENTE_DT DECIMAL(10, 2) DEFAULT 0,
                            QTE_STK_REEL INT  NOT NULL DEFAULT 0,
                            QTE_EN_VRAC INT NOT NULL DEFAULT 0,
                            ID_L_ACHAT    INT NOT NULL,
                            ACTIVE CHAR(1) NOT NULL DEFAULT 'N',
                            CONSTRAINT ART_PK PRIMARY KEY (ID),
                            CONSTRAINT CHECK_QTE_STK CHECK (QTE_STK_REEL >= 0),
                            CONSTRAINT ART_L_ACHAT_FK FOREIGN KEY (ID_L_ACHAT) REFERENCES LIGNE_ACHAT ON UPDATE RESTRICT ON DELETE CASCADE);
--~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~--
-- 09/03/2013
ALTER TABLE ARTICLE ADD COLUMN QTE_EN_VRAC INT NOT NULL DEFAULT 0;
-- 10/03/2013
ALTER TABLE ARTICLE ADD CONSTRAINT CHECK_QTE_STK CHECK (QTE_STK_REEL >= 0);
-- ALTER TABLE ARTICLE ADD COLUMN QTE_CMND_VRAC INT NOT NULL DEFAULT 0;
-- ALTER TABLE ARTICLE ADD COLUMN QTE_THR_VRAC INT NOT NULL DEFAULT 0;
-- 04/10/2013
ALTER TABLE ARTICLE DROP COLUMN QTE_STK_THR;
ALTER TABLE ARTICLE DROP COLUMN QTE_STK_CMND;
ALTER TABLE ARTICLE DROP COLUMN QTE_CMND_VRAC;
ALTER TABLE ARTICLE DROP COLUMN QTE_THR_VRAC;
--~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~--
-- !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! --
-- This view is replaced by another view that eliminate some non-usfull columns to optimize performances.
CREATE VIEW V_ARTICLE_TOUS AS -- ATTENTION
SELECT ART.ID, ART.ID_L_ACHAT, LACH.ID_PROD, PROD.ID_CATEG, CAT.ID_FAM, PROD.REFER AS "Réf", PROD.DES AS "Produit", 
LACH.PU_ACHAT AS "PU.Achat", PU_VENTE_GR AS "PU.Vente.Gr", PU_VENTE_DT AS "PU.Vente.Dt", 
QTE_STK_REEL AS "Qte.Stk", QTE_EN_VRAC AS "Qte.En.Vrac", LACH.QTE_ACHETE AS "Qte.Achté", EMB.DES AS "Unité.Achat", 
SUM(LVNT.QTE_VEND_COLIS) AS "Qte.Vend.Coli", SUM(LVNT.QTE_VEND_VRAC) AS "Qte.Vend.Vrac", QTE_STK_THR 
AS "Qte.Thr", QTE_STK_CMND AS "Qte.Cmnd", ACTIVE 
FROM ((ARTICLE ART INNER JOIN (LIGNE_ACHAT LACH INNER JOIN EMBALLAGE EMB 
                                               ON LACH.UNITE_ACHAT = EMB.ID) 
                  ON ART.ID_L_ACHAT = LACH.ID) INNER JOIN (PRODUIT PROD INNER JOIN CATEGORIE CAT 
                                                                        ON PROD.ID_CATEG = CAT.ID) 
                                               ON LACH.ID_PROD = PROD.ID) LEFT JOIN LIGNE_VNT LVNT
                                                                            ON LVNT.VALIDEE = 'Y' AND LVNT.ID_ART = ART.ID
GROUP BY ART.ID, ART.ID_L_ACHAT, LACH.ID_PROD, PROD.ID_CATEG, CAT.ID_FAM, PROD.REFER, PROD.DES, LACH.PU_ACHAT, PU_VENTE_GR, PU_VENTE_DT, QTE_STK_REEL, QTE_EN_VRAC, QTE_ACHETE, EMB.DES, QTE_STK_THR, QTE_STK_CMND, ACTIVE
ORDER BY ART.ID;
--~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~--
-- Last Modification: 02/11/2013
-- The new version of V_ARTICLE_TOUS, eliminate some un-usefull columns. 
DROP VIEW V_ARTICLE_A_VENDRE;
DROP VIEW V_ARTICLE_ACTIVE;
DROP VIEW  V_ARTICLE_TOUS;
CREATE VIEW V_ARTICLE_TOUS AS
SELECT ART.ID, ART.ID_L_ACHAT, LACH.ID_PROD, PROD.ID_CATEG, CAT.ID_FAM, PROD.REFER AS "Réf", PROD.DES AS "Produit", 
LACH.PU_ACHAT AS "PU.Achat", PU_VENTE_GR AS "PU.Vente.Gr", PU_VENTE_DT AS "PU.Vente.Dt", 
QTE_STK_REEL AS "Qte.Stk", QTE_EN_VRAC AS "Qte.En.Vrac", ACTIVE 
FROM ((ARTICLE ART INNER JOIN LIGNE_ACHAT LACH 
                    ON ART.ID_L_ACHAT = LACH.ID) INNER JOIN PRODUIT PROD  
                                                    ON LACH.ID_PROD = PROD.ID) INNER JOIN CATEGORIE CAT 
                                                                                ON PROD.ID_CATEG = CAT.ID
GROUP BY ART.ID, ART.ID_L_ACHAT, LACH.ID_PROD, PROD.ID_CATEG, CAT.ID_FAM, PROD.REFER, PROD.DES, LACH.PU_ACHAT, PU_VENTE_GR, PU_VENTE_DT, QTE_STK_REEL, QTE_EN_VRAC, ACTIVE
ORDER BY ART.ID;
--~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~--
DROP VIEW V_ARTICLE_ACTIVE;
CREATE VIEW V_ARTICLE_ACTIVE AS
SELECT * FROM V_ARTICLE_TOUS WHERE ACTIVE = 'Y';
SELECT * FROM V_INVENTAIRE_STK;
SELECT SUM("Total.Stk") FROM V_INVENTAIRE_STK;
--~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~--
DROP VIEW V_ARTICLE_A_VENDRE;
CREATE VIEW V_ARTICLE_A_VENDRE AS
SELECT * FROM V_ARTICLE_ACTIVE WHERE ("Qte.Stk" != 0 OR "Qte.En.Vrac" != 0);
--~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~--
DROP VIEW  V_INVENTAIRE_STK;
CREATE VIEW V_INVENTAIRE_STK AS
SELECT ART.ID, ART.ID_L_ACHAT, LACH.ID_PROD, PROD.ID_CATEG, CAT.ID_FAM, PROD.REFER AS "Réf", PROD.DES AS "Produit", 
        QTE_STK_REEL AS "Qte.Stk", QTE_EN_VRAC AS "Qte.En.Vrac", LACH.PU_ACHAT AS "PU.Achat", LACH.PU_ACHAT * QTE_STK_REEL AS "Total.Stk"
FROM ((ARTICLE ART INNER JOIN LIGNE_ACHAT LACH 
                    ON ART.QTE_STK_REEL != 0
                    AND ART.ID_L_ACHAT = LACH.ID) INNER JOIN PRODUIT PROD 
                                                    ON LACH.ID_PROD = PROD.ID) INNER JOIN CATEGORIE CAT 
                                                                                ON PROD.ID_CATEG = CAT.ID 
GROUP BY ART.ID, ART.ID_L_ACHAT, LACH.ID_PROD, PROD.ID_CATEG, CAT.ID_FAM, PROD.REFER, PROD.DES, LACH.PU_ACHAT, QTE_STK_REEL, QTE_EN_VRAC
ORDER BY ART.ID;
--~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~--
SELECT * FROM V_ARTICLE_TOUS;
SELECT * FROM V_ARTICLE_ACTIVE; 
SELECT * FROM V_ARTICLE_A_VENDRE;
SELECT * FROM V_BENIFICE_ART;
INSERT INTO ARTICLE (PU_VENTE_GR, PU_VENTE_DT, QTE_STK_REEL, QTE_EN_VRAC, ID_L_ACHAT, ACTIVE) VALUES (?, ?, ?, ?, ?, ?, ?);
UPDATE ARTICLE SET PU_VENTE_GR = ?, PU_VENTE_DT = ?, QTE_STK_REEL = ?, QTE_EN_VRAC = ?, ID_L_ACHAT = ?, ACTIVE = ?  WHERE ID = ?;
UPDATE ARTICLE SET ACTIVE = '_' WHERE ID_L_ACHAT IN 
(SELECT ID FROM LIGNE_ACHAT WHERE ID_ACHAT = 2);
--~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~--
-- Trigger name changed
DROP TRIGGER DELETE_LIGNE_ACHAT_BY_ART;
-- This trigger delete a 'Ligne Achat' after deleting the corresponding 'Article' 
DROP TRIGGER T_AFT_DEL_ART_DEL_LACH;
CREATE TRIGGER T_AFT_DEL_ART_DEL_LACH
AFTER DELETE ON ARTICLE
REFERENCING OLD ROW AS DELETED_ROW
FOR EACH ROW
    DELETE FROM LIGNE_ACHAT
    WHERE ID = DELETED_ROW.ID_L_ACHAT;
--~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~--