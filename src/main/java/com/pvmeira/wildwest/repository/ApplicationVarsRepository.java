package com.pvmeira.wildwest.repository;

import com.pvmeira.wildwest.model.ApplicationVars;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ApplicationVarsRepository extends CrudRepository<ApplicationVars, String> {
}
