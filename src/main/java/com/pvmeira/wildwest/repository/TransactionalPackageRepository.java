package com.pvmeira.wildwest.repository;

import com.pvmeira.wildwest.model.TransactionalPackage;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

@Repository
public interface TransactionalPackageRepository extends CrudRepository<TransactionalPackage, LocalDate> {
}
