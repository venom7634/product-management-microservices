package com.example.productmanagementservice.database.verificators;

import com.example.productmanagementservice.clients.ProductsServiceClient;
import com.example.productmanagementservice.entity.Application;
import com.example.productmanagementservice.exceptions.NoAccessException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class ProductsVerificator {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final ProductsServiceClient productsServiceClient;

    @Autowired
    public ProductsVerificator(ProductsServiceClient productsServiceClient) {
        this.productsServiceClient = productsServiceClient;
    }

    public void checkProductInApplicationsClient(String product, List<Application> applications) {
        List<Application> applicationsWithProduct =
                applications
                        .stream()
                        .filter(app -> app.getProduct() != null)
                        .filter(app -> app.getProduct().equals(product)
                                && app.getStatus() == Application.statusApp.APPROVED.getStatus())
                        .collect(Collectors.toList());

        if (!applicationsWithProduct.isEmpty()) {
            logger.warn("Such product already exists by user");
            throw new NoAccessException();
        }

    }

    public void checkOnAllProductsInApplicationsClient(List<Application> applications){
        Set<String> allProducts = new HashSet<>(productsServiceClient.getAllProducts());
        Set<String> productsInApplications = new HashSet<>();

        for(Application application : applications){
            productsInApplications.add(application.getProduct());
        }

        if (!allProducts.containsAll(productsInApplications)){
            logger.warn("User already all products");
            throw new NoAccessException();
        }
    }
}
