package com.example.productmanagementservice.services;

import com.example.productmanagementservice.database.repositories.UsersRepository;
import com.example.productmanagementservice.database.verificators.UserVerificator;
import com.example.productmanagementservice.entity.User;
import com.example.productmanagementservice.entity.data.Token;
import com.example.productmanagementservice.exceptions.NoAccessException;
import io.jsonwebtoken.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

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
        List<User> users = usersRepository.getUsersByLogin(login);
        userVerificator.checkingUser(users, password);

        return new Token(createToken(login));
    }

    private String createToken(String login) {
        LocalDateTime localDateTime = LocalDateTime.now().plusMinutes(1);
        Date date = Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());

        User user = usersRepository.getUsersByLogin(login).get(0);

        String token = Jwts.builder()
                .setSubject("" + user.getId())
                .signWith(SignatureAlgorithm.HS512, login)
                .setExpiration(date)
                .setAudience(user.getSecurity() + "")
                .compact();

        return token;
    }

    public long getIdByToken(String token) {
        int i = token.lastIndexOf('.');
        String tokenWithoutKey = token.substring(0,i+1);

        return Long.parseLong(Jwts.parser().parseClaimsJwt(tokenWithoutKey).getBody().getSubject());
    }

    public boolean checkTokenOnValidation(String token, String login) {
        try {
            Jwts.parser().setSigningKey(login).parseClaimsJws(token).getBody().getExpiration();
        } catch (ExpiredJwtException e) {
            throw new NoAccessException();
        }
        return true;
    }
}
