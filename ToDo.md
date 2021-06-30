================= eTajer 1.2 ===========================
***** Famille Client (Des, Remise, Nbr_Point_Fédilité)
*** Client<>FamilleCl()
***** Tarifs (Des, Marge, Qte_Min) => Detail, Gros, Demi-Gros, Super-Gros
*** Client<>Tarif()
***** Prod<>Tarif(PU)
****** Save Settings to DB
** Rename En_Stock (ListPanel, MajPanel) to Lot
** Blockage du produit, numéro de série, garantie
**** Add Details Panel to List Panel (showing infos of selected item).
***** Mouvement Stock (Historique: Entrée, Sortie, Pertes).
** Ajout: Garentie, Numéro de Série, Numéro d'inventaire au Produit.
**** Ouvrire un inventaire => Blocker les ventes et les achats.
**** Clôturer l'inventaire => rénistialiser les qtes.
***** Commande Fournisseur / Reception.
# Maj Linge Achat;
** Retour Achat, Remboursement fournisseur;
***** Retour du Livraison ; Remboursement Client;
+ Calc PU-Achat Moyen;
*** choose dbs folder path (db_path).
*** choose db exercise year (db_name).
****** MAJ Lot En Stock: Ajouter le PU.Ach, les PrixU.Vnts:
	{	
		si: nouv prix ach ou nouv date exp => nouv lot;
		si: nouv prix vnt et existe ancien lot => ? maj le prix vnt?
	}
**** Authoriser des code/réf du produit null.
**** authoriser les categories null.
******* ViwLotEnStock => show only active lots.
******* LotEnStock => Activate Lot only by validating LigneAch.
**** Whene adding a new lot to stock verifiy if there an old lot with != prices an propose to update theme.
***** Executer une tache de maintenance => supprimer les lot non utils.
************* Retour du livraison => restorer les qtes  
**** Choose dbs folder path (db_path).
**** Choose db exercise year (db_name).