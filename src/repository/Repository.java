package repository;

import domain.entities.HasID;
import domain.validator.ValidationException;
import domain.validator.Validator;

import java.util.HashMap;
import java.util.Iterator;

public class Repository<ID, E extends HasID<ID>> implements CrudRepository<ID, E> {

    HashMap<ID, E> map=new HashMap<>();
    Validator<E> validator;

    /**
     * @param validator the validator to be used in the repo
     */
    public Repository(Validator<E> validator){
        this.validator=validator;
    }

    /**
     * checks if an entity exists in the repo
     * @param id the id of the entity to be checked
     * @return true, if entity already exists in repo
     *         false, otherwise
     */
    private boolean contains(ID id){
        return map.containsKey(id);
    }

    /**
     * @return number of entites currently in the repo
     */
    public int size(){
        return map.size();
    }

    @Override
    public E findOne(ID id) {
        return map.get(id);
    }

    @Override
    public Iterable<E> findAll() {
        return map.values();
    }

    @Override
    public E save(E entity) throws ValidationException {
        if(contains(entity.getID())){
            throw new RepositoryException("Id already exists!");
        }
        validator.validate(entity);
        return map.put(entity.getID(), entity);
    }

    @Override
    public E delete(ID id) {
        if(!contains(id)){
            throw new RepositoryException("This id doesn't exist!!!!!!! :((");
        }
        return map.remove(id);
    }

    @Override
    public E update(E entity) {
        if(!contains(entity.getID())){
            throw new RepositoryException("This id doesn't exist!!!!!!! :((");
        }
        return map.put(entity.getID(), entity);
    }
}
