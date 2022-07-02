package com.smartiq.pim.repository;

import com.smartiq.pim.domain.Product;
import java.util.List;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the Product entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    @Query("SELECT p FROM Product p where p.category.id = :categoryId")
    List<Product> getProductListOfCategory(@Param("categoryId") Long categoryId);
}
