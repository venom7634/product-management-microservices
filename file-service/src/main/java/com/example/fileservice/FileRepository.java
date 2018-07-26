package com.example.fileservice;

import com.example.fileservice.entity.Access;
import com.example.fileservice.entity.FileUser;
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
    FileUser getFileById(@Param("id") long id);

    @Delete("DELETE FROM files WHERE id = #{id}")
    void deleteFile(@Param("id") long id);

    @Select("SELECT *, user_id as userId FROM files WHERE user_id = #{userId}")
    List<FileUser> getAllUserFiles(@Param("userId") long userId);

    @Select("SELECT granted_access as grantedAccess, user_id as userId, file_id as fileId FROM access WHERE user_id = #{userId}")
    List<Access> getAllAccessForUser(@Param("userId") long userId);
}
