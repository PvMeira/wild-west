package com.pvmeira.wildwest.controller;

import com.pvmeira.wildwest.configuration.Pages;
import com.pvmeira.wildwest.configuration.URI;
import com.pvmeira.wildwest.dto.ThreatAssessment;
import com.pvmeira.wildwest.exception.ApplicationException;
import com.pvmeira.wildwest.service.TransactionalPackageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;

@Controller
@RequestMapping(URI.TRNSACTIONS_ANALYSIS)
public class TransactionAnalysisController {

    private final TransactionalPackageService service;

    @Autowired
    public TransactionAnalysisController(TransactionalPackageService service) {
        this.service = service;
    }

    @GetMapping
    public String showForm() {
        return Pages.TRANSACTIONS_ANALYSIS;
    }

    @PostMapping(value = "/execute/{packageDate}")
    public String analyseTransactionsOfGivenDate(@PathVariable("packageDate")@DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate packageDate, Model model, RedirectAttributes attributes) {
        try {
            ThreatAssessment threatAssessment = this.service.analyseTransactions(packageDate);
            model.addAttribute("threatAssessment", threatAssessment);
            return Pages.TRANSACTIONS_ANALYSIS;
        } catch (ApplicationException e){
            attributes.addFlashAttribute("messageError", e.getMessage());
            return "redirect:" + URI.TRNSACTIONS_ANALYSIS;
        }


    }
}
