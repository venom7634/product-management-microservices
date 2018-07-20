package com.example.usersservice;

import io.jsonwebtoken.Jwts;
import org.springframework.stereotype.Service;


@Service
public class UserService {

    public long getIdByToken(String token) {
        int i = token.lastIndexOf('.');
        String tokenWithoutKey = token.substring(0,i+1);
        return Long.parseLong(Jwts.parser().parseClaimsJwt(tokenWithoutKey).getBody().getSubject());
    }


}
