/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package myComponents.myJTableRenderers;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import javax.swing.*;
import javax.swing.plaf.FontUIResource;
import javax.swing.plaf.UIResource;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;

/**
 *
 * @author alilo
 */
public class MyTableColumnHeader extends JLabel implements TableCellRenderer, UIResource {

    private boolean horizontalTextPositionSet;

    public MyTableColumnHeader() {
        setHorizontalAlignment(JLabel.CENTER);
        setPreferredSize(new Dimension(0, 30));
        setFont(new FontUIResource("tahoma", FontUIResource.PLAIN, 13));
        setToolTipText("Clicker pour trié!");
        setBorder(javax.swing.BorderFactory.createTitledBorder(""));
        setOpaque(true);
    }

    @Override
    public void setHorizontalTextPosition(int textPosition) {
        horizontalTextPositionSet = true;
        super.setHorizontalTextPosition(textPosition);
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        Icon sortIcon = null;
        boolean isPaintingForPrint = false;

        if (table != null) {
            JTableHeader header = table.getTableHeader();
            if (header != null) {
                Color fgColor = null;
                Color bgColor = null;
                if (hasFocus) {
                    fgColor = UIManager.getColor("TableHeader.focusCellForeground");
                    bgColor = UIManager.getColor("TableHeader.focusCellBackground");
                }

                if (fgColor == null) {
                    fgColor = header.getForeground();
                }

                if (bgColor == null) {
                    bgColor = header.getBackground();
                }
                setForeground(fgColor);
                setBackground(bgColor);
                isPaintingForPrint = header.isPaintingForPrint();

            }

            if (!isPaintingForPrint && table.getRowSorter() != null) {
                if (!horizontalTextPositionSet) {
                    // There is a row sorter, and the developer hasn't
                    // set a text position, change to leading.
                    setHorizontalTextPosition(JLabel.LEADING);
                }

                //sortIcon = new ImageIcon(getClass().getResource("/res/actions/normal.png"));
                SortOrder sortOrder = getColumnSortOrder(table, column);
                if (sortOrder != null) {

                    switch (sortOrder) {
                        case ASCENDING:
                            //sortIcon = UIManager.getIcon("Table.ascendingSortIcon");
                            sortIcon = new ImageIcon(getClass().getResource("res/down.png"));
                            //System.out.println(getClass().getResource("res/up.png").getPath());
                            break;
                        case DESCENDING:
                            //sortIcon = UIManager.getIcon("Table.descendingSortIcon");
                            sortIcon = new ImageIcon(getClass().getResource("res/up.png"));
                            break;
                        case UNSORTED:
                            sortIcon = UIManager.getIcon("Table.naturalSortIcon");
                            //sortIcon = new ImageIcon(getClass().getResource("res/actions/normal.png"));
                            break;
                    }
                }
            }
        }
        this.setIcon(sortIcon);
        this.setText((value == null) ? "" : (String) value);
        return this;
    }

    public static SortOrder getColumnSortOrder(JTable table, int column) {
        
        SortOrder sortOrder = null;
        if (table.getRowSorter() == null) {
            return sortOrder;
        }

        java.util.List<? extends RowSorter.SortKey> sortKeys = table.getRowSorter().getSortKeys();

        if (sortKeys.size() > 0 && sortKeys.get(0).getColumn() == table.convertColumnIndexToModel(column)) {
            sortOrder = sortKeys.get(0).getSortOrder();
        }
        return sortOrder;
    }
}
