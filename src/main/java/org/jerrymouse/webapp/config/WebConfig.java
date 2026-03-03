package org.jerrymouse.webapp.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@Configuration
@EnableWebMvc
@ComponentScan(basePackages = "org.jerrymouse.webapp")
public class WebConfig {
    // No code needed here usually! Spring handles the Jackson registration 
    // automatically once it sees the library on the classpath.
}