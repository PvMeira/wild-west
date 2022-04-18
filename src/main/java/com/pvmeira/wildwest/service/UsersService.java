package com.pvmeira.wildwest.service;

import com.pvmeira.wildwest.dto.UserRequest;
import com.pvmeira.wildwest.dto.UserResponse;
import com.pvmeira.wildwest.exception.ApplicationException;
import com.pvmeira.wildwest.model.Users;
import com.pvmeira.wildwest.repository.UsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class UsersService {

    private final UsersRepository repository;
    private final PasswordService passwordService;
    private final MailService mailService;

    @Autowired
    public UsersService(UsersRepository repository, PasswordService passwordService, MailService mailService) {
        this.repository = repository;
        this.passwordService = passwordService;
        this.mailService = mailService;
    }


    public Users create(UserRequest userRequest) {

        Users checkUser = this.repository.findFirstByEmailEqualsIgnoreCase(userRequest.getEmail());
        String randomPassword = this.passwordService.getRandomNumber();
        if (null != checkUser)
            throw new ApplicationException("Já existe um usuário com o email : " + userRequest.getEmail());

        Users user = Users.builder()
                .withEmail(userRequest.getEmail())
                .withName(userRequest.getName())
                .withPassword(this.passwordService.createFirstAcessPassword(randomPassword))
                .withStatus("A")
                .build();
        Users cratedUser = this.repository.save(user);
        this.mailService.send(user.getEmail(), randomPassword);

        return cratedUser;
    }

    public Users find(Long id) {
        return this.repository.findById(id).orElseThrow( () -> new ApplicationException("Usuário não encontrado"));
    }

    public Users find(String email) {
        Users checkUser = this.repository.findFirstByEmailEqualsIgnoreCase(email);
        if (null == checkUser)
            throw new ApplicationException("Usuário não encontrado");
        return checkUser;

    }

    public void removeUser(Long id) {
        Users users = this.find(id);
        this.repository.save(users.withStatus("I"));
    }

    public Users edit(Long id, Users user) {
        this.find(id);
        return this.repository.save(user);
    }
    public Users edit(Long id, UserRequest user) {
        Users a = this.find(id);

        return this.repository.save(a.withEmail(user.getEmail()).withName(user.getName()));
    }

    public List<UserResponse> findAll() {
        return StreamSupport.stream(this.repository.findAll().spliterator(), false)
                .filter(a -> !a.getName().equalsIgnoreCase("Admin"))
                .map(a -> UserResponse.builder()
                .withEmail(a.getEmail())
                .withName(a.getName())
                .withStatus(a.getStatus())
                .withId(a.getId())
                .build()).collect(Collectors.toList());
    }
}

