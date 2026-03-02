package org.jerrymouse.impl;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import org.jerrymouse.IProcessor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ClientHandlerTest {

    private ClientHandler clientHandler;
    private Socket mockSocket;
    private IProcessor mockProcessor;

    @BeforeEach
    void setUp() throws Exception {
        mockSocket = mock(Socket.class);
        mockProcessor = mock(IProcessor.class);
        when(mockSocket.getInputStream()).thenReturn(mock(InputStream.class));
        when(mockSocket.getOutputStream()).thenReturn(mock(OutputStream.class));

        clientHandler = new ClientHandler(mockSocket, mockProcessor);
    }

    @Test
    void testRunDelegatesToProcessor() throws Exception {
        clientHandler.run();

        // Verify that the processor's 'process' method was actually called
        // with an InputStream and an OutputStream
        verify(mockProcessor).process(any(InputStream.class), any(OutputStream.class));
    }

    @Test
    void testSocketIsClosedAfterRun() throws Exception {
        clientHandler.run();

        // Verify that the handler followed the "Try-with-resources" contract
        // and closed the socket
        verify(mockSocket).close();
    }
}