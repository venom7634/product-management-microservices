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

    @Insert("INSERT INTO accesses (granted_access, user_id, file_id)" +
            "VALUES (#{grantedAccess}, #{userId}, #{fileId})")
    void addAccessToFileForUser(@Param("grantedAccess") long grantedAccess,
                                @Param("userId") long userId, @Param("fileId") long fileId);

    @Select("SELECT *,user_id as userId FROM files WHERE user_id = #{userId} ORDER BY id DESC limit 1")
    FileUser getLastCreatedFile(@Param("userId") long userId);

    @Select("SELECT *,user_id as userId FROM files WHERE id = #{id}")
    FileUser getFileById(@Param("id") long id);

    @Delete("DELETE FROM accesses WHERE file_id = #{fileId}")
    void deleteAllAccessToFile(@Param("fileId") long fileId);

    @Delete("DELETE FROM files WHERE id = #{id}")
    void deleteFile(@Param("id") long id);

    @Select("SELECT *, user_id as userId FROM files WHERE user_id = #{userId}")
    List<FileUser> getAllUserFiles(@Param("userId") long userId);

    @Select("SELECT granted_access as grantedAccess, user_id as userId, file_id as fileId " +
            "FROM accesses WHERE user_id = #{userId}")
    List<Access> getAllAccessForUser(@Param("userId") long userId);
}
