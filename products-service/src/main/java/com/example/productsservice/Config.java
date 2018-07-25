package com.example.productsservice;

import com.example.productsservice.entity.Token;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;

import javax.servlet.http.HttpServletRequest;
import javax.sql.DataSource;

@MapperScan("com.example.productsservice.repositories")
@Configuration
public class Config {

    private final DataSource dataSource;
    private final HttpServletRequest httpServletRequest;

    @Autowired
    public Config(DataSource dataSource, HttpServletRequest httpServletRequest) {
        this.dataSource = dataSource;
        this.httpServletRequest = httpServletRequest;
    }

    @Bean
    public SqlSessionFactory sqlSessionFactory() throws Exception {
        SqlSessionFactoryBean sessionFactory = new SqlSessionFactoryBean();
        sessionFactory.setDataSource(dataSource);
        return sessionFactory.getObject();
    }

    @Bean
    @Scope(value = "request", proxyMode = ScopedProxyMode.TARGET_CLASS)
    public Token token() {
        Token token = new Token();
        token.setToken(httpServletRequest.getHeader("token"));
        return token;
    }
}
