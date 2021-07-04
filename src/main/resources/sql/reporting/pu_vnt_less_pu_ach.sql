select v."DATE", u.LOGIN as "Vendeur", p.COD_BAR as "Code", p.DES as "Produit", lv.PU_ACH as "Prix Achat", lv.PU_VNT as "Prix Vente", lv.QTE_UNIT as "Qte"
from LIGNE_VNT lv
inner join VENTE v on v.ID = lv.ID_VNT
inner join EN_STOCK s on s.ID = lv.ID_EN_STK
inner join PRODUIT p on p.ID = s.ID_PROD
inner join APP_USER u on u.ID = v.ID_USER
where v.VALIDEE = true
and v."DATE" > '2016-06-05' 
and lv.PU_VNT <= lv.PU_ACH
order by  v."DATE" asc