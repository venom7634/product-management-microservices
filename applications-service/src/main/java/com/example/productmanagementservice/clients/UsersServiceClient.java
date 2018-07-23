package com.example.productmanagementservice.clients;

import com.example.productmanagementservice.dto.UserRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient("users-service")
public interface UsersServiceClient {

    @RequestMapping(value = "/users/{id}", method = RequestMethod.GET)
    UserRequest getUserById(@PathVariable("id") long id);
}
