package com.example.productmanagementservice.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

@FeignClient("products-service")
public interface ProductsServiceClient {

    @RequestMapping(value = "/products/", method = RequestMethod.GET)
    List<String> getAllProducts();
}
