package org.jerrymouse.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jerrymouse.IProcessor;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

@Slf4j
@RequiredArgsConstructor
public class ClientHandler implements Runnable {

    private final Socket clientSocket;
    private final IProcessor processor;

    @Override
    public void run() {
        try (Socket socket = this.clientSocket;
             InputStream is = socket.getInputStream();
             OutputStream os = socket.getOutputStream()) {

            log.info("Handing off connection from {} to processor", socket.getRemoteSocketAddress());
            processor.process(is, os);

        } catch (Exception e) {
            log.error("Protocol processing error: {}", e.getMessage(), e);
        }
    }
}