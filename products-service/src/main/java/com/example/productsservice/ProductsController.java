package com.example.productsservice;

import com.example.productsservice.dto.StatisticResponse;
import com.example.productsservice.entity.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class ProductsController {

    private final
    ProductService productService;

    @Autowired
    public ProductsController(ProductService productService) {
        this.productService = productService;
    }

    @RequestMapping(value = "/products/debit-card", method = RequestMethod.GET)
    public Product getDescriptionDebitCard() {
        return productService.getDescriptionDebitCard();
    }

    @RequestMapping(value = "/products", method = RequestMethod.GET)
    public List<String> getAllProducts() {
        return productService.getAllProducts();
    }


    @RequestMapping(value = "/products/credit-card", method = RequestMethod.GET)
    public Product getDescriptionCreditCard() {
        return productService.getDescriptionCreditCard();
    }

    @RequestMapping(value = "/products/credit-cash", method = RequestMethod.GET)
    public Product getDescriptionCreditCash() {
        return productService.getDescriptionCreditCash();
    }


    @RequestMapping(value = "/products/statistics/approvedApplications", method = RequestMethod.GET)
    public List<StatisticResponse> getStatisticsApprovedApplications() {
        return productService.getStatisticUsesProducts();
    }

    @RequestMapping(value = "/products/statistics/negativeApplications", method = RequestMethod.GET)
    public List<StatisticResponse> getStatisticsNegativeApplications() {
        return productService.getStatisticsNegativeApplications();
    }
}
