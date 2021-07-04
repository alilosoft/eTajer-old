-- ALTERATIONS
-- 23/11/2015;
ALTER TABLE VENTE ADD COLUMN RETOUR BOOLEAN NOT NULL DEFAULT FALSE;
-- 21/12/2015: EN_STOCK Alteration: Add PUs columns;
-- 24/12/2015: Update EN_STOCK Views;
-- 24/12/2015: AlertExp => Create Table & View;

--~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~--
-- VIEWS
--~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~--