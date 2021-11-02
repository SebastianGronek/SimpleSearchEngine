package searchEngine;

import com.findwise.IndexEntry;
import com.findwise.SearchEngine;
import errors.TermNotFoundException;
import index.IndexEntryImplementation;
import model.Document;

import java.util.*;
import java.util.stream.Collectors;

public class SearchEngineImplementation implements SearchEngine {

    public Map<String, Map<String, Long>> index = new HashMap<>();

    long numberOfAllDocuments;

    @Override
    public void indexDocument(String id, String content) {
        Map<String, Long> wordAndNumberOfOccurrences = creatingInvertedIndex(content);
        for (Map.Entry<String, Long> entry : wordAndNumberOfOccurrences.entrySet()) {
            index.merge(entry.getKey(), new HashMap<String, Long>() {{
                put(id, entry.getValue());
            }}, (t, s) -> {
                t.put(id, entry.getValue());
                return t;
            });
        }
    }

    public void indexAllDocuments(List<Document> listOfDocuments) {
        index = new HashMap<>();
        numberOfAllDocuments = 0;
        addNewDocumentsToIndex(listOfDocuments);
    }

    public void addNewDocumentsToIndex(List<Document> listOfDocuments) {
        for (Document d : listOfDocuments) {
            indexDocument(d.getName(), d.getContent());
        }
        numberOfAllDocuments = listOfDocuments.size() + numberOfAllDocuments;
    }

    @Override
    public List<IndexEntry> search(String term) {
        Map<String, Long> termSearchResults;
        if (index.containsKey(term)) {
            termSearchResults = index.get(term);
        } else {
            throw new TermNotFoundException("Sorry, no document with searched term was found");
        }
        List<IndexEntry> result = new ArrayList<>();
        Set<Map.Entry<String, Long>> allEntriesInIndex = flatTheStructure();
        Set<Map.Entry<String, Long>> entriesWithQueriedTerm = termSearchResults.entrySet();
        for (Map.Entry<String, Long> e : entriesWithQueriedTerm) {
            Long rawFrequencyOfOfMostOccurringTerm = findRawFrequencyOfOfMostOccurringTerm(allEntriesInIndex, e.getKey());
            System.out.println("Raw frequency of most occurring term: " + rawFrequencyOfOfMostOccurringTerm);
            IndexEntry indexEntry = new IndexEntryImplementation(e.getKey(), calculateScoreTFIDF(e.getValue(), rawFrequencyOfOfMostOccurringTerm, numberOfAllDocuments, entriesWithQueriedTerm.size()));
            result.add(indexEntry);
        }
        result.sort((t1, t2) -> (int) (t1.getScore() - t2.getScore()));
        return result;
    }

  /*  private long calculatingNumberOfOccurrencesOfGivenTermInDocument(String term, Document document) {
        return Arrays.stream(document.getContent().split("(\\b[^\\s]+\\b)")).filter(t -> t.equalsIgnoreCase(term)).count();
    }*/

    private Long findRawFrequencyOfOfMostOccurringTerm(Set<Map.Entry<String, Long>> entrySet, String documentID) {
        return entrySet.stream().filter(e -> e.getKey().equalsIgnoreCase(documentID)).max((t, s) -> (int) (t.getValue() - s.getValue())).get().getValue();
    }

    private Set<Map.Entry<String, Long>> flatTheStructure() {
        Set<Map.Entry<String, Long>> result = new HashSet<>();
        index.values().stream().forEach(t -> result.addAll(t.entrySet()));
        return result;
    }

    private Map<String, Long> creatingInvertedIndex(String content) {
        return Arrays.stream(content.split("(\\W)")).collect(Collectors.groupingBy((t -> t), Collectors.counting()));
    }

    private double calculateScoreTFIDF(long rawFrequencyOfTerm, long rawFrequencyOfOfMostOccurringTerm, long numberOfAllDocuments, long numberOfDocumentsWithGivenTerm) {
        System.out.println(calculateTF(rawFrequencyOfTerm, rawFrequencyOfOfMostOccurringTerm));
        System.out.println(calculateIDF(numberOfAllDocuments, numberOfDocumentsWithGivenTerm));
        return calculateTF(rawFrequencyOfTerm, rawFrequencyOfOfMostOccurringTerm) * calculateIDF(numberOfAllDocuments, numberOfDocumentsWithGivenTerm);
    }

    private double calculateTF(long rawFrequencyOfTerm, long rawFrequencyOfOfMostOccurringTerm) {
        return 0.5 + 0.5 * ((double) rawFrequencyOfTerm / rawFrequencyOfOfMostOccurringTerm);
    }

    private double calculateIDF(long numberOfAllDocuments, long numberOfDocumentsWithGivenTerm) {
        return Math.log10((double) numberOfAllDocuments / numberOfDocumentsWithGivenTerm);
    }

    public static void main(String[] args) {
        SearchEngine searchEngine = new SearchEngineImplementation();
        List<Document> documents = new ArrayList<>();
        documents.add(new Document("Document 1", "the brown fox jumped over the brown dog"));
        documents.add(new Document("Document 2", "the lazy brown dog sat in the corner"));
        documents.add(new Document("Document 3", "the red fox bit the lazy dog"));

        ((SearchEngineImplementation) searchEngine).indexAllDocuments(documents);
        System.out.println("index: " + ((SearchEngineImplementation) searchEngine).index);
        System.out.println("brown: " + searchEngine.search("brown"));
        System.out.println("fox: " + searchEngine.search("fox"));
        System.out.println("the: "+searchEngine.search("the"));
        System.out.println(((SearchEngineImplementation) searchEngine).index.values());
    }
}