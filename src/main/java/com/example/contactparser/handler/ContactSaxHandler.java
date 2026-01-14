package com.example.contactparser.handler;

import com.example.contactparser.model.Contact;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

public class ContactSaxHandler extends DefaultHandler {

    private final List<Contact> rootContacts = new ArrayList<>();
    private final Deque<Contact> stack = new ArrayDeque<>();
    private final StringBuilder content = new StringBuilder();

    public List<Contact> getRootContacts() {
        return rootContacts;
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) {
        content.setLength(0);

        if ("contact".equals(qName)) {
            Contact contact = new Contact(attributes.getValue("id"));

            if (stack.isEmpty()) {
                rootContacts.add(contact);
            } else {
                stack.peek().addSubContact(contact);
            }

            stack.push(contact);
        }
    }

    @Override
    public void characters(char[] ch, int start, int length) {
        content.append(ch, start, length);
    }

    @Override
    public void endElement(String uri, String localName, String qName) {
        if (stack.isEmpty()) return;

        Contact current = stack.peek();
        String value = content.toString().trim();

        switch (qName) {
            case "name" -> current.setName(value);
            case "lastName" -> current.setLastName(value);
            case "contact" -> stack.pop();
        }
    }
}