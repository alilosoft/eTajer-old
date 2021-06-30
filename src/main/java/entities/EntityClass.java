/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package entities;

import dao.TableDAO;

/**
 *
 * @author alilo
 * @param <T>
 */
public class EntityClass<T extends TableDAO> {

    private EntityPK entityPK;

    public EntityClass() {
        this(-1);
    }

    public EntityClass(int id) {
        this.entityPK = new EntityPK(id);
    }

    public EntityClass(EntityPK epk) {
        this.entityPK = epk;
    }
    
    public Integer getId() {
        return entityPK.getId();
    }

    public void setId(Integer id) {
        this.entityPK.setId(id);
    }

    public EntityPK getEntityPK() {
        return entityPK;
    }

    public void setEntityPK(EntityPK epk) {
        this.entityPK = epk;
    }
    
    public String getShortDesc(){
        return "Override me please! "+getClass().getName();
    }
    
    public T getTableDAO(){
        throw new UnsupportedOperationException("getTableDAO() must be overridden in: "+getClass().getCanonicalName());
    }
    
    public boolean insert(){
        return getTableDAO().insert(this);
    }
    
    public boolean update(){
        return getTableDAO().update(this);
    }
    
    public boolean delete(){
        return getTableDAO().delete(this.getId());
    }

    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof EntityClass)){
            return false;
        }
        EntityClass other = (EntityClass) obj;
        return this.getId().equals(other.getId());
    }

    @Override
    public int hashCode() {
        int hash = 3;
        return hash;
    }
}
