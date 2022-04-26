package com.pvmeira.wildwest.controller;

import com.pvmeira.wildwest.configuration.Pages;
import com.pvmeira.wildwest.configuration.URI;
import com.pvmeira.wildwest.dto.RawFileRead;
import com.pvmeira.wildwest.exception.ApplicationException;
import com.pvmeira.wildwest.model.Transaction;
import com.pvmeira.wildwest.model.TransactionalPackage;
import com.pvmeira.wildwest.model.Users;
import com.pvmeira.wildwest.service.RawFileService;
import com.pvmeira.wildwest.service.TransactionService;
import com.pvmeira.wildwest.service.TransactionalPackageService;
import com.pvmeira.wildwest.service.UsersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping(URI.TRNSACTIONS)
public class TransactionsController {

    private final RawFileService rawFileService;
    private final UsersService usersService;
private final TransactionService transactionService;
    private final TransactionalPackageService transactionalPackageService;

    @Autowired
    public TransactionsController(RawFileService rawFileService, UsersService usersService, TransactionService transactionService, TransactionalPackageService transactionalPackageService) {
        this.rawFileService = rawFileService;
        this.usersService = usersService;
        this.transactionService = transactionService;
        this.transactionalPackageService = transactionalPackageService;
    }

    @GetMapping("/detail/{packageDate}")
    public String showEditPage(@PathVariable("packageDate")@DateTimeFormat(pattern = "yyyy-MM-dd")LocalDate packageDate, Model model) {
        TransactionalPackage aPackage = this.transactionalPackageService.find(packageDate);
        List<Transaction> allTransactionsFromPackge = this.transactionService.findAllTransactionsFromPackge(aPackage);

        model.addAttribute("transactionalPackage", aPackage);
        model.addAttribute("transactions",allTransactionsFromPackge);
        return Pages.TRANSACTION_DETAIL;
    }

    @GetMapping
    public String showSignUpForm(Model model) {
        List<TransactionalPackage> data = this.transactionalPackageService.findAll();
        model.addAttribute("transactions", data);
        return Pages.TRANSACTIONS;
    }

    @GetMapping(URI.UPLOAD_FILE)
    public String userForm() {
        return Pages.TRANSACTIONS_UPLOAD_FILE;
    }

    @PostMapping(URI.UPLOAD_FILE)
    public String uploadFile(@RequestParam("file") MultipartFile file, RedirectAttributes attributes, Authentication authentication) {
        if (file.isEmpty()) {
            attributes.addFlashAttribute("messageError", "Por favor selecione um arquivo.");
            return "redirect:" + URI.TRNSACTIONS + URI.UPLOAD_FILE;
        }

        try {
            Users user = this.usersService.find(authentication.getName());
            RawFileRead fileRead = this.rawFileService.read(file, user);
            attributes.addFlashAttribute("message", "Você importou com sucesso o arquivo : " + fileRead.getOriginalFileName() + '!');
            return "redirect:" + URI.TRNSACTIONS + URI.UPLOAD_FILE;
        } catch (ApplicationException e1) {
            attributes.addFlashAttribute("messageError", e1.getMessage());
            return "redirect:" + URI.TRNSACTIONS + URI.UPLOAD_FILE;
        } catch (Exception e) {
            attributes.addFlashAttribute("messageError", "Um Erro inesperado ocorreu");
            return "redirect:" + URI.TRNSACTIONS + URI.UPLOAD_FILE;
        }

    }
}
