package com.smartiq.pim.repository;

import com.smartiq.pim.domain.Order;
import java.util.List;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the Order entity.
 */
@SuppressWarnings("unused")
@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    @Query("select jhiOrder from Order jhiOrder where jhiOrder.user.login = ?#{principal.username}")
    List<Order> findByUserIsCurrentUser();
}
