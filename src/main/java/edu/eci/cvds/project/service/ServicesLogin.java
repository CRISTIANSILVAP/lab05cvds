package edu.eci.cvds.project.service;

import edu.eci.cvds.project.exception.UserException;

public interface ServicesLogin {
    String loginUser(String username, String password)throws UserException.UserNotFoundException, UserException.UserIncorrectPasswordException;
}
