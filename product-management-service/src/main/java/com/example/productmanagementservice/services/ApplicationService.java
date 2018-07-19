package com.example.productmanagementservice.services;

import com.example.productmanagementservice.database.repositories.ApplicationsRepository;
import com.example.productmanagementservice.database.repositories.ProductsRepository;
import com.example.productmanagementservice.database.repositories.UsersRepository;
import com.example.productmanagementservice.database.verificators.ApplicationVerificator;
import com.example.productmanagementservice.database.verificators.ProductsVerificator;
import com.example.productmanagementservice.database.verificators.UserVerificator;
import com.example.productmanagementservice.entity.Application;
import com.example.productmanagementservice.entity.User;
import com.example.productmanagementservice.exceptions.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ApplicationService {

    private final ApplicationVerificator applicationVerificator;
    private final ProductsVerificator productsVerificator;
    private final UserVerificator userVerificator;
    private final ApplicationsRepository applicationsRepository;
    private final ProductsRepository productsRepository;
    private final LoginService loginService;
    private final UsersRepository usersRepository;
    private final ProductService productService;

    @Autowired
    public ApplicationService(LoginService loginService, ApplicationsRepository applicationsRepository,
                              ProductsRepository productsRepository, ApplicationVerificator applicationVerificator,
                              ProductsVerificator productsVerificator, UserVerificator userVerificator,
                              ProductService productService, UsersRepository usersRepository) {
        this.loginService = loginService;
        this.applicationsRepository = applicationsRepository;
        this.productsRepository = productsRepository;
        this.applicationVerificator = applicationVerificator;
        this.productsVerificator = productsVerificator;
        this.userVerificator = userVerificator;
        this.usersRepository = usersRepository;
        this.productService = productService;
    }

    private boolean checkToken(List<User> users, String token) {
        userVerificator.isExistsUser(users);
        loginService.checkTokenOnValidation(token, users.get(0).getLogin());

        return false;
    }

    public Application createApplication(String token) {
        List<User> users = usersRepository.getUsersByToken(token);
        checkToken(users, token);
        return createNewApplication(token);
    }

    public void addDebitCardToApplication(String token, long idApplication) {
        List<User> users = usersRepository.getUsersByToken(token);
        List<Application> applications = applicationsRepository.getAllClientApplications(users.get(0).getId());

        applicationVerificator.isExistsApplication(applications, idApplication);
        checkForAddProduct(users, token, idApplication);

        productsRepository.addDebitCardToApplication(idApplication);
    }

    public void addCreditCardToApplication(String token, long idApplication, int limit) {
        List<User> users = usersRepository.getUsersByToken(token);
        List<Application> applications = applicationsRepository.getAllClientApplications(users.get(0).getId());

        applicationVerificator.isExistsApplication(applications, idApplication);
        checkForAddProduct(users, token, idApplication);

        if (limit > 0 || limit <= 1000) {
            productsRepository.addCreditCardToApplication(idApplication, limit);
        } else {
            throw new IncorrectValueException();
        }
    }

    public void addCreditCashToApplication(String token, long idApplication, int amount, int timeInMonth) {
        List<User> users = usersRepository.getUsersByToken(token);
        List<Application> applications = applicationsRepository.getAllClientApplications(users.get(0).getId());

        applicationVerificator.isExistsApplication(applications, idApplication);
        checkForAddProduct(users, token, idApplication);

        if ((amount > 0 || amount <= 1000) || timeInMonth > 0) {
            productsRepository.addCreditCashToApplication(idApplication, amount, timeInMonth);
        } else {
            throw new IncorrectValueException();
        }
    }

    private void checkForAddProduct(List<User> users, String token, long idApplication) {
        checkToken(users, token);
        List<Application> applications = applicationsRepository.getAllClientApplications(users.get(0).getId());
        applicationVerificator.verificationOfBelongingApplicationToClient(applications, users.get(0).getId(), idApplication);
    }

    public List<Application> getApplicationsForApproval(String token) {
        List<User> users = usersRepository.getUsersByToken(token);
        checkToken(users, token);
        return applicationsRepository
                .getListSentApplicationsOfDataBase(users.get(0).getId(), Application.statusApp.SENT.ordinal());

    }

    public void sendApplicationForApproval(String token, long idApplication) {
        List<User> users = usersRepository.getUsersByToken(token);
        List<Application> applications = applicationsRepository.getAllClientApplications(users.get(0).getId());

        checkToken(users, token);
        applicationVerificator.isExistsApplication(applications, idApplication);
        applicationVerificator.verificationOfBelongingApplicationToClient(applications, users.get(0).getId(), idApplication);
        applicationVerificator.checkIsEmptyOfApplication(applications, idApplication);
        productsVerificator.checkProductInApplicationsClient
                (productService.getProductApplication(applications, idApplication), applications);
        checkTotalAmountMoneyHasReachedMax(idApplication);

        applicationsRepository.sendApplicationToConfirmation(idApplication, Application.statusApp.SENT.ordinal());
    }

    public List<Application> getApplicationsClientForApproval(long userId, String token) {
        List<User> users = usersRepository.getUsersByToken(token);

        checkToken(users, token);
        userVerificator.authenticationOfBankEmployee(users.get(0).getSecurity());

        return applicationsRepository.getListSentApplicationsOfDataBase(userId, Application.statusApp.SENT.ordinal());

    }

    public void approveApplication(long idApplication, String token) {
        List<User> users = usersRepository.getUsersByToken(token);
        List<Application> applications = applicationsRepository
                .getAllClientApplications(usersRepository.getUsersByIdApplication(idApplication).get(0).getId());

        checkToken(users, token);
        applicationVerificator.isExistsApplication(applications, idApplication);
        applicationVerificator.checkForChangeStatusApplication(applications, idApplication);
        productsVerificator.checkProductInApplicationsClient
                (productService.getProductApplication(applications, idApplication), applications);
        userVerificator.authenticationOfBankEmployee(users.get(0).getSecurity());
        checkTotalAmountMoneyHasReachedMax(idApplication);

        applicationsRepository.setNegativeOfAllIdenticalProducts
                (applicationsRepository.getApplicationsById(idApplication).get(0).getProduct(),
                        Application.statusApp.NEGATIVE.ordinal());
        applicationsRepository.approveApplication(idApplication, Application.statusApp.APPROVED.ordinal());

    }

    public void negativeApplication(long idApplication, String token, String reason) {
        List<User> users = usersRepository.getUsersByToken(token);
        List<Application> applications = applicationsRepository
                .getAllClientApplications(usersRepository.getUsersByIdApplication(idApplication).get(0).getId());

        checkToken(users, token);
        applicationVerificator.isExistsApplication(applications, idApplication);
        applicationVerificator.checkForChangeStatusApplication(applications, idApplication);
        userVerificator.authenticationOfBankEmployee(users.get(0).getSecurity());

        applicationsRepository.negativeApplication(idApplication, reason, Application.statusApp.NEGATIVE.ordinal());

    }

    public Application createNewApplication(String token) {
        applicationsRepository.createNewApplicationInDatabase(loginService.getIdByToken(token),
                Application.statusApp.CREATED.ordinal());
        List<Application> applications = applicationsRepository.getNewApplication(loginService.getIdByToken(token));

        return applications.get(0);
    }

    public boolean checkTotalAmountMoneyHasReachedMax(long idApplication) {
        int totalAmount = 0;

        List<Application> applications = applicationsRepository.getListApprovedApplicationsOfDatabase
                (usersRepository.getUsersByIdApplication(idApplication).get(0).getId(), Application.statusApp.APPROVED.ordinal());

        for (Application app : applications) {
            if (app.getAmount() != null) {
                totalAmount += Integer.parseInt(app.getAmount());
            }
            if (app.getLimit() != null) {
                totalAmount += Integer.parseInt(app.getLimit());
            }
        }

        Application application = applicationsRepository.getApplicationsById(idApplication).get(0);

        if (application.getLimit() != null) {
            if ((Integer.parseInt(application.getLimit()) + totalAmount) <= 1000) {
                throw new MaxAmountCreditReachedException();
            }
        }
        if (application.getAmount() != null) {
            if ((Integer.parseInt(application.getAmount()) + totalAmount) <= 1000) {
                throw new MaxAmountCreditReachedException();
            }
        }

        if (application.getAmount() == null && application.getLimit() == null) {
            throw new MaxAmountCreditReachedException();
        }
        return true;
    }
}
