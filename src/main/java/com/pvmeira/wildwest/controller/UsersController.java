package com.pvmeira.wildwest.controller;

import com.pvmeira.wildwest.configuration.Pages;
import com.pvmeira.wildwest.configuration.URI;
import com.pvmeira.wildwest.dto.UserRequest;
import com.pvmeira.wildwest.dto.UserResponse;
import com.pvmeira.wildwest.exception.ApplicationException;
import com.pvmeira.wildwest.model.Users;
import com.pvmeira.wildwest.service.UsersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping(URI.USERS)
public class UsersController {

    private final UsersService service;

    @Autowired
    public UsersController(UsersService service) {
        this.service = service;
    }

    @GetMapping
    public String showUsersList(Model model) {
        List<UserResponse> data = this.service.findAll();
        model.addAttribute("users", data);
        return Pages.USERS;
    }

    @GetMapping("/add")
    public String showUserForm() {
        return Pages.USERS_ADD;
    }

    @GetMapping("/edit/{id}")
    public String showEditPage(@PathVariable("id") long id, Model model) {
        Users foundUser = this.service.find(id);
        model.addAttribute("user", foundUser);
        return Pages.USERS_EDIT;
    }

    @PostMapping("/edit/{id}")
    public String updateUser(@PathVariable("id") long id,  UserRequest user,
                             BindingResult result, Model model) {

        Users updateUser = this.service.edit(id, user);
        return "redirect:" + URI.USERS;
    }

    @GetMapping("/delete/{id}")
    public String deleteUser(@PathVariable("id") long id, Model model) {
        this.service.removeUser(id);
        return "redirect:"+ URI.USERS;
    }

    @PostMapping("/add")
    public String addUser(UserRequest user, BindingResult result, RedirectAttributes attributes, Model model) {
        if (result.hasErrors())
            return URI.USERS_ADD;
        try {
            Users createdUser = this.service.create(user);
            attributes.addFlashAttribute("message", "Usuário criado com sucesso.");
            return "redirect:" + URI.USERS;
        }catch (ApplicationException e) {
            attributes.addFlashAttribute("messageError", e.getMessage());
            return "redirect:" + URI.USERS_ADD;
        }
    }
}
