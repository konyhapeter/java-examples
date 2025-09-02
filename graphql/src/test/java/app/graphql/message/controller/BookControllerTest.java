package app.graphql.message.controller;

import app.model.Book;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.graphql.tester.AutoConfigureHttpGraphQlTester;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.graphql.test.tester.HttpGraphQlTester;

import java.util.Arrays;
import java.util.List;

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

        List<String> bookIds = Arrays.asList("book-1","book-2","book-3");
        //https://github.com/hantsy/spring-graphql-sample/blob/master/spring-graphql-webmvc/src/test/java/com/example/demo/QueryTests.java
        //when, then
        graphQlTester.document(getAllBookQuery)
                .execute()
                .path("getAllBook[*].id")
                .entityList(String.class).satisfies(idList -> Assertions.assertTrue(idList.containsAll(bookIds), "Ids are not equal"));
    }
}
