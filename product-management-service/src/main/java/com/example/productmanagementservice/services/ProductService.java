package com.example.productmanagementservice.services;

import com.example.productmanagementservice.database.repositories.ProductsRepository;
import com.example.productmanagementservice.database.repositories.UsersRepository;
import com.example.productmanagementservice.entity.Application;
import com.example.productmanagementservice.entity.User;
import com.example.productmanagementservice.entity.Product;
import com.example.productmanagementservice.dto.Statistic;
import com.example.productmanagementservice.exceptions.PageNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {

    private final ProductsRepository productsRepository;
    private final UserService userService;
    private final UsersRepository usersRepository;

    @Autowired
    public ProductService(UserService userService, ProductsRepository productsRepository, UsersRepository usersRepository) {
        this.userService = userService;
        this.productsRepository = productsRepository;
        this.usersRepository = usersRepository;
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

    public List<Product> getProductsForClient(String token, long userId) {
        User user = usersRepository.getUserById(userService.getIdByToken(token));

        userService.isExistsUser(user);
        userService.authenticationOfBankEmployee(user.getSecurity());

        return productsRepository.getProductsForClient(userId);
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
        User user = usersRepository.getUserById(userService.getIdByToken(token));

        userService.isExistsUser(user);
        userService.authenticationOfBankEmployee(user.getSecurity());

        return calculatePercent(productsRepository.getApprovedStatistics());
    }

    public List<Statistic> getStatisticsNegativeApplications(String token) {
        User user = usersRepository.getUserById(userService.getIdByToken(token));

        userService.isExistsUser(user);
        userService.authenticationOfBankEmployee(user.getSecurity());

        return calculatePercent(productsRepository.getNegativeStatistics());
    }


    public List<Statistic> calculatePercent(List<Statistic> statistics) {
        double count = 0;

        for (Statistic statistic : statistics) {
            count += statistic.getCount();
        }

        for (Statistic statistic : statistics) {
            statistic.setPercent(Math.round(statistic.getCount() / count * 10000) / 100.0);
        }

        return statistics;
    }

    public String getProductApplication(List<Application> applications, long idApplication) {
        for (Application application : applications) {
            if (application.getId() == idApplication) {
                return application.getProduct();
            }
        }
        throw new PageNotFoundException();
    }
}
