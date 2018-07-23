package com.example.productsservice;

import com.example.productsservice.clients.ApplicationsServiceClient;
import com.example.productsservice.clients.UsersServiceClient;
import com.example.productsservice.dto.StatisticResponse;
import com.example.productsservice.entity.Statistic;
import com.example.productsservice.entity.Product;
import com.example.productsservice.entity.User;
import com.example.productsservice.exceptions.NoAccessException;
import com.example.productsservice.repositories.ProductsRepository;
import io.jsonwebtoken.Jwts;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ProductService {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final ProductsRepository productsRepository;
    private final UsersServiceClient usersServiceClient;
    private final ApplicationsServiceClient applicationsServiceClient;
    @Autowired
    public ProductService(ApplicationsServiceClient applicationsServiceClient, UsersServiceClient usersServiceClient,
                          ProductsRepository productsRepository) {
        this.usersServiceClient = usersServiceClient;
        this.productsRepository = productsRepository;
        this.applicationsServiceClient = applicationsServiceClient;
    }

    public Product getDescriptionDebitCard() {
        logger.info("Try add debit-card");
        return getProductOfName("debit-card");
    }

    public Product getDescriptionCreditCard() {
        logger.info("Try add credit-card");
        return getProductOfName("credit-card");
    }

    public Product getDescriptionCreditCash() {
        logger.info("Try add credit-cash");
        return getProductOfName("credit-cash");
    }

    public Product getProductOfName(String checkingProduct) {
        int id = 0;

        for (Product.type product : Product.type.values()) {
            if (checkingProduct.equals(product.getName())) {
                id = product.getId();
            }
        }
        logger.info("Get product for id = " + id);
        return productsRepository.getProductOfDataBase(id);
    }

    public List<StatisticResponse> getStatisticUsesProducts(String token) {
        User user = usersServiceClient.getUserById(getIdByToken(token));

        checkUser(user);
        authenticationOfBankEmployee(user.getSecurity());

        return calculatePercent(applicationsServiceClient.getApprovedStatistics());
    }

    public List<StatisticResponse> getStatisticsNegativeApplications(String token) {
        User user = usersServiceClient.getUserById(getIdByToken(token));

        checkUser(user);
        authenticationOfBankEmployee(user.getSecurity());

        return calculatePercent(applicationsServiceClient.getNegativeStatistics());
    }


    public List<StatisticResponse> calculatePercent(List<Statistic> statistics) {
        double count = 0;
        List<StatisticResponse> statisticResponses = new ArrayList<>();
        for (Statistic statistic : statistics) {
            count += statistic.getCount();
        }

        for (Statistic statistic : statistics) {
            StatisticResponse statisticResponse = new StatisticResponse();
            statisticResponse.setPercent(Math.round(statistic.getCount() / count * 10000) / 100.0);
            statisticResponse.setName(statistic.getName());
            statisticResponse.setReason(statistic.getReason());
            statisticResponses.add(statisticResponse);
        }

        return statisticResponses;
    }

    private void checkUser(User user) {
        if (user == null) {
            logger.error("User not found");
            throw new NoAccessException();
        }
    }

    private void authenticationOfBankEmployee(int securityStatus) {
        if(!(securityStatus == User.access.EMPLOYEE_BANK.getNumber())) {
            logger.warn("User not employee bank");
            throw new NoAccessException();
        }
    }

    private long getIdByToken(String token) {
        int i = token.lastIndexOf('.');
        String tokenWithoutKey = token.substring(0,i+1);
        return Long.parseLong(Jwts.parser().parseClaimsJwt(tokenWithoutKey).getBody().getSubject());
    }
}
