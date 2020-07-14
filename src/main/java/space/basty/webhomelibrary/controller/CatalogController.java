package space.basty.webhomelibrary.controller;

import lombok.SneakyThrows;
import org.hibernate.cfg.NotYetImplementedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import space.basty.webhomelibrary.model.Book;
import space.basty.webhomelibrary.repository.BookRepository;
import space.basty.webhomelibrary.service.BnfSruService;
import space.basty.webhomelibrary.service.MessageBagService;

import java.util.List;
import java.util.Optional;

@EnableWebMvc
@Controller
@RequestMapping(value = "/catalog")
public class CatalogController {

    BnfSruService bnfSruService;
    BookRepository bookRepository;
    MessageBagService messageBagService;

    @Autowired
    public CatalogController(BnfSruService bnfSruService, BookRepository bookRepository, MessageBagService messageBagService) {
        this.bnfSruService = bnfSruService;
        this.bookRepository = bookRepository;
        this.messageBagService = messageBagService;
    }

    @GetMapping(value = "/")
    public String getCatalog(Model model) {
        model.addAttribute("bookList", bookRepository.findAll());
        model.addAttribute("title", "Catalogue");

        return "catalog/show";
    }

    @GetMapping(value = "/add")
    public String getAddBook(Model model, @RequestParam(name = "multiple", defaultValue = "0") Boolean multiple) {
        model.addAttribute("title", "Ajout d'un livre");
        model.addAttribute("multiple", multiple);
        return "catalog/add";
    }

    @SneakyThrows
    @PostMapping(value = "/add")
    public String postAddBook(
            @RequestParam(value = "isbn", required = false) String isbn,
            @RequestParam(value = "bookId", required = false) String bookId,
            @RequestParam(name = "multiple", defaultValue = "0") Boolean multiple
    ) {
        String finalRedirect;
        if (multiple) {
            finalRedirect = "redirect:/catalog/add?multiple=1";
        } else {
            finalRedirect = "redirect:/catalog/";
        }

        List<Book> books;
        if (bookId != null && isbn != null) {
            throw HttpClientErrorException.create(HttpStatus.BAD_REQUEST, "ISBN and Book ID should not be set at the same time", new HttpHeaders(), null, null);
        } else if (bookId != null) {
            throw new NotYetImplementedException("Search by book id not implemented");
        } else if (isbn != null) {
            books = bnfSruService.getBookByISBN(isbn);
        } else {
            throw HttpClientErrorException.create(HttpStatus.BAD_REQUEST, "No ISBN or Book ID provided", new HttpHeaders(), null, null);
        }

        if (books.size() == 1) {
            Book found = books.get(0);
            Optional<Book> isInDb = bookRepository.findByRecordId(found.getRecordId());
            if (isInDb.isEmpty()) {
                bookRepository.save(found);
                messageBagService.addMessageToSession("success", found.getFullTitle() + " has been added");
            } else {
                messageBagService.addMessageToSession("warning", found.getFullTitle() + " is already in database");
            }
            return finalRedirect;
        } else if (books.size() > 1) {
            throw new NotYetImplementedException("Search returning more than one book are not yet supported");
        } else {
            messageBagService.addMessageToSession("info", "No books returned");
            return finalRedirect;
        }
    }

//    @GetMapping(value = "/search/book", params = {"isbn"})
//    @ResponseBody
//    public String searchBook(@RequestParam("isbn") String isbn){
//        try {
//            return bnfSruService.getBookByISBN(isbn).toString();
//        } catch (IOException e) {
//            e.printStackTrace();
//            throw new RuntimeException("Something went wrong");
//        }
//    }
//
//    @GetMapping(value = "/search/author", params = {"authorId"})
//    @ResponseBody
//    public String searchAuthor(@RequestParam("authorId") String authorId){
//        return bnfSruService.getBookByAuthor(authorId).toString();
//    }
}
