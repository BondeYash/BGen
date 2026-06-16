package com.banking.platform.users;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;

public interface UserRepository extends JpaRepository<User , UUID> {
    Optional<User> findByTenantIdAndEmail(UUID tenantId , String email);

    boolean existsByTenantIdAndEmail(UUID tenantId , String email);
}
