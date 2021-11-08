package model;

import errors.InvalidInputException;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

    static Stream<String> splitContentIntoStreamOfWords(String content) {
        if (content == null) {
            throw new NullPointerException("Content of the document equals null");
        } else if (content.replaceAll("(\\p{Punct})", "").isBlank()) {
            throw new InvalidInputException("The document is empty - no content provided or contains only punctuation");
        }
        return Arrays.stream(content.split("(\\W+)"));
    }

    private long calculateLengthOfADocument(String content) {
        return splitContentIntoStreamOfWords(content).count();
    }

    public static Map<String, Long> splitDocumentIntoWordsAndCountOccurrences(String content) {
        return splitContentIntoStreamOfWords(content).collect(Collectors.groupingBy((t -> t), Collectors.counting()));
    }
}
