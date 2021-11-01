package searchEngine;

import com.findwise.IndexEntry;
import com.findwise.SearchEngine;
import model.Document;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class SearchEngineImplementation implements SearchEngine {

    public Map<String, Map<String, Long>> index = new HashMap<>();

    double numberOfDocumentsWithGivenTerm;


    @Override
    public void indexDocument(String id, String content) {
        Map<String, Long> wordAndNumberOfOccurrences = creatingInvertedIndex(content);
        System.out.println("word and document: "+wordAndNumberOfOccurrences);
        for (Map.Entry<String, Long> entry : wordAndNumberOfOccurrences.entrySet()) {
            System.out.println("key: "+entry.getKey());
            index.merge(entry.getKey(), new HashMap<String, Long>() {{
                put(id, entry.getValue());
            }}, (t, s) -> {
                t.put(id, entry.getValue());
                return t;
            });
        }
    }

    @Override
    public List<IndexEntry> search(String term) {
        return null;
    }

    private Map<String, Long> creatingInvertedIndex(String content) {
        return Arrays.stream(content.split(" ")).collect(Collectors.groupingBy((t -> t), Collectors.counting()));
    }

    /* private double calculateScoreTFIDF() {
         return calculateTF() * calculateIDF();
     }

     private double calculateTF() {
         return 0.5 + 0.5 * (rawFrequencyOfTerm / rawFrequencyOfOfMostOccurringTerm);
     }

     private double calculateIDF() {
         return Math.log10(numberOfAllDocuments / numberOfDocumentsWithGivenTerm);
     }*/
    public static void main(String[] args) {
        SearchEngine searchEngine = new SearchEngineImplementation();
        Map<String, String> documents = new HashMap<>();
        documents.put("Document 1", "the brown fox jumped over the brown dog");
        documents.put("Document 2", "the lazy brown dog sat in the corner");
        documents.put("Document 3", "the red fox bit the lazy dog");

        for (Map.Entry<String, String> entry : documents.entrySet()
        ) {
            searchEngine.indexDocument(entry.getKey(), entry.getValue());
        }
        System.out.println("index: "+((SearchEngineImplementation) searchEngine).index);
    }
}