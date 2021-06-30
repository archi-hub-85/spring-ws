package ru.akh.spring_ws.ws.endpoint;

import java.io.IOException;
import java.util.Arrays;

import javax.xml.transform.Source;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.ws.test.server.RequestCreators;
import org.springframework.ws.test.server.ResponseMatchers;
import org.springframework.xml.transform.StringSource;

import ru.akh.spring_ws.dto.Author;
import ru.akh.spring_ws.dto.Book;

public class BookEndpointTest extends AbstractEndpointTest {

    @Test
    public void testGet() throws IOException {
        long id = 1;

        Book book = createBook(id, "title1", 2021, 2L, "name1");
        Mockito.when(repository.get(id)).thenReturn(book);

        Source requestPayload = new StringSource(new StringBuilder()
                .append("<GetRequest xmlns='").append(NAMESPACE_URI).append("'>")
                .append("<id>").append(id).append("</id>")
                .append("</GetRequest>")
                .toString());
        Source responsePayload = new StringSource(new StringBuilder()
                .append("<GetResponse xmlns='").append(NAMESPACE_URI).append("'>")
                .append("<result>").append(buildBookXml(book)).append("</result>")
                .append("</GetResponse>")
                .toString());

        mockClient.sendRequest(RequestCreators.withPayload(requestPayload))
                .andExpect(ResponseMatchers.noFault())
                .andExpect(ResponseMatchers.payload(responsePayload))
                .andExpect(ResponseMatchers.validPayload(schemaResource));
    }

    @Test
    public void testPutBook() throws IOException {
        long id = 1;
        Book book = createBook(id, "title", 2021, 1L, "author");
        Mockito.when(repository.put(Mockito.any())).thenReturn(id);

        Source requestPayload = new StringSource(new StringBuilder()
                .append("<PutRequest xmlns='").append(NAMESPACE_URI).append("'>")
                .append("<book>").append(buildBookXml(book)).append("</book>")
                .append("</PutRequest>")
                .toString());
        Source responsePayload = new StringSource(new StringBuilder()
                .append("<PutResponse xmlns='").append(NAMESPACE_URI).append("'>")
                .append("<result>").append(id).append("</result>")
                .append("</PutResponse>")
                .toString());

        mockClient.sendRequest(RequestCreators.withPayload(requestPayload))
                .andExpect(ResponseMatchers.noFault())
                .andExpect(ResponseMatchers.payload(responsePayload))
                .andExpect(ResponseMatchers.validPayload(schemaResource));
    }

    @Test
    public void testGetTopBooks() throws IOException {
        Book book1 = createBook(1L, "title1", 2021, 1L, "name1");
        Book book2 = createBook(2L, "title2", 2022, 2L, "name2");
        Mockito.when(repository.getTopBooks(Book.Field.ID, 2))
                .thenReturn(Arrays.asList(book1, book2));

        Source requestPayload = new StringSource(new StringBuilder()
                .append("<GetTopBooksRequest xmlns='").append(NAMESPACE_URI).append("'>")
                .append("<field>").append(Book.Field.ID.name()).append("</field>")
                .append("<limit>").append(2).append("</limit>")
                .append("</GetTopBooksRequest>")
                .toString());
        Source responsePayload = new StringSource(new StringBuilder()
                .append("<GetTopBooksResponse xmlns='").append(NAMESPACE_URI).append("'>")
                .append("<result>").append(buildBookXml(book1)).append("</result>")
                .append("<result>").append(buildBookXml(book2)).append("</result>")
                .append("</GetTopBooksResponse>")
                .toString());

        mockClient.sendRequest(RequestCreators.withPayload(requestPayload))
                .andExpect(ResponseMatchers.noFault())
                .andExpect(ResponseMatchers.payload(responsePayload))
                .andExpect(ResponseMatchers.validPayload(schemaResource));
    }

    @Test
    public void testGetBooksByAuthor() throws IOException {
        String author = "author";
        Book book1 = createBook(1L, "title1", 2021, 1L, author);
        Book book2 = createBook(2L, "title2", 2022, 1L, author);
        Mockito.when(repository.getBooksByAuthor(author))
                .thenReturn(Arrays.asList(book1, book2));

        Source requestPayload = new StringSource(new StringBuilder()
                .append("<GetBooksByAuthorRequest xmlns='").append(NAMESPACE_URI).append("'>")
                .append("<author>").append(author).append("</author>")
                .append("</GetBooksByAuthorRequest>")
                .toString());
        Source responsePayload = new StringSource(new StringBuilder()
                .append("<GetBooksByAuthorResponse xmlns='").append(NAMESPACE_URI).append("'>")
                .append("<result>").append(buildBookXml(book1)).append("</result>")
                .append("<result>").append(buildBookXml(book2)).append("</result>")
                .append("</GetBooksByAuthorResponse>")
                .toString());

        mockClient.sendRequest(RequestCreators.withPayload(requestPayload))
                .andExpect(ResponseMatchers.noFault())
                .andExpect(ResponseMatchers.payload(responsePayload))
                .andExpect(ResponseMatchers.validPayload(schemaResource));
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

    private static String buildBookXml(Book book) {
        return new StringBuilder()
                .append("<id>").append(book.getId()).append("</id>")
                .append("<title>").append(book.getTitle()).append("</title>")
                .append("<year>").append(book.getYear()).append("</year>")
                .append("<author>").append(buildAuthorXml(book.getAuthor())).append("</author>")
                .toString();
    }

    private static String buildAuthorXml(Author author) {
        return new StringBuilder()
                .append("<id>").append(author.getId()).append("</id>")
                .append("<name>").append(author.getName()).append("</name>")
                .toString();
    }

}
