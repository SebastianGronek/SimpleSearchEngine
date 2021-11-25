package index;

import errors.InvalidInputException;

import java.util.Arrays;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DocumentHelper {
    public static Stream<String> splitContentIntoStreamOfWords(String content) {
        if (content == null) {
            throw new NullPointerException("Content of the document equals null");
        } else if (content.replaceAll("(\\p{Punct})", "").isBlank()) {
            throw new InvalidInputException("The document is empty - no content provided or contains only punctuation");
        }
        return Arrays.stream(content.split("(\\W+)"));
    }

    public static long calculateLengthOfADocument(String content) {
        return splitContentIntoStreamOfWords(content).count();
    }

    public static Map<String, Long> splitDocumentIntoWordsAndCountOccurrences(String content) {
        return splitContentIntoStreamOfWords(content).collect(Collectors.groupingBy((t -> t.toLowerCase(Locale.ROOT)), Collectors.counting()));
    }
}