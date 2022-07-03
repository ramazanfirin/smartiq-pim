package com.smartiq.pim.repository;

import com.smartiq.pim.domain.Basket;
import java.util.List;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the Basket entity.
 */
@SuppressWarnings("unused")
@Repository
public interface BasketRepository extends JpaRepository<Basket, Long> {
    @Query("select basket from Basket basket where basket.user.login = ?#{principal.username}")
    List<Basket> findByUserIsCurrentUser();

    @Query(
        "select basket from Basket basket where basket.user.login = ?#{principal.username} and basket.status='ACTIVE' order by basket.createDate desc"
    )
    List<Basket> findActiveBasketOfCurrentUser();
}
