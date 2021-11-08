package searchEngine;

import com.findwise.IndexEntry;
import com.findwise.SearchEngine;
import errors.TermNotFoundException;
import index.DocumentManager;
import index.IndexEntryImplementation;
import model.Document;

import java.util.*;
import java.util.stream.Collectors;

import static searchEngine.TFIDFCalculator.calculateScoreTFIDF;

public class SearchEngineImplementation implements SearchEngine {

    private DocumentManager documentManagerInstance;

    public SearchEngineImplementation(List<Document> documents) {
        this.documentManagerInstance = DocumentManager.DocumentManagerFactory(documents);
    }

    private Comparator<IndexEntry> indexEntryComparator = (t1, t2) -> {
        int difference = (int) (t1.getScore() - t2.getScore());
        if (difference == 0) {
            return (int) (documentManagerInstance.getLengthsOfAllDocuments().get(t1.getId()) - documentManagerInstance.getLengthsOfAllDocuments().get(t2.getId()));
        }
        return difference;
    };


    public void addNewDocumentsToIndex(List<Document> listOfDocuments) {
        for (Document d : listOfDocuments) {
            indexDocument(d.getName(), d.getContent());
        }
        documentManagerInstance.createSetOfAllEntriesInIndex();
    }

    @Override
    public void indexDocument(String id, String content) {
        Map<String, Long> wordAndNumberOfOccurrences = Document.splitDocumentIntoWordsAndCountOccurrences(content);
        for (Map.Entry<String, Long> entry : wordAndNumberOfOccurrences.entrySet()) {
            addingEntryFromDocumentToInvertedIndex(id, entry);
        }
    }

    private void addingEntryFromDocumentToInvertedIndex(String id, Map.Entry<String, Long> entry) {
        documentManagerInstance.getInvertedIndex().merge(entry.getKey(), new HashMap<>() {{
            put(id, entry.getValue());
        }}, (t, s) -> {
            t.put(id, entry.getValue());
            return t;
        });
    }

    @Override
    public List<IndexEntry> search(String term) {
        if (term == null) {
            throw new NullPointerException("Term provided for indexDocumentsAndSearchForTerm method equals null");
        }
        Set<Map.Entry<String, Long>> termSearchResults = searchForQueriedTerm(term);
        List<Map.Entry<String, Long>> allEntriesInIndex = documentManagerInstance.getAllEntriesInIndex();
        return termSearchResults.stream().map(e -> createIndexEntry(e, termSearchResults, allEntriesInIndex)).sorted(indexEntryComparator).collect(Collectors.toList());
    }

    private Set<Map.Entry<String, Long>> searchForQueriedTerm(String term) {
        if (documentManagerInstance.getInvertedIndex().containsKey(term)) {
            return documentManagerInstance.getInvertedIndex().get(term).entrySet();
        } else {
            throw new TermNotFoundException("Sorry, no document with searched term was found");
        }
    }

    private IndexEntry createIndexEntry(Map.Entry<String, Long> entryInTermSearchResult, Set<Map.Entry<String, Long>> termSearchResults, List<Map.Entry<String, Long>> allEntriesInIndexMap) {
        Long rawFrequencyOfOfMostOccurringTerm = findRawFrequencyd .OfMostOccurringTerm(allEntriesInIndexMap, entryInTermSearchResult.getKey());
        System.out.println("Raw frequency of most occurring term: " + rawFrequencyOfOfMostOccurringTerm);
        return new IndexEntryImplementation(entryInTermSearchResult.getKey(), calculateScoreTFIDF(entryInTermSearchResult.getValue(), rawFrequencyOfOfMostOccurringTerm, documentManagerInstance.getLengthsOfAllDocuments().size(), termSearchResults.size()));
    }

    private Long findRawFrequencyOfMostOccurringTerm(List<Map.Entry<String, Long>> allEntriesInIndex, String documentID) {
        return allEntriesInIndex.stream().filter(e -> e.getKey().equalsIgnoreCase(documentID)).max((t, s) -> (int) (t.getValue() - s.getValue())).get().getValue();
    }
}