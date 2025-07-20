package com.polarbookshop.orderservice.book;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.*;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.io.IOException;

import static org.springframework.http.HttpHeaders.CONTENT_TYPE;


@TestMethodOrder(MethodOrderer.Random.class)
public class BookClientTests {
    private MockWebServer mockWebServer;
    private BookClient bookClient;

    @BeforeEach
    void setUp() throws IOException {
        this.mockWebServer = new MockWebServer();
        this.mockWebServer.start(); // Starts the mock server before running a test case
        var webClient = WebClient.builder()
                .baseUrl(mockWebServer.url("/").uri().toString())
                .build();
        this.bookClient = new BookClient(webClient);
    }

    @AfterEach
    void clean() throws IOException {
        this.mockWebServer.shutdown(); // Shuts the mock server down after completing a test case
    }


    @Test
    void whenBookExistsThenReturnBook() {
        var bookIsbn = "1234567890";

        var mockResponse = new MockResponse()
                .addHeader(CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .setBody("""
                        {
                        "isbn": %s,
                        "title": "Title",
                        "author": "Author",
                        "price": 9.90,
                        "publisher": "Polarsophia"
                        }
                        """.formatted(bookIsbn));
        mockWebServer.enqueue(mockResponse); // Adds a mock response to the queue processed by the mock server
        Mono<Book> book = bookClient.getBookByIsbn(bookIsbn);

        StepVerifier.create(book)
                .expectNextMatches(
                        b -> b.isbn().equals(bookIsbn)
                ).verifyComplete();
    }


    @Test
    void whenBookNotExistsThenReturnEmpty() {
        var bookIsbn = "1234567891";
        var mockResponse = new MockResponse()
                .addHeader(CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .setResponseCode(404);

        mockWebServer.enqueue(mockResponse);

        StepVerifier.create(bookClient.getBookByIsbn(bookIsbn))
                .expectNextCount(0)
                .verifyComplete();
    }
}
