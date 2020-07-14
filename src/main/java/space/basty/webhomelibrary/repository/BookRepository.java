package space.basty.webhomelibrary.repository;

import org.springframework.data.repository.CrudRepository;
import space.basty.webhomelibrary.model.Book;

import java.util.Optional;

public interface BookRepository extends CrudRepository<Book, Long> {
    Optional<Book> findByRecordId(String recordId);
}
