package org.jerrymouse.impl;

import lombok.extern.slf4j.Slf4j;
import org.jerrymouse.IProcessor;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.util.ServletRequestPathUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

@Slf4j
public class SpringRequestProcessor implements IProcessor {

    private final DispatcherServlet dispatcher;

    public SpringRequestProcessor(DispatcherServlet dispatcher) {
        this.dispatcher = dispatcher;
    }

    @Override
    public void process(InputStream in, OutputStream out) {
        try {
            JerryRequest jerryRequest = new JerryRequest();
            BufferedReader reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8));
            String requestLine = reader.readLine();

            if (requestLine == null || requestLine.isEmpty()) return;
            log.info("Processing Request: {}", requestLine);

            String[] parts = requestLine.split(" ");
            if (parts.length < 2) {
                this.sendError(out, 431, "Request Header Fields Too Large");
                return;
            }

            String method = parts[0];
            String fullUri = parts[1];
            String path;
            String queryString = null;

            int queryIndex = fullUri.indexOf('?');
            if (queryIndex != -1) {
                path = fullUri.substring(0, queryIndex);
                queryString = fullUri.substring(queryIndex + 1);
            } else {
                path = fullUri;
            }

            jerryRequest.setMethod(method);
            jerryRequest.setUri(path);
            jerryRequest.setQueryString(queryString);

            String headerLine;
            int headerCount = 0;
            int MAX_HEADERS = 100;

            while ((headerLine = reader.readLine()) != null && !headerLine.isEmpty()) {
                if (++headerCount > MAX_HEADERS) {
                    this.sendError(out, 431, "Request Header Fields Too Large");
                    return;
                }

                parts = headerLine.split(":", 2);

                if (parts.length != 2) {
                    this.sendError(out, 400, "Bad Request: Malformed Header");
                    return;
                }

                String name = parts[0].trim();
                String value = parts[1].trim();

                jerryRequest.addHeader(name, value);
            }

            JerryResponse jerryResponse = new JerryResponse(out);
            ServletRequestPathUtils.parseAndCache(jerryRequest);
            dispatcher.service(jerryRequest, jerryResponse);

            out.flush();

        } catch (Exception e) {
            log.error("Spring Processing Error: {}", e.getMessage(), e);
        }
    }

    private void parseHeader(String name, String value) {

    }

    private void sendError(OutputStream out, int statusCode, String message) {
        try {
            String statusLine = "HTTP/1.1 " + statusCode + " " + getStatusText(statusCode) + "\r\n";
            String body = "Error " + statusCode + ": " + message;
            String response = statusLine +
                    "Content-Type: text/plain; charset=UTF-8\r\n" +
                    "Content-Length: " + body.getBytes(StandardCharsets.UTF_8).length + "\r\n" +
                    "Connection: close\r\n" +
                    "\r\n" +
                    body;

            out.write(response.getBytes(StandardCharsets.UTF_8));
            out.flush();

        } catch (IOException e) {
            log.error("Failed to send error response: {}", e.getMessage());
        }
    }

    private String getStatusText(int code) {
        return switch (code) {
            case 400 -> "Bad Request";
            case 431 -> "Request Header Fields Too Large";
            case 500 -> "Internal Server Error";
            default -> "Error";
        };
    }

}
