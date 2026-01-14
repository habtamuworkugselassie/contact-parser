package com.example.contactparser.model;

import java.util.ArrayList;
import java.util.List;

public class Contact {

    private String id;
    private String name;
    private String lastName;
    private List<Contact> contacts = new ArrayList<>();

    public Contact(String id) {
        this.id = id;
    }

    public void addSubContact(Contact contact) {
        this.contacts.add(contact);
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getLastName() {
        return lastName;
    }

    public List<Contact> getContacts() {
        return contacts;
    }

    @Override
    public String toString() {
        return "Contact{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", lastName='" + lastName + '\'' +
                ", contacts=" + contacts +
                '}';
    }
}