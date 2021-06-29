package ru.akh.spring_ws.dao;

import java.nio.charset.StandardCharsets;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.validation.ConstraintViolationException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;

import ru.akh.spring_ws.AbstractTest;
import ru.akh.spring_ws.dao.exception.AuthorNotFoundException;
import ru.akh.spring_ws.dao.exception.BookContentNotFoundException;
import ru.akh.spring_ws.dao.exception.BookNotFoundException;
import ru.akh.spring_ws.dto.Author;
import ru.akh.spring_ws.dto.Book;
import ru.akh.spring_ws.dto.BookContent;

@SpringBootTest(webEnvironment = WebEnvironment.NONE)
@Import(RepositoryConfig.class)
@ActiveProfiles("inMemory")
public class BookRepositoryTest extends AbstractTest {

    @Autowired
    private BookRepository repository;

    @Test
    public void testGetBook() {
        Book book = repository.get(1);
        Author author = book.getAuthor();

        Assertions.assertEquals(1, book.getId(), "book.id");
        Assertions.assertEquals("The Dark Tower: The Gunslinger", book.getTitle(), "book.title");
        Assertions.assertEquals(1982, book.getYear(), "book.year");
        Assertions.assertNotNull(author, "book.author");
        Assertions.assertEquals(1, author.getId(), "author.id");
        Assertions.assertEquals("Stephen King", author.getName(), "author.name");
    }

    @Test
    public void testPutBook() {
        long id = putBook(null, "titleNew", 2020, null, "authorNew");
        Assertions.assertTrue(id > 18, "new book's id must be greater than 18");
    }

    @Test
    public void testUpdateBook() {
        String authorName = "authorNew3";
        String title = "titleNew3";
        int year = 2020;
        long id = putBook(null, title, year, null, authorName);
        Book newBook = repository.get(id);
        Author newAuthor = newBook.getAuthor();

        Assertions.assertEquals(id, newBook.getId(), "newBook.id");
        Assertions.assertEquals(title, newBook.getTitle(), "newBook.title");
        Assertions.assertEquals(year, newBook.getYear(), "newBook.year");
        Assertions.assertNotNull(newAuthor, "newBook.author");
        Assertions.assertTrue(newAuthor.getId() > 3, "newAuthor's id nust be greater than 3");
        Assertions.assertEquals(authorName, newAuthor.getName(), "newAuthor.name");

        String newAuthorName = "authorNew3_2";
        String newTitle = "titleNew3_2";
        int newYear = 2021;
        putBook(id, newTitle, newYear, newAuthor.getId(), newAuthorName);
        Book updatedBook = repository.get(id);
        Author updatedAuthor = updatedBook.getAuthor();

        Assertions.assertEquals(id, updatedBook.getId(), "updatedBook.id");
        Assertions.assertEquals(newTitle, updatedBook.getTitle(), "updatedBook.title");
        Assertions.assertEquals(newYear, updatedBook.getYear(), "updatedBook.year");
        Assertions.assertNotNull(updatedAuthor, "updatedBook.author");
        Assertions.assertEquals(newAuthor.getId(), updatedAuthor.getId(), "updatedAuthor.id");
        Assertions.assertEquals(newAuthorName, updatedAuthor.getName(), "updatedAuthor.name");
    }

    @Test
    public void testGetNonExistingBook() {
        assertThrows(BookNotFoundException.class, () -> {
            repository.get(100);
        });
    }

    @Test
    public void testPutBookWithNonExistingId() {
        assertThrows(BookNotFoundException.class, () -> {
            putBook(100L, "title", 2020, null, "author");
        });
    }

    @Test
    public void testPutBookWithoutTitle() {
        assertThrows(ConstraintViolationException.class, () -> {
            putBook(1L, null, 2020, null, "author");
        });
    }

    @Test
    public void testPutBookWithoutAuthor() {
        assertThrows(ConstraintViolationException.class, () -> {
            putBook(1L, "title", 2020, null, null);
        });
    }

    @Test
    public void testPutBookWithoutAuthorName() {
        assertThrows(ConstraintViolationException.class, () -> {
            putBook(1L, "title", 2020, 1L, null);
        });
    }

    @Test
    public void testPutBookWithNonExistingAuthorId() {
        assertThrows(AuthorNotFoundException.class, () -> {
            putBook(1L, "title", 2020, 100L, "author");
        });
    }

    @Test
    public void testGetTopBooks() {
        List<Book> allBooks = repository.getTopBooks(Book.Field.ID, 18);

        for (Book.Field field : Book.Field.values()) {
            testGetTopBooks(allBooks, field);
        }
    }

    private void testGetTopBooks(List<Book> allBooks, Book.Field field) {
        logger.debug("testGetTopBooks: {}", field);

        Comparator<Book> comparator;
        switch (field) {
        case ID:
            comparator = Comparator.comparing(Book::getId);
            break;
        case TITLE:
            comparator = Comparator.comparing(Book::getTitle);
            break;
        case YEAR:
            comparator = Comparator.comparing(Book::getYear);
            break;
        case AUTHOR:
            comparator = Comparator.comparing(((Function<Book, Author>) Book::getAuthor).andThen(Author::getName));
            break;
        default:
            throw new IllegalArgumentException("Unknown field value: " + field);
        }

        int limit = 5;
        List<Book> expectedBooks = allBooks.stream().sorted(comparator).limit(limit).collect(Collectors.toList());

        List<Book> topBooks = repository.getTopBooks(field, limit);
        Assertions.assertEquals(limit, topBooks.size(), "topBooks.size");

        org.assertj.core.api.Assertions.assertThat(topBooks).usingElementComparator(comparator)
                .isEqualTo(expectedBooks);
    }

    @Test
    public void testGetTopBooksWithNullField() {
        assertThrows(ConstraintViolationException.class, () -> {
            repository.getTopBooks(null, 5);
        });
    }

    @Test
    public void testGetTopBooksWithZeroLimit() {
        assertThrows(ConstraintViolationException.class, () -> {
            repository.getTopBooks(Book.Field.ID, 0);
        });
    }

    @Test
    public void testGetBooksByAuthor() {
        List<Book> books = repository.getBooksByAuthor("Arthur Conan Doyle");
        Assertions.assertEquals(4, books.size(), "books.size");
    }

    @Test
    public void testGetBooksByAuthorWithNullAuthor() {
        assertThrows(ConstraintViolationException.class, () -> {
            repository.getBooksByAuthor(null);
        });
    }

    @Test
    public void testGetContent() {
        testGetContent(1, "dark_tower_1.txt", "The Dark Tower: The Gunslinger");
    }

    @Test
    public void testPutContent() {
        putContent(2, "test.txt", "test content");
    }

    @Test
    public void testUpdateContent() {
        long id = 3;
        String fileName = "newTest2.txt";
        String content = "new test content #2";

        putContent(id, fileName, content);
        testGetContent(id, fileName, content);
    }

    @Test
    public void testGetContentForNonExistingBook() {
        assertThrows(BookContentNotFoundException.class, () -> {
            repository.getContent(100);
        });
    }

    @Test
    public void testPutContentForNonExistingBook() {
        assertThrows(BookNotFoundException.class, () -> {
            putContent(100, "test.txt", "test content");
        });
    }

    @Test
    public void testPutContentWithEmptyFileName() {
        assertThrows(ConstraintViolationException.class, () -> {
            putContent(1, "", "test content");
        });
    }

    @Test
    public void testPutContentWithEmptyContent() {
        assertThrows(ConstraintViolationException.class, () -> {
            putContent(1, "test.txt", "");
        });
    }

    private long putBook(Long id, String title, int year, Long authorId, String authorName) {
        Author author = null;
        if (authorId != null || authorName != null) {
            author = new Author();
            author.setId(authorId);
            author.setName(authorName);
        }

        Book book = new Book();
        book.setId(id);
        book.setAuthor(author);
        book.setTitle(title);
        book.setYear(year);

        return repository.put(book);
    }

    private void testGetContent(long id, String expectedFileName, String expectedContent) {
        BookContent bookContent = repository.getContent(id);
        String content = new String(bookContent.getContent(), StandardCharsets.UTF_8);

        Assertions.assertEquals(expectedFileName, bookContent.getFileName(), "bookContent.fileName");
        Assertions.assertEquals(MediaType.TEXT_PLAIN_VALUE, bookContent.getMimeType(), "bookContent.mimeType");
        Assertions.assertEquals(expectedContent, content, "bookContent.content");
    }

    private void putContent(long id, String fileName, String content) {
        BookContent bookContent = new BookContent();
        bookContent.setId(id);
        bookContent.setFileName(fileName);
        bookContent.setMimeType(MediaType.TEXT_PLAIN_VALUE);
        bookContent.setContent(content.getBytes(StandardCharsets.UTF_8));

        repository.putContent(bookContent);
    }

    private void assertThrows(Class<? extends Throwable> expectedType, Executable executable) {
        Throwable exception = Assertions.assertThrows(expectedType, executable);
        logger.debug("exception = {}", exception.toString());
    }

}
