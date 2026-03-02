package org.jerrymouse.impl;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import lombok.extern.slf4j.Slf4j;
import org.jerrymouse.IProcessor;

@Slf4j
public class HttpRequestProcessorImpl implements IProcessor {

    @Override
    public void process(InputStream input, OutputStream output) throws Exception {
        BufferedInputStream bis = new BufferedInputStream(input);
        BufferedOutputStream bos = new BufferedOutputStream(output);
        BufferedReader reader = new BufferedReader(new InputStreamReader(bis, StandardCharsets.UTF_8));

        String requestLine = reader.readLine();
        if (requestLine == null || requestLine.isEmpty()) return;
        log.info("Incoming: {}", requestLine);
        sendDashboardResponse(bos);
    }

    private void sendDashboardResponse(BufferedOutputStream bos) throws Exception {
        byte[] fileBytes;
        try (InputStream is = getClass().getClassLoader().getResourceAsStream("static/index.html")) {
            if (is == null) {
                log.error("Could not find index.html in resources/static/");
                return;
            }
            fileBytes = is.readAllBytes();
        }

        String head = "HTTP/1.1 200 OK\r\n" +
                "Content-Type: text/html; charset=UTF-8\r\n" +
                "Content-Length: " + fileBytes.length + "\r\n" +
                "Connection: close\r\n\r\n";

        bos.write(head.getBytes(StandardCharsets.UTF_8));
        bos.write(fileBytes);
        bos.flush();
    }
}