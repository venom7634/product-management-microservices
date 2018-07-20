package com.example.productmanagementservice.database.verificators;

import com.example.productmanagementservice.entity.Application;
import com.example.productmanagementservice.exceptions.NoAccessException;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ProductsVerificator {

    public void checkProductInApplicationsClient(String product, List<Application> applications) {
        List<Application> applicationsWithProduct =
                applications
                        .stream()
                        .filter(app -> app.getProduct() != null)
                        .filter(app -> app.getProduct().equals(product)
                                && app.getStatus() == Application.statusApp.CREATED.getStatus())
                        .collect(Collectors.toList());

        if (applicationsWithProduct.isEmpty()) {
            throw new NoAccessException();
        }
    }

}