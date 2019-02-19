package domain.entities;

public interface HasID<ID> {

    /**
     * @return id of current element
     */
    ID getID();

    /**
     * sets the id of the current element
     * @param id
     */
    void setID(ID id);
}