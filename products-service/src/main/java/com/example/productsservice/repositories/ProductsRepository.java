package com.example.productsservice.repositories;

import com.example.productsservice.dto.Statistic;
import com.example.productsservice.entity.Product;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface ProductsRepository {

    @Select("select * from products where id = #{id}")
    Product getProductOfDataBase(@Param("id") long id);

    @Select("SELECT products.id, products.name FROM products " +
            "INNER JOIN applications ON applications.product = products.name " +
            "INNER JOIN users ON users.id = applications.id " +
            "WHERE applications.status = 1 AND users.id = #{id} ")
    List<Product> getProductsForClient(@Param("id") long userId);

    @Select("SELECT COUNT(id) as count, product FROM applications GROUP BY product, status HAVING status = 2")
    List<Statistic> getApprovedStatistics();

    @Select("SELECT COUNT(id) as count, description as reason FROM applications GROUP BY description, status HAVING status = 3")
    List<Statistic> getNegativeStatistics();
}
