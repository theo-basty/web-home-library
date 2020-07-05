package space.basty.webhomelibrary.model;

import lombok.Data;

import javax.persistence.*;
import java.util.List;

@Entity
@Data
public class Publisher {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long publisherId;

    String name;

    @OneToMany(mappedBy = "publisher")
    List<Book> books;
}
