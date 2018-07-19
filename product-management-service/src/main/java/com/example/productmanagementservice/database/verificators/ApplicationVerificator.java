package com.example.productmanagementservice.database.verificators;

import com.example.productmanagementservice.database.repositories.ApplicationsRepository;
import com.example.productmanagementservice.entity.Application;
import com.example.productmanagementservice.exceptions.ApplicationNoExistsException;
import com.example.productmanagementservice.exceptions.IncorrectValueException;
import com.example.productmanagementservice.exceptions.NoAccessException;
import com.example.productmanagementservice.exceptions.NotMatchUserException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ApplicationVerificator {

    private final ApplicationsRepository applicationsRepository;
    private final UserVerificator userVerificator;

    @Autowired
    public ApplicationVerificator(UserVerificator userVerificator, ApplicationsRepository applicationsRepository) {
        this.userVerificator = userVerificator;
        this.applicationsRepository = applicationsRepository;
    }

    public boolean isExistsApplication(List<Application> applications, long idApplication) {
        List<Application> createdApplications =
                applications
                        .stream()
                        .filter(app -> app.getStatus() == Application.statusApp.CREATED.ordinal()
                                && app.getId() == idApplication)
                        .collect(Collectors.toList());

        if (createdApplications.isEmpty()){
            throw new ApplicationNoExistsException();
        }
        return true;
    }

    public boolean verificationOfBelongingApplicationToClient(List<Application> applications, long userId, long idApplication) {
        List<Application> filteredApplications =
                applications
                        .stream()
                        .filter(app -> app.getClient_id() == userId && app.getId() == idApplication)
                        .collect(Collectors.toList());

        if(filteredApplications.isEmpty()){
            throw new NotMatchUserException();
        }
        return true;
    }



    public boolean checkForChangeStatusApplication(List<Application> applications, long idApplication) {
        List<Application> filteredApplications =
                applications
                        .stream()
                        .filter(app -> app.getStatus() == Application.statusApp.SENT.ordinal()
                                && app.getId() == idApplication)
                        .collect(Collectors.toList());
        if(filteredApplications.isEmpty()){
            throw new NoAccessException();
        }
        return true;
    }

    public boolean checkIsEmptyOfApplication(List<Application> applications, long idApplication) {
        List<Application> filteredApplications =
                applications
                        .stream()
                        .filter(app -> app.getId() == idApplication)
                        .collect(Collectors.toList());

        if(filteredApplications.isEmpty()){
            throw new ApplicationNoExistsException();
        }
        if(filteredApplications.get(0).getProduct() == null){
            throw new IncorrectValueException();
        }

        return true;
    }
}
