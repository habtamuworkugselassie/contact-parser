package com.example.contactparser.service;

import com.example.contactparser.exception.XmlParsingException;
import com.example.contactparser.model.Contact;
import com.example.contactparser.handler.ContactSaxHandler;
import org.springframework.stereotype.Service;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.*;
import java.util.List;

@Service
public class ContactXmlParserService {

    public List<Contact> parse(String filePath) throws Exception {
        File file = new File(filePath);

        if (!file.exists()) {
            throw new FileNotFoundException("File not found: " + filePath);
        }

        if (!file.isFile()) {
            throw new IllegalArgumentException("Path is not a file: " + filePath);
        }

        if (!file.canRead()) {
            throw new SecurityException("Cannot read file: " + filePath);
        }

        try {
            SAXParserFactory factory = SAXParserFactory.newInstance();
            factory.setNamespaceAware(false);
            factory.setValidating(false);
            factory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);

            SAXParser parser = factory.newSAXParser();
            ContactSaxHandler handler = new ContactSaxHandler();

            parser.parse(file, handler);
            return handler.getRootContacts();

        } catch (SAXParseException e) {
            String errorType = determineErrorType(e.getMessage());
            String userFriendlyMessage = formatParseError(e, errorType);
            throw new Exception(userFriendlyMessage, e);
        } catch (SAXException e) {
            String errorType = determineErrorType(e.getMessage());
            String userFriendlyMessage = formatSaxError(e, errorType);
            throw new Exception(userFriendlyMessage, e);
        } catch (IOException e) {
            throw new Exception("Error reading XML file: " + e.getMessage() + ". Please check that the file is accessible and properly formatted.", e);
        } catch (Exception e) {
            throw new Exception("Unexpected error while parsing XML file: " + e.getMessage() + ". Please verify that the file contains well-formed XML.", e);
        }
    }

    public List<Contact> parseFromContent(String xmlContent) {
        if (xmlContent == null || xmlContent.trim().isEmpty()) {
            throw new XmlParsingException(
                "Invalid XML: The XML content is empty or null. Please provide valid XML content.",
                "EMPTY_CONTENT"
            );
        }

        String trimmedContent = xmlContent.trim();
        
        if (!trimmedContent.startsWith("<")) {
            throw new XmlParsingException(
                "Invalid XML format: XML content must start with '<' character. The provided content does not appear to be valid XML.",
                "INVALID_XML_START"
            );
        }

        if (!trimmedContent.contains("<contacts>") && !trimmedContent.contains("<contacts ")) {
            throw new XmlParsingException(
                "Invalid XML structure: The root element must be '<contacts>'. Expected format: <contacts>...</contacts>",
                "MISSING_ROOT_ELEMENT"
            );
        }

        try {
            SAXParserFactory factory = SAXParserFactory.newInstance();
            factory.setNamespaceAware(false);
            factory.setValidating(false);
            factory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);

            SAXParser parser = factory.newSAXParser();
            ContactSaxHandler handler = new ContactSaxHandler();

            try (StringReader reader = new StringReader(xmlContent)) {
                InputSource inputSource = new InputSource(reader);
                parser.parse(inputSource, handler);
            }

            return handler.getRootContacts();

        } catch (SAXParseException e) {
            String errorType = determineErrorType(e.getMessage());
            String userFriendlyMessage = formatParseError(e, errorType);
            throw new XmlParsingException(
                userFriendlyMessage,
                errorType,
                e.getLineNumber(),
                e.getColumnNumber()
            );
        } catch (SAXException e) {
            String errorType = determineErrorType(e.getMessage());
            String userFriendlyMessage = formatSaxError(e, errorType);
            throw new XmlParsingException(
                userFriendlyMessage,
                errorType
            );
        } catch (IOException e) {
            throw new XmlParsingException(
                "Error reading XML content: " + e.getMessage() + ". Please check that the XML content is properly formatted.",
                "IO_ERROR"
            );
        } catch (Exception e) {
            throw new XmlParsingException(
                "Unexpected error while parsing XML: " + e.getMessage() + ". Please verify that the XML content is well-formed and follows the expected structure.",
                "UNEXPECTED_ERROR"
            );
        }
    }

    public List<Contact> parseFromInputStream(InputStream inputStream) {
        if (inputStream == null) {
            throw new XmlParsingException(
                "Invalid input: InputStream cannot be null. Please provide a valid file or XML content.",
                "NULL_INPUT_STREAM"
            );
        }

        try {
            SAXParserFactory factory = SAXParserFactory.newInstance();
            factory.setNamespaceAware(false);
            factory.setValidating(false);
            factory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);

            SAXParser parser = factory.newSAXParser();
            ContactSaxHandler handler = new ContactSaxHandler();

            parser.parse(inputStream, handler);
            return handler.getRootContacts();

        } catch (SAXParseException e) {
            String errorType = determineErrorType(e.getMessage());
            String userFriendlyMessage = formatParseError(e, errorType);
            throw new XmlParsingException(
                userFriendlyMessage,
                errorType,
                e.getLineNumber(),
                e.getColumnNumber()
            );
        } catch (SAXException e) {
            String errorType = determineErrorType(e.getMessage());
            String userFriendlyMessage = formatSaxError(e, errorType);
            throw new XmlParsingException(
                userFriendlyMessage,
                errorType
            );
        } catch (IOException e) {
            throw new XmlParsingException(
                "Error reading XML file: " + e.getMessage() + ". Please check that the file is accessible and properly formatted.",
                "IO_ERROR"
            );
        } catch (Exception e) {
            throw new XmlParsingException(
                "Unexpected error while parsing XML file: " + e.getMessage() + ". Please verify that the file contains well-formed XML following the expected structure.",
                "UNEXPECTED_ERROR"
            );
        }
    }

    private String determineErrorType(String errorMessage) {
        if (errorMessage == null) {
            return "UNKNOWN_ERROR";
        }
        
        String lowerMessage = errorMessage.toLowerCase();
        
        if (lowerMessage.contains("unclosed tag") || lowerMessage.contains("must be terminated")) {
            return "UNCLOSED_TAG";
        }
        if (lowerMessage.contains("attribute") && lowerMessage.contains("duplicate")) {
            return "DUPLICATE_ATTRIBUTE";
        }
        if (lowerMessage.contains("element") && (lowerMessage.contains("not allowed") || lowerMessage.contains("invalid"))) {
            return "INVALID_ELEMENT";
        }
        if (lowerMessage.contains("character") && lowerMessage.contains("invalid")) {
            return "INVALID_CHARACTER";
        }
        if (lowerMessage.contains("entity") || lowerMessage.contains("reference")) {
            return "ENTITY_ERROR";
        }
        if (lowerMessage.contains("premature end") || lowerMessage.contains("unexpected end")) {
            return "PREMATURE_END";
        }
        if (lowerMessage.contains("mismatched tag") || lowerMessage.contains("tag name")) {
            return "MISMATCHED_TAG";
        }
        if (lowerMessage.contains("root element") || lowerMessage.contains("document element")) {
            return "ROOT_ELEMENT_ERROR";
        }
        
        return "XML_FORMAT_ERROR";
    }

    private String formatParseError(SAXParseException e, String errorType) {
        StringBuilder message = new StringBuilder();
        
        message.append("XML Format Error: ");
        
        String originalMessage = e.getMessage();
        if (originalMessage != null) {
            String cleanedMessage = cleanErrorMessage(originalMessage);
            message.append(cleanedMessage);
        } else {
            message.append("The XML document contains formatting errors.");
        }
        
        if (e.getLineNumber() > 0) {
            message.append(String.format(" (Line %d", e.getLineNumber()));
            if (e.getColumnNumber() > 0) {
                message.append(String.format(", Column %d", e.getColumnNumber()));
            }
            message.append(")");
        }
        
        message.append("\n\n");
        message.append("Common issues:\n");
        message.append("- Ensure all tags are properly closed (e.g., <tag>content</tag>)\n");
        message.append("- Check for mismatched opening and closing tags\n");
        message.append("- Verify that the root element is '<contacts>'\n");
        message.append("- Ensure special characters are properly escaped\n");
        message.append("- Check for unclosed quotes in attributes\n");
        
        return message.toString();
    }

    private String formatSaxError(SAXException e, String errorType) {
        StringBuilder message = new StringBuilder();
        
        message.append("XML Parsing Error: ");
        
        String originalMessage = e.getMessage();
        if (originalMessage != null) {
            String cleanedMessage = cleanErrorMessage(originalMessage);
            message.append(cleanedMessage);
        } else {
            message.append("The XML document could not be parsed. Please check the XML structure.");
        }
        
        message.append("\n\n");
        message.append("Please verify:\n");
        message.append("- The XML is well-formed and valid\n");
        message.append("- All elements are properly nested\n");
        message.append("- The document follows the expected structure: <contacts><contact>...</contact></contacts>\n");
        
        return message.toString();
    }

    private String cleanErrorMessage(String message) {
        if (message == null) {
            return "Unknown error occurred";
        }
        
        String cleaned = message
            .replace("org.xml.sax.SAXParseException;", "")
            .replace("The entity name must immediately follow the '&' in the entity reference.", 
                     "Invalid entity reference. Use '&amp;' for '&', '&lt;' for '<', '&gt;' for '>'")
            .replace("Content is not allowed in prolog.", 
                     "Invalid content before the XML declaration or root element. Ensure the XML starts with '<contacts>'")
            .replace("The markup in the document following the root element must be well-formed.", 
                     "Content after the root element is not properly formatted")
            .trim();
        
        if (cleaned.isEmpty()) {
            return "The XML document contains formatting errors that prevent parsing.";
        }
        
        return cleaned;
    }
}