package com.example.productmanagementservice.services;

import com.example.productmanagementservice.clients.ProductsServiceClient;
import com.example.productmanagementservice.clients.UsersServiceClient;
import com.example.productmanagementservice.database.repositories.ApplicationsRepository;
import com.example.productmanagementservice.database.verificators.ApplicationVerificator;
import com.example.productmanagementservice.database.verificators.ProductsVerificator;
import com.example.productmanagementservice.dto.ApplicationResponse;
import com.example.productmanagementservice.entity.Application;
import com.example.productmanagementservice.entity.Token;
import com.example.productmanagementservice.entity.User;
import com.example.productmanagementservice.exceptions.*;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class ApplicationService {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final ApplicationVerificator applicationVerificator;
    private final ProductsVerificator productsVerificator;
    private final ApplicationsRepository applicationsRepository;
    private final UsersServiceClient usersServiceClient;
    private final ProductsServiceClient productsServiceClient;

    @Resource(name = "token")
    Token token;

    @Autowired
    public ApplicationService(UsersServiceClient usersServiceClient, ApplicationsRepository applicationsRepository,
                              ApplicationVerificator applicationVerificator, ProductsVerificator productsVerificator,
                              ProductsServiceClient productsServiceClient) {
        this.productsServiceClient = productsServiceClient;
        this.usersServiceClient = usersServiceClient;
        this.applicationsRepository = applicationsRepository;
        this.applicationVerificator = applicationVerificator;
        this.productsVerificator = productsVerificator;
    }

    public ApplicationResponse createApplication() {
        long id = getIdByToken(token.getToken());
        User user = usersServiceClient.getUserById(id);
        Set<String> allProducts = new HashSet<>(productsServiceClient.getAllProducts());
        applicationVerificator.checkUser(user);
        List<Application> applications = applicationsRepository.getAllClientApplications(user.getId());
        productsVerificator.checkOnAllProductsInApplicationsClient(applications, allProducts);
        applicationsRepository.createNewApplicationInDatabase(id);
        return applicationsRepository.getNewApplication(id);
    }

    public void addDebitCardToApplication(long idApplication) {
        User user = usersServiceClient.getUserById(getIdByToken(token.getToken()));
        List<Application> applications = applicationsRepository.getAllClientApplications(user.getId());

        applicationVerificator.checkApplication(applications, idApplication);
        checkForAddProduct(user, idApplication);

        applicationsRepository.addDebitCardToApplication(idApplication);
    }

    public void addCreditCardToApplication(long idApplication, int limit) {
        User user = usersServiceClient.getUserById(getIdByToken(token.getToken()));
        List<Application> applications = applicationsRepository.getAllClientApplications(user.getId());

        applicationVerificator.checkApplication(applications, idApplication);
        checkForAddProduct(user, idApplication);

        if (limit > 0 || limit <= 1000) {
            applicationsRepository.addCreditCardToApplication(idApplication, limit);
        } else {
            throw new IncorrectValueException();
        }
    }

    public void addCreditCashToApplication(long idApplication, int amount, int timeInMonth) {
        User user = usersServiceClient.getUserById(getIdByToken(token.getToken()));
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

    public List<ApplicationResponse> getApplicationsForApproval() {
        User user = usersServiceClient.getUserById(getIdByToken(token.getToken()));
        applicationVerificator.checkUser(user);
        return applicationsRepository.getListSentApplicationsOfDataBase(user.getId());
    }

    public void sendApplicationForApproval(long idApplication) {
        User user = usersServiceClient.getUserById(getIdByToken(token.getToken()));
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

    public List<ApplicationResponse> getApplicationsClientForApproval(long userId) {
        User user = usersServiceClient.getUserById(getIdByToken(token.getToken()));

        applicationVerificator.checkUser(user);
        applicationVerificator.authenticationOfBankEmployee(user.getSecurity());

        return applicationsRepository.getListSentApplicationsOfDataBase(userId);
    }

    public void approveApplication(long idApplication) {
        User user = usersServiceClient.getUserById(getIdByToken(token.getToken()));
        Application application = applicationsRepository.getApplicationById(idApplication);

        applicationVerificator.checkApplication(application);
        List<Application> applications = applicationsRepository.getAllClientApplications(application.getClientId());

        applicationVerificator.checkUser(user);
        applicationVerificator.checkForChangeStatusApplication(applications, idApplication);
        productsVerificator.checkProductInApplicationsClient
                (getProductApplication(applications, idApplication), applications);
        applicationVerificator.authenticationOfBankEmployee(user.getSecurity());
        checkTotalAmountMoneyHasReachedMax(idApplication);

        applicationsRepository.setNegativeOfAllIdenticalProducts
                (applicationsRepository.getApplicationById(idApplication).getProduct());
        applicationsRepository.approveApplication(idApplication);

    }

    public void negativeApplication(long idApplication, String reason) {
        User user = usersServiceClient.getUserById(getIdByToken(token.getToken()));
        Application application = applicationsRepository.getApplicationById(idApplication);

        applicationVerificator.checkApplication(application);
        List<Application> applications = applicationsRepository.getAllClientApplications(application.getClientId());

        applicationVerificator.checkUser(user);
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
                logger.warn("Maximum amount reached");
                throw new MaxAmountCreditReachedException();
            }
        }
        if (application.getAmount() != null) {
            if ((Integer.parseInt(application.getAmount()) + totalAmount) >= 1000) {
                logger.warn("Maximum amount reached");
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
        logger.error("No product in application or application no exists or application no user");
        throw new PageNotFoundException();
    }

    private long getIdByToken(String token) {
        int i = token.lastIndexOf('.');
        String tokenWithoutKey = token.substring(0, i + 1);
        if (tokenWithoutKey.equals("")) {
            throw new SignatureException("Signature token not valid");
        }
        return Long.parseLong(Jwts.parser().parseClaimsJwt(tokenWithoutKey).getBody().getSubject());
    }
}
