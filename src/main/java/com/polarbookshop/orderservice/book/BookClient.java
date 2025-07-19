package com.polarbookshop.orderservice.book;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;

@Component
public class BookClient {

    private static final String BOOKS_ROOT_API = "/books/";
    private final WebClient webClient;

    public BookClient(WebClient webClient) {
        this.webClient = webClient; // A WebClient bean as configured previously
    }

    public Mono<Book> getBookByIsbn(String isbn) {
        return webClient
                .get()
                .uri(BOOKS_ROOT_API + isbn)
                .retrieve()
                .bodyToMono(Book.class) // Returns the retrieved object as Mono<Book>
                .timeout(Duration.ofSeconds(3)) // Sets a 3 seconds timeout with fallback returns an empty Mono
                .onErrorResume(WebClientResponseException.NotFound.class,
                        exception -> Mono.empty()) // Returns an empty object when a 404 response is received
                .retryWhen(Retry.backoff(3, Duration.ofMillis(100)))
                .onErrorResume(Exception.class, // If any error happens after the 3 retry attempts, catch the exception and return an empty object
                        exception -> Mono.empty());
    }
}
