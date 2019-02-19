package domain.validator;

import domain.entities.Homework;

public class HomeworkValidator implements Validator<Homework> {
    @Override
    public void validate(Homework homework) {
        String msg="";
        if(homework.getDeadlineWeek()<0){
            msg+="Deadline can't be negative!";
        }
        if(!msg.equals("")){
            throw new ValidationException(msg);
        }
    }
}
