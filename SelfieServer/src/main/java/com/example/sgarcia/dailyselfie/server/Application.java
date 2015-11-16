package com.example.sgarcia.dailyselfie.server;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.data.rest.RepositoryRestMvcAutoConfiguration;
import org.springframework.boot.context.embedded.MultipartConfigFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import javax.servlet.MultipartConfigElement;

/**
 * Created by sgarcia on 11/1/2015.
 */
@EnableAutoConfiguration
@Configuration
@EnableWebMvc
@ComponentScan
public class Application extends RepositoryRestMvcAutoConfiguration{
//public class Application {
    private static final String MAX_REQUEST_SIZE = "150MB";

    // Tell Spring to launch our app!
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Value("${AWS_ACCESS_KEY_ID}")
    private String amazonAWSAccessKey;

    @Value("${AWS_SECRET_KEY}")
    private String amazonAWSSecretKey;

    @Bean
    public AWSCredentials amazonAWSCredentials() {
        return new BasicAWSCredentials(amazonAWSAccessKey, amazonAWSSecretKey);
    }
    /*@Bean
    public MultipartConfigElement multipartConfigElement() {
        // Setup the application container to be accept multipart requests
        final MultipartConfigFactory factory = new MultipartConfigFactory();
        // Place upper bounds on the size of the requests to ensure that
        // clients don't abuse the web container by sending huge requests
        factory.setMaxFileSize(MAX_REQUEST_SIZE);
        factory.setMaxRequestSize(MAX_REQUEST_SIZE);

        // Return the configuration to setup multipart in the container
        return factory.createMultipartConfig();
    }

    @Bean
    public CommonsMultipartResolver multipartResolver() {
        CommonsMultipartResolver resolver=new CommonsMultipartResolver();
        resolver.setDefaultEncoding("utf-8");
        return resolver;
    }*/
}
