package com.example.productmanagementservice.database.repositories;

import com.example.productmanagementservice.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface UsersRepository {

    @Select("SELECT * FROM users WHERE login = #{login}")
    List<User> getUsersByLogin(@Param("login") String login);

    @Select("SELECT * FROM users WHERE id = #{id}")
    List<User> getUsersById(@Param("id") long id);

    @Select("select users.id, login, password, security, users.name, users.description " +
            "from users JOIN applications ON users.id = client_id where applications.id = #{id}")
    List<User> getUsersByIdApplication(@Param("id") long idApplication);
}
