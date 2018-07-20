package com.example.usersservice;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;


@Mapper
@Repository
public interface UsersRepository {

    @Select("SELECT * FROM users WHERE id = #{id}")
    User getUserById(@Param("id") long id);

    @Select("SELECT * FROM users WHERE login = #{login}")
    User getUserByLogin(@Param("login") String login);
}
