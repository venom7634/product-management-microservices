package com.example.datausersservice;

import com.example.datausersservice.dto.Account;
import com.example.datausersservice.dto.Token;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LoginController {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final LoginService loginHandler;

    @Autowired
    public LoginController(LoginService loginHandler) {
        this.loginHandler = loginHandler;
    }

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public Token login(@RequestBody Account account) {
        logger.info("Try to login");
        return loginHandler.login(account.getLogin(), account.getPassword());
    }

}
