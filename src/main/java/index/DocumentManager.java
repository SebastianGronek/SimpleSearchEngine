package index;

import errors.InvalidInputException;
import model.Document;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DocumentManager {
    /**
     * Map with <i>terms</i> (words) as keys and <i>map(String, Long)</i> as values.
     * Inner map contains entries with names of documents in which <i>term</i> was found as keys and number of occurrences in particular document as values.
     */
    private final Map<String, Map<String, Long>> invertedIndex = new HashMap<>();

    /**
     * Map with names of documents as keys and their lengths (number of words) as values, used in sorting <I>IndexEntries</I>.
     */
    private final Map<String, Long> lengthsOfAllDocuments = new HashMap<>();

    private final List<Map.Entry<String, Long>> allEntriesInIndex = new ArrayList<>();

    private DocumentManager() {
    }

    /**
     * Creates DocumentManager instance using given list of <i>Document</i> objects and set its fields
     * (<i>invertedIndex</i>, <i>lengthsOfAllDocuments</i> and <i>allEntriesInIndex</i>) accordingly.
     *
     * @param documents list of <i>Document</i> objects
     * @return DocumentManager instance
     */
    public static DocumentManager DocumentManagerFactory(List<Document> documents) {
        if (documents == null) {
            throw new NullPointerException("Provided list of documents is null");
        } else if (documents.isEmpty()) {
            throw new InvalidInputException("Provided list of documents is empty");
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

    /**
     * Add documents from <i>documentsToAdd</i> list to <i>lengthsOfAllDocuments</i> map
     *
     * @param documentsToAdd
     */
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
        Map<String, Long> wordAndNumberOfOccurrences = DocumentHelper.splitDocumentIntoWordsAndCountOccurrences(content);
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
