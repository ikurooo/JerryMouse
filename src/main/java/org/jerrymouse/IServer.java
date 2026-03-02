package org.jerrymouse;

/**
 * Interface defining the core lifecycle of the HTTP Server (Catalina-style).
 * Acts as the primary entry point for initializing the connector and
 * managing the thread pool to handle incoming HTTP requests.
 */
public interface IServer {

    /**
     * Initializes the server socket and enters the main execution loop.
     * This method should block while listening for incoming TCP connections,
     * dispatching them to a handler or thread pool for HTTP request processing.
     */
    void run();

    /**
     * Initiates a graceful shutdown of the HTTP server.
     * Implementation should stop accepting new connections, allow active
     * requests to complete, and release system resources like ports and thread pools.
     */
    void shutdown();
}