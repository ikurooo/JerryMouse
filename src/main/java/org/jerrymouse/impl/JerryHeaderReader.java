package org.jerrymouse.impl;

import org.jerrymouse.IReader;

import java.io.IOException;
import java.io.InputStream;

public class JerryHeaderReader implements IReader {
    private final InputStream in;
    private final int maxTotalHeaderSize; // e.g., 8192 (8KB)
    private int totalBytesRead = 0;

    public JerryHeaderReader(InputStream in, int maxTotalHeaderSize) {
        this.in = in;
        this.maxTotalHeaderSize = maxTotalHeaderSize;
    }

    @Override
    public String readLine() throws IOException {
        StringBuilder sb = new StringBuilder();
        int byteRead;

        while ((byteRead = in.read()) != -1) {
            totalBytesRead++;
            if (totalBytesRead > maxTotalHeaderSize) {
                throw new IOException("Total header size exceeded " + maxTotalHeaderSize);
            }

            char c = (char) byteRead;
            if (c == '\n') break;
            if (c != '\r') {
                sb.append(c);
            }
        }

        String line = sb.toString();
        return (byteRead == -1 && line.isEmpty()) ? null : line;
    }
}