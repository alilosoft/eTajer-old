package panels;

import entities.EntityClass;
import java.awt.Container;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.event.ListSelectionEvent;
import myComponents.MyJListe;
import myModels.ResultSet2ListeModel;
import dao.TableDAO;

//this panel used for showing resultset as JListe
public abstract class ResultSet2Liste_Panel<Entity extends EntityClass, DAO extends TableDAO> extends ResultSet_Panel<Entity, DAO, ResultSet2ListeModel> {

    protected MyJListe liste;
    protected ResultSet2ListeModel model;

    public ResultSet2Liste_Panel(Container owner, ResultSet2ListeModel model) {
        super(owner);

        this.model = model;
        this.liste = new MyJListe(model, model.isCheckable()) {

            @Override
            public void doOnSelectionChange(ListSelectionEvent evt, int selIndex) {
                setModelSelectedRow(selIndex);
            }
        };
        //add the tabe to the scrollpane
        scrollPan.setViewportView(getList());
    }

    //Setters
    public void setList(MyJListe liste) {
        this.liste = liste;
    }

    @Override
    public void setModel(ResultSet2ListeModel model) {
        this.model = model;
    }

    
    //Getters
    public final MyJListe getList() {
        return liste;
    }

    @Override
    public ResultSet2ListeModel getModel() {
        return model;
    }

    @Override
    public void clearSelection() {
        getList().getSelectionModel().clearSelection();
    }

    /**
     * Select the new row after reload
     */
    @Override
    public void insert() {
        super.insert();
        getList().setSelectedIndex(getModel().getIndexOfID(getTableDAO().getGeneratedID()));
    }

    @Override
    public void edit() {
        super.edit();
        /**
         * Select the edited row after reload In a List the index of
         */
        getList().setSelectedIndex(getOldModelSelRow());
    }

    @Override
    public void masterRowChanged(String masterColName) {
       
    }

    /**
     * customization section
     * @return 
     */
    public abstract ResultSet2Liste_Panel customizedTableView();

    public void customizeListBehavior() {
        // customize the liste Behavior
        getList().addKeyListener(new KeyAdapter() {

            @Override
            public void keyPressed(KeyEvent e) {
                //doOnKeyPressed(e);
            }
        });

        getList().addMouseListener(new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent e) {
                //super.mouseClicked(e);
                if (e.getClickCount() == 2) {
                    //doOnMouseDoubleClicked(e);
                }
            }
        });
    }
}
