package com.example.contactparser.service;

import com.example.contactparser.model.Contact;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ContactXmlParserServiceTest {

    private ContactXmlParserService service;

    @BeforeEach
    void setUp() {
        service = new ContactXmlParserService();
    }

    @Test
    void testParseFromFile(@TempDir Path tempDir) throws Exception {
        File xmlFile = tempDir.resolve("test.xml").toFile();
        try (FileWriter writer = new FileWriter(xmlFile)) {
            writer.write("""
                    <contacts>
                        <contact id="1">
                            <name>John</name>
                            <lastName>DOE</lastName>
                        </contact>
                    </contacts>
                    """);
        }

        List<Contact> contacts = service.parse(xmlFile.getAbsolutePath());

        assertEquals(1, contacts.size());
        assertEquals("1", contacts.get(0).getId());
        assertEquals("John", contacts.get(0).getName());
        assertEquals("DOE", contacts.get(0).getLastName());
    }

    @Test
    void testParseFromFileNotFound() {
        Exception exception = assertThrows(Exception.class, () -> {
            service.parse("nonexistent.xml");
        });

        assertTrue(exception.getMessage().contains("File not found"));
    }

    @Test
    void testParseFromDirectory(@TempDir Path tempDir) {
        String dirPath = tempDir.toString();
        Exception exception = assertThrows(Exception.class, () -> {
            service.parse(dirPath);
        });

        assertTrue(exception.getMessage().contains("Path is not a file"));
    }

    @Test
    void testParseFromContent() throws Exception {
        String xmlContent = """
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

        List<Contact> contacts = service.parseFromContent(xmlContent);

        assertEquals(2, contacts.size());
        assertEquals("1", contacts.get(0).getId());
        assertEquals("2", contacts.get(1).getId());
    }

    @Test
    void testParseFromContentNull() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            service.parseFromContent(null);
        });

        assertTrue(exception.getMessage().contains("empty") || exception.getMessage().contains("null"));
    }

    @Test
    void testParseFromContentEmpty() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            service.parseFromContent("   ");
        });

        assertTrue(exception.getMessage().contains("empty") || exception.getMessage().contains("Invalid XML"));
    }

    @Test
    void testParseFromContentNested() throws Exception {
        String xmlContent = """
                <contacts>
                    <contact id="1">
                        <name>David</name>
                        <lastName>FRALEY</lastName>
                        <contacts>
                            <contact id="2">
                                <name>Mary</name>
                                <lastName>JANE</lastName>
                            </contact>
                        </contacts>
                    </contact>
                </contacts>
                """;

        List<Contact> contacts = service.parseFromContent(xmlContent);

        assertEquals(1, contacts.size());
        Contact parent = contacts.get(0);
        assertEquals("David", parent.getName());
        assertEquals(1, parent.getContacts().size());
        assertEquals("Mary", parent.getContacts().get(0).getName());
    }

    @Test
    void testParseFromContentMalformed() {
        String malformedXml = "<contacts><contact id=\"1\"><name>John</name></contact>";

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            service.parseFromContent(malformedXml);
        });

        assertTrue(exception.getMessage().contains("XML") || exception.getMessage().contains("format") || exception.getMessage().contains("parsing"));
    }

    @Test
    void testParseFromContentInvalidStart() {
        String invalidXml = "This is not XML";

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            service.parseFromContent(invalidXml);
        });

        assertTrue(exception.getMessage().contains("Invalid XML") || exception.getMessage().contains("start"));
    }

    @Test
    void testParseFromContentMissingRoot() {
        String invalidXml = "<other><tag>content</tag></other>";

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            service.parseFromContent(invalidXml);
        });

        assertTrue(exception.getMessage().contains("root") || exception.getMessage().contains("contacts"));
    }

    @Test
    void testParseFromInputStream() throws Throwable {
        String xmlContent = """
                <contacts>
                    <contact id="1">
                        <name>John</name>
                        <lastName>DOE</lastName>
                    </contact>
                </contacts>
                """;

        try (InputStream inputStream = new java.io.ByteArrayInputStream(xmlContent.getBytes())) {
            List<Contact> contacts = service.parseFromInputStream(inputStream);

            assertEquals(1, contacts.size());
            assertEquals("1", contacts.get(0).getId());
            assertEquals("John", contacts.get(0).getName());
        }
    }

    @Test
    void testParseFromInputStreamNull() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            service.parseFromInputStream(null);
        });

        assertTrue(exception.getMessage().contains("null") || exception.getMessage().contains("InputStream"));
    }

    @Test
    void testParseFromFileWithSpecialCharacters(@TempDir Path tempDir) throws Exception {
        File xmlFile = tempDir.resolve("test.xml").toFile();
        try (FileWriter writer = new FileWriter(xmlFile)) {
            writer.write("""
                    <contacts>
                        <contact id="1">
                            <name>John &amp; Jane</name>
                            <lastName>O'Brien</lastName>
                        </contact>
                    </contacts>
                    """);
        }

        List<Contact> contacts = service.parse(xmlFile.getAbsolutePath());

        assertEquals(1, contacts.size());
        assertNotNull(contacts.get(0).getName());
        assertNotNull(contacts.get(0).getLastName());
    }

    @Test
    void testParseFromFileWithWhitespace(@TempDir Path tempDir) throws Exception {
        File xmlFile = tempDir.resolve("test.xml").toFile();
        try (FileWriter writer = new FileWriter(xmlFile)) {
            writer.write("""
                    <contacts>
                        <contact id="1">
                            <name>  John  </name>
                            <lastName>  DOE  </lastName>
                        </contact>
                    </contacts>
                    """);
        }

        List<Contact> contacts = service.parse(xmlFile.getAbsolutePath());

        assertEquals(1, contacts.size());
        assertEquals("John", contacts.get(0).getName());
        assertEquals("DOE", contacts.get(0).getLastName());
    }

    @Test
    void testParseFromContentWithEmptyContacts() throws Exception {
        String xmlContent = """
                <contacts>
                    <contact id="1">
                        <name>John</name>
                        <lastName>DOE</lastName>
                        <contacts />
                    </contact>
                </contacts>
                """;

        List<Contact> contacts = service.parseFromContent(xmlContent);

        assertEquals(1, contacts.size());
        assertTrue(contacts.get(0).getContacts().isEmpty());
    }
}
