package com.example.productmanagementservice.database.repositories;

import com.example.productmanagementservice.entity.Application;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface ApplicationsRepository {

    @Insert("INSERT INTO applications " +
            "(client_id, status, product, limit_on_card, amount, time_in_month, description) " +
            "VALUES (#{id}, #{status}, null, null, null, null, null)")
    void createNewApplicationInDatabase(@Param("id") long idUser, @Param("status") int status);

    @Select("select * from applications where id = #{id}")
    Application getApplicationById(@Param("id") long idApplication);

    @Select("select * from applications where client_id = #{userId}")
    List<Application> getAllClientApplications(@Param("userID") long userId);

    @Select("select * from applications where client_id = #{userId} order by id desc limit 1")
    Application getNewApplication(@Param("userId") long userId);

    @Update("UPDATE applications SET status = #{status} WHERE id = #{id}")
    void sendApplicationToConfirmation(@Param("id") long idApplication, @Param("status") int status);

    @Select("select applications.id, client_id, status, product, limit_on_card as limit, amount, time_in_month " +
            "from applications " +
            "INNER JOIN users ON client_id = users.id " +
            "where client_id = #{userId} AND status = 1")
    List<Application> getListSentApplicationsOfDataBase(@Param("userId") long userId);

    @Select("select * from applications where client_id = #{userId} and status = 2")
    List<Application> getListApprovedApplicationsOfDatabase(@Param("userId") long userId);

    @Update("UPDATE applications SET status = 2, description = 'Approved' WHERE id = #{idApplication}")
    void approveApplication(@Param("idApplication") long idApplication);

    @Update("UPDATE applications SET status = 3, " +
            "description = 'One user can have only one product of the same type' WHERE product = #{product}")
    void setNegativeOfAllIdenticalProducts(@Param("product") String product);

    @Update("UPDATE applications SET status = 3, description = #{reason} WHERE id = #{idApplication}")
    void negativeApplication(@Param("idApplication") long idApplication, @Param("reason") String reason);
}
