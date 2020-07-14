package space.basty.webhomelibrary.model;

import lombok.Data;
import lombok.ToString;

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

    @ToString.Exclude
    @OneToMany(mappedBy = "author")
    List<Book> books;

    public void updateWith(Author newValue){

        if(newValue.getIdNotice() != null){
            this.setIdNotice(newValue.getIdNotice());
        }

        if(newValue.getFirstName() != null){
            this.setFirstName(newValue.getFirstName());
        }

        if(newValue.getLastName() != null){
            this.setLastName(newValue.getLastName());
        }

        if(newValue.getBirth() != null){
            this.setBirth(newValue.getBirth());
        }

        if(newValue.getDeath() != null){
            this.setDeath(newValue.getDeath());
        }
    }

    public String getFullName(){
        return firstName + " " + lastName;
    }
}
