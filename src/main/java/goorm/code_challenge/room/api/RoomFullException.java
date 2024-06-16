package goorm.code_challenge.room.api;

public class RoomFullException extends RuntimeException {
    public RoomFullException(String message) {
        super(message);
    }
}

