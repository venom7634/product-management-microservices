package com.example.datausersservice;

import com.example.datausersservice.dto.Token;
import com.example.datausersservice.entity.User;
import io.jsonwebtoken.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Service
public class LoginService {


    private final UsersServiceClient usersServiceClient;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    public LoginService(UsersServiceClient usersServiceClient) {
        this.usersServiceClient = usersServiceClient;
    }

    public Token login(String login, String password) {
        User user = usersServiceClient.getUserByLogin(login);
        checkUser(user, password);

        return new Token(createToken(login));
    }

    private String createToken(String login) {
        LocalDateTime localDateTime = LocalDateTime.now().plusMinutes(30);
        Date date = Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());

        User user = usersServiceClient.getUserByLogin(login);

        String token = Jwts.builder()
                .setSubject("" + user.getId())
                .signWith(SignatureAlgorithm.HS512, login)
                .setExpiration(date)
                .setAudience(user.getSecurity() + "")
                .compact();

        logger.info("Token created");
        return token;
    }

    private void checkUser(User user, String password) {
        if (!(user == null) && user.getPassword().equals(password)) {
        } else {
            logger.error("User not found");
            throw new NoAccessException();
        }
    }
}
