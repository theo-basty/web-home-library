package space.basty.webhomelibrary.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import space.basty.webhomelibrary.service.BnfSruService;

import java.io.IOException;

@EnableWebMvc
@Controller
@RequestMapping(value = "/catalog")
public class CatalogController {

    BnfSruService bnfSruService;

    @Autowired
    public CatalogController(BnfSruService bnfSruService) {
        this.bnfSruService = bnfSruService;
    }

    @GetMapping(value = "/search/book", params = {"isbn"})
    @ResponseBody
    public String searchBook(@RequestParam("isbn") String isbn){
        try {
            return bnfSruService.getBookByISBN(isbn);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Something went wrong");
        }
    }

    @GetMapping(value = "/search/author", params = {"authorId"})
    @ResponseBody
    public String searchAuthor(@RequestParam("authorId") String authorId){
        return bnfSruService.getBookByAuthor(authorId);
    }
}
