package com.example.contactparser.controller;

import com.example.contactparser.exception.XmlParsingException;
import com.example.contactparser.model.Contact;
import com.example.contactparser.service.ContactXmlParserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ContactParserController.class)
class ContactParserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ContactXmlParserService parserService;

    @Autowired
    private ObjectMapper objectMapper;

    private List<Contact> testContacts;

    @BeforeEach
    void setUp() {
        testContacts = new ArrayList<>();
        Contact contact1 = new Contact("1");
        contact1.setName("John");
        contact1.setLastName("DOE");
        testContacts.add(contact1);

        Contact contact2 = new Contact("2");
        contact2.setName("Jane");
        contact2.setLastName("SMITH");
        testContacts.add(contact2);
    }

    @Test
    void testParseWithFilePath() throws Exception {
        when(parserService.parse(anyString())).thenReturn(testContacts);

        String requestBody = objectMapper.writeValueAsString(
                java.util.Map.of("filePath", "test.xml")
        );

        mockMvc.perform(post("/api/parse")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.count").value(2))
                .andExpect(jsonPath("$.contacts").isArray())
                .andExpect(jsonPath("$.contacts[0].id").value("1"))
                .andExpect(jsonPath("$.contacts[0].name").value("John"))
                .andExpect(jsonPath("$.contacts[1].id").value("2"));
    }

    @Test
    void testParseWithXmlContent() throws Exception {
        when(parserService.parseFromContent(anyString())).thenReturn(testContacts);

        String requestBody = objectMapper.writeValueAsString(
                java.util.Map.of("xmlContent", "<contacts></contacts>")
        );

        mockMvc.perform(post("/api/parse")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.count").value(2));
    }

    @Test
    void testParseWithEmptyRequest() throws Exception {
        String requestBody = objectMapper.writeValueAsString(
                java.util.Map.of()
        );

        mockMvc.perform(post("/api/parse")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error").value("Either file path or XML content is required"));
    }

    @Test
    void testParseWithEmptyFilePath() throws Exception {
        String requestBody = objectMapper.writeValueAsString(
                java.util.Map.of("filePath", "")
        );

        mockMvc.perform(post("/api/parse")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    void testParseWithServiceException() throws Exception {
        when(parserService.parse(anyString())).thenThrow(new Exception("File not found"));

        String requestBody = objectMapper.writeValueAsString(
                java.util.Map.of("filePath", "nonexistent.xml")
        );

        mockMvc.perform(post("/api/parse")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error").value("An unexpected error occurred: File not found"))
                .andExpect(jsonPath("$.errorType").value("INTERNAL_ERROR"));
    }

    @Test
    void testParseWithNestedContacts() throws Exception {
        Contact parent = new Contact("1");
        parent.setName("David");
        parent.setLastName("FRALEY");
        Contact child = new Contact("2");
        child.setName("Mary");
        child.setLastName("JANE");
        parent.addSubContact(child);

        List<Contact> nestedContacts = List.of(parent);
        when(parserService.parse(anyString())).thenReturn(nestedContacts);

        String requestBody = objectMapper.writeValueAsString(
                java.util.Map.of("filePath", "test.xml")
        );

        mockMvc.perform(post("/api/parse")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.contacts[0].id").value("1"))
                .andExpect(jsonPath("$.contacts[0].contacts[0].id").value("2"));
    }

    @Test
    void testUploadFile() throws Throwable {
        String xmlContent = """
                <contacts>
                    <contact id="1">
                        <name>John</name>
                        <lastName>DOE</lastName>
                    </contact>
                </contacts>
                """;

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.xml",
                "text/xml",
                xmlContent.getBytes()
        );

        List<Contact> contacts = List.of(testContacts.get(0));
        when(parserService.parseFromInputStream(any())).thenReturn(contacts);

        mockMvc.perform(multipart("/api/parse/upload")
                        .file(file))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.count").value(1))
                .andExpect(jsonPath("$.fileName").value("test.xml"))
                .andExpect(jsonPath("$.contacts[0].id").value("1"));
    }

    @Test
    void testUploadEmptyFile() throws Exception {
        MockMultipartFile emptyFile = new MockMultipartFile(
                "file",
                "empty.xml",
                "text/xml",
                new byte[0]
        );

        mockMvc.perform(multipart("/api/parse/upload")
                        .file(emptyFile))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error").value("File is empty"));
    }

    @Test
    void testUploadFileWithException() throws Throwable {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test-contents.xml",
                "text/xml",
                "invalid xml".getBytes()
        );

        when(parserService.parseFromInputStream(any())).thenThrow(new IllegalArgumentException("XML parsing failed"));

        mockMvc.perform(multipart("/api/parse/upload")
                        .file(file))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error").value("XML parsing failed"))
                .andExpect(jsonPath("$.errorType").value("VALIDATION_ERROR"));
    }

    @Test
    void testUploadFileWithXmlParsingException() throws Throwable {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.xml",
                "text/xml",
                "invalid xml".getBytes()
        );

        XmlParsingException exception = new XmlParsingException(
                "XML Format Error: Unclosed tag",
                "UNCLOSED_TAG",
                5,
                12
        );

        when(parserService.parseFromInputStream(any())).thenThrow(exception);

        mockMvc.perform(multipart("/api/parse/upload")
                        .file(file))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error").value("XML Format Error: Unclosed tag"))
                .andExpect(jsonPath("$.errorType").value("UNCLOSED_TAG"))
                .andExpect(jsonPath("$.lineNumber").value(5))
                .andExpect(jsonPath("$.columnNumber").value(12));
    }

    @Test
    void testParseWithXmlContentPriority() throws Exception {
        when(parserService.parseFromContent(anyString())).thenReturn(testContacts);

        String requestBody = objectMapper.writeValueAsString(
                java.util.Map.of(
                        "filePath", "test.xml",
                        "xmlContent", "<contacts></contacts>"
                )
        );

        mockMvc.perform(post("/api/parse")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }
}
