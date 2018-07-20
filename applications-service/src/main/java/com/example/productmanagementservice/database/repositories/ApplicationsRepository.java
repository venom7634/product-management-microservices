package com.example.productmanagementservice.database.repositories;

import com.example.productmanagementservice.dto.Statistic;
import com.example.productmanagementservice.entity.Application;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface ApplicationsRepository {

    @Insert("INSERT INTO applications " +
            "(client_id, status, product, limit_on_card, amount, time_in_month, description) " +
            "VALUES (#{id}, 0, null, null, null, null, null)")
    void createNewApplicationInDatabase(@Param("id") long idUser);

    @Select("select * from applications where id = #{id}")
    Application getApplicationById(@Param("id") long idApplication);

    @Select("select *, client_id as clientId from applications where client_id = #{userId}")
    List<Application> getAllClientApplications(@Param("userId") long userId);

    @Select("select * from applications where client_id = #{userId} order by id desc limit 1")
    Application getNewApplication(@Param("userId") long userId);

    @Update("UPDATE applications SET status = 1 WHERE id = #{id}")
    void sendApplicationToConfirmation(@Param("id") long idApplication);

    @Select("select applications.id, client_id, status, product, limit_on_card as limit, amount, time_in_month " +
            "from applications " +
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

    @Update("UPDATE applications SET product = 'debit-card', limit_on_card = NULL, amount = null, " +
            "time_in_month = null WHERE id = #{id}")
    void addDebitCardToApplication(@Param("id") long idApplication);

    @Update("UPDATE applications SET product = 'credit-card',amount = null, time_in_month = null," +
            " limit_on_card = #{limitOnCard} WHERE id = #{id}")
    void addCreditCardToApplication(@Param("id") long idApplication, @Param("limitOnCard") int limit);

    @Update("UPDATE applications SET product = 'credit-cash',limit_on_card = null,  amount = #{amount}," +
            "time_in_month = #{timeInMonth} WHERE id = #{id}")
    void addCreditCashToApplication(@Param("id") long idApplication, @Param("amount") int amount,
                                    @Param("timeInMonth") int timeInMonth);

    @Select("SELECT client_id FROM applications WHERE id = #{id}")
    long getIdUserByApplications(@Param("id") long id);

    @Select("SELECT COUNT(id) as count, product FROM applications GROUP BY product, status HAVING status = 2")
    List<Statistic> getApprovedStatistics();

    @Select("SELECT COUNT(id) as count, description as reason FROM applications GROUP BY description, status HAVING status = 3")
    List<Statistic> getNegativeStatistics();
}
