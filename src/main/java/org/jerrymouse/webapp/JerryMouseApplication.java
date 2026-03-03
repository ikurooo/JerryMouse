package org.jerrymouse.webapp;

import org.jerrymouse.impl.JerryServletConfig;
import org.jerrymouse.impl.JerryServletContext;
import org.jerrymouse.impl.ServerImpl;
import org.jerrymouse.webapp.config.WebConfig;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;
import jakarta.servlet.ServletException;

public class JerryMouseApplication {
    public static void main(String[] args) {
        AnnotationConfigWebApplicationContext context = new AnnotationConfigWebApplicationContext();
        JerryServletContext jerryServletContext = new JerryServletContext();
        context.setServletContext(jerryServletContext);
        context.register(WebConfig.class);
        context.refresh();
        DispatcherServlet dispatcher = new DispatcherServlet(context);
        try {
            dispatcher.init(new JerryServletConfig(context.getServletContext()));
        } catch (ServletException e) {
            throw new RuntimeException("Spring Init Failed", e);
        }
        ServerImpl server = new ServerImpl(9000, dispatcher);
        server.run();
    }
}