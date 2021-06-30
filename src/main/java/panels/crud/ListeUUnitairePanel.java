/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package panels.crud;

import java.awt.Container;
import java.awt.Dimension;
import myComponents.MyJTable;
import panels.CRUDPanel;

/**
 *
 * @author alilo
 */
public class ListeUUnitairePanel extends ListeUnitesPanel {
    
    {
        setPreferredSize(new Dimension(300, 500));
        getTable().setColumnVisible("U.Unitaire", false);
        getTable().setColumnVisible("Colis", false);
        getTable().setColumnVisible("Qte.En.Colis", false);
        getTable().setColumnVisible("Morceau", false);
        getTable().setColumnVisible("Morceaux.P.Unité", false);
    }
    
    public ListeUUnitairePanel(Container owner, boolean checkable) {
        super(owner, checkable);
        //filterList("U.Unitaire", "true", MyJTable.EXACT_MATCH_FILTER);
    }

    

    @Override
    public CRUDPanel getNavigNEditList() {
        return this.initTableView();
    }
}
