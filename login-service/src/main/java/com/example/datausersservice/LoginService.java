package com.example.datausersservice;

import com.example.datausersservice.dto.Token;
import com.example.datausersservice.entity.User;
import io.jsonwebtoken.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Service
public class LoginService {

    private final UsersRepository usersRepository;
    private final UserVerificator userVerificator;

    @Autowired
    public LoginService(UserVerificator userVerificator, UsersRepository usersRepository) {
        this.userVerificator = userVerificator;
        this.usersRepository = usersRepository;
    }

    public Token login(String login, String password) {
        User user = usersRepository.getUserByLogin(login);
        userVerificator.checkingUser(user, password);

        return new Token(createToken(login));
    }

    private String createToken(String login) {
        LocalDateTime localDateTime = LocalDateTime.now().plusMinutes(1);
        Date date = Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());

        User user = usersRepository.getUserByLogin(login);

        String token = Jwts.builder()
                .setSubject("" + user.getId())
                .signWith(SignatureAlgorithm.HS512, login)
                .setExpiration(date)
                .setAudience(user.getSecurity() + "")
                .compact();

        return token;
    }
}
