package com.example.productmanagementservice.services;

import com.example.productmanagementservice.database.repositories.ProductsRepository;
import com.example.productmanagementservice.database.repositories.UsersRepository;
import com.example.productmanagementservice.database.verificators.UserVerificator;
import com.example.productmanagementservice.entity.Application;
import com.example.productmanagementservice.entity.User;
import com.example.productmanagementservice.entity.products.Product;
import com.example.productmanagementservice.entity.products.Statistic;
import com.example.productmanagementservice.exceptions.PageNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {

    private final ProductsRepository productsRepository;
    private final LoginService loginService;
    private final UserVerificator userVerificator;
    private final UsersRepository usersRepository;

    @Autowired
    public ProductService(UsersRepository usersRepository, LoginService loginService, ProductsRepository productsRepository,
                          UserVerificator userVerificator) {
        this.loginService = loginService;
        this.userVerificator = userVerificator;
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
        List<User> users = usersRepository.getUsersByToken(token);

        userVerificator.isExistsUser(users);
        loginService.checkTokenOnValidation(token, users.get(0).getLogin());
        userVerificator.authenticationOfBankEmployee(users.get(0).getSecurity());

        return productsRepository.getProductsForClient(Application.statusApp.APPROVED.ordinal(), userId);
    }

    public Product getProductOfName(String checkingProduct) {
        int id = 0;

        for (Product.type product : Product.type.values()) {
            if (checkingProduct.equals(product.getName())) {
                id = product.ordinal() + 1;
            }
        }

        return productsRepository.getProductOfDataBase(id);
    }

    public List<Statistic> getStatisticUsesProducts(String token) {
        List<User> users = usersRepository.getUsersByToken(token);

        userVerificator.isExistsUser(users);
        loginService.checkTokenOnValidation(token, users.get(0).getLogin());
        userVerificator.authenticationOfBankEmployee(users.get(0).getSecurity());

        return calculatePercent(productsRepository.getApprovedStatistics(Application.statusApp.APPROVED.ordinal()));
    }

    public List<Statistic> getStatisticsNegativeApplications(String token) {
        List<User> users = usersRepository.getUsersByToken(token);

        userVerificator.isExistsUser(users);
        loginService.checkTokenOnValidation(token, users.get(0).getLogin());
        userVerificator.authenticationOfBankEmployee(users.get(0).getSecurity());

        return calculatePercent(productsRepository.getNegativeStatistics(Application.statusApp.NEGATIVE.ordinal()));
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

    public String getProductApplication(List<Application> applications, long idApplication){
        for(Application application : applications){
            if(application.getId() == idApplication){
                return application.getProduct();
            }
        }
        throw new PageNotFoundException();
    }
}
