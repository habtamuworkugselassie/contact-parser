package com.example.contactparser.controller;

import com.example.contactparser.exception.XmlParsingException;
import com.example.contactparser.model.Contact;
import com.example.contactparser.service.ContactXmlParserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class ContactParserController {

    private final ContactXmlParserService parserService;

    public ContactParserController(ContactXmlParserService parserService) {
        this.parserService = parserService;
    }

    @PostMapping("/parse")
    public ResponseEntity<Map<String, Object>> parseXml(@RequestBody Map<String, String> request) {
        String filePath = request.get("filePath");
        String xmlContent = request.get("xmlContent");
        Map<String, Object> response = new HashMap<>();

        try {
            List<Contact> contacts;

            if (xmlContent != null && !xmlContent.trim().isEmpty()) {
                contacts = parserService.parseFromContent(xmlContent);
            } else if (filePath != null && !filePath.trim().isEmpty()) {
                contacts = parserService.parse(filePath);
            } else {
                response.put("success", false);
                response.put("error", "Either file path or XML content is required");
                return ResponseEntity.badRequest().body(response);
            }

            response.put("success", true);
            response.put("contacts", contacts);
            response.put("count", contacts.size());
            return ResponseEntity.ok(response);
        } catch (XmlParsingException e) {
            response.put("success", false);
            response.put("error", e.getMessage());
            response.put("errorType", e.getErrorType());
            if (e.getLineNumber() != null) {
                response.put("lineNumber", e.getLineNumber());
            }
            if (e.getColumnNumber() != null) {
                response.put("columnNumber", e.getColumnNumber());
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        } catch (IllegalArgumentException e) {
            response.put("success", false);
            response.put("error", e.getMessage());
            response.put("errorType", "VALIDATION_ERROR");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("error", "An unexpected error occurred: " + e.getMessage());
            response.put("errorType", "INTERNAL_ERROR");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PostMapping("/parse/upload")
    public ResponseEntity<Map<String, Object>> parseXmlFile(@RequestParam("file") MultipartFile file) {
        Map<String, Object> response = new HashMap<>();

        if (file.isEmpty()) {
            response.put("success", false);
            response.put("error", "File is empty");
            return ResponseEntity.badRequest().body(response);
        }

        try {
            List<Contact> contacts = parserService.parseFromInputStream(file.getInputStream());
            response.put("success", true);
            response.put("contacts", contacts);
            response.put("count", contacts.size());
            response.put("fileName", file.getOriginalFilename());
            return ResponseEntity.ok(response);
        } catch (XmlParsingException e) {
            response.put("success", false);
            response.put("error", e.getMessage());
            response.put("errorType", e.getErrorType());
            if (e.getLineNumber() != null) {
                response.put("lineNumber", e.getLineNumber());
            }
            if (e.getColumnNumber() != null) {
                response.put("columnNumber", e.getColumnNumber());
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        } catch (IllegalArgumentException e) {
            response.put("success", false);
            response.put("error", e.getMessage());
            response.put("errorType", "VALIDATION_ERROR");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        } catch (Throwable e) {
            response.put("success", false);
            response.put("error", "An unexpected error occurred while processing the file: " + e.getMessage());
            response.put("errorType", "INTERNAL_ERROR");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}
