package index;

import model.Document;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Index {
    private Map<String, Map<String, Long>> index = new HashMap<>();

    private Map<String, Long> lengthsOfAllDocuments = new HashMap<>();

    private Set<Map.Entry<String, Long>> allEntriesInIndex = new HashSet<>();

    private long numberOfAllDocuments;

    public void setAllEntriesInIndex(Set<Map.Entry<String, Long>> allEntriesInIndex) {
        this.allEntriesInIndex = allEntriesInIndex;
    }

    public Set<Map.Entry<String, Long>> getAllEntriesInIndex() {
        return allEntriesInIndex;
    }

    public Map<String, Map<String, Long>> getIndex() {
        return index;
    }

    public Map<String, Long> getLengthsOfAllDocuments() {
        return lengthsOfAllDocuments;
    }

    public long getNumberOfAllDocuments() {
        return numberOfAllDocuments;
    }

    public void setIndex(Map<String, Map<String, Long>> index) {
        this.index = index;
    }

    public void setLengthsOfAllDocuments(Map<String, Long> lengthsOfAllDocuments) {
        this.lengthsOfAllDocuments = lengthsOfAllDocuments;
    }

    public void setNumberOfAllDocuments(long numberOfAllDocuments) {
        this.numberOfAllDocuments = numberOfAllDocuments;
    }

    public Map<String, Long> createInvertedIndex(String content) {
        return splitContentIntoStreamOfWords(content).collect(Collectors.groupingBy((t -> t), Collectors.counting()));
    }

    public void addDocumentsToLengthsOfAllDocumentsMap(List<Document> documentsToAdd) {
        documentsToAdd.forEach(d -> lengthsOfAllDocuments.put(d.getName(), calculateLengthOfADocument(d)));
    }

    private Stream<String> splitContentIntoStreamOfWords(String content) {
        return Arrays.stream(content.split("(\\W)"));
    }

    private long calculateLengthOfADocument(Document document) {
        return splitContentIntoStreamOfWords(document.getContent()).count();
    }

    public void createSetOfAllEntriesInIndex() {
        index.values().forEach(t -> allEntriesInIndex.addAll(t.entrySet()));
    }


}