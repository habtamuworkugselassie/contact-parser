# JUnit Test Suite

This project includes comprehensive JUnit tests for all components of the Contact XML Parser application.

## Test Structure

### 1. ContactTest (`src/test/java/com/example/contactparser/model/ContactTest.java`)
Tests for the Contact model class:
- Contact creation and initialization
- Setting name and last name
- Adding sub-contacts
- Nested contact structures
- toString() method

### 2. ContactSaxHandlerTest (`src/test/java/com/example/contactparser/handler/ContactSaxHandlerTest.java`)
Tests for the SAX handler that processes XML:
- Parsing simple contacts
- Parsing multiple contacts
- Parsing nested contacts
- Handling contacts without IDs
- Handling empty elements
- Trimming whitespace
- Deeply nested structures

### 3. ContactXmlParserServiceTest (`src/test/java/com/example/contactparser/service/ContactXmlParserServiceTest.java`)
Tests for the service layer:
- Parsing from file path
- Parsing from XML content string
- Parsing from InputStream
- Error handling (file not found, invalid paths, null inputs)
- Malformed XML handling
- Special characters handling
- Whitespace trimming

### 4. ContactParserControllerTest (`src/test/java/com/example/contactparser/controller/ContactParserControllerTest.java`)
Tests for the REST API endpoints:
- POST `/api/parse` with file path
- POST `/api/parse` with XML content
- POST `/api/parse/upload` for file uploads
- Error handling and validation
- Response format validation
- Nested contacts in responses

## Running Tests

To run all tests:
```bash
mvn test
```

To run a specific test class:
```bash
mvn test -Dtest=ContactTest
mvn test -Dtest=ContactSaxHandlerTest
mvn test -Dtest=ContactXmlParserServiceTest
mvn test -Dtest=ContactParserControllerTest
```

To run tests with verbose output:
```bash
mvn test -X
```

## Test Coverage

The test suite covers:
- ✅ Model functionality
- ✅ XML parsing logic
- ✅ Service layer operations
- ✅ REST API endpoints
- ✅ Error handling
- ✅ Edge cases (empty inputs, malformed XML, etc.)
- ✅ Nested contact structures
- ✅ File operations

## Dependencies

Tests use:
- JUnit 5 (included in `spring-boot-starter-test`)
- Mockito (for mocking in controller tests)
- Spring Boot Test (for MockMvc and web layer testing)

All test dependencies are included via `spring-boot-starter-test` in `pom.xml`.
