package myComponents;

/*
 * To change this template, choose Tools | Templates and open the template in
 * the editor.
 */
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Level;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.RowSorterEvent;
import javax.swing.event.RowSorterListener;
import javax.swing.plaf.FontUIResource;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;
import javax.swing.table.TableRowSorter;
import myComponents.myJTableRenderers.BooleanEditor;
import myComponents.myJTableRenderers.MyTableColumnHeader;
import myModels.ResultSet2TableModel;
import tools.DateTools;
import tools.ExceptionReporting;
import tools.MessageReporting;

/**
 *
 * @author alilo
 * @param <Model>
 */
public class MyJTable<Model extends ResultSet2TableModel> extends JTable {

    public static final String MODEL_SELECTED_ROW_PROP = "modelSelectedRow";
    public static final int EXACT_MATCH_FILTER = 1;
    public static final int START_WITH_FILTER = 2;
    public static final int END_WITH_FILTER = 3;
    public static final int ANY_MATCH_FILTER = 4;
    private final Model model;
    private final Map<Integer, RowFilter<ResultSet2TableModel, Integer>> andFiltersMap = new HashMap<>();
    private final Map<Integer, RowFilter<ResultSet2TableModel, Integer>> orFiltersMap = new HashMap<>();
    private TableRowSorter<ResultSet2TableModel> sorter;
    private static final Comparator CHECKBOX_COMPARATOR = new JCheckBoxComparator();

    /**
     * @param model
     */
    public MyJTable(final Model model) {
        super(model);
        this.model = model;
        initTable();
    }

    protected final void initTable() {
        getTableHeader().setDefaultRenderer(new MyTableColumnHeader());
        customizeView();
        customizeBehavior();
        sorter = new TableRowSorter<ResultSet2TableModel>(model) {

            @Override
            public Comparator getComparator(int column) {
                // if the model is checkable, the column of checkable cells will be the last column.
                if (getModel().isCheckable() && column == getModel().getColumnCount() - 1) {
                    return CHECKBOX_COMPARATOR;
                }

                if (getModel().getColumnClass(column).getName().equals("java.sql.Date") || getModel().getColumnClass(column).getName().equals("java.sql.Timestamp")) {
                    return new Comparator() {

                        @Override
                        public int compare(Object o1, Object o2) {
                            try {
                                Comparable c1 = DateTools.SHORT_DATE.parse(o1.toString());
                                Comparable c2 = DateTools.SHORT_DATE.parse(o2.toString());
                                return c1.compareTo(c2);
                            } catch (ParseException e) {
                                //ExceptionReporting.showException(e);
                                return ((Comparable) o1).compareTo(o2);
                            }
                        }
                    };
                }
                return super.getComparator(column);
            }
        };
        sorter.addRowSorterListener(new RowSorterListener() {

            @Override
            public void sorterChanged(RowSorterEvent e) {
                doOnTableRowSorte();
            }
        });
        setRowSorter(sorter);
    }

    @Override
    public final Model getModel() {
        return (Model) super.getModel();
    }

    public MyJTable getPrintableTable() {
        MyJTable copy = new MyJTable(getModel()) {

            @Override
            public void customizeView() {
                setShowVerticalLines(false);
                setFont(new FontUIResource("tahoma", FontUIResource.PLAIN, 16));
                setRowHeight(30);
            }

            @Override
            public void customizeBehavior() {
                //
            }

            @Override
            protected void createDefaultRenderers() {
                defaultRenderersByColumnClass = new UIDefaults(8, 0.75f);
                setLazyRenderer(Object.class, "javax.swing.table.DefaultTableCellRenderer$UIResource");
            }

        };
        //copy.removeFocusListener(copy.getFocusListeners()[copy.getFocusListeners().length - 1]);
        copy.setRowSorter(sorter);

        return copy;
    }
    
   
    /**
     * Create a RowFilter
     *
     * @param col
     * @param pattern
     * @param matchType
     * @return
     */
    public RowFilter createRowFilter(int col, String pattern, int matchType) {
        switch (matchType) {
            case EXACT_MATCH_FILTER:
                if (pattern.isEmpty()) {
                    return RowFilter.regexFilter("", col);
                } else {
                    return RowFilter.regexFilter("^" + pattern + "$", col);
                }
            case START_WITH_FILTER:
                return RowFilter.regexFilter("^" + pattern, col);
            case END_WITH_FILTER:
                return RowFilter.regexFilter(pattern + "$", col);
            case ANY_MATCH_FILTER:
                return RowFilter.regexFilter(pattern, col);
            default:
                return RowFilter.regexFilter(pattern, col);
        }
    }

    /**
     * Filter the table at the specified col index with the specified entry. The
     * filters was set by this method will behave as andFilter(Iterable
     * filters).
     *
     * @param modelColIndex the column on witch apply the andFilter. if = -1
     * then apply the andFilter to all columns.
     * @param pattern
     * @param matchType
     */
    public void andFilter(int modelColIndex, String pattern, int matchType) {
        RowFilter filter;
        try {
            filter = createRowFilter(modelColIndex, pattern, matchType);
            andFiltersMap.put(modelColIndex, filter);
            sorter.setRowFilter(RowFilter.andFilter(andFiltersMap.values()));
        } catch (Exception e) {
            ExceptionReporting.showException(e);
        }
    }

    /**
     * Filter the table at the specified col name with the specified entry. The
     * filters was set by this method will behave as andFilter(Iterable
     * filters).
     *
     * @param modelColName the column on with apply the andFilter.
     * @param pattern the entry to search.
     * @param matchType
     */
    public void andFilter(String modelColName, String pattern, int matchType) {
        int ind = getModel().getColumnIndex(modelColName);
        if (ind >= 0) {
            andFilter(ind, pattern, matchType);
        } else {
            String mess = "Column '" + modelColName + "' dosn't exist in the model:" + getModel().getClass().getName();
            MessageReporting.showMessage(Level.SEVERE, getClass(), "andFilter(String modelColName, String pattern, int matchType)", mess);
        }
    }

    public void orFilter(int[] colIndexes, String pattern, int matchType) {
        orFiltersMap.clear();
        for (int ind : colIndexes) {
            //System.out.println("orFilter map key: " + ind);
            RowFilter filter = createRowFilter(ind, pattern, matchType);
            orFiltersMap.put(ind, filter);
        }

        try {
            RowFilter<ResultSet2TableModel, Integer> orRowFilter = RowFilter.orFilter(orFiltersMap.values());
            //System.out.println("andFilter map key: " + this.hashCode());
            andFiltersMap.put(this.hashCode(), orRowFilter);
            sorter.setRowFilter(RowFilter.andFilter(andFiltersMap.values()));
        } catch (Exception e) {
            ExceptionReporting.showException(e);
        }
    }

    /**
     * Filter the table based on one or multiple columns.
     *
     * @param colNames
     * @param pattern
     * @param matchType
     */
    public void orFilter(String[] colNames, String pattern, int matchType) {
        orFiltersMap.clear();
        int[] colIndexes = new int[colNames.length];
        for (int i = 0; i < colNames.length; i++) {
            colIndexes[i] = getModel().getColumnIndex(colNames[i]);
        }
        orFilter(colIndexes, pattern, matchType);
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        // don't allow the cell editing except if the table is checkable and the column is the first column.
        return getModel().isCheckable() && column == 0 && getColumnClass(column).getName().equals("java.lang.Boolean");
    }

    public void customizeView() {
        setIntercellSpacing(new Dimension(3, 2));
        setShowVerticalLines(false);
        setShowHorizontalLines(false);
        setFont(new FontUIResource("tahoma", FontUIResource.BOLD, 14));
        setRowHeight(30);
        //setAutoResizeMode(JTable.AUTO_);

        if (getModel().isCheckable()) {
            getColumnModel().getColumn(getColumnCount() - 1).setCellEditor(new BooleanEditor() {

                @Override
                public void cellCheckChenged(boolean checked, int row) {
                    doOnRowCheckChange(row, convertRowIndexToModel(row), checked);
                }
            });
            getColumnModel().moveColumn(getColumnCount() - 1, 0);
        }
    }

    public void customizeBehavior() {
        setFocuseListner();
        setKeyListner();
        setListSelectionListner();
    }

    private FocusListener focusListener;

    private void setFocuseListner() {
        focusListener = new FocusAdapter() {

            MyJTable myJTable;
            Border oldBorder;
            Color oldBackground;

            @Override
            public void focusGained(FocusEvent e) {
                super.focusGained(e);
                if (getSelectionModel().isSelectionEmpty() && getRowCount() > 0) {
                    setRowSelectionInterval(0, 0);
                }
                myJTable = (MyJTable) e.getSource();
                oldBackground = myJTable.getBackground();
                setBackground(Color.GRAY);
            }

            @Override
            public void focusLost(FocusEvent e) {
                super.focusLost(e);
                myJTable = (MyJTable) e.getSource();
                myJTable.setBackground(oldBackground);
            }
        };
        addFocusListener(focusListener);
    }

    private void setKeyListner() {
        addKeyListener(new KeyAdapter() {

            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_TAB) {
                    if (e.isShiftDown()) {
                        transferFocusBackward();
                    } else {
                        transferFocus();
                    }
                    e.consume();
                }
            }
        });
    }

    private void setListSelectionListner() {
        // listen to selection in the table
        getSelectionModel().addListSelectionListener(new ListSelectionListener() {

            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (e.getValueIsAdjusting()) {
                    return;
                }
                ListSelectionModel lsm = (ListSelectionModel) e.getSource();
                if (!lsm.isSelectionEmpty()) {
                    getModel().clearSelectedRows();
                    int modelRow;
                    for (int ind = lsm.getMinSelectionIndex(); ind <= lsm.getMaxSelectionIndex(); ind++) {
                        if (lsm.isSelectedIndex(ind)) {
                            modelRow = convertRowIndexToModel(ind);
                            getModel().addSelectedRow(modelRow);
                        }
                    }
                    int tabRow = lsm.getMaxSelectionIndex();
                    modelRow = convertRowIndexToModel(tabRow);
                    doOnSelectionChange(e, tabRow, modelRow);
                } else {
                    doOnSelectionChange(e, -1, -1);
                    getModel().clearSelectedRows();
                }
            }
        });
    }

    /**
     * Invoked when the table selection changes.<br> This method is designed to
     * be overridden in subclasses to do some jobs if selection is changed.
     *
     * @param evt
     * @param tabSelRow
     * @param modelSelRow
     */
    public void doOnSelectionChange(ListSelectionEvent evt, int tabSelRow, int modelSelRow) {
    }

    /**
     * Invoked when a JTable is checkable and a tableRow check state
     * changed.<br> This method is designed to be overridden in subclasses to do
     * something if a tableRow check is changed.
     *
     * @param checked the state of the tableRow (checked or no)
     * @param modelRow
     * @param tableRow the index of the changed tableRow.
     */
    public void doOnRowCheckChange(int tableRow, int modelRow, boolean checked) {
    }

    /**
     * Invoked when the row sort of the jTable is changed. The row andFilter
     * also will invoked by the row sorter listener.
     */
    public void doOnTableRowSorte() {
    }

    /*
     * customizeView columns prefered width
     */
    public void setColumnsPreferredWidths(int widths[]) {
        if (widths.length > 0) {
            for (int i = 0; i < (getColumnCount() < widths.length ? getColumnCount() : widths.length); i++) {
                getColumnModel().getColumn(i).setPreferredWidth(widths[i]);
                getColumnModel().getColumn(i).sizeWidthToFit();
            }
        }
    }

    /**
     * Get the view index of a col name if is exist.
     *
     * @param colName
     * @return the index if the col if exist, -1 else.
     */
    public int getColumnIndex(String colName) {
        int index;
        try {
            index = getColumnModel().getColumnIndex(colName);
        } catch (Exception e) {
            index = -1;
        }
        return index;
    }

    public Object getValueAt(int row, String column) {
        
        return getValueAt(row, getColumnIndex(column));
    }

    /**
     * Show or hide (remove) a column in the table view.<br> if visible ==
     * false, then remove the column by its view index,<br> else (<i>restore a
     * removed col</i>), by getting its index from the underling model.
     *
     * @param colName the col name to remove.
     * @param visible false to remove, restore else.
     */
    public void setColumnVisible(final String colName, final boolean visible) {
        if (visible && isColumnVisible(colName)) {
            return;
        }

        int colIndex;
        if (visible) {
            colIndex = getModel().getColumnIndex(colName);
        } else {
            colIndex = getColumnIndex(colName);
        }
        if (colIndex >= 0) {
            setColumnVisible(colIndex, visible);
        }
    }

    /**
     * Remove or Restore a column from the table column model by its index.
     *
     * @param index the index of the column to remove or restore.
     * @param visible true to restore, false to remove.
     */
    public void setColumnVisible(final int index, final boolean visible) {
        if (visible == false) {
            getColumnModel().removeColumn(getColumnModel().getColumn(index));
        } else {
            TableColumn col = new TableColumn(index);
            col.setHeaderValue(getModel().getColumnName(index));
            getColumnModel().addColumn(col);
            if (index < getColumnCount() - 1) {
                getColumnModel().moveColumn(getColumnCount() - 1, index);
            }
        }
    }

    public void moveColumn(String colName, int newIndex) {
        if (isColumnVisible(colName)) {
            int colIndex = getColumnIndex(colName);
            getColumnModel().moveColumn(colIndex, newIndex);
        }
    }

    public boolean isColumnVisible(String colName) {
        int colInd = getColumnIndex(colName);
        return colInd >= 0;
    }

    public void setColumnName(int index, String name) {
        getColumnModel().getColumn(index).setHeaderValue(name);
    }

    //Select a table view row 
    public void selectViewRow(int viewIndex) {
        getSelectionModel().setSelectionInterval(viewIndex, viewIndex);
        scrollRectToVisible(getCellRect(viewIndex, 0, true));
    }

    //Select the table view row by selection a model row
    public void selectModelRow(int modelIndex) {
        if (modelIndex < 0 || modelIndex >= getModel().getRowCount()) {
            return;
        }
        int viewIndex = convertRowIndexToView(modelIndex);
        selectViewRow(viewIndex);
        scrollRectToVisible(getCellRect(viewIndex, 0, true));
    }

    public void next() {
        try {
            if (getRowCount() > 0) {
                int selRow = (getSelectedRow() >= getRowCount() - 1) ? -1 : getSelectedRow();
                setRowSelectionInterval(selRow + 1, selRow + 1);
            }
        } catch (Exception e) {
            ExceptionReporting.showException(e);
        }
    }

    public void previous() {
        try {
            if (getRowCount() > 0) {
                int selRow = (getSelectedRow() <= 0) ? getRowCount() : getSelectedRow();
                setRowSelectionInterval(selRow - 1, selRow - 1);
            }
        } catch (Exception e) {
            ExceptionReporting.showException(e);
        }
    }

    public void first() {
        try {
            if (getRowCount() > 0) {
                setRowSelectionInterval(0, 0);
            }
        } catch (Exception e) {
            ExceptionReporting.showException(e);
        }
    }

    public void last() {
        try {
            if (getRowCount() > 0) {
                setRowSelectionInterval(getRowCount() - 1, getRowCount() - 1);
            }
        } catch (Exception e) {
            ExceptionReporting.showException(e);
        }
    }

    public void setLazyRenderer(Class c, String s) {
        defaultRenderersByColumnClass.put(c, new UIDefaults.ProxyLazyValue(s));
    }

    @Override
    protected JTableHeader createDefaultTableHeader() {
        JTableHeader tabHeader = new JTableHeader(getColumnModel());
        tabHeader.setDefaultRenderer(new MyTableColumnHeader());
        return tabHeader;
    }

    @Override
    protected void createDefaultRenderers() {
        super.createDefaultRenderers();

        defaultRenderersByColumnClass = new UIDefaults(8, 0.75f);

        // Objects
        setLazyRenderer(Object.class, "myComponents.myJTableRenderers.ObjectRenderer");

        // Numbers
        setLazyRenderer(Number.class, "myComponents.myJTableRenderers.NumberRenderer");

        // Doubles and Floats
        setLazyRenderer(Float.class, "myComponents.myJTableRenderers.DoubleRenderer");
        setLazyRenderer(Double.class, "myComponents.myJTableRenderers.DoubleRenderer");

        // Dates
        setLazyRenderer(Date.class, "myComponents.myJTableRenderers.DateRenderer");
        setLazyRenderer(Timestamp.class, "myComponents.myJTableRenderers.DateRenderer");

        // Icons and ImageIcons
        setLazyRenderer(Icon.class, "myComponents.myJTableRenderers.IconRenderer");
        setLazyRenderer(ImageIcon.class, "myComponents.myJTableRenderers.IconRenderer");

        // Booleans
        setLazyRenderer(Boolean.class, "myComponents.myJTableRenderers.BooleanRenderer");
    }

    @Override
    protected void createDefaultEditors() {
        super.createDefaultEditors();
    }

    private static class JCheckBoxComparator implements Comparator {

        @Override
        public int compare(Object o1, Object o2) {
            if (o1 instanceof JCheckBox) {
                o1 = ((JCheckBox) o1).isSelected();
                o2 = ((JCheckBox) o2).isSelected();
                return ((Comparable) o1).compareTo(o2);
            } else {
                return ((Comparable) o1).compareTo(o2);
            }
        }
    }
}
