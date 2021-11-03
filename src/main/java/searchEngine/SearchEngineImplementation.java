package searchEngine;

import com.findwise.IndexEntry;
import com.findwise.SearchEngine;
import errors.TermNotFoundException;
import index.Index;
import index.IndexEntryImplementation;
import model.Document;

import java.util.*;

import static searchEngine.TFIDFCalculator.calculateScoreTFIDF;

public class SearchEngineImplementation implements SearchEngine {

    Index indexInstance = new Index();

    Comparator<IndexEntry> indexEntryComparator = (t1, t2) -> {
        Map<String, Long> lengthsOfAllDocuments = indexInstance.getLengthsOfAllDocuments();
        int difference = (int) (t1.getScore() - t2.getScore());
        if (difference == 0) {
            return (int) (lengthsOfAllDocuments.get(t2.getId()) - lengthsOfAllDocuments.get(t1.getId()));
        }
        return difference;
    };

    @Override
    public void indexDocument(String id, String content) {
        Map<String, Long> wordAndNumberOfOccurrences = indexInstance.createInvertedIndex(content);
        for (Map.Entry<String, Long> entry : wordAndNumberOfOccurrences.entrySet()) {
            indexInstance.getIndex().merge(entry.getKey(), new HashMap<String, Long>() {{
                put(id, entry.getValue());
            }}, (t, s) -> {
                t.put(id, entry.getValue());
                return t;
            });
        }
    }

    public void indexAllDocuments(List<Document> listOfDocuments) {
        indexInstance.setIndex(new HashMap<>());
        indexInstance.setLengthsOfAllDocuments(new HashMap<>());
        indexInstance.setNumberOfAllDocuments(0);
        indexInstance.setAllEntriesInIndex(new HashSet<>());
        addNewDocumentsToIndex(listOfDocuments);
    }

    public void addNewDocumentsToIndex(List<Document> listOfDocuments) {
        for (Document d : listOfDocuments) {
            indexDocument(d.getName(), d.getContent());
        }
        indexInstance.setNumberOfAllDocuments(listOfDocuments.size() + indexInstance.getNumberOfAllDocuments());
        indexInstance.addDocumentsToLengthsOfAllDocumentsMap(listOfDocuments);
        indexInstance.createSetOfAllEntriesInIndex();
    }

    @Override
    public List<IndexEntry> search(String term) {
        Set<Map.Entry<String, Long>> termSearchResults = checkIfIndexContainsQueriedTerm(term);
        List<IndexEntry> result = new ArrayList<>();
        Set<Map.Entry<String, Long>> allEntriesInIndex = indexInstance.getAllEntriesInIndex();
        for (Map.Entry<String, Long> e : termSearchResults) {
            result.add(createIndexEntry(e, termSearchResults, allEntriesInIndex));
        }
        result.sort(indexEntryComparator);
        return result;
    }

    private Set<Map.Entry<String, Long>> checkIfIndexContainsQueriedTerm(String term) {
        if (indexInstance.getIndex().containsKey(term)) {
            return indexInstance.getIndex().get(term).entrySet();
        } else {
            throw new TermNotFoundException("Sorry, no document with searched term was found");
        }
    }

    private IndexEntry createIndexEntry(Map.Entry<String, Long> entryInTermSearchResult, Set<Map.Entry<String, Long>> termSearchResults, Set<Map.Entry<String, Long>> allEntriesInIndex) {
        Long rawFrequencyOfOfMostOccurringTerm = findRawFrequencyOfOfMostOccurringTerm(allEntriesInIndex, entryInTermSearchResult.getKey());
        System.out.println("Raw frequency of most occurring term: " + rawFrequencyOfOfMostOccurringTerm);
        return new IndexEntryImplementation(entryInTermSearchResult.getKey(), calculateScoreTFIDF(entryInTermSearchResult.getValue(), rawFrequencyOfOfMostOccurringTerm, indexInstance.getNumberOfAllDocuments(), termSearchResults.size()));
    }



  /*  private long calculatingNumberOfOccurrencesOfGivenTermInDocument(String term, Document document) {
        return Arrays.stream(document.getContent().split("(\\b[^\\s]+\\b)")).filter(t -> t.equalsIgnoreCase(term)).count();
    }*/

    private Long findRawFrequencyOfOfMostOccurringTerm(Set<Map.Entry<String, Long>> allEntriesInIndex, String documentID) {
        return allEntriesInIndex.stream().filter(e -> e.getKey().equalsIgnoreCase(documentID)).max((t, s) -> (int) (t.getValue() - s.getValue())).get().getValue();
    }

    public static void main(String[] args) {
        SearchEngine searchEngine = new SearchEngineImplementation();
        List<Document> documents = new ArrayList<>();
        documents.add(new Document("Document 1", "the brown fox jumped over the brown dog"));
        documents.add(new Document("Document 2", "the lazy brown dog sat in the corner"));
        documents.add(new Document("Document 3", "the red fox bit the lazy dog"));

        ((SearchEngineImplementation) searchEngine).indexAllDocuments(documents);
        System.out.println(((SearchEngineImplementation) searchEngine).indexInstance.getAllEntriesInIndex());
        System.out.println("index: " + ((SearchEngineImplementation) searchEngine).indexInstance);
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