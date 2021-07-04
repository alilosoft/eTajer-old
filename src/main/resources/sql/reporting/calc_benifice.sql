select cast(sum(lv.TOTAL_LVNT - lv.PU_ACH * lv.QTE_UNIT) as decimal(15,2))
from LIGNE_VNT lv inner join VENTE v on lv.ID_VNT = v.ID and v.VALIDEE = 'Y'
where v."DATE" between '2016-12-01' and '2016-12-31';

-- Result: 1,239,677,282 cents;

select cast(sum(lv.TOTAL_LVNT) as decimal(15,2))
from LIGNE_VNT lv inner join VENTE v on lv.ID_VNT = v.ID and v.VALIDEE = 'Y'
where v."DATE" between '2016-12-01' and '2016-12-01';

