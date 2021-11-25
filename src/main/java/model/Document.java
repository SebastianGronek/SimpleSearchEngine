package model;

import static index.DocumentHelper.calculateLengthOfADocument;

public class Document {
    private final String name;
    private final String content;
    private final long lengthOfADocument;

    public Document(String name, String content) {
        this.name = name;
        this.content = content;
        lengthOfADocument = calculateLengthOfADocument(content);
    }

    public String getName() {
        return name;
    }

    public String getContent() {
        return content;
    }

    public long getLengthOfADocument() {
        return lengthOfADocument;
    }
}
