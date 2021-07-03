package ru.akh.spring_ws.ws.client;

import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.http.MediaType;

import ru.akh.spring_ws.dto.BookContent;

public class BookContentServiceTest extends AbstractServiceTest<BookContentServiceClient> {

    @Test
    @WithReader
    public void testGetContent() throws Exception {
        long id = 1;
        BookContent content = createBookContent(id, "test.txt", "test content");
        Mockito.when(repository.getContent(id)).thenReturn(content);

        BookContent actualContent = client.getContent(id);
        assertEquals(content, actualContent, "bookContent.");
    }

    @Test
    @WithWriter
    public void testPutContent() throws Exception {
        BookContent content = createBookContent(2, "test.txt", "test content");

        client.putContent(content);

        ArgumentCaptor<BookContent> captor = ArgumentCaptor.forClass(BookContent.class);
        Mockito.verify(repository).putContent(captor.capture());
        BookContent actualContent = captor.getValue();
        assertEquals(content, actualContent, "bookContent.");
    }

    @Test
    @WithUser(username = UsersConstants.WRONG_USERNAME, password = UsersConstants.WRONG_PASSWORD)
    public void testGetContentWithWrongUser() {
        assertThrowsSoapFault("Client", null, () -> {
            client.getContent(1);
        });
    }

    @Test
    @WithUser(username = UsersConstants.READER_USERNAME, password = UsersConstants.WRONG_PASSWORD)
    public void testGetContentWithWrongPassword() {
        assertThrowsSoapFault("Client", null, () -> {
            client.getContent(1);
        });
    }

    @Test
    @WithWriter
    public void testGetContentWithWrongRole() {
        assertThrowsSoapFault("Client", "Access is denied", () -> {
            client.getContent(1);
        });
    }

    @Test
    @WithUser(username = UsersConstants.WRONG_USERNAME, password = UsersConstants.WRONG_PASSWORD)
    public void testPutContentWithWrongUser() {
        BookContent content = createBookContent(1, "test.txt", "test content");

        assertThrowsSoapFault("Client", null, () -> {
            client.putContent(content);
        });
    }

    @Test
    @WithUser(username = UsersConstants.WRITER_USERNAME, password = UsersConstants.WRONG_PASSWORD)
    public void testPutContentWithWrongPassword() {
        BookContent content = createBookContent(1, "test.txt", "test content");

        assertThrowsSoapFault("Client", null, () -> {
            client.putContent(content);
        });
    }

    @Test
    @WithReader
    public void testPutContentWithWrongRole() {
        BookContent content = createBookContent(1, "test.txt", "test content");

        assertThrowsSoapFault("Client", "Access is denied", () -> {
            client.putContent(content);
        });
    }

    private static BookContent createBookContent(long id, String fileName, String content) {
        BookContent bookContent = new BookContent();
        bookContent.setId(id);
        bookContent.setFileName(fileName);
        bookContent.setMimeType(MediaType.TEXT_PLAIN_VALUE);
        byte[] contentBytes = content.getBytes(StandardCharsets.UTF_8);
        bookContent.setContent(contentBytes);
        bookContent.setSize(contentBytes.length);

        return bookContent;
    }

    private static void assertEquals(BookContent expected, BookContent actual, String messagePrefix) {
        Assertions.assertEquals(expected.getFileName(), actual.getFileName(), messagePrefix + "fileName");
        Assertions.assertEquals(expected.getMimeType(), actual.getMimeType(), messagePrefix + "mimeType");
        Assertions.assertArrayEquals(expected.getContent(), actual.getContent(), messagePrefix + "content");
        Assertions.assertEquals(expected.getSize(), actual.getSize(), messagePrefix + "size");
    }

}
