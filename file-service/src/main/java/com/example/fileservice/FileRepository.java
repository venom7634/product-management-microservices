package com.example.fileservice;

import com.example.fileservice.entity.UserFile;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface FileRepository {


    @Insert("INSERT INTO files (user_id, name, size, accessibility) " +
            "VALUES (#{userId},#{name},#{size},#{accessibility})")
    void addFileInDataBase(@Param("userId") long userId, @Param("name") String name,
                           @Param("size") long size, @Param("accessibility") int accessibility);

    @Select("SELECT *,user_id as userId FROM files WHERE id = #{id}")
    UserFile getFileById(@Param("id") long id);

    @Delete("DELETE FROM files WHERE id = #{id}")
    void deleteFile(@Param("id") long id);

    @Select("SELECT *, user_id as userId FROM files WHERE user_id = #{userId}")
    List<UserFile> getAllUserFiles(@Param("userId") long userId);
}
