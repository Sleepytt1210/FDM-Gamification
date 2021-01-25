package com.team33.FDMGamification.DAO;

import com.team33.FDMGamification.Model.Admin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
@org.springframework.transaction.annotation.Transactional(readOnly = true)
public interface AdminRepository extends JpaRepository<Admin, Integer> {

    @Query("SELECT a FROM Admin a WHERE a.username = :username")
    Admin findByUsername(@Param("username") String username);
}
