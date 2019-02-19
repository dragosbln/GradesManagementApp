package domain.entities;

import sun.security.util.Password;
import util.UserType;

public class User implements HasID<String> {
    private String email;
    private String password;
    private UserType type;

    public User(){}

    public User(String email, String password){
        this.email=email;
        this.type=UserType.RESTRICTED;
        this.password=password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public UserType getType() {
        return type;
    }

    public void setType(UserType type) {
        this.type = type;
    }

    @Override
    public String getID() {
        return email;
    }

    @Override
    public void setID(String s) { }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
