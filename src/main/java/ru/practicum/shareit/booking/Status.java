package ru.practicum.shareit.booking;

public enum Status {
    WAITING,       //новое броннирование, ожидает поддтверждения
    APPROVED,      //бронирование подтверждено владельцем
    REJECTED,      //бронирование отклонено владельцем
    CANCELED      //бронирование отменено создателем
}
