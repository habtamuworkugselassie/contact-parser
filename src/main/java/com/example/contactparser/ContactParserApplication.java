package com.example.contactparser;

import com.example.contactparser.service.ContactXmlParserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ContactParserApplication implements CommandLineRunner {

    private final ContactXmlParserService parserService;

    public ContactParserApplication(ContactXmlParserService parserService) {
        this.parserService = parserService;
    }

    public static void main(String[] args) {
        SpringApplication.run(ContactParserApplication.class, args);
    }

    @Override
    public void run(String... args) {

        if (args.length == 0) {
            System.err.println("Please provide XML file path");
            return;
        }

        try {
            parserService.parse(args[0])
                    .forEach(System.out::println);
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}