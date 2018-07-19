package com.example.productmanagementservice.database.verificators;

import com.example.productmanagementservice.database.repositories.UsersRepository;
import com.example.productmanagementservice.entity.User;
import com.example.productmanagementservice.exceptions.NoAccessException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class UserVerificator {

    private final UsersRepository usersRepository;

    @Autowired
    public UserVerificator(UsersRepository usersRepository) {
        this.usersRepository = usersRepository;
    }

    public boolean checkingUser(List<User> users, String password) {
        if (!users.isEmpty() && users.get(0).getPassword().equals(password)) {
            return true;
        }
        throw new NoAccessException();
    }

    public boolean authenticationOfBankEmployee(int securityStatus) {
        return securityStatus == User.access.EMPLOYEE_BANK.ordinal();
    }

    public boolean isExistsUser(List<User> users) {
        if(users.isEmpty()){
            throw new NoAccessException();
        }
        return true;
    }
}
