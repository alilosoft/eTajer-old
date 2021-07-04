select sum(la.TOTAL_LACH)
from LIGNE_ACH la inner join ACHAT a on la.ID_ACH = a.ID
where a."DATE" = current_date
and la.VALIDEE = true; 
