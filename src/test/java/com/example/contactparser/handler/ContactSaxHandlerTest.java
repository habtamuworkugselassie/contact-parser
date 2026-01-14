package com.example.contactparser.handler;

import com.example.contactparser.model.Contact;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.AttributesImpl;

import org.xml.sax.InputSource;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.StringReader;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ContactSaxHandlerTest {

    private ContactSaxHandler handler;
    private SAXParser parser;

    @BeforeEach
    void setUp() throws Exception {
        handler = new ContactSaxHandler();
        SAXParserFactory factory = SAXParserFactory.newInstance();
        factory.setNamespaceAware(false);
        factory.setValidating(false);
        parser = factory.newSAXParser();
    }

    @Test
    void testParseSimpleContact() throws Exception {
        String xml = """
                <contacts>
                    <contact id="1">
                        <name>John</name>
                        <lastName>DOE</lastName>
                    </contact>
                </contacts>
                """;

        parser.parse(new InputSource(new StringReader(xml)), handler);
        List<Contact> contacts = handler.getRootContacts();

        assertEquals(1, contacts.size());
        Contact contact = contacts.get(0);
        assertEquals("1", contact.getId());
        assertEquals("John", contact.getName());
        assertEquals("DOE", contact.getLastName());
        assertTrue(contact.getContacts().isEmpty());
    }

    @Test
    void testParseMultipleContacts() throws Exception {
        String xml = """
                <contacts>
                    <contact id="1">
                        <name>John</name>
                        <lastName>DOE</lastName>
                    </contact>
                    <contact id="2">
                        <name>Jane</name>
                        <lastName>SMITH</lastName>
                    </contact>
                </contacts>
                """;

        parser.parse(new InputSource(new StringReader(xml)), handler);
        List<Contact> contacts = handler.getRootContacts();

        assertEquals(2, contacts.size());
        assertEquals("1", contacts.get(0).getId());
        assertEquals("John", contacts.get(0).getName());
        assertEquals("2", contacts.get(1).getId());
        assertEquals("Jane", contacts.get(1).getName());
    }

    @Test
    void testParseNestedContacts() throws Exception {
        String xml = """
                <contacts>
                    <contact id="1">
                        <name>David</name>
                        <lastName>FRALEY</lastName>
                        <contacts>
                            <contact id="2">
                                <name>Mary</name>
                                <lastName>JANE</lastName>
                                <contacts />
                            </contact>
                        </contacts>
                    </contact>
                </contacts>
                """;

        parser.parse(new InputSource(new StringReader(xml)), handler);
        List<Contact> contacts = handler.getRootContacts();

        assertEquals(1, contacts.size());
        Contact parent = contacts.get(0);
        assertEquals("1", parent.getId());
        assertEquals("David", parent.getName());
        assertEquals("FRALEY", parent.getLastName());

        assertEquals(1, parent.getContacts().size());
        Contact child = parent.getContacts().get(0);
        assertEquals("2", child.getId());
        assertEquals("Mary", child.getName());
        assertEquals("JANE", child.getLastName());
        assertTrue(child.getContacts().isEmpty());
    }

    @Test
    void testParseContactWithoutId() throws Exception {
        String xml = """
                <contacts>
                    <contact>
                        <name>John</name>
                        <lastName>DOE</lastName>
                    </contact>
                </contacts>
                """;

        parser.parse(new InputSource(new StringReader(xml)), handler);
        List<Contact> contacts = handler.getRootContacts();

        assertEquals(1, contacts.size());
        assertNull(contacts.get(0).getId());
    }

    @Test
    void testParseContactWithEmptyElements() throws Exception {
        String xml = """
                <contacts>
                    <contact id="1">
                        <name></name>
                        <lastName></lastName>
                    </contact>
                </contacts>
                """;

        parser.parse(new InputSource(new StringReader(xml)), handler);
        List<Contact> contacts = handler.getRootContacts();

        assertEquals(1, contacts.size());
        assertEquals("1", contacts.get(0).getId());
        assertEquals("", contacts.get(0).getName());
        assertEquals("", contacts.get(0).getLastName());
    }

    @Test
    void testParseContactWithWhitespace() throws Exception {
        String xml = """
                <contacts>
                    <contact id="1">
                        <name>  John  </name>
                        <lastName>  DOE  </lastName>
                    </contact>
                </contacts>
                """;

        parser.parse(new InputSource(new StringReader(xml)), handler);
        List<Contact> contacts = handler.getRootContacts();

        assertEquals(1, contacts.size());
        assertEquals("John", contacts.get(0).getName());
        assertEquals("DOE", contacts.get(0).getLastName());
    }

    @Test
    void testParseEmptyContacts() throws Exception {
        String xml = "<contacts></contacts>";

        parser.parse(new InputSource(new StringReader(xml)), handler);
        List<Contact> contacts = handler.getRootContacts();

        assertTrue(contacts.isEmpty());
    }

    @Test
    void testParseDeeplyNestedContacts() throws Exception {
        String xml = """
                <contacts>
                    <contact id="1">
                        <name>Level1</name>
                        <contacts>
                            <contact id="2">
                                <name>Level2</name>
                                <contacts>
                                    <contact id="3">
                                        <name>Level3</name>
                                    </contact>
                                </contacts>
                            </contact>
                        </contacts>
                    </contact>
                </contacts>
                """;

        parser.parse(new InputSource(new StringReader(xml)), handler);
        List<Contact> contacts = handler.getRootContacts();

        assertEquals(1, contacts.size());
        Contact level1 = contacts.get(0);
        assertEquals("Level1", level1.getName());
        assertEquals(1, level1.getContacts().size());

        Contact level2 = level1.getContacts().get(0);
        assertEquals("Level2", level2.getName());
        assertEquals(1, level2.getContacts().size());

        Contact level3 = level2.getContacts().get(0);
        assertEquals("Level3", level3.getName());
    }
}
