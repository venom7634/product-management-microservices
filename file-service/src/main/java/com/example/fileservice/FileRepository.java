package com.example.fileservice;

import com.example.fileservice.entity.UserFile;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface FileRepository {


    @Insert("INSERT INTO files (user_id, name) VALUES (#{userId},#{name})")
    void addFileInDataBase(@Param("userId") long userId, @Param("name") String name);

    @Select("SELECT *,user_id as userId FROM files WHERE id = #{id}")
    UserFile getFileById(@Param("id") long id);

    @Delete("DELETE FROM files WHERE id = #{id}")
    void deleteFile(@Param("id") long id);
}
