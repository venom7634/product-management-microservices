package com.example.usersservice;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class UsersController {

    private final UsersRepository usersRepository;
    private final UserService userService;

    @Autowired
    public UsersController(UsersRepository usersRepository, UserService userService) {
        this.usersRepository = usersRepository;
        this.userService = userService;
    }

    @RequestMapping(value = "/users/{id}", method = RequestMethod.GET)
    public User getUserById(@PathVariable("id") long id) {
        return usersRepository.getUserById(id);
    }

    @RequestMapping(value = "/users/userApplications/{id}", method = RequestMethod.GET)
    public User getUserByIdApplication(@PathVariable("id") long id) {
        return usersRepository.getUserByIdApplication(id);
    }

    @RequestMapping(value = "/users/getIdByToken", method = RequestMethod.POST)
    public long getIdByToken(@RequestBody String token) {
        return userService.getIdByToken(token);
    }

    @RequestMapping(value = "/users/getUserByLogin", method = RequestMethod.POST)
    public User getUserByLogin(@RequestBody String login) {
        User user = usersRepository.getUserByLogin(login);
        return user;
    }
}
