package com.example.productmanagementservice.services;

import com.example.productmanagementservice.clients.UsersServiceClient;
import com.example.productmanagementservice.database.repositories.ApplicationsRepository;
import com.example.productmanagementservice.database.verificators.ApplicationVerificator;
import com.example.productmanagementservice.database.verificators.ProductsVerificator;
import com.example.productmanagementservice.dto.ApplicationResponse;
import com.example.productmanagementservice.entity.Application;
import com.example.productmanagementservice.entity.User;
import com.example.productmanagementservice.exceptions.*;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ApplicationService {

    private final ApplicationVerificator applicationVerificator;
    private final ProductsVerificator productsVerificator;
    private final ApplicationsRepository applicationsRepository;
    private final UsersServiceClient usersServiceClient;

    @Autowired
    public ApplicationService(UsersServiceClient usersServiceClient, ApplicationsRepository applicationsRepository,
                              ApplicationVerificator applicationVerificator, ProductsVerificator productsVerificator) {

        this.usersServiceClient = usersServiceClient;
        this.applicationsRepository = applicationsRepository;
        this.applicationVerificator = applicationVerificator;
        this.productsVerificator = productsVerificator;
    }

    public ApplicationResponse createApplication(String token) {
        long id = getIdByToken(token);
        User user = usersServiceClient.getUserById(id);
        applicationVerificator.checkUser(user);
        List<Application> applications = applicationsRepository.getAllClientApplications(user.getId());
        productsVerificator.checkOnAllProductsInApplicationsClient(applications);
        applicationsRepository.createNewApplicationInDatabase(id);
        return applicationsRepository.getNewApplication(id);
    }

    public void addDebitCardToApplication(String token, long idApplication) {
        User user = usersServiceClient.getUserById(getIdByToken(token));
        List<Application> applications = applicationsRepository.getAllClientApplications(user.getId());

        applicationVerificator.checkApplication(applications, idApplication);
        checkForAddProduct(user, idApplication);

        applicationsRepository.addDebitCardToApplication(idApplication);
    }

    public void addCreditCardToApplication(String token, long idApplication, int limit) {
        User user = usersServiceClient.getUserById(getIdByToken(token));
        List<Application> applications = applicationsRepository.getAllClientApplications(user.getId());

        applicationVerificator.checkApplication(applications, idApplication);
        checkForAddProduct(user, idApplication);

        if (limit > 0 || limit <= 1000) {
            applicationsRepository.addCreditCardToApplication(idApplication, limit);
        } else {
            throw new IncorrectValueException();
        }
    }

    public void addCreditCashToApplication(String token, long idApplication, int amount, int timeInMonth) {
        User user = usersServiceClient.getUserById(getIdByToken(token));
        List<Application> applications = applicationsRepository.getAllClientApplications(user.getId());

        applicationVerificator.checkApplication(applications, idApplication);
        checkForAddProduct(user, idApplication);

        if ((amount > 0 || amount <= 1000) || timeInMonth > 0) {
            applicationsRepository.addCreditCashToApplication(idApplication, amount, timeInMonth);
        } else {
            throw new IncorrectValueException();
        }
    }

    private void checkForAddProduct(User user, long idApplication) {
        applicationVerificator.checkUser(user);
        List<Application> applications = applicationsRepository.getAllClientApplications(user.getId());
        applicationVerificator.checkApplicationToClient(applications, user.getId(), idApplication);
        productsVerificator.checkProductInApplicationsClient
                (getProductApplication(applications, idApplication), applications);
    }

    public List<ApplicationResponse> getApplicationsForApproval(String token) {
        User user = usersServiceClient.getUserById(getIdByToken(token));
        applicationVerificator.checkUser(user);
        return applicationsRepository.getListSentApplicationsOfDataBase(user.getId());
    }

    public void sendApplicationForApproval(String token, long idApplication) {
        User user = usersServiceClient.getUserById(getIdByToken(token));
        List<Application> applications = applicationsRepository.getAllClientApplications(user.getId());

        applicationVerificator.checkUser(user);
        applicationVerificator.checkApplication(applications, idApplication);
        applicationVerificator.checkApplicationToClient(applications, user.getId(), idApplication);
        applicationVerificator.checkIsEmptyOfApplication(applications, idApplication);
        productsVerificator.checkProductInApplicationsClient
                (getProductApplication(applications, idApplication), applications);

        checkTotalAmountMoneyHasReachedMax(idApplication);

        applicationsRepository.sendApplicationToConfirmation(idApplication);
    }

    public List<ApplicationResponse> getApplicationsClientForApproval(long userId, String token) {
        User user = usersServiceClient.getUserById(getIdByToken(token));

        applicationVerificator.checkUser(user);
        applicationVerificator.authenticationOfBankEmployee(user.getSecurity());

        return applicationsRepository.getListSentApplicationsOfDataBase(userId);
    }

    public void approveApplication(long idApplication, String token) {
        User user = usersServiceClient.getUserById(getIdByToken(token));
        List<Application> applications = applicationsRepository
                .getAllClientApplications(usersServiceClient.getUserById
                        (applicationsRepository.getIdUserByApplications(idApplication)).getId());

        applicationVerificator.checkUser(user);
        applicationVerificator.checkApplication(applications, idApplication);
        applicationVerificator.checkForChangeStatusApplication(applications, idApplication);
        productsVerificator.checkProductInApplicationsClient
                (getProductApplication(applications, idApplication), applications);
        applicationVerificator.authenticationOfBankEmployee(user.getSecurity());
        checkTotalAmountMoneyHasReachedMax(idApplication);

        applicationsRepository.setNegativeOfAllIdenticalProducts
                (applicationsRepository.getApplicationById(idApplication).getProduct());
        applicationsRepository.approveApplication(idApplication);

    }

    public void negativeApplication(long idApplication, String token, String reason) {
        User user = usersServiceClient.getUserById(getIdByToken(token));
        List<Application> applications = applicationsRepository
                .getAllClientApplications(usersServiceClient.getUserById
                        (applicationsRepository.getIdUserByApplications(idApplication)).getId());

        applicationVerificator.checkUser(user);
        applicationVerificator.checkApplication(applications, idApplication);
        applicationVerificator.checkForChangeStatusApplication(applications, idApplication);
        applicationVerificator.authenticationOfBankEmployee(user.getSecurity());

        applicationsRepository.negativeApplication(idApplication, reason);

    }

    private void checkTotalAmountMoneyHasReachedMax(long idApplication) {
        int totalAmount = 0;

        Application application = applicationsRepository.getApplicationById(idApplication);
        List<Application> applications = applicationsRepository.getListApprovedApplicationsOfDatabase(application.getClientId());

        for (Application app : applications) {
            if (app.getAmount() != null) {
                totalAmount += Integer.parseInt(app.getAmount());
            }
            if (app.getLimit() != null) {
                totalAmount += Integer.parseInt(app.getLimit());
            }
        }

        if (application.getLimit() != null) {
            if ((Integer.parseInt(application.getLimit()) + totalAmount) >= 1000) {
                throw new MaxAmountCreditReachedException();
            }
        }
        if (application.getAmount() != null) {
            if ((Integer.parseInt(application.getAmount()) + totalAmount) >= 1000) {
                throw new MaxAmountCreditReachedException();
            }
        }
    }

    private String getProductApplication(List<Application> applications, long idApplication) {
        for (Application application : applications) {
            if (application.getId() == idApplication) {
                return application.getProduct();
            }
        }
        throw new PageNotFoundException();
    }

    private long getIdByToken(String token) {
        int i = token.lastIndexOf('.');
        String tokenWithoutKey = token.substring(0,i+1);
        return Long.parseLong(Jwts.parser().parseClaimsJwt(tokenWithoutKey).getBody().getSubject());
    }
}
