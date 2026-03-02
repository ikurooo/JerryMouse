package org.jerrymouse.impl;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import lombok.extern.slf4j.Slf4j;
import org.jerrymouse.IProcessor;

@Slf4j
public class ProcessorImpl implements IProcessor {

    @Override
    public void process(InputStream input, OutputStream output) throws Exception {
        BufferedInputStream bis = new BufferedInputStream(input);
        BufferedOutputStream bos = new BufferedOutputStream(output);
        BufferedReader reader = new BufferedReader(new InputStreamReader(bis, StandardCharsets.UTF_8));

        String requestLine = reader.readLine();
        if (requestLine == null || requestLine.isEmpty()) {
            return;
        }

        log.info("Processing Request: {}", requestLine);

        String header;
        while ((header = reader.readLine()) != null && !header.isEmpty()) {
            log.trace("Parsed Header: {}", header);
        }

        sendDefaultResponse(bos);
    }

    private void sendDefaultResponse(BufferedOutputStream bos) throws Exception {
        String html = """
        <!DOCTYPE html>
        <html lang="en">
        <head>
            <meta charset="UTF-8">
            <meta name="viewport" content="width=device-width, initial-scale=1.0">
            <title>JerryMouse | The Minimalist Server</title>
            <link href="https://fonts.googleapis.com/css2?family=Inter:wght@300;400;600&display=swap" rel="stylesheet">
            <style>
                :root {
                    --primary: #FF2D20; /* Laravel Red */
                    --bg: #F3F4F6;
                    --text: #1F2937;
                }
                body {
                    font-family: 'Inter', sans-serif;
                    background-color: var(--bg);
                    color: var(--text);
                    margin: 0;
                    display: flex;
                    align-items: center;
                    justify-content: center;
                    height: 100vh;
                }
                .container {
                    text-align: center;
                    background: white;
                    padding: 3rem;
                    border-radius: 1rem;
                    box-shadow: 0 10px 25px rgba(0,0,0,0.1);
                    max-width: 500px;
                }
                .logo {
                    font-size: 4rem;
                    margin-bottom: 1rem;
                }
                h1 {
                    font-weight: 600;
                    margin: 0;
                    color: var(--primary);
                }
                p {
                    color: #6B7280;
                    line-height: 1.6;
                }
                .badge {
                    display: inline-block;
                    background: #FEE2E2;
                    color: var(--primary);
                    padding: 0.25rem 0.75rem;
                    border-radius: 9999px;
                    font-size: 0.875rem;
                    font-weight: 600;
                    margin-bottom: 1rem;
                }
                .footer {
                    margin-top: 2rem;
                    font-size: 0.75rem;
                    color: #9CA3AF;
                }
            </style>
        </head>
        <body>
            <div class="container">
                <div class="logo">🐭</div>
                <div class="badge">v1.0.0-alpha</div>
                <h1>JerryMouse</h1>
                <p>A minimalist, high-performance Java servlet container built from the ground up for the modern web.</p>
                <div class="footer">
                    Running on Java 21
                </div>
            </div>
        </body>
        </html>
        """;

        byte[] bodyBytes = html.getBytes(StandardCharsets.UTF_8);

        String responseHeaders = """
        HTTP/1.1 200 OK\r
        Content-Type: text/html; charset=UTF-8\r
        Content-Length: %d\r
        Connection: close\r
        \r
        """.formatted(bodyBytes.length);

        bos.write(responseHeaders.getBytes(StandardCharsets.UTF_8));
        bos.write(bodyBytes);
        bos.flush();
    }
}