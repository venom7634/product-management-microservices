package com.example.datausersservice;

import com.example.datausersservice.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;


@Mapper
@Repository
public interface UsersRepository {

    @Select("SELECT * FROM users WHERE login = #{login}")
    User getUserByLogin(@Param("login") String login);
}
