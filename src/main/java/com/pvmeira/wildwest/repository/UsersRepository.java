package com.pvmeira.wildwest.repository;

import com.pvmeira.wildwest.model.Users;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UsersRepository  extends CrudRepository<Users, Long>  {

    Users findFirstByEmailEqualsIgnoreCase(String email);
}
