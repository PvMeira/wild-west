package com.pvmeira.wildwest.repository;

import com.pvmeira.wildwest.model.Users;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface UsersRepository  extends CrudRepository<Users, String>  {

    @Transactional
    @Modifying(clearAutomatically = true)

    @Query(value = "INSERT INTO authorities (USERNAME, AUTHORITY) VALUES (:email, 'ROLE_USER')", nativeQuery = true)
    void createDefaultUserAuthorities(String email);
}
