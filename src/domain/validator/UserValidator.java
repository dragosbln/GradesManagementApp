package domain.validator;

import domain.entities.User;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UserValidator implements Validator<User> {
    @Override
    public void validate(User user) {
        String msg="";
        Pattern pattern = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[^@]+$", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(user.getEmail());
        if(!matcher.find()){
            msg+="Invalid email!";
        }

        if(user.getPassword().length()<6){
            msg+="Password is too short!";
        }

        if(!msg.equals("")){
            throw new ValidationException(msg);
        }
    }
}
