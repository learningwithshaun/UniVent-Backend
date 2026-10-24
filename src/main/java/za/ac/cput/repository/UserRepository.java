package za.ac.cput.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import za.ac.cput.domain.RoleEnum;
import za.ac.cput.domain.User;

public interface UserRepository extends JpaRepository<User, String> {

    Long countByRole(RoleEnum role);

    @Query("SELECT u FROM User u " +
            "WHERE (:role IS NULL OR u.role = :role) " +
            "AND (:search IS NULL OR LOWER(u.name) LIKE CONCAT('%', LOWER(:search), '%') " +
            "OR LOWER(u.email) LIKE CONCAT('%', LOWER(:search), '%'))")
    Page<User> findByRoleAndSearch(@Param("role") RoleEnum role,
                                   @Param("search") String search,
                                   Pageable pageable);
}
