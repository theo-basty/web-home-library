package space.basty.webhomelibrary.service;

import lombok.SneakyThrows;
import org.marc4j.MarcReader;
import org.marc4j.MarcXmlReader;
import org.marc4j.marc.DataField;
import org.marc4j.marc.Record;
import org.marc4j.marc.Subfield;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSSerializer;
import org.xml.sax.InputSource;
import space.basty.webhomelibrary.model.Author;
import space.basty.webhomelibrary.model.Book;
import space.basty.webhomelibrary.repository.AuthorRepository;
import space.basty.webhomelibrary.util.ParameterStringBuilder;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.Year;
import java.time.format.DateTimeParseException;
import java.util.*;

@Service
public class BnfSruService {
    private URL url;
    private final Logger logger = LoggerFactory.getLogger(BnfSruService.class);
    private final AuthorRepository authorRepository;

    @Autowired
    public BnfSruService(AuthorRepository authorRepository) {
        this.authorRepository = authorRepository;
        try {
            url = new URL("http://catalogue.bnf.fr/api/SRU");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    @SneakyThrows
    private List<Book> executeQuery(String query){
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");

        Map<String, String> parameters = new HashMap<>();
        parameters.put("version", "1.2");
        parameters.put("operation", "searchRetrieve");
        parameters.put("recordSchema", "intermarcXchange");
        parameters.put("query", query);

        con.setDoOutput(true);
        DataOutputStream out = new DataOutputStream(con.getOutputStream());
        out.writeBytes(ParameterStringBuilder.getParamsString(parameters));
        out.flush();
        out.close();

        HttpStatus status = HttpStatus.resolve(con.getResponseCode());
        StringBuffer content;
        switch (Objects.requireNonNull(status)) {
            case OK:
                BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
                String inputLine;
                content = new StringBuffer();
                while ((inputLine = in.readLine()) != null) {
                    content.append(inputLine);
                }
                in.close();
                break;

            default:
                throw new RuntimeException();
        }

        con.disconnect();

        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document doc = dBuilder.parse(new InputSource(new StringReader(content.toString())));
        LSSerializer ser = ((DOMImplementationLS) doc.getImplementation()).createLSSerializer();
        doc.getDocumentElement().normalize();

        NodeList bookList = doc.getElementsByTagName("srw:record");
        List<Book> results = new ArrayList<>();
        for (int i = 0; i < bookList.getLength(); i++) {
            Element nBook = (Element) bookList.item(i);
            Node nBookData = nBook.getElementsByTagName("mxc:record").item(0);

            MarcReader reader = new MarcXmlReader(new InputSource(new StringReader(ser.writeToString(nBookData))));
            Record record = reader.next();
            results.add(record2Book(record));
        }
        return results;
    }

    @SneakyThrows
    public List<Book> getBookByISBN(String isbn) throws IOException {
        return executeQuery("bib.isbn adj \"" + isbn + "\"");
    }

    @SneakyThrows
    public List<Book> getBookByAuthor(String authorId) {
        return executeQuery("author2bib any \"" + authorId + "\"");
    }

    private Book record2Book(Record record){
        Book book = new Book();
        Author author = new Author();

        book.setAuthorityRawValue(record.toString());
        book.setRecordId(record.getControlNumber());
        for (DataField field :
                record.getDataFields()) {
            switch (field.getTag()) {
                case "038":
                    for(Subfield sfield : field.getSubfields()) {
                        switch (sfield.getCode()) {
                            case 'a':
                                book.setIsbn(sfield.getData());
                                break;
                        }
                    }
                    break;

                case "100":
                    for(Subfield sfield : field.getSubfields()) {
                        switch (sfield.getCode()) {
                            case 'a':
                                author.setLastName(sfield.getData());
                                break;
                            case 'm':
                                author.setFirstName(sfield.getData());
                                break;
                            case '1':
                                author.setIdNotice(sfield.getData());
                                break;
                            case 'd':
                                String[] birthDeath = sfield.getData().split("-");
                                author.setBirth(Year.parse(birthDeath[0]));
                                try {
                                    author.setDeath(Year.parse(birthDeath[1]));
                                }
                                catch (DateTimeParseException ex){
                                    logger.debug("No death birth for the author");
                                    author.setDeath(null);
                                }
                                break;
                        }
                    }
                    break;

                case "245":
                    for(Subfield sfield : field.getSubfields()) {
                        switch (sfield.getCode()) {
                            case 'a':
                                book.setTitle(sfield.getData());
                                break;
                            case 'e':
                                book.setTitleCmpl(sfield.getData());
                                break;
                        }
                    }
                    break;

                case "460":
                    for(Subfield sfield : field.getSubfields()) {
                        switch (sfield.getCode()) {
                            case 't':
                                book.setSerie(sfield.getData());
                                break;
                            case 'v':
                                book.setTome(sfield.getData());
                                break;
                        }
                    }
                    break;

                case "830":
                    for(Subfield sfield : field.getSubfields()) {
                        switch (sfield.getCode()) {
                            case 'a':
                                book.setDescription(sfield.getData());
                                break;
                        }
                    }
                    break;
            }
        }

        Optional<Author> optionalAuthorInDb = authorRepository.findOneByIdNotice(author.getIdNotice());
        if(optionalAuthorInDb.isPresent()){
            optionalAuthorInDb.get().updateWith(author);
            book.setAuthor(optionalAuthorInDb.get());
        }
        else {
            book.setAuthor(author);
        }
        return book;
    }
}
