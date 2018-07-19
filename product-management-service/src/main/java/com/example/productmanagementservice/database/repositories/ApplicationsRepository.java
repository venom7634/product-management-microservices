package com.example.productmanagementservice.database.repositories;

import com.example.productmanagementservice.entity.Application;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface ApplicationsRepository {

    @Insert("INSERT INTO applications " +
            "(client_id, statusApp, product, limit_on_card, amount, time_in_month, description) " +
            "VALUES (#{id}, #{statusApp}, null, null, null, null, null)")
    void createNewApplicationInDatabase(@Param("id") long idUser, @Param("statusApp") int status);

    @Select("select * from applications where id = #{id}")
    List<Application> getApplicationsById(@Param("id") long idApplication);

    @Select("select * from applications where client_id = #{userId}")
    List<Application> getAllClientApplications(@Param("userID") long userId);

    @Select("select * from applications where client_id = #{userId} order by id desc limit 1")
    List<Application> getNewApplication(@Param("userId") long userId);

    @Update("UPDATE applications SET statusApp = #{statusApp} WHERE id = #{id}")
    void sendApplicationToConfirmation(@Param("id") long idApplication, @Param("statusApp") int status);

    @Select("select applications.id, client_id, statusApp, product, limit_on_card as limit, amount, time_in_month " +
            "from applications " +
            "INNER JOIN users ON client_id = users.id " +
            "where client_id = #{userId} AND statusApp = #{statusApp}")
    List<Application> getListSentApplicationsOfDataBase(@Param("userId") long userId, @Param("statusApp") int status);

    @Select("select * from applications where client_id = #{userId} and statusApp = #{statusApp}")
    List<Application> getListApprovedApplicationsOfDatabase(@Param("userId") long userId, @Param("statusApp") int status);

    @Update("UPDATE applications SET statusApp = #{statusApp}, description = 'Approved' WHERE id = #{idApplication}")
    void approveApplication(@Param("idApplication") long idApplication, @Param("statusApp") int status);

    @Update("UPDATE applications SET statusApp = #{statusApp}, " +
            "description = 'One user can have only one product of the same type' WHERE product = #{product}")
    void setNegativeOfAllIdenticalProducts(@Param("product") String product, @Param("statusApp") int status);

    @Update("UPDATE applications SET statusApp = #{statusApp}, description = #{reason} WHERE id = #{idApplication}")
    void negativeApplication(@Param("idApplication") long idApplication, @Param("reason") String reason,
                             @Param("statusApp") int status);
}
