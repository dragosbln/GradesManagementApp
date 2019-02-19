package domain.validator;

public interface Validator<E> {
    void validate(E e);
}
