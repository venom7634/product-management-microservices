package com.example.productsservice;


import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;

@FeignClient(serviceId = "applications-service")
public interface ApplicationsServiceClient {


}
