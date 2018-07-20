package com.example.productsservice;

import com.example.productsservice.clients.ApplicationsServiceClient;
import com.example.productsservice.clients.UsersServiceClient;
import com.example.productsservice.dto.Statistic;
import com.example.productsservice.entity.Product;
import com.example.productsservice.entity.User;
import com.example.productsservice.exceptions.NoAccessException;
import com.example.productsservice.repositories.ProductsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {

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
        return getProductOfName("debit-card");
    }

    public Product getDescriptionCreditCard() {
        return getProductOfName("credit-card");
    }

    public Product getDescriptionCreditCash() {
        return getProductOfName("credit-cash");
    }

    public Product getProductOfName(String checkingProduct) {
        int id = 0;

        for (Product.type product : Product.type.values()) {
            if (checkingProduct.equals(product.getName())) {
                id = product.getId();
            }
        }

        return productsRepository.getProductOfDataBase(id);
    }

    public List<Statistic> getStatisticUsesProducts(String token) {
        User user = usersServiceClient.getUserById(usersServiceClient.getIdByToken(token));

        isExistsUser(user);
        authenticationOfBankEmployee(user.getSecurity());

        return calculatePercent(applicationsServiceClient.getApprovedStatistics());
    }

    public List<Statistic> getStatisticsNegativeApplications(String token) {
        User user = usersServiceClient.getUserById(usersServiceClient.getIdByToken(token));

        isExistsUser(user);
        authenticationOfBankEmployee(user.getSecurity());

        return calculatePercent(applicationsServiceClient.getNegativeStatistics());
    }


    public List<Statistic> calculatePercent(List<Statistic> statistics) {
        double count = 0;

        for (Statistic statistic : statistics) {
            count += statistic.getCount();
        }

        for (Statistic statistic : statistics) {
            statistic.setPercent(Math.round(statistic.getCount() / count * 10000) / 100.0);
            statistic.setCount(null);
        }

        return statistics;
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
