package app.graphql.message.controller;

import app.model.Book;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.graphql.tester.AutoConfigureHttpGraphQlTester;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.graphql.test.tester.HttpGraphQlTester;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@AutoConfigureHttpGraphQlTester
public class BookControllerTest {

    @Autowired
    HttpGraphQlTester graphQlTester;

    @Test
    public void testGetAllBook() {

        //given
        String getAllBookQuery = """
                query{
                  getAllBook{
                    id
                    name
                    pageCount
                    author {
                      id
                      firstName
                      lastName
                    }
                  }
                }
                """;

        List<String> bookIds = Arrays.asList("book-1", "book-2", "book-3");
        //https://github.com/hantsy/spring-graphql-sample/blob/master/spring-graphql-webmvc/src/test/java/com/example/demo/QueryTests.java
        //when, then
        graphQlTester.document(getAllBookQuery)
                .execute()
                .path("getAllBook[*].id")
                .entityList(String.class).satisfies(idList -> assertTrue(idList.containsAll(bookIds), "Ids are not equal"));
    }

    @Test
    public void testBookById() {
        //given

        String bookByIdQuery = """
                query bookDetails($bookId:String!) {
                  bookById(bookId:$bookId) {
                    id
                    name
                    pageCount
                    author {
                      id
                      firstName
                      lastName
                    }
                  }
                }
                """;
        graphQlTester.document(bookByIdQuery)
                .variable("bookId", "book-1")
                .execute()
                .path("bookById.name")
                .entity(String.class).satisfies(name -> assertEquals("Effective Java", name, "Names should equal"));

    }

    @Test
    public void testAddBookToAuthor() {
        String mutation = """
                mutation addBookToAuthor($bookInput:BookInput){
                  addBookToAuthor(bookInput:$bookInput){
                    id
                    firstName
                    lastName
                    books {
                      id
                      name
                    }
                  }
                }
                """;
        graphQlTester.document(mutation)
                .variable("bookInput", Map.of(
                        "id", "book-4",
                        "name", "Yet Another book2",
                        "pageCount", 220,
                        "authorId", "author-3"

                )).execute()
                .path("addBookToAuthor.firstName").entity(String.class).isEqualTo("Bill")
                .path("addBookToAuthor.books").entityList(Book.class).satisfies(books -> assertEquals(2, books.size(), "Books size does not match"));
    }
}