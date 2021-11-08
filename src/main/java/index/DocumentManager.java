package index;

import errors.InvalidInputException;
import model.Document;

import java.util.*;

public class DocumentManager {
    private final Map<String, Map<String, Long>> invertedIndex = new HashMap<>();

    private final HashMap<String, Long> lengthsOfAllDocuments = new HashMap<>();

    private final List<Map.Entry<String, Long>> allEntriesInIndex = new ArrayList<>();


    private DocumentManager() {
    }

    public static DocumentManager DocumentManagerFactory(List<Document> documents) {
        if (documents == null) {
            throw new NullPointerException("Provided list of documents is null");
        } else if (documents.isEmpty()) {
            throw new InvalidInputException("Provided list of documents is empty");
        }
        DocumentManager documentManager = new DocumentManager();
        documentManager.addDocumentsToLengthsOfAllDocumentsMap(documents);
        return documentManager;
    }

    public Map<String, Long> getLengthsOfAllDocuments() {
        return lengthsOfAllDocuments;
    }

    public List<Map.Entry<String, Long>> getAllEntriesInIndex() {
        return allEntriesInIndex;
    }

    public Map<String, Map<String, Long>> getInvertedIndex() {
        return invertedIndex;
    }

    void addDocumentsToLengthsOfAllDocumentsMap(List<Document> documentsToAdd) {
        documentsToAdd.forEach(d -> lengthsOfAllDocuments.put(d.getName(), d.getLengthOfADocument()));
    }

    public void createSetOfAllEntriesInIndex() {
        invertedIndex.values().forEach(t -> allEntriesInIndex.addAll(t.entrySet()));
    }
}
