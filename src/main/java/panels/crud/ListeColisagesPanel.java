/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package panels.crud;

import java.awt.Container;
import java.awt.Dimension;
import myComponents.MyJTable;

/**
 *
 * @author alilo
 */
public class ListeColisagesPanel extends ListeUnitesPanel {

    public ListeColisagesPanel(Container owner, boolean checkable) {
        super(owner, checkable);
        //filterList("Colis", "true", MyJTable.EXACT_MATCH_FILTER);
    }

    {
        setPreferredSize(new Dimension(300, 500));
        getTable().setColumnVisible("U.Unitaire", false);
        getTable().setColumnVisible("Colis", false);
        getTable().setColumnVisible("Morceau", false);
        getTable().setColumnVisible("Morceaux.P.Unité", false);
    }
}
