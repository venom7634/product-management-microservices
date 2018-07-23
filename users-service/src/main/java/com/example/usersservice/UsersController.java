package com.example.usersservice;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class UsersController {

    private final UsersRepository usersRepository;

    @Autowired
    public UsersController(UsersRepository usersRepository) {
        this.usersRepository = usersRepository;
    }

    @RequestMapping(value = "/users/{id}", method = RequestMethod.GET)
    public User getUserById(@PathVariable("id") long id) {
        return usersRepository.getUserById(id);
    }

    @RequestMapping(value = "/users/getUserByLogin", method = RequestMethod.POST)
    public User getUserByLogin(@RequestBody String login) {
        User user = usersRepository.getUserByLogin(login);
        return user;
    }
}
