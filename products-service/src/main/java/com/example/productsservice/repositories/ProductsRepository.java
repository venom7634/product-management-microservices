package com.example.productsservice.repositories;

import com.example.productsservice.entity.Product;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;


@Mapper
@Repository
public interface ProductsRepository {

    @Select("select * from products where id = #{id}")
    Product getProductOfDataBase(@Param("id") long id);

}
