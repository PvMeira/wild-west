package com.pvmeira.wildwest.controller;

import com.pvmeira.wildwest.dto.RawFileRead;
import com.pvmeira.wildwest.service.RawFileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class FileController {

    private final RawFileService rawFileService;

    @Autowired
    public FileController(RawFileService rawFileService) {
        this.rawFileService = rawFileService;
    }

    @GetMapping("/files")
    public String showSignUpForm() {
        return "files";
    }

    @GetMapping("/upload-file")
    public String userForm() {
        return "upload-file";
    }

    @PostMapping("/upload-file")
    public String uploadFile(@RequestParam("file") MultipartFile file, RedirectAttributes attributes) {

        System.out.println("The file Name is" + file.getOriginalFilename() + " with content Type" + file.getContentType());
        if (file.isEmpty()) {
            attributes.addFlashAttribute("messageError", "Please select a file to upload.");
            return "redirect:/upload-file";
        }


        RawFileRead fileRead = this.rawFileService.read(file);

        attributes.addFlashAttribute("messageError", "Error on reading the file.");

        // return success response
        attributes.addFlashAttribute("message", "You successfully uploaded " + "TESTETETET" + '!');

        return "redirect:/index";
    }

    @GetMapping("/index")
    public String showUserList(Model model) {
        return "index";
    }
}
