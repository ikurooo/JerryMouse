package org.jerrymouse;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class SimpleClient {
    public static void main(String[] args) {
        ExecutorService executor = Executors.newFixedThreadPool(50);

        for (int i = 0; i < 50; i++) {
            int requestId = i + 1;
            executor.submit(() -> sendHttpRequest(requestId));
        }

        executor.shutdown();
        try {
            if (!executor.awaitTermination(30, TimeUnit.SECONDS)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
        }
    }

    private static void sendHttpRequest(int id) {
        try (Socket socket = new Socket("localhost", 9000);
             PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
             InputStream is = socket.getInputStream()) {

            writer.println("GET /test-" + id + " HTTP/1.1");
            writer.println("Host: localhost");
            writer.println("Connection: close");
            writer.println();

            byte[] buffer = new byte[4096];
            int bytesRead;
            int totalBytes = 0;
            while ((bytesRead = is.read(buffer)) != -1) {
                totalBytes += bytesRead;
            }

            System.out.println("Request #" + id + " received " + totalBytes + " bytes.");

        } catch (IOException e) {
            System.err.println("Request #" + id + " failed: " + e.getMessage());
        }
    }
}