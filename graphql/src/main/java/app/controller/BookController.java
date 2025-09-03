package app.controller;

import app.model.Author;
import app.model.Book;
import app.model.BookInput;
import app.model.Message;
import lombok.extern.slf4j.Slf4j;
import org.springframework.graphql.data.method.annotation.*;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

import java.time.Duration;
import java.util.List;
import java.util.UUID;

@Controller
@Slf4j
public class BookController {

    private final Sinks.Many<Book> sink = Sinks.many().multicast().onBackpressureBuffer();

    @QueryMapping
    public Book bookById(@Argument String bookId) {
        return Book.getById(bookId);
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
    public List<Book> books(Author author) {
        return Book.getAllBooks().stream().filter(book -> book.authorId().equals(author.id())).toList();
    }

    @MutationMapping
    public Author addBookToAuthor(@Argument BookInput bookInput) {
        Book.addBook(bookInput);
        sink.tryEmitNext(Book.getById(bookInput.id()));
        return Author.getById(bookInput.authorId());
    }

    @SubscriptionMapping
    public Flux<Message> bookAdded() {
        log.info("bookAdded called");
        return Flux.interval(Duration.ofSeconds(1))
                .map(i -> new Message(UUID.randomUUID().toString(), "Message number" + i));
    }
}
