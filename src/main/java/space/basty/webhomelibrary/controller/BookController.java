package space.basty.webhomelibrary.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import space.basty.webhomelibrary.model.Book;
import space.basty.webhomelibrary.repository.BookRepository;

import javax.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

@Controller
@RequestMapping(value = "/book")
public class BookController {
    private BookRepository bookRepository;

    @Autowired
    public BookController(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    @GetMapping(value = "/{bookId}")
    public String showBook(
            Model model,
            @PathVariable(name = "bookId") Long bookId
    ){
        Optional<Book> oBookInDb = bookRepository.findById(bookId);
        if (oBookInDb.isEmpty()){
            throw HttpClientErrorException.create(HttpStatus.NOT_FOUND, "No book with id " + bookId, new HttpHeaders(), null, StandardCharsets.UTF_8);
        }

        model.addAttribute("book", oBookInDb.get());

        return "book/show";
    }

    @GetMapping(value = "/{bookId}/lendBack")
    public String lendBookBack(
            @PathVariable(name = "bookId") Long bookId,
            HttpServletRequest request
    ){
        Optional<Book> oBookInDb = bookRepository.findById(bookId);
        if (oBookInDb.isEmpty()){
            throw HttpClientErrorException.create(HttpStatus.NOT_FOUND, "No book with id " + bookId, new HttpHeaders(), null, StandardCharsets.UTF_8);
        }

        oBookInDb.get().setLentTo(null);
        bookRepository.save(oBookInDb.get());

        String referer = request.getHeader("Referer");
        return "redirect:"+ referer;
    }


    @PostMapping(value = "/{bookId}/lendTo")
    public String lendBookBack(
            @PathVariable(name = "bookId") Long bookId,
            @RequestParam(name = "lentTo") String lentTo,
            HttpServletRequest request
    ){
        Optional<Book> oBookInDb = bookRepository.findById(bookId);
        if (oBookInDb.isEmpty()){
            throw HttpClientErrorException.create(HttpStatus.NOT_FOUND, "No book with id " + bookId, new HttpHeaders(), null, StandardCharsets.UTF_8);
        }

        oBookInDb.get().setLentTo(lentTo);
        bookRepository.save(oBookInDb.get());

        String referer = request.getHeader("Referer");
        return "redirect:"+ referer;
    }
}
