package domain.validator;

import domain.entities.Student;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StudentValidator implements Validator<Student> {
    @Override
    public void validate(Student student) {
        String msg="";
        if(student.getID().equals("")||student.getEmail().equals("")||student.getName().equals("")||student.getGroup().equals("")){
            msg+="No fields can be empty!";
        }
        Pattern pattern = Pattern.compile("^^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[^@]+$", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(student.getEmail());
        if(!matcher.find()){
            msg+="Invalid email!";
        }

        if(!msg.equals("")){
            throw new ValidationException(msg);
        }
    }
}
