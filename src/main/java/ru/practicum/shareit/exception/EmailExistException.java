package ru.practicum.shareit.exception;

public class EmailExistException extends RuntimeException {
    public EmailExistException(String massage) {
        super((massage));
    }
}