package space.basty.webhomelibrary.model;

import lombok.Data;

import javax.persistence.*;
import java.time.Year;
import java.util.List;

@Entity
@Data
public class Author {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long authorId;

    String idNotice;

    String firstName;

    String lastName;

    Year birth;

    Year death;

    @OneToMany(mappedBy = "author")
    List<Book> livres;
}
