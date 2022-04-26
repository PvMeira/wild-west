package com.pvmeira.wildwest.service;

import com.pvmeira.wildwest.dto.UserRequest;
import com.pvmeira.wildwest.dto.UserResponse;
import com.pvmeira.wildwest.exception.ApplicationException;
import com.pvmeira.wildwest.model.Users;
import com.pvmeira.wildwest.repository.UsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
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

        if (this.repository.findById(userRequest.getEmail()).isPresent())
            throw new ApplicationException("Já existe um usuário com o email : " + userRequest.getEmail());
        String randomPassword = this.passwordService.getRandomNumber();

        System.out.println("The password generated is : " + randomPassword);
        Users user = Users.builder()
                .withEmail(userRequest.getEmail())
                .withName(userRequest.getName())
                .withPassword(this.passwordService.createFirstAcessPassword(randomPassword))
                .withEnabled(true)
                .withStatus("A")
                .build();
        Users cratedUser = this.repository.save(user);
        this.repository.createDefaultUserAuthorities(user.getEmail());
        this.mailService.send(user.getEmail(), randomPassword);

        return cratedUser;
    }


    public Users find(String email) {
        Optional<Users> usersOptional = this.repository.findById(email);
        if (usersOptional.isEmpty())
            throw new ApplicationException("Não existe um usuário com o email : " + email);
        return usersOptional.get();

    }

    public void removeUser(String username) {
        Users users = this.find(username);
        this.repository.save(users.withStatus("I"));
    }

    public Users edit(String username, Users user) {
        this.find(username);
        return this.repository.save(user);
    }
    public Users edit(String username, UserRequest user) {
        Users a = this.find(username);

        return this.repository.save(a.withEmail(user.getEmail()).withName(user.getName()));
    }

    public List<UserResponse> findAll() {
        return StreamSupport.stream(this.repository.findAll().spliterator(), false)
                .filter(a -> !a.getName().equalsIgnoreCase("Admin"))
                .map(a -> UserResponse.builder()
                .withEmail(a.getEmail())
                .withName(a.getName())
                .withStatus(a.getStatus())
                .build()).collect(Collectors.toList());
    }
}

