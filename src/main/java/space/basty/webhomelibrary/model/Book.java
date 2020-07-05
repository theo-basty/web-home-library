package space.basty.webhomelibrary.model;

import lombok.Data;
import org.springframework.context.annotation.Primary;

import javax.persistence.*;
import java.sql.Date;

@Entity
@Data
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long bookId;

    String recordId; //Internal identifier of Bibliothèque National de France

    String isbn;

    String title;

    String tomeIndication;

    @ManyToOne(targetEntity = Author.class)
    Author author;

    @ManyToOne(targetEntity = Publisher.class)
    Publisher publisher;



}
