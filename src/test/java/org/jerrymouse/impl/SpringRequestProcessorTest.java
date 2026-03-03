package org.jerrymouse.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.servlet.DispatcherServlet;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

class SpringRequestProcessorTest {

    private DispatcherServlet mockDispatcher;
    private SpringRequestProcessor processor;

    @BeforeEach
    void setUp() {
        mockDispatcher = mock(DispatcherServlet.class);
        processor = new SpringRequestProcessor(mockDispatcher);
    }

    @Test
    void testInvalidHttpMethodReturns501() throws Exception {
        String rawRequest = """
                INVALID_METHOD /greet HTTP/1.1\r
                Host: localhost\r
                \r
                """;
        InputStream in = new ByteArrayInputStream(rawRequest.getBytes(StandardCharsets.UTF_8));
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        SpringRequestProcessor processor = new SpringRequestProcessor(mockDispatcher);
        processor.process(in, out);
        String response = out.toString(StandardCharsets.UTF_8);
        assertTrue(response.contains("501"), "Should return 501 for unknown methods. Found: " + response);
    }

    @Test
    void testMalformedRequestLineReturns400() throws Exception {
        String rawRequest = "GET /greet\r\n\r\n";
        InputStream in = new ByteArrayInputStream(rawRequest.getBytes(StandardCharsets.UTF_8));
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        SpringRequestProcessor processor = new SpringRequestProcessor(mockDispatcher);
        processor.process(in, out);
        String response = out.toString(StandardCharsets.UTF_8);
        assertTrue(response.contains("400"), "Should return 400 for malformed request lines.");
    }

    @Test
    void testRequestLineLengthLimit() throws Exception {
        String longMethod = "A".repeat(10000) + " / HTTP/1.1\r\n";
        InputStream in = new ByteArrayInputStream(longMethod.getBytes());
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        processor.process(in, out);
        String response = out.toString();
        assertTrue(response.contains("414") || response.contains("431"),
                "Server should reject oversized request lines");
        verify(mockDispatcher, never()).service(any(), any());
    }
}