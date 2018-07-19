package com.example.datausersservice;


import com.example.datausersservice.entity.User;
import com.example.datausersservice.NoAccessException;
import org.springframework.stereotype.Component;

@Component
public class UserVerificator {

    public boolean checkingUser(User user, String password) {
        if (!(user == null) && user.getPassword().equals(password)) {
            return true;
        }
        throw new NoAccessException();
    }

}
