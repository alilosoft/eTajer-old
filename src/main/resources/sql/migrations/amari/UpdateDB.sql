--*****************************************************************************;
--TABLES;
--*****************************************************************************;
--23/05/2015
ALTER TABLE DEPOT ADD CONSTRAINT UNIQUE_ADR UNIQUE(ADR);
-- 26/05/2015;
ALTER TABLE QUANTIFIER DROP COLUMN UNITAIRE;
ALTER TABLE QUANTIFIER DROP COLUMN COLISAGE;
ALTER TABLE QUANTIFIER DROP COLUMN MORCELLEMENT;
-- 27/05/2015;
UPDATE EN_STOCK SET ACTIF = FALSE WHERE QTE <= 0;
--08/11/2015;
ALTER TABLE PRODUIT ADD COLUMN PU_VENTE_DGR DECIMAL(10, 2) NOT NULL DEFAULT 0;
ALTER TABLE PRODUIT ADD COLUMN PU_VENTE_SGR DECIMAL(10, 2) NOT NULL DEFAULT 0;
-- 08/11/2015;
ALTER TABLE QUANTIFIER ADD COLUMN UNITE_DT BOOLEAN NOT NULL DEFAULT FALSE;
ALTER TABLE QUANTIFIER ADD COLUMN UNITE_GR BOOLEAN NOT NULL DEFAULT FALSE;
ALTER TABLE QUANTIFIER ADD COLUMN UNITE_DGR BOOLEAN NOT NULL DEFAULT FALSE;
ALTER TABLE QUANTIFIER ADD COLUMN UNITE_SGR BOOLEAN NOT NULL DEFAULT FALSE;
--*****************************************************************************;
-- VIEWS;
--*****************************************************************************;
DROP VIEW V_LOT_EN_STOCK;

CREATE VIEW V_LOT_EN_STOCK AS
SELECT S.ID, S.ID_PROD, S.ID_DEPOT, P.COD_BAR AS "Réf/C.B", P.DES AS "Désignation", P.PU_ACHAT AS "PU.Achat", 
       S.QTE AS "Qte", D.ADR AS "Dépôt", D.DE_VENTE AS "Vente", D.DE_STOCKAGE AS "Stock", 
       D.DE_RESERVE AS "Résèrve", S.DATE_EXP AS "Date.Exp", S.DATE_ALERT AS "Date.Alert", S.ACTIF AS "Actif"
FROM (EN_STOCK S INNER JOIN DEPOT D ON S.ID_DEPOT = D.ID) INNER JOIN PRODUIT P ON S.ID_PROD = P.ID;
--*****************************************************************************;
-- TRIGGERS;
--*****************************************************************************;
-- 10/11/2015; 
DROP TRIGGER T_AFT_UPD_QTE_DEL_STK;
--*****************************************************************************;
done;