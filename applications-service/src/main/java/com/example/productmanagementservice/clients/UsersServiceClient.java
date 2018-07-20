package com.example.productmanagementservice.clients;

import com.example.productmanagementservice.entity.User;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient("users-service")
public interface UsersServiceClient {

    @RequestMapping(value = "/users/{id}", method = RequestMethod.GET)
    User getUserById(@PathVariable("id") long id);

    @RequestMapping(value = "/users/getIdByToken", method = RequestMethod.POST)
    long getIdByToken(@RequestBody String token);

    @RequestMapping(value = "/users/userApplications/{id}", method = RequestMethod.GET)
    User getUserByIdApplication(@PathVariable("id") long id);
}
