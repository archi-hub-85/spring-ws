package ru.akh.spring_ws.ws.client;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import ru.akh.spring_ws.dto.Author;
import ru.akh.spring_ws.dto.Book;

public class BookServiceTest extends AbstractServiceTest {

    @Test
    @WithReader
    public void testGet() {
        long id = 1;

        Book book = createBook(id, "title1", 2021, 2L, "name1");
        Mockito.when(repository.get(id)).thenReturn(book);

        Book actualBook = client.get(id);
        Assertions.assertEquals(book.getId(), actualBook.getId(), "book.id");
        Assertions.assertEquals(book.getTitle(), actualBook.getTitle(), "book.title");
        Assertions.assertEquals(book.getYear(), actualBook.getYear(), "book.year");
        Assertions.assertNotNull(actualBook.getAuthor(), "book.author");
        Assertions.assertEquals(book.getAuthor().getId(), actualBook.getAuthor().getId(), "author.id");
        Assertions.assertEquals(book.getAuthor().getName(), actualBook.getAuthor().getName(), "author.name");
    }

    private static Book createBook(Long id, String title, int year, Long authorId, String authorName) {
        Book book = new Book();
        book.setId(id);
        book.setTitle(title);
        book.setYear(year);

        Author author = new Author();
        author.setId(authorId);
        author.setName(authorName);
        book.setAuthor(author);

        return book;
    }

}
