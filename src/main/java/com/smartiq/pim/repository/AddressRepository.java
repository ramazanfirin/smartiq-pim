package com.smartiq.pim.repository;

import com.smartiq.pim.domain.Address;
import java.util.List;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the Address entity.
 */
@SuppressWarnings("unused")
@Repository
public interface AddressRepository extends JpaRepository<Address, Long> {
    @Query("select address from Address address where address.user.login = ?#{principal.username}")
    List<Address> findByUserIsCurrentUser();
}
