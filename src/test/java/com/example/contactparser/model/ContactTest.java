package com.example.contactparser.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ContactTest {

    private Contact contact;

    @BeforeEach
    void setUp() {
        contact = new Contact("1");
    }

    @Test
    void testContactCreation() {
        assertNotNull(contact);
        assertEquals("1", contact.getId());
        assertNull(contact.getName());
        assertNull(contact.getLastName());
        assertNotNull(contact.getContacts());
        assertTrue(contact.getContacts().isEmpty());
    }

    @Test
    void testSetName() {
        contact.setName("John");
        assertEquals("John", contact.getName());
    }

    @Test
    void testSetLastName() {
        contact.setLastName("Doe");
        assertEquals("Doe", contact.getLastName());
    }

    @Test
    void testAddSubContact() {
        Contact subContact = new Contact("2");
        subContact.setName("Jane");
        subContact.setLastName("Smith");

        contact.addSubContact(subContact);

        List<Contact> contacts = contact.getContacts();
        assertEquals(1, contacts.size());
        assertEquals("2", contacts.get(0).getId());
        assertEquals("Jane", contacts.get(0).getName());
        assertEquals("Smith", contacts.get(0).getLastName());
    }

    @Test
    void testAddMultipleSubContacts() {
        Contact subContact1 = new Contact("2");
        Contact subContact2 = new Contact("3");

        contact.addSubContact(subContact1);
        contact.addSubContact(subContact2);

        assertEquals(2, contact.getContacts().size());
    }

    @Test
    void testNestedContacts() {
        Contact subContact = new Contact("2");
        Contact nestedContact = new Contact("3");
        nestedContact.setName("Nested");
        nestedContact.setLastName("Contact");

        subContact.addSubContact(nestedContact);
        contact.addSubContact(subContact);

        assertEquals(1, contact.getContacts().size());
        assertEquals(1, contact.getContacts().get(0).getContacts().size());
        assertEquals("Nested", contact.getContacts().get(0).getContacts().get(0).getName());
    }

    @Test
    void testToString() {
        contact.setName("John");
        contact.setLastName("Doe");

        String toString = contact.toString();
        assertTrue(toString.contains("id='1'"));
        assertTrue(toString.contains("name='John'"));
        assertTrue(toString.contains("lastName='Doe'"));
    }
}
