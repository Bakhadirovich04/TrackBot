package uz.husan.usercardregister.exeptions;

public class UserBadRequestException extends RuntimeException{
    public UserBadRequestException(String message) {
        super(message);
    }
}
