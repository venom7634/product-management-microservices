package com.example.productsservice;

import com.example.productsservice.dto.Statistic;
import com.example.productsservice.entity.Product;
import com.example.productsservice.repositories.ProductsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class ProductsController {

    private final
    ProductService productService;

    private ProductsRepository productsRepository;
    @Autowired
    public ProductsController(ProductService productService) {
        this.productsRepository = productsRepository;
        this.productService = productService;
    }

    @RequestMapping(value = "/products/debit-card", method = RequestMethod.GET)
    public Product getDescriptionDebitCard() {
        return productService.getDescriptionDebitCard();
    }

    @RequestMapping(value = "/products/", method = RequestMethod.GET)
    public List<Product> getAllProducts() {

        return productsRepository.getAllProducts();
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
    public List<Statistic> getStatisticsApprovedApplications(@RequestHeader("token") String token) {
        return productService.getStatisticUsesProducts(token);
    }

    @RequestMapping(value = "/products/statistics/negativeApplications", method = RequestMethod.GET)
    public List<Statistic> getStatisticsNegativeApplications(@RequestHeader("token") String token) {
        return productService.getStatisticsNegativeApplications(token);
    }
}
