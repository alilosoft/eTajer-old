-- id = 830
select sum(lv.QTE_UNIT) ---, lv.PU_VNT, u.LOGIN
from LIGNE_VNT lv inner join VENTE v on v.ID = lv.ID_VNT
inner join EN_STOCK stk on lv.ID_EN_STK = stk.ID 
inner join PRODUIT p on p.ID = stk.ID_PROD 
inner join APP_USER u on v.ID_USER = u.ID
WHERE p.ID = 830
and v."DATE" = current_date
and v.VALIDEE = true;
