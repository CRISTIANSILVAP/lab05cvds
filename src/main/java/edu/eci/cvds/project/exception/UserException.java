package edu.eci.cvds.project.exception;

public class UserException extends Exception{
    public UserException(String message){
        super(message);
    }

    public static class UserNotFoundException extends UserException {
        public UserNotFoundException(String message){
            super(message);
        }
    }

    public static class UserIncorrectPasswordException extends UserException {
        public UserIncorrectPasswordException(String message){
            super(message);
        }
    }
}
