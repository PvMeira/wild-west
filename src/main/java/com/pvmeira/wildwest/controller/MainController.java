package com.pvmeira.wildwest.controller;

import com.pvmeira.wildwest.configuration.Pages;
import com.pvmeira.wildwest.configuration.URI;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(URI.HOME)
public class MainController {

    public String defaultRedirect() {
        return Pages.INDEX;
    }

    @GetMapping(URI.INDEX)
    public String index() {
        return Pages.INDEX;
    }
}
