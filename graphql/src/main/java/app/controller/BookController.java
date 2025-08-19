package app.controller;

import app.model.Author;
import app.model.Book;
import app.model.BookInput;
import org.reactivestreams.Publisher;
import org.springframework.graphql.data.method.annotation.*;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Sinks;

import java.util.List;

@Controller
public class BookController {

    private final Sinks.Many<Book> sink = Sinks.many().multicast().onBackpressureBuffer();

    @QueryMapping
    public Book bookById(@Argument String id) {
        return Book.getById(id);
    }

    @QueryMapping
    public List<Book> getAllBook() {
        return Book.getAllBooks();
    }

    @SchemaMapping
    public Author author(Book book) {
        return Author.getById(book.authorId());
    }

    @SchemaMapping
    public List<Book> books(Author author){
        return Book.getAllBooks().stream().filter(book -> book.authorId().equals(author.id())).toList();
    }

    @MutationMapping
    public Author addBookToAuthor(@Argument BookInput bookRequest) {
        Book.addBook(bookRequest);
        sink.tryEmitNext(Book.getById(bookRequest.id()));
        return Author.getById(bookRequest.authorId());
    }

    @SubscriptionMapping
    public Publisher<Book> bookAdded(){
        return sink.asFlux();
        //https://chatgpt.com/c/68a4cd92-e6d8-8322-aef5-22e32562c587
    }
}
