package entryPoint;

import com.findwise.IndexEntry;
import model.Document;
import searchEngine.SearchEngineImplementation;

import java.util.List;

public class EntryPoint {
    public static List<IndexEntry> indexDocumentsAndSearchForTerm(List<Document> documents, String term) {
        SearchEngineImplementation searchEngineInStaticMethod = new SearchEngineImplementation(documents);
        searchEngineInStaticMethod.addNewDocumentsToIndex(documents);
        return searchEngineInStaticMethod.search(term);
    }

    public static void main(String[] args) {
    }
}
