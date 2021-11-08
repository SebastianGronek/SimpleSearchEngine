package searchEngine;

import com.findwise.IndexEntry;
import com.findwise.SearchEngine;
import errors.InvalidInputException;
import errors.TermNotFoundException;
import index.DocumentManager;
import index.IndexEntryImplementation;
import model.Document;

import java.util.*;
import java.util.stream.Collectors;

import static searchEngine.TFIDFCalculator.calculateScoreTFIDF;

public class SearchEngineImplementation implements SearchEngine {

    private DocumentManager documentManagerInstance;

    private final List<Document> documents;

    public SearchEngineImplementation(Map<String, String> documentsNameAndContent) {
        documents = new ArrayList<>();
        indexAllDocuments(documentsNameAndContent);
        this.documentManagerInstance = DocumentManager.DocumentManagerFactory(documents);
    }

    private final Comparator<IndexEntry> indexEntryComparator = (t1, t2) -> {
        int difference = (int) (t1.getScore() - t2.getScore());
        if (difference == 0) {
            return (int) (documentManagerInstance.getLengthsOfAllDocuments().get(t1.getId()) - documentManagerInstance.getLengthsOfAllDocuments().get(t2.getId()));
        }
        return difference;
    };

    public void indexAllDocuments(Map<String, String> documentsNameAndContent) {
        if (documentsNameAndContent == null) {
            throw new NullPointerException("Provided map of documents is null");
        } else if (documentsNameAndContent.isEmpty()) {
            throw new InvalidInputException("Provided map of documents is empty");
        }
        for (Map.Entry<String, String> e : documentsNameAndContent.entrySet()) {
            indexDocument(e.getKey(), e.getValue());
        }
    }

    @Override
    public void indexDocument(String id, String content) {
        documents.add(new Document(id, content));
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
        term = term.toLowerCase(Locale.ROOT);
        if (documentManagerInstance.getInvertedIndex().containsKey(term)) {
            return documentManagerInstance.getInvertedIndex().get(term).entrySet();
        } else {
            throw new TermNotFoundException("Sorry, no document with searched term was found");
        }
    }

    private IndexEntry createIndexEntry(Map.Entry<String, Long> entryInTermSearchResult, Set<Map.Entry<String, Long>> termSearchResults, List<Map.Entry<String, Long>> allEntriesInIndexMap) {
        Long rawFrequencyOfOfMostOccurringTerm = findRawFrequencyOfMostOccurringTerm(allEntriesInIndexMap, entryInTermSearchResult.getKey());
        return new IndexEntryImplementation(entryInTermSearchResult.getKey(), calculateScoreTFIDF(entryInTermSearchResult.getValue(), rawFrequencyOfOfMostOccurringTerm, documentManagerInstance.getLengthsOfAllDocuments().size(), termSearchResults.size()));
    }

    private Long findRawFrequencyOfMostOccurringTerm(List<Map.Entry<String, Long>> allEntriesInIndex, String documentID) {
        return allEntriesInIndex.stream().filter(e -> e.getKey().equalsIgnoreCase(documentID)).max((t, s) -> (int) (t.getValue() - s.getValue())).get().getValue();
    }
}