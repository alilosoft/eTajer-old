/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package panels.maj;

import entities.EntityClass;
import javax.swing.JOptionPane;
import panels.crud.ListeGroupsPanel;
import dao.UserDAO;
import entities.AppUser;
import entities.UserGp;
import java.awt.Component;
import panels.CRUDPanel;

/**
 *
 * @author alilo
 */
public class MajUtilisateurPanel extends MajPanel<AppUser, UserDAO> {

    private final ListeGroupsPanel rolesPanel = new ListeGroupsPanel(this, false).initTableView();
    ;
    private UserGp group;
    private boolean allowRoleChange = true;
    private String login;
    private String pw;

    /**
     * Creates new form MajCategoriePanel
     *
     * @param listPanel
     */
    public MajUtilisateurPanel(CRUDPanel listPanel) {
        super(listPanel);
        initComponents();
        initMajPanel(fieldsPanel);
        selectGrpPanel.setSelListPanel(rolesPanel);
    }

    // Getters
    @Override
    public UserDAO getTableDAO() {
        return UserDAO.getInstance();
    }

    @Override
    public Component getDefaultFocusedComp() {
        return selectGrpPanel.getDescField();
    }

    public void setAllowRoleChange(boolean allowRoleChange) {
        selectGrpPanel.setAllowSelChange(allowRoleChange);
    }

    @Override
    public void setChildEntity(EntityClass child, boolean allowChng) {
        if (child instanceof UserGp) {
            group = (UserGp) child;
            selectGrpPanel.setSelEntity(group);
            selectGrpPanel.setAllowSelChange(allowChng);
            loginField.requestFocus();
        }
    }

    @java.lang.Override
    public void initFields(AppUser oldEntity) {
        group = oldEntity.getUserGp();
        selectGrpPanel.setSelEntity(group);
        if (oldEntity.isAdmin() && getTableDAO().isLastAdmin()) {
            setAllowRoleChange(false);
        }
        loginField.setText(oldEntity.getLogin());
        pwField.setText(oldEntity.getPw());
    }

    @Override
    public boolean verifyFields() {
        group = selectGrpPanel.getSelEntity();
        String loginTxt = loginField.getText().trim();
        String pwTxt = "";
        String pwConfirmTxt = "";

        if ((group == null) || (loginTxt.length() == 0)) {
            String mess = "";
            if (group == null) {
                mess = "Séléctionner le type de cet utilisateur SVP!";
                selectGrpPanel.getDescField().requestFocus();
            }
            if (loginTxt.length() == 0) {
                mess = "Entrée le nom d'utilisateur SVP!";
                loginField.requestFocus();
            }
            JOptionPane.showMessageDialog(this, mess, "Attention...", JOptionPane.WARNING_MESSAGE);
            return false;
        }

        if (pwField.getPassword().length == 0) {
            int rep = JOptionPane.showConfirmDialog(pwField, "Voullez vous vraiment créer cet utilisateur sans mot de passe?", "Attention!", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (rep == JOptionPane.NO_OPTION) {
                pwField.requestFocus();
                return false;
            }
        }

        pwTxt = String.valueOf(pwField.getPassword());
        pwConfirmTxt = String.valueOf(pwConfirmField.getPassword());

        if (!pwTxt.equals(pwConfirmTxt)) {
            JOptionPane.showMessageDialog(this, "Les deux mots de passe ne sont pas identique!", "Attention...", JOptionPane.WARNING_MESSAGE);
            pwConfirmField.requestFocus();
            return false;
        }

        this.login = loginTxt.toUpperCase();
        this.pw = pwTxt;
        return true;
    }

    @Override
    public boolean save() {
        if (verifyFields()) {
            setEditedEntity(new AppUser(0, login, pw));
            getEditedEntity().setUserGp(group);
            return super.save();
        } else {
            setSaved(false);
            return false;
        }
    }

    @Override
    public void clearFields() {
        setAllowRoleChange(true);
        loginField.setText("");
        pwField.setText("");
        pwConfirmField.setText("");
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        fieldsPanel = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        selectGrpPanel = new panels.SelectionPanel<entities.UserGp>();
        jPanel2 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        loginField = new myComponents.MyJField();
        jPanel3 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        pwField = new javax.swing.JPasswordField();
        jPanel4 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        pwConfirmField = new javax.swing.JPasswordField();

        fieldsPanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 5, 5, 5));

        jPanel1.setLayout(new javax.swing.BoxLayout(jPanel1, javax.swing.BoxLayout.LINE_AXIS));

        jLabel6.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel6.setText("Rôle.Utilisateur:  ");
        jLabel6.setPreferredSize(new java.awt.Dimension(95, 15));
        jPanel1.add(jLabel6);
        jPanel1.add(selectGrpPanel);

        jPanel2.setLayout(new javax.swing.BoxLayout(jPanel2, javax.swing.BoxLayout.LINE_AXIS));

        jLabel1.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel1.setText("Nom.Utilisateur:");
        jLabel1.setPreferredSize(new java.awt.Dimension(95, 15));
        jPanel2.add(jLabel1);

        loginField.setPrefsLangKey("MajUtilis_Nom_Lang");
        jPanel2.add(loginField);

        jPanel3.setLayout(new javax.swing.BoxLayout(jPanel3, javax.swing.BoxLayout.LINE_AXIS));

        jLabel2.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel2.setText("Mot.De.Passe:");
        jLabel2.setPreferredSize(new java.awt.Dimension(95, 15));
        jPanel3.add(jLabel2);
        jPanel3.add(pwField);

        jPanel4.setLayout(new javax.swing.BoxLayout(jPanel4, javax.swing.BoxLayout.LINE_AXIS));

        jLabel3.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel3.setText("Confirmer.M.P:");
        jLabel3.setPreferredSize(new java.awt.Dimension(95, 15));
        jPanel4.add(jLabel3);
        jPanel4.add(pwConfirmField);

        javax.swing.GroupLayout fieldsPanelLayout = new javax.swing.GroupLayout(fieldsPanel);
        fieldsPanel.setLayout(fieldsPanelLayout);
        fieldsPanelLayout.setHorizontalGroup(
            fieldsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 407, Short.MAX_VALUE)
            .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, 407, Short.MAX_VALUE)
            .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, 407, Short.MAX_VALUE)
        );
        fieldsPanelLayout.setVerticalGroup(
            fieldsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(fieldsPanelLayout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        setLayout(null);
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel fieldsPanel;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private myComponents.MyJField loginField;
    private javax.swing.JPasswordField pwConfirmField;
    private javax.swing.JPasswordField pwField;
    private panels.SelectionPanel<entities.UserGp> selectGrpPanel;
    // End of variables declaration//GEN-END:variables

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {

        java.awt.EventQueue.invokeLater(new Runnable() {

            @Override
            public void run() {
                new MajUtilisateurPanel(null).showNewPanel(null);
            }
        });
    }
}