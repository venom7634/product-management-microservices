package com.example.datausersservice;

import com.example.datausersservice.entity.User;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient(serviceId = "users-service", configuration = UsersServiceConfiguration.class)
public interface UsersServiceClient {

    @RequestMapping(value = "/users/getUserByLogin", method = RequestMethod.POST)
    User getUserByLogin(@RequestBody String login);
}
