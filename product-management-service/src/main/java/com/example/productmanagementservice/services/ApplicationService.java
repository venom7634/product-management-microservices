package com.example.productmanagementservice.services;

import com.example.productmanagementservice.database.repositories.ApplicationsRepository;
import com.example.productmanagementservice.database.repositories.ProductsRepository;
import com.example.productmanagementservice.database.repositories.UsersRepository;
import com.example.productmanagementservice.database.verificators.ApplicationVerificator;
import com.example.productmanagementservice.database.verificators.ProductsVerificator;
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
    private final UserService userService;
    private final ApplicationsRepository applicationsRepository;
    private final ProductsRepository productsRepository;
    private final ProductService productService;
    private final UsersRepository usersRepository;

    @Autowired
    public ApplicationService(UserService userService, ApplicationsRepository applicationsRepository,
                              ProductsRepository productsRepository, ApplicationVerificator applicationVerificator,
                              ProductsVerificator productsVerificator, ProductService productService,
                              UsersRepository usersRepository) {
        this.usersRepository = usersRepository;
        this.userService = userService;
        this.applicationsRepository = applicationsRepository;
        this.productsRepository = productsRepository;
        this.applicationVerificator = applicationVerificator;
        this.productsVerificator = productsVerificator;
        this.productService = productService;
    }

    public Application createApplication(String token) {
        User user = usersRepository.getUserById(userService.getIdByToken(token));
        userService.isExistsUser(user);
        applicationsRepository.createNewApplicationInDatabase(userService.getIdByToken(token));
        return applicationsRepository.getNewApplication(userService.getIdByToken(token));
    }

    public void addDebitCardToApplication(String token, long idApplication) {
        User user = usersRepository.getUserById(userService.getIdByToken(token));
        List<Application> applications = applicationsRepository.getAllClientApplications(user.getId());

        applicationVerificator.isExistsApplication(applications, idApplication);
        checkForAddProduct(user, idApplication);

        productsRepository.addDebitCardToApplication(idApplication);
    }

    public void addCreditCardToApplication(String token, long idApplication, int limit) {
        User user = usersRepository.getUserById(userService.getIdByToken(token));
        List<Application> applications = applicationsRepository.getAllClientApplications(user.getId());

        applicationVerificator.isExistsApplication(applications, idApplication);
        checkForAddProduct(user, idApplication);

        if (limit > 0 || limit <= 1000) {
            productsRepository.addCreditCardToApplication(idApplication, limit);
        } else {
            throw new IncorrectValueException();
        }
    }

    public void addCreditCashToApplication(String token, long idApplication, int amount, int timeInMonth) {
        User user = usersRepository.getUserById(userService.getIdByToken(token));
        List<Application> applications = applicationsRepository.getAllClientApplications(user.getId());

        applicationVerificator.isExistsApplication(applications, idApplication);
        checkForAddProduct(user, idApplication);

        if ((amount > 0 || amount <= 1000) || timeInMonth > 0) {
            productsRepository.addCreditCashToApplication(idApplication, amount, timeInMonth);
        } else {
            throw new IncorrectValueException();
        }
    }

    private void checkForAddProduct(User user, long idApplication) {
        userService.isExistsUser(user);
        List<Application> applications = applicationsRepository.getAllClientApplications(user.getId());
        applicationVerificator.checkApplicationToClient(applications, user.getId(), idApplication);
    }

    public List<Application> getApplicationsForApproval(String token) {
        User user = usersRepository.getUserById(userService.getIdByToken(token));
        userService.isExistsUser(user);
        return applicationsRepository.getListSentApplicationsOfDataBase(user.getId());
    }

    public void sendApplicationForApproval(String token, long idApplication) {
        User user = usersRepository.getUserById(userService.getIdByToken(token));
        List<Application> applications = applicationsRepository.getAllClientApplications(user.getId());

        userService.isExistsUser(user);
        applicationVerificator.isExistsApplication(applications, idApplication);
        applicationVerificator.checkApplicationToClient(applications, user.getId(), idApplication);
        applicationVerificator.checkIsEmptyOfApplication(applications, idApplication);
        productsVerificator.checkProductInApplicationsClient
                (productService.getProductApplication(applications, idApplication), applications);
        checkTotalAmountMoneyHasReachedMax(idApplication);

        applicationsRepository.sendApplicationToConfirmation(idApplication);
    }

    public List<Application> getApplicationsClientForApproval(long userId, String token) {
        User user = usersRepository.getUserById(userService.getIdByToken(token));

        userService.isExistsUser(user);
        userService.authenticationOfBankEmployee(user.getSecurity());

        return applicationsRepository.getListSentApplicationsOfDataBase(userId);
    }

    public void approveApplication(long idApplication, String token) {
        User user = usersRepository.getUserById(userService.getIdByToken(token));
        List<Application> applications = applicationsRepository
                .getAllClientApplications(usersRepository.getUserByIdApplication(idApplication).getId());

        userService.isExistsUser(user);
        applicationVerificator.isExistsApplication(applications, idApplication);
        applicationVerificator.checkForChangeStatusApplication(applications, idApplication);
        productsVerificator.checkProductInApplicationsClient
                (productService.getProductApplication(applications, idApplication), applications);
        userService.authenticationOfBankEmployee(user.getSecurity());
        checkTotalAmountMoneyHasReachedMax(idApplication);

        applicationsRepository.setNegativeOfAllIdenticalProducts
                (applicationsRepository.getApplicationById(idApplication).getProduct());
        applicationsRepository.approveApplication(idApplication);

    }

    public void negativeApplication(long idApplication, String token, String reason) {
        User user = usersRepository.getUserById(userService.getIdByToken(token));
        List<Application> applications = applicationsRepository
                .getAllClientApplications(usersRepository.getUserByIdApplication(idApplication).getId());

        userService.isExistsUser(user);
        applicationVerificator.isExistsApplication(applications, idApplication);
        applicationVerificator.checkForChangeStatusApplication(applications, idApplication);
        userService.authenticationOfBankEmployee(user.getSecurity());

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
            if ((Integer.parseInt(application.getLimit()) + totalAmount) <= 1000) {
                throw new MaxAmountCreditReachedException();
            }
        }
        if (application.getAmount() != null) {
            if ((Integer.parseInt(application.getAmount()) + totalAmount) <= 1000) {
                throw new MaxAmountCreditReachedException();
            }
        }
    }
}
