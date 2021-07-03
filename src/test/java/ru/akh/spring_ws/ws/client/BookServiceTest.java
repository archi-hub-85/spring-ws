package ru.akh.spring_ws.ws.client;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import ru.akh.spring_ws.dto.Author;
import ru.akh.spring_ws.dto.Book;

public class BookServiceTest extends AbstractServiceTest<BookServiceClient> {

    @Test
    @WithReader
    public void testGet() {
        long id = 1;

        Book book = createBook(id, "title1", 2021, 2L, "name1");
        Mockito.when(repository.get(id)).thenReturn(book);

        Book actualBook = client.get(id);
        assertEquals(book, actualBook, "book.");
    }

    @Test
    @WithWriter
    public void testPutBook() {
        long id = 1;
        Book book = createBook(id, "title", 2021, 1L, "author");
        Mockito.when(repository.put(Mockito.any())).thenReturn(id);

        long result = client.put(book);
        Assertions.assertEquals(id, result, "result");
    }

    @Test
    @WithReader
    public void testGetTopBooks() {
        Book book1 = createBook(1L, "title1", 2021, 1L, "name1");
        Book book2 = createBook(2L, "title2", 2022, 2L, "name2");
        Mockito.when(repository.getTopBooks(Book.Field.ID, 2))
                .thenReturn(Arrays.asList(book1, book2));

        List<Book> result = client.getTopBooks(Book.Field.ID, 2);
        Assertions.assertEquals(2, result.size(), "result.size");
        assertEquals(book1, result.get(0), "book1.");
        assertEquals(book2, result.get(1), "book2.");
    }

    @Test
    @WithReader
    public void testGetBooksByAuthor() {
        String author = "author";
        Book book1 = createBook(1L, "title1", 2021, 1L, author);
        Book book2 = createBook(2L, "title2", 2022, 1L, author);
        Mockito.when(repository.getBooksByAuthor(author))
                .thenReturn(Arrays.asList(book1, book2));

        List<Book> result = client.getBooksByAuthor(author);
        Assertions.assertEquals(2, result.size(), "result.size");
        assertEquals(book1, result.get(0), "book1.");
        assertEquals(book2, result.get(1), "book2.");
    }

    @Test
    @WithUser(username = UsersConstants.WRONG_USERNAME, password = UsersConstants.WRONG_PASSWORD)
    public void testGetWithWrongUser() {
        assertThrowsSoapFault("Client", null, () -> {
            client.get(1);
        });
    }

    @Test
    @WithUser(username = UsersConstants.READER_USERNAME, password = UsersConstants.WRONG_PASSWORD)
    public void testGetWithWrongPassword() {
        assertThrowsSoapFault("Client", null, () -> {
            client.get(1);
        });
    }

    @Test
    @WithWriter
    public void testGetWithWrongRole() {
        assertThrowsSoapFault("Client", "Access is denied", () -> {
            client.get(1);
        });
    }

    @Test
    @WithUser(username = UsersConstants.WRONG_USERNAME, password = UsersConstants.WRONG_PASSWORD)
    public void testPutWithWrongUser() {
        assertThrowsSoapFault("Client", null, () -> {
            Book book = createBook(1L, "title", 2020, null, "author");
            client.put(book);
        });
    }

    @Test
    @WithUser(username = UsersConstants.WRITER_USERNAME, password = UsersConstants.WRONG_PASSWORD)
    public void testPutWithWrongPassword() {
        assertThrowsSoapFault("Client", null, () -> {
            Book book = createBook(1L, "title", 2020, null, "author");
            client.put(book);
        });
    }

    @Test
    @WithReader
    public void testPutWithWrongRole() {
        assertThrowsSoapFault("Client", "Access is denied", () -> {
            Book book = createBook(1L, "title", 2020, null, "author");
            client.put(book);
        });
    }

    @Test
    @WithUser(username = UsersConstants.WRONG_USERNAME, password = UsersConstants.WRONG_PASSWORD)
    public void testGetTopBooksWithWrongUser() {
        assertThrowsSoapFault("Client", null, () -> {
            client.getTopBooks(Book.Field.ID, 2);
        });
    }

    @Test
    @WithUser(username = UsersConstants.READER_USERNAME, password = UsersConstants.WRONG_PASSWORD)
    public void testGetTopBooksWithWrongPassword() {
        assertThrowsSoapFault("Client", null, () -> {
            client.getTopBooks(Book.Field.ID, 2);
        });
    }

    @Test
    @WithWriter
    public void testGetTopBooksWithWrongRole() {
        assertThrowsSoapFault("Client", "Access is denied", () -> {
            client.getTopBooks(Book.Field.ID, 2);
        });
    }

    @Test
    @WithUser(username = UsersConstants.WRONG_USERNAME, password = UsersConstants.WRONG_PASSWORD)
    public void testGetBooksByAuthorWithWrongUser() {
        assertThrowsSoapFault("Client", null, () -> {
            client.getBooksByAuthor("author");
        });
    }

    @Test
    @WithUser(username = UsersConstants.READER_USERNAME, password = UsersConstants.WRONG_PASSWORD)
    public void testGetBooksByAuthorWithWrongPassword() {
        assertThrowsSoapFault("Client", null, () -> {
            client.getBooksByAuthor("author");
        });
    }

    @Test
    @WithWriter
    public void testGetBooksByAuthorWithWrongRole() {
        assertThrowsSoapFault("Client", "Access is denied", () -> {
            client.getBooksByAuthor("author");
        });
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

    private static void assertEquals(Book expected, Book actual, String messagePrefix) {
        Assertions.assertEquals(expected.getId(), actual.getId(), messagePrefix + "id");
        Assertions.assertEquals(expected.getTitle(), actual.getTitle(), messagePrefix + "title");
        Assertions.assertEquals(expected.getYear(), actual.getYear(), messagePrefix + "year");
        Assertions.assertNotNull(actual.getAuthor(), messagePrefix + "author");
        Assertions.assertEquals(expected.getAuthor().getId(), actual.getAuthor().getId(), messagePrefix + "author.id");
        Assertions.assertEquals(expected.getAuthor().getName(), actual.getAuthor().getName(),
                messagePrefix + "author.name");
    }

}
