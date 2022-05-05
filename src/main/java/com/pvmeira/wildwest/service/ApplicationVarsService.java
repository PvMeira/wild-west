package com.pvmeira.wildwest.service;

import com.pvmeira.wildwest.model.ApplicationVars;
import com.pvmeira.wildwest.repository.ApplicationVarsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class ApplicationVarsService {

    private final ApplicationVarsRepository repository;

    @Autowired
    public ApplicationVarsService(ApplicationVarsRepository repository) {
        this.repository = repository;
    }

    public Map<String, ApplicationVars> findAll() {
        return StreamSupport.stream(this.repository.findAll().spliterator(),false)
                .collect(Collectors.toMap(ApplicationVars::getName, Function.identity()));

    }
}
