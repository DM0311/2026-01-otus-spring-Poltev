package ru.otus.hw.repositories;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.otus.hw.models.Book;

public interface BookRepositoryCustom {

    Mono<Book> findBookEntityById(long id);

    Flux<Book> findAllBookEntities();

    Mono<Book> saveBookEntity(Book book);

    Mono<Book> updateBookEntity(Book book);
}
