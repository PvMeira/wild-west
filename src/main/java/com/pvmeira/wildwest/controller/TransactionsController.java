package com.pvmeira.wildwest.controller;

import com.pvmeira.wildwest.configuration.Pages;
import com.pvmeira.wildwest.dto.RawFileRead;
import com.pvmeira.wildwest.exception.ApplicationException;
import com.pvmeira.wildwest.model.TransactionalPackage;
import com.pvmeira.wildwest.service.RawFileService;
import com.pvmeira.wildwest.service.TransactionalPackageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.pvmeira.wildwest.configuration.URI;

import java.util.List;

@Controller
@RequestMapping(URI.TRNSACTIONS)
public class TransactionsController {

    private final RawFileService rawFileService;
    private final TransactionalPackageService transactionalPackageService;

    @Autowired
    public TransactionsController(RawFileService rawFileService, TransactionalPackageService transactionalPackageService) {
        this.rawFileService = rawFileService;
        this.transactionalPackageService = transactionalPackageService;
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
    public String uploadFile(@RequestParam("file") MultipartFile file, RedirectAttributes attributes) {

        if (file.isEmpty()) {
            attributes.addFlashAttribute("messageError", "Please select a file to upload.");
            return "redirect:" + URI.TRNSACTIONS + URI.UPLOAD_FILE;
        }

        try {
            RawFileRead fileRead = this.rawFileService.read(file);
            attributes.addFlashAttribute("message", "You successfully uploaded the File" + fileRead.getOriginalFileName() + '!');
            return "redirect:" + URI.HOME;
        } catch (ApplicationException e1) {
            attributes.addFlashAttribute("messageError", e1.getMessage());
            return "redirect:" + URI.TRNSACTIONS + URI.UPLOAD_FILE;
        } catch (Exception e) {
            attributes.addFlashAttribute("messageError", "A error has occur");
            return "redirect:" + URI.TRNSACTIONS + URI.UPLOAD_FILE;
        }

    }
}
