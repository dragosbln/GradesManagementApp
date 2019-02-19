package domain.validator;

import domain.entities.Grade;

public class GradeValidator implements Validator<Grade> {
    @Override
    public void validate(Grade grade) {
        String msg="";
        if(grade.getGrade()<0||grade.getGrade()>10){
            msg+="Invalid grade!";
        }
        if (!msg.equals("")){
            throw new ValidationException(msg);
        }
    }
}
