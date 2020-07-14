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

    @Column(unique = true)
    String recordId; //Internal identifier of Bibliothèque National de France

    String isbn;

    String title;

    String titleCmpl;

    String serie;

    String tome;

    @Lob
    String description;

    @ManyToOne(targetEntity = Author.class, cascade = CascadeType.PERSIST)
    Author author;

    @ManyToOne(targetEntity = Publisher.class, cascade = CascadeType.PERSIST)
    Publisher publisher;

    @Transient
    String authorityRawValue;

    public String getFullTitle(){
        String fullTitle = "";
        if(titleCmpl == null){
            fullTitle =  title;
        }
        else{
            fullTitle =  title + " - " + titleCmpl;
        }

        String result = "";
        if(inSerie()) {
            result += serie + " T" + tome;
            if (!serie.equals(title)) {
                result += " - " + fullTitle;
            }
        }
        else {
            result += fullTitle;
        }

        return result;
    }

    public Boolean inSerie(){
        return serie != null && tome != null;
    }

}
