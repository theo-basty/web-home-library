package space.basty.webhomelibrary.controller;

import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import space.basty.webhomelibrary.model.Book;
import space.basty.webhomelibrary.service.BnfSruService;

import java.util.stream.Collectors;

@Controller
@RequestMapping(value = "/dev")
public class DevelopmentController {

    BnfSruService bnfSruService;

    @Autowired
    public DevelopmentController(BnfSruService bnfSruService) {
        this.bnfSruService = bnfSruService;
    }

    @SneakyThrows
    @GetMapping(value = "/rawBook")
    @ResponseBody
    public String rawMarcAnswer(@RequestParam(name = "isbn") String isbn){
        return bnfSruService.getBookByISBN(isbn).stream()
                .map(Book::getAuthorityRawValue)
                .collect(Collectors.joining("------------------\n"));
    }

    @SneakyThrows
    @GetMapping(value = "/parsedBook")
    @ResponseBody
    public String parsedAnswer(@RequestParam(name = "isbn") String isbn){
        return bnfSruService.getBookByISBN(isbn).stream()
                .map(Book::toString)
                .collect(Collectors.joining("------------------\n"));
    }
}
