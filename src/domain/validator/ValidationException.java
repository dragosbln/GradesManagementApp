package domain.validator;

public class ValidationException extends RuntimeException {
    public ValidationException(String msg){
        super(msg);
    }
}
