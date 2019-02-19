package service;

import domain.entities.User;
import domain.validator.ValidationException;
import repository.UsersXMLRepo;
import util.Password;
import util.UserType;

import java.util.Observable;

public class AuthService extends Observable {
    UsersXMLRepo usersXMLRepo;

    public AuthService(UsersXMLRepo usersXMLRepo){
        this.usersXMLRepo=usersXMLRepo;
    }

    public void addUser(String email, String password) throws Exception {
        String hashedPass=password;
        hashedPass = Password.getSaltedHash(password);
        usersXMLRepo.save(new User(email, hashedPass));
    }

    public void updateUser(String email, String type){
        User u = usersXMLRepo.findOne(email);
        u.setType(UserType.valueOf(type));
        usersXMLRepo.update(u);
        setChanged();
        notifyObservers();
        clearChanged();
    }

    public void deleteUser(String email){
        int admins=0;
        for(User u:getAll()){
            if(u.getType()==UserType.ADMIN)
                admins++;
        }
        if(admins<2){
            throw new ValidationException("Can't delete the only admin!!!");
        }

        usersXMLRepo.delete(email);
        setChanged();
        notifyObservers();
        clearChanged();
    }

    public Iterable<User> getAll(){
        return usersXMLRepo.findAll();
    }

    public void login(String email, String password) throws Exception {
        for(User u:getAll()){
            if(u.getEmail().equals(email))
                if(Password.check(password, u.getPassword())){
                    setChanged();
                    notifyObservers(u);
                    clearChanged();
                    return;
                }
                else throw new ValidationException("Invalid password!");
        }
        throw new ValidationException("Invalid email!");
    }
}
