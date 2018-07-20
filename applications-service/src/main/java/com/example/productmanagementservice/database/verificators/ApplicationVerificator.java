package com.example.productmanagementservice.database.verificators;

import com.example.productmanagementservice.entity.Application;
import com.example.productmanagementservice.entity.User;
import com.example.productmanagementservice.exceptions.ApplicationNoExistsException;
import com.example.productmanagementservice.exceptions.IncorrectValueException;
import com.example.productmanagementservice.exceptions.NoAccessException;
import com.example.productmanagementservice.exceptions.NotMatchUserException;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ApplicationVerificator {

    public void isExistsApplication(List<Application> applications, long idApplication) {
        List<Application> createdApplications =
                applications
                        .stream()
                        .filter(app -> app.getStatus() == Application.statusApp.CREATED.getStatus()
                                && app.getId() == idApplication)
                        .collect(Collectors.toList());

        if (createdApplications.isEmpty()) {
            throw new ApplicationNoExistsException();
        }
    }

    public void checkApplicationToClient(List<Application> applications, long userId, long idApplication) {
        List<Application> filteredApplications =
                applications
                        .stream()
                        .filter(app -> app.getClientId() == userId && app.getId() == idApplication)
                        .collect(Collectors.toList());

        if (filteredApplications.isEmpty()) {
            throw new NotMatchUserException();
        }
    }

    public void checkForChangeStatusApplication(List<Application> applications, long idApplication) {
        List<Application> filteredApplications =
                applications
                        .stream()
                        .filter(app -> app.getStatus() == Application.statusApp.SENT.getStatus()
                                && app.getId() == idApplication)
                        .collect(Collectors.toList());
        if (filteredApplications.isEmpty()) {
            throw new NoAccessException();
        }
    }

    public void checkIsEmptyOfApplication(List<Application> applications, long idApplication) {
        List<Application> filteredApplications =
                applications
                        .stream()
                        .filter(app -> app.getId() == idApplication)
                        .collect(Collectors.toList());

        if (filteredApplications.isEmpty()) {
            throw new ApplicationNoExistsException();
        }
        if (filteredApplications.get(0).getProduct() == null) {
            throw new IncorrectValueException();
        }
    }
    public void isExistsUser(User user) {
        if (user == null) {
            throw new NoAccessException();
        }
    }

    public boolean authenticationOfBankEmployee(int securityStatus) {
        return securityStatus == User.access.EMPLOYEE_BANK.ordinal();
    }

}
