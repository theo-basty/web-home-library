package space.basty.webhomelibrary.repository;

import org.springframework.data.repository.CrudRepository;
import space.basty.webhomelibrary.model.Author;

import java.util.Optional;

public interface AuthorRepository extends CrudRepository<Author, Long> {
    public Optional<Author> findOneByIdNotice(String idNotice);
}
