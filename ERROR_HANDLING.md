# XML Error Handling and Messaging

This document describes the improved error handling system for XML format validation and parsing errors.

## Overview

The application now provides detailed, user-friendly error messages when XML format issues are detected. Errors are categorized by type and include specific information about what went wrong and how to fix it.

## Error Types

### 1. **EMPTY_CONTENT**
- **When it occurs**: XML content is null or empty
- **Message**: "Invalid XML: The XML content is empty or null. Please provide valid XML content."

### 2. **INVALID_XML_START**
- **When it occurs**: XML doesn't start with '<' character
- **Message**: "Invalid XML format: XML content must start with '<' character..."

### 3. **MISSING_ROOT_ELEMENT**
- **When it occurs**: Root element is not '<contacts>'
- **Message**: "Invalid XML structure: The root element must be '<contacts>'..."

### 4. **UNCLOSED_TAG**
- **When it occurs**: Tags are not properly closed
- **Message**: Includes line/column numbers and suggestions for fixing

### 5. **MISMATCHED_TAG**
- **When it occurs**: Opening and closing tags don't match
- **Message**: Includes location information

### 6. **PREMATURE_END**
- **When it occurs**: XML ends unexpectedly
- **Message**: Indicates where the document ended prematurely

### 7. **INVALID_CHARACTER**
- **When it occurs**: Invalid characters in XML
- **Message**: Explains character encoding issues

### 8. **ENTITY_ERROR**
- **When it occurs**: Entity references are malformed
- **Message**: Explains proper entity usage (e.g., &amp; for &)

### 9. **ROOT_ELEMENT_ERROR**
- **When it occurs**: Issues with root element structure
- **Message**: Explains expected root element format

### 10. **XML_FORMAT_ERROR**
- **When it occurs**: General XML formatting issues
- **Message**: General formatting error with helpful suggestions

## Error Response Format

When an XML parsing error occurs, the API returns:

```json
{
  "success": false,
  "error": "Detailed error message with suggestions",
  "errorType": "ERROR_TYPE",
  "lineNumber": 5,
  "columnNumber": 12
}
```

## Example Error Messages

### Example 1: Unclosed Tag
```json
{
  "success": false,
  "error": "XML Format Error: The element type \"contact\" must be terminated by the matching end-tag \"</contact>\". (Line 3, Column 15)\n\nCommon issues:\n- Ensure all tags are properly closed (e.g., <tag>content</tag>)\n- Check for mismatched opening and closing tags\n- Verify that the root element is '<contacts>'\n- Ensure special characters are properly escaped\n- Check for unclosed quotes in attributes\n",
  "errorType": "UNCLOSED_TAG",
  "lineNumber": 3,
  "columnNumber": 15
}
```

### Example 2: Missing Root Element
```json
{
  "success": false,
  "error": "Invalid XML structure: The root element must be '<contacts>'. Expected format: <contacts>...</contacts>",
  "errorType": "MISSING_ROOT_ELEMENT"
}
```

### Example 3: Empty Content
```json
{
  "success": false,
  "error": "Invalid XML: The XML content is empty or null. Please provide valid XML content.",
  "errorType": "EMPTY_CONTENT"
}
```

## Error Message Features

1. **User-Friendly Language**: Technical errors are translated into understandable messages
2. **Location Information**: Line and column numbers when available
3. **Actionable Suggestions**: Common issues and how to fix them
4. **Categorized Errors**: Error types help identify the category of problem
5. **Detailed Context**: Explains what was expected vs. what was found

## Implementation Details

### Custom Exception Class
- `XmlParsingException` extends `IllegalArgumentException`
- Includes error type, line number, and column number
- Provides structured error information

### Error Formatting Methods
- `determineErrorType()`: Categorizes errors based on message content
- `formatParseError()`: Formats SAXParseException with helpful context
- `formatSaxError()`: Formats general SAXException errors
- `cleanErrorMessage()`: Cleans technical error messages for users

### Validation
- Pre-validation checks for empty content
- Checks for valid XML start character
- Validates root element structure
- Provides early feedback before parsing

## Testing

Error handling is tested in:
- `ContactXmlParserServiceTest`: Tests various error scenarios
- `ContactParserControllerTest`: Tests API error responses

## Best Practices

1. Always check error responses for `errorType` to understand the category
2. Use `lineNumber` and `columnNumber` when available to locate issues
3. Follow the suggestions in error messages to fix XML format issues
4. Ensure XML follows the expected structure: `<contacts><contact>...</contact></contacts>`
