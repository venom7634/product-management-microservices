package com.example.productsservice;

import com.example.productsservice.entity.User;
import com.example.productsservice.exceptions.NoAccessException;
import io.jsonwebtoken.Jwts;
import org.springframework.stereotype.Service;


@Service
public class UserService {
    public long getIdByToken(String token) {
        int i = token.lastIndexOf('.');
        String tokenWithoutKey = token.substring(0,i+1);
        return Long.parseLong(Jwts.parser().parseClaimsJwt(tokenWithoutKey).getBody().getSubject());
    }

    public void isExistsUser(User user) {
        if (user == null) {
            throw new NoAccessException();
        }
    }

    public boolean authenticationOfBankEmployee(int securityStatus) {
        return securityStatus == User.access.EMPLOYEE_BANK.ordinal();
    }
}
