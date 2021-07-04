-- 
SELECT SUM(V.TOTAL)
FROM VENTE V
WHERE V.VALIDEE = true 
AND V.ID_USER = 6;

-- total par user;
SELECT U.LOGIN,  SUM(V.TOTAL) as total
FROM VENTE V INNER JOIN APP_USER U ON V.ID_USER = U.ID 
WHERE V.VALIDEE = true
Group by U.LOGIN 
order by total desc; 


-- djahida, id = 6, total = 663,195,347; 
-- ilham, id = 5 , total = 276,642,529;
