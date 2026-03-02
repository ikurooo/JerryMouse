package org.jerrymouse;

import java.io.InputStream;
import java.io.OutputStream;

public interface IProcessor {
    /**
     * Processes the raw streams from a client connection.
     * * @param input The raw input stream from the socket.
     * @param output The raw output stream to the socket.
     * @throws Exception if processing fails.
     */
    void process(InputStream input, OutputStream output) throws Exception;
}