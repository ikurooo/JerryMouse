package org.jerrymouse.impl;

import lombok.extern.slf4j.Slf4j;
import org.jerrymouse.IProcessor;
import org.jerrymouse.IServer;
import org.springframework.web.servlet.DispatcherServlet;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

@Slf4j
public class ServerImpl implements IServer {

    private final int port;
    private final IProcessor processor;
    private ServerSocket serverSocket;

    private volatile boolean running = false;

    //The Cached Thread Pool: Executors.newCachedThreadPool() is dangerous
    // for a public server. If 10,000 bots connect at once, it will try to
    // spawn 10,000 threads and crash your RAM.
    private final ExecutorService clientHandlerPool = Executors.newFixedThreadPool(200);

    public ServerImpl(int port, DispatcherServlet dispatcher) {
        this.port = port;
        this.processor = new SpringRequestProcessor(dispatcher);
        try {
            this.serverSocket = new ServerSocket(port);
            this.running = true;
        } catch (IOException e) {
            log.error("Failed to initialize ServerSocket on port {}: {}", port, e.getMessage());
        }
    }

    @Override
    public void run() {
        if (!running) {
            log.error("Server cannot start: ServerSocket was not initialized.");
            return;
        }

        log.info("JerryMouse is running. Listening for clients on port {}", port);

        Stream.generate(this::tryAcceptClient)
                .takeWhile(clientSocket -> this.running)
                .forEach(clientSocket -> clientSocket.ifPresent(
                        socket -> {
                            log.debug("Dispatching new connection from {} to thread pool", socket.getRemoteSocketAddress());
                            this.clientHandlerPool.submit(new ClientHandler(socket, processor));
                        }));
    }

    private Optional<Socket> tryAcceptClient() {
        try {
            return Optional.of(this.serverSocket.accept());
        } catch (IOException e) {
            if (running)
                log.error("Error accepting client connection: {}", e.getMessage());
            return Optional.empty();
        }
    }

    @Override
    public void shutdown() {
        log.info("Shutting down JerryMouse...");
        this.running = false;
        try {
            if (serverSocket != null && !serverSocket.isClosed())
                serverSocket.close();

            clientHandlerPool.shutdown();
            if (!clientHandlerPool.awaitTermination(5, TimeUnit.SECONDS))
                clientHandlerPool.shutdownNow();

            log.info("Server stopped successfully.");
        } catch (IOException | InterruptedException e) {
            log.error("Error during server shutdown", e);
        }
    }
}