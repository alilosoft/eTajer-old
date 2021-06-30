/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package panels.crud;

import entities.Categorie;
import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import myComponents.MyJFrame;
import panels.RSTablePanel;
import dao.CategorieDAO;
import entities.EntityClass;
import entities.Famille;
import java.sql.ResultSet;
import panels.maj.MajCategoriePanel;

/**
 *
 * @author alilo
 */
public class ListeCategoriesPanel extends RSTablePanel<Categorie, CategorieDAO> {

    {
        setPreferredSize(new Dimension(300, 450));
        setMajPanel(new MajCategoriePanel(this));
        setFilterShortcut("CTRL", "T");
    }
    private Famille masterFam;

    public ListeCategoriesPanel(Container owner, boolean checkable) {
        super(owner, checkable);
    }

    @Override
    public ListeCategoriesPanel initTableView() {
        getTable().setColumnVisible("ID", false);
        getTable().setColumnVisible("ID_FAM", false);
        getTable().setColumnsPreferredWidths(new int[]{200});
        return this;
    }

    @Override
    public CategorieDAO getTableDAO() {
        return CategorieDAO.getInstance();
    }

    @Override
    public ResultSet getResultSet() {
        if (masterFam == null || masterFam.getId() <= 0) {
            return super.getResultSet();
        }else{
            return getTableDAO().getByFamille(masterFam);
        }
    }

    @Override
    public void setMasterEntity(EntityClass masterEntity) {
        super.setMasterEntity(masterEntity);
        if (masterEntity instanceof Famille) {
            setMasterFamille((Famille) masterEntity);
        }else{
            setMasterFamille(null);
        }
    }

    public void setMasterFamille(Famille f) {
        this.masterFam = f;
        reload();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        setPreferredSize(new java.awt.Dimension(250, 500));
        setLayout(new java.awt.BorderLayout());
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {

        java.awt.EventQueue.invokeLater(new Runnable() {

            @Override
            public void run() {
                MyJFrame frame = new MyJFrame();
                frame.getContentPane().add(new ListeCategoriesPanel(frame, false).initTableView(), BorderLayout.CENTER);
                frame.pack();
                frame.setVisible(true);
            }
        });
    }
}