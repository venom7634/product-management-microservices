package com.example.productsservice.clients;

import com.example.productsservice.dto.Statistic;
import com.example.productsservice.entity.User;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

@FeignClient("applications-service")
public interface ApplicationsServiceClient {

    @RequestMapping(value = "/applications/getApprovedStatistics", method = RequestMethod.GET)
    List<Statistic> getApprovedStatistics();

    @RequestMapping(value = "/applications/getNegativeStatistics", method = RequestMethod.GET)
    List<Statistic> getNegativeStatistics();
}
