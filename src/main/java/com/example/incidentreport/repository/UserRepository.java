package com.example.incidentreport.repository;

import com.example.incidentreport.model.UserDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserDetail,Long> {

    boolean existsByUserNameIgnoreCase(String userName);
     Optional<UserDetail> findByUserName(String userName);

}
