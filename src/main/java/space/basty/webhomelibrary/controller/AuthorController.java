package space.basty.webhomelibrary.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.client.HttpClientErrorException;
import space.basty.webhomelibrary.model.Author;
import space.basty.webhomelibrary.model.Book;
import space.basty.webhomelibrary.repository.AuthorRepository;

import java.nio.charset.StandardCharsets;
import java.util.Optional;

@Controller
@RequestMapping(value = "/author/")
public class AuthorController {
    private AuthorRepository authorRepository;

    @Autowired
    public AuthorController(AuthorRepository authorRepository) {
        this.authorRepository = authorRepository;
    }

    @GetMapping(value = "/{authorId}")
    public String showAuthor(
            Model model,
            @PathVariable(name = "authorId") Long authorId
    ){
        Optional<Author> oAuthorInDb = authorRepository.findById(authorId);
        if (oAuthorInDb.isEmpty()){
            throw HttpClientErrorException.create(HttpStatus.NOT_FOUND, "No Author with id " + authorId, new HttpHeaders(), null, StandardCharsets.UTF_8);
        }

        model.addAttribute("author", oAuthorInDb.get());

        return "author/show";
    }
}
