package searchEngine;

import com.findwise.IndexEntry;
import com.findwise.SearchEngine;
import errors.TermNotFoundException;
import index.DocumentManager;
import index.IndexEntryImplementation;
import model.Document;

import java.util.*;

import static searchEngine.TFIDFCalculator.calculateScoreTFIDF;

public class SearchEngineImplementation implements SearchEngine {

    DocumentManager documentManagerInstance = new DocumentManager();

    Comparator<IndexEntry> indexEntryComparator = (t1, t2) -> {
        Map<String, Long> lengthsOfAllDocuments = documentManagerInstance.getLengthsOfAllDocuments();
        int difference = (int) (t1.getScore() - t2.getScore());
        if (difference == 0) {
            return (int) (lengthsOfAllDocuments.get(t1.getId()) - lengthsOfAllDocuments.get(t2.getId()));
        }
        return difference;
    };

    public static List<IndexEntry> indexDocumentsAndSearchForTerm(List<Document> documents, String term) {
        SearchEngineImplementation searchEngineInStaticMethod = new SearchEngineImplementation();
        searchEngineInStaticMethod.indexAllDocuments(documents);
        return searchEngineInStaticMethod.search(term);
    }

    @Override
    public void indexDocument(String id, String content) {
        Map<String, Long> wordAndNumberOfOccurrences = documentManagerInstance.splitDocumentIntoWordsAndCountOccurrences(content);
        for (Map.Entry<String, Long> entry : wordAndNumberOfOccurrences.entrySet()) {
            addingEntriesFromDocumentToInvertedIndex(id, entry);
        }
    }

    private void addingEntriesFromDocumentToInvertedIndex(String id, Map.Entry<String, Long> entry) {
        documentManagerInstance.getInvertedIndex().merge(entry.getKey(), new HashMap<>() {{
            put(id, entry.getValue());
        }}, (t, s) -> {
            t.put(id, entry.getValue());
            return t;
        });
    }

    public void indexAllDocuments(List<Document> listOfDocuments) {
        documentManagerInstance.setInvertedIndex(new HashMap<>());
        documentManagerInstance.setLengthsOfAllDocuments(new HashMap<>());
        documentManagerInstance.setNumberOfAllDocuments(0);
        documentManagerInstance.setAllEntriesInIndex(new HashSet<>());
        addNewDocumentsToIndex(listOfDocuments);
    }

    public void addNewDocumentsToIndex(List<Document> listOfDocuments) {
        for (Document d : listOfDocuments) {
            indexDocument(d.getName(), d.getContent());
        }
        documentManagerInstance.setNumberOfAllDocuments(listOfDocuments.size() + documentManagerInstance.getNumberOfAllDocuments());
        documentManagerInstance.addDocumentsToLengthsOfAllDocumentsMap(listOfDocuments);
        documentManagerInstance.createSetOfAllEntriesInIndex();
    }

    @Override
    public List<IndexEntry> search(String term) {
        Set<Map.Entry<String, Long>> termSearchResults = checkIfIndexMapContainsQueriedTerm(term);
        List<IndexEntry> result = new ArrayList<>();
        Set<Map.Entry<String, Long>> allEntriesInIndex = documentManagerInstance.getAllEntriesInIndex();
        for (Map.Entry<String, Long> e : termSearchResults) {
            result.add(createIndexEntry(e, termSearchResults, allEntriesInIndex));
        }
        result.sort(indexEntryComparator);
        return result;
    }

    private Set<Map.Entry<String, Long>> checkIfIndexMapContainsQueriedTerm(String term) {
        if (documentManagerInstance.getInvertedIndex().containsKey(term)) {
            return documentManagerInstance.getInvertedIndex().get(term).entrySet();
        } else {
            throw new TermNotFoundException("Sorry, no document with searched term was found");
        }
    }

    private IndexEntry createIndexEntry(Map.Entry<String, Long> entryInTermSearchResult, Set<Map.Entry<String, Long>> termSearchResults, Set<Map.Entry<String, Long>> allEntriesInIndexMap) {
        Long rawFrequencyOfOfMostOccurringTerm = findRawFrequencyOfOfMostOccurringTerm(allEntriesInIndexMap, entryInTermSearchResult.getKey());
        System.out.println("Raw frequency of most occurring term: " + rawFrequencyOfOfMostOccurringTerm);
        return new IndexEntryImplementation(entryInTermSearchResult.getKey(), calculateScoreTFIDF(entryInTermSearchResult.getValue(), rawFrequencyOfOfMostOccurringTerm, documentManagerInstance.getNumberOfAllDocuments(), termSearchResults.size()));
    }

    private Long findRawFrequencyOfOfMostOccurringTerm(Set<Map.Entry<String, Long>> allEntriesInIndex, String documentID) {
        return allEntriesInIndex.stream().filter(e -> e.getKey().equalsIgnoreCase(documentID)).max((t, s) -> (int) (t.getValue() - s.getValue())).get().getValue();
    }

    public static void main(String[] args) {
        SearchEngine searchEngine = new SearchEngineImplementation();
        List<Document> documents = new ArrayList<>();
        documents.add(new Document("Document 1", "the brown fox jumped over    the brown dog"));
        documents.add(new Document("Document 2", "the lazy brown dog sat, in the corner"));
        documents.add(new Document("Document 3", "the red fox bit the ... lazy dog"));



        ((SearchEngineImplementation) searchEngine).indexAllDocuments(documents);
        System.out.println(((SearchEngineImplementation) searchEngine).documentManagerInstance.getAllEntriesInIndex());
        System.out.println("index: " + ((SearchEngineImplementation) searchEngine).documentManagerInstance);
        System.out.println("brown: " + searchEngine.search("brown"));
        System.out.println("fox: " + searchEngine.search("fox"));
        System.out.println("the: " + searchEngine.search("the"));
//        System.out.println(((SearchEngineImplementation) searchEngine).index.values());

     /*   SearchEngine searchEngineForWikipediaExample = new SearchEngineImplementation();
        List<Document> documentsFromWikipedia = new ArrayList<>();
        documentsFromWikipedia.add(new Document("Document 1", "this is a a sample"));
        documentsFromWikipedia.add(new Document("Document 2", "this is another another example example example"));
        ((SearchEngineImplementation) searchEngineForWikipediaExample).indexAllDocuments(documentsFromWikipedia);
        System.out.println("index: " + ((SearchEngineImplementation) searchEngineForWikipediaExample).index);
        System.out.println("this: " + searchEngineForWikipediaExample.search("this"));
        System.out.println("example: " + searchEngineForWikipediaExample.search("example"));*/
    }
}