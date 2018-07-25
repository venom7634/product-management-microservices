package com.example.productmanagementservice.database.verificators;

import com.example.productmanagementservice.entity.Application;
import com.example.productmanagementservice.entity.User;
import com.example.productmanagementservice.exceptions.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ApplicationVerificator {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public void checkApplication(Application application) {
        if (application == null) {
            throw new PageNotFoundException();
        }
    }

    public void checkApplication(List<Application> applications, long idApplication) {
        List<Application> createdApplications =
                applications
                        .stream()
                        .filter(app -> (app.getStatus() == Application.statusApp.CREATED.getStatus())
                                && app.getId() == idApplication)
                        .collect(Collectors.toList());

        if (createdApplications.isEmpty()) {
            logger.error("Application no exists");
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
            logger.warn("Application not match user");
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
            logger.warn("No access for change status application");
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
            logger.error("Application no exists");
            throw new ApplicationNoExistsException();
        }
        if (filteredApplications.get(0).getProduct() == null) {
            logger.warn("Incorrect values");
            throw new IncorrectValueException();
        }
    }

    public void checkUser(User user) {
        if (user == null) {
            logger.error("User not created");
            throw new NoAccessException();
        }
    }

    public void authenticationOfBankEmployee(int securityStatus) {
        if (!(securityStatus == User.access.EMPLOYEE_BANK.getNumber())) {
            logger.warn("User no employee bank");
            throw new NoAccessException();
        }
    }

}
