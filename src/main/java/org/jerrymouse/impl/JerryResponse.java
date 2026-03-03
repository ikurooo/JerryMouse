package org.jerrymouse.impl;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.WriteListener;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Getter;
import lombok.Setter;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class JerryResponse implements HttpServletResponse {
    private final OutputStream out;
    @Getter @Setter private String contentType;
    private Map<String, List<String>> headers;

    public JerryResponse(OutputStream out) {
        this.out = out;
    }

    @Override
    public String getCharacterEncoding() {
        return "";
    }

    @Override
    public ServletOutputStream getOutputStream() {
        return new ServletOutputStream() {
            @Override public void write(int b) throws IOException { out.write(b); }
            @Override public boolean isReady() { return true; }
            @Override public void setWriteListener(WriteListener writeListener) {}
        };
    }

    @Override
    public PrintWriter getWriter() throws IOException {
        return null;
    }

    @Override
    public void setCharacterEncoding(String s) {

    }

    @Override
    public void setContentLength(int i) {

    }

    @Override
    public void setContentLengthLong(long l) {

    }

    @Override
    public void setBufferSize(int i) {

    }

    @Override
    public int getBufferSize() {
        return 0;
    }

    @Override
    public void flushBuffer() throws IOException {

    }

    @Override
    public void resetBuffer() {

    }

    @Override
    public boolean isCommitted() {
        return false;
    }

    @Override
    public void reset() {

    }

    @Override
    public void setLocale(Locale locale) {

    }

    @Override
    public Locale getLocale() {
        return null;
    }

    @Override
    public void addCookie(Cookie cookie) {

    }

    @Override
    public boolean containsHeader(String s) {
        return false;
    }

    @Override
    public String encodeURL(String s) {
        return "";
    }

    @Override
    public String encodeRedirectURL(String s) {
        return "";
    }

    @Override
    public void sendError(int i, String s) throws IOException {

    }

    @Override
    public void sendError(int i) throws IOException {

    }

    @Override
    public void sendRedirect(String s, int i, boolean b) throws IOException {

    }

    @Override
    public void setDateHeader(String s, long l) {

    }

    @Override
    public void addDateHeader(String s, long l) {

    }

    @Override
    public void setHeader(String name, String value) {
        if (headers == null) headers = new HashMap<>(8);

        List<String> values = new ArrayList<>(1);
        values.add(value);
        headers.put(name.toLowerCase(), values);
    }

    @Override
    public void addHeader(String name, String value) {
        if (headers == null) headers = new HashMap<>(8);

        headers.computeIfAbsent(name.toLowerCase(),
                k -> new ArrayList<>(1)).add(value);
    }

    @Override
    public String getHeader(String name) {
        if (headers == null || name == null) return null;
        List<String> values = headers.get(name.toLowerCase());
        return (values != null && !values.isEmpty()) ? values.getFirst() : null;
    }

    @Override
    public void setIntHeader(String s, int i) {

    }

    @Override
    public void addIntHeader(String s, int i) {

    }

    @Override
    public void setStatus(int sc) {
        try {
            out.write("HTTP/1.1 200 OK\r\nContent-Type: application/json\r\n\r\n".getBytes());
        } catch (IOException e) {}
    }

    @Override
    public int getStatus() {
        return 0;
    }

    @Override
    public Collection<String> getHeaders(String s) {
        return List.of();
    }

    @Override
    public Collection<String> getHeaderNames() {
        return List.of();
    }
}