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

    /**Invokes method <i>indexDocument</i> for each entry in documentsNameAndContent map. Throws runtime exceptions if documentsNameAndContent is null or empty.
     * @param documentsNameAndContent
     */
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

    /**Create <i>Document</i> class object with constructor using provided parameters and add it to <i>documents</i> (field in SearchEngineImplementation instances).
     * @param id      name of the indexed document
     * @param content content of the document
     */
    @Override
    public void indexDocument(String id, String content) {
        documents.add(new Document(id, content));
    }

    /**Returns list of IndexEntry objects for given term. Each index entry object has fields String <i>id</i> (name of document where given term was found)
     * and double <i>score</i> (score calculated by TF-IDF algorithm with augmented frequency - check README for details). Documents for search are provided
     * during creation of SearchEngineImplementation instance. Return value is sorted by score, descending. In case of identical score,
     * IndexEntries are sorted by overall length of document, ascending.
     * @param term searched term (single word)
     * @return list of IndexEntries for searched term, consisting of id (name) of a document where term was found and calculated TF-IDF score
     */
    @Override
    public List<IndexEntry> search(String term) {
        if (term == null) {
            throw new NullPointerException("Term provided for search method equals null");
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

    private IndexEntry createIndexEntry(Map.Entry<String, Long> entryInTermSearchResult,
                                        Set<Map.Entry<String, Long>> termSearchResults,
                                        List<Map.Entry<String, Long>> allEntriesInIndexMap) {
        Long rawFrequencyOfOfMostOccurringTerm = findRawFrequencyOfMostOccurringTerm(allEntriesInIndexMap, entryInTermSearchResult.getKey());
        double TFIDFScore = calculateScoreTFIDF(entryInTermSearchResult.getValue(), rawFrequencyOfOfMostOccurringTerm,
                documentManagerInstance.getLengthsOfAllDocuments().size(), termSearchResults.size());
        return new IndexEntryImplementation(entryInTermSearchResult.getKey(), TFIDFScore);
    }

    private Long findRawFrequencyOfMostOccurringTerm(List<Map.Entry<String, Long>> allEntriesInIndex,
                                                     String documentID) {
        return allEntriesInIndex.stream().filter(e -> e.getKey().equalsIgnoreCase(documentID)).max((t, s) -> (int) (t.getValue() - s.getValue())).get().getValue();
    }

    public static void main(String[] args) {

    }

}