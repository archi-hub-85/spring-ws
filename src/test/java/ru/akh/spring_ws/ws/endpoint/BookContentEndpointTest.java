package ru.akh.spring_ws.ws.endpoint;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import javax.xml.transform.Source;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.http.MediaType;
import org.springframework.util.Base64Utils;
import org.springframework.ws.test.server.RequestCreators;
import org.springframework.ws.test.server.ResponseMatchers;
import org.springframework.xml.transform.StringSource;

import ru.akh.spring_ws.dto.BookContent;

public class BookContentEndpointTest extends AbstractEndpointTest {

    private static final String NAMESPACE_URI = BookContentEndpoint.NAMESPACE_URI;

    @Test
    public void testGetContent() {
        long id = 1;
        BookContent content = createBookContent(id, "test.txt", "test content");
        Mockito.when(repository.getContent(id)).thenReturn(content);

        Source requestPayload = new StringSource(new StringBuilder()
                .append("<GetContentRequest xmlns='").append(NAMESPACE_URI).append("'>")
                .append("<id>").append(id).append("</id>")
                .append("</GetContentRequest>")
                .toString());

        Map<String, String> namespaceMapping = new HashMap<>(2);
        namespaceMapping.put("ns2", NAMESPACE_URI);
        namespaceMapping.put("xop", "http://www.w3.org/2004/08/xop/include");

        mockClient.sendRequest(RequestCreators.withPayload(requestPayload))
                .andExpect(ResponseMatchers.noFault())
                .andExpect(ResponseMatchers.xpath("/ns2:GetContentResponse/ns2:result/ns2:id", namespaceMapping)
                        .evaluatesTo((int) id))
                .andExpect(ResponseMatchers.xpath("/ns2:GetContentResponse/ns2:result/ns2:fileName", namespaceMapping)
                        .evaluatesTo(content.getFileName()))
                .andExpect(ResponseMatchers.xpath("/ns2:GetContentResponse/ns2:result/ns2:mimeType", namespaceMapping)
                        .evaluatesTo(content.getMimeType()))
                .andExpect(ResponseMatchers
                        .xpath("boolean(/ns2:GetContentResponse/ns2:result/ns2:content/xop:Include)", namespaceMapping)
                        .evaluatesTo(true))
                .andExpect(ResponseMatchers.xpath("/ns2:GetContentResponse/ns2:result/ns2:size", namespaceMapping)
                        .evaluatesTo((int) content.getSize()));
    }

    @Test
    public void testPutContent() {
        BookContent content = createBookContent(2, "test.txt", "test content");

        Source requestPayload = new StringSource(new StringBuilder()
                .append("<PutContentRequest xmlns='").append(NAMESPACE_URI).append("'>")
                .append("<content>").append(buildBookContentXml(content)).append("</content>")
                .append("</PutContentRequest>")
                .toString());

        mockClient.sendRequest(RequestCreators.withPayload(requestPayload))
                .andExpect(ResponseMatchers.noFault());

        ArgumentCaptor<BookContent> captor = ArgumentCaptor.forClass(BookContent.class);
        Mockito.verify(repository).putContent(captor.capture());
        BookContent actualContent = captor.getValue();
        Assertions.assertEquals(content.getFileName(), actualContent.getFileName(), "bookContent.fileName");
        Assertions.assertEquals(content.getMimeType(), actualContent.getMimeType(), "bookContent.mimeType");
        Assertions.assertArrayEquals(content.getContent(), actualContent.getContent(), "bookContent.content");
        Assertions.assertEquals(content.getSize(), actualContent.getSize(), "bookContent.size");
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

    private static String buildBookContentXml(BookContent content) {
        return new StringBuilder()
                .append("<id>").append(content.getId()).append("</id>")
                .append("<fileName>").append(content.getFileName()).append("</fileName>")
                .append("<mimeType>").append(content.getMimeType()).append("</mimeType>")
                .append("<content>").append(Base64Utils.encodeToString(content.getContent())).append("</content>")
                .append("<size>").append(content.getSize()).append("</size>")
                .toString();
    }

}
