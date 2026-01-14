package com.example.contactparser.exception;

public class XmlParsingException extends IllegalArgumentException {
    
    private final String errorType;
    private final Integer lineNumber;
    private final Integer columnNumber;
    
    public XmlParsingException(String message) {
        super(message);
        this.errorType = "XML_FORMAT_ERROR";
        this.lineNumber = null;
        this.columnNumber = null;
    }
    
    public XmlParsingException(String message, String errorType) {
        super(message);
        this.errorType = errorType;
        this.lineNumber = null;
        this.columnNumber = null;
    }
    
    public XmlParsingException(String message, String errorType, Integer lineNumber, Integer columnNumber) {
        super(message);
        this.errorType = errorType;
        this.lineNumber = lineNumber;
        this.columnNumber = columnNumber;
    }
    
    public XmlParsingException(String message, Throwable cause, String errorType, Integer lineNumber, Integer columnNumber) {
        super(message, cause);
        this.errorType = errorType;
        this.lineNumber = lineNumber;
        this.columnNumber = columnNumber;
    }
    
    public String getErrorType() {
        return errorType;
    }
    
    public Integer getLineNumber() {
        return lineNumber;
    }
    
    public Integer getColumnNumber() {
        return columnNumber;
    }
}
