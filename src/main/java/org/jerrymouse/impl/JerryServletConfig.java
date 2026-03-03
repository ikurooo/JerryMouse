package org.jerrymouse.impl;

import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletContext;
import java.util.Collections;
import java.util.Enumeration;

public class JerryServletConfig implements ServletConfig {

    private final ServletContext servletContext;
    private final String servletName;

    public JerryServletConfig(ServletContext servletContext) {
        this.servletContext = servletContext;
        this.servletName = "JerryMouseDispatcher";
    }

    @Override
    public String getServletName() {
        return servletName;
    }

    @Override
    public ServletContext getServletContext() {
        return servletContext;
    }

    @Override
    public String getInitParameter(String name) {
        return null;
    }

    @Override
    public Enumeration<String> getInitParameterNames() {
        return Collections.enumeration(Collections.emptyList());
    }
}