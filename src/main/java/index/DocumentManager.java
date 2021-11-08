package index;

import errors.InvalidInputException;
import model.Document;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DocumentManager {
    private final Map<String, Map<String, Long>> invertedIndex = new HashMap<>();

    private final HashMap<String, Long> lengthsOfAllDocuments = new HashMap<>();

    private final List<Map.Entry<String, Long>> allEntriesInIndex = new ArrayList<>();


    private DocumentManager() {
    }

    public static DocumentManager DocumentManagerFactory(List<Document> documents) {
        if (documents == null) {
            throw new NullPointerException("Provided map of documents is null");
        } else if (documents.isEmpty()) {
            throw new InvalidInputException("Provided map of documents is empty");
        }
        DocumentManager documentManager = new DocumentManager();
        documentManager.addDocumentsToLengthsOfAllDocumentsMap(documents);
        documentManager.addNewDocumentsToIndex(documents);
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


    private void addNewDocumentsToIndex(List<Document> listOfDocuments) {
        for (Document d : listOfDocuments) {
            createInvertedIndexFromADocument(d.getName(), d.getContent());
        }
        createListOfAllEntriesInIndex();
    }

    private void createInvertedIndexFromADocument(String id, String content) {
        Map<String, Long> wordAndNumberOfOccurrences = Document.splitDocumentIntoWordsAndCountOccurrences(content);
        for (Map.Entry<String, Long> entry : wordAndNumberOfOccurrences.entrySet()) {
            addSingleEntryFromDocumentToInvertedIndex(id, entry);
        }
    }

    private void addSingleEntryFromDocumentToInvertedIndex(String id, Map.Entry<String, Long> entry) {
        getInvertedIndex().merge(entry.getKey(), new HashMap<>() {{
            put(id, entry.getValue());
        }}, (t, s) -> {
            t.put(id, entry.getValue());
            return t;
        });
    }

    private void createListOfAllEntriesInIndex() {
        invertedIndex.values().forEach(t -> allEntriesInIndex.addAll(t.entrySet()));
    }
}
