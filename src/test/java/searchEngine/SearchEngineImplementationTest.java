package searchEngine;

import com.findwise.IndexEntry;
import com.findwise.SearchEngine;
import entryPoint.EntryPoint;
import errors.TermNotFoundException;
import index.DocumentManager;
import index.IndexEntryImplementation;
import model.Document;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SearchEngineImplementationTest {
    private SearchEngine searchEngine;
    private static List<Document> documents;

    @BeforeAll
    private static void instantiateDocumentList() {
        documents = new ArrayList<>();
        documents.add(new Document("Document 1", "the brown fox jumped over the brown dog"));
        documents.add(new Document("Document 2", "the lazy brown dog sat in the corner"));
        documents.add(new Document("Document 3", "the red fox bit the lazy dog"));
    }

    @BeforeEach
    private void instantiatingSearchEngine() {
        searchEngine = new SearchEngineImplementation(documents);
    }

    @Test
    void shouldIndexDocumentAndSearch() {
        //given

        //when
        List<IndexEntry> result = EntryPoint.indexDocumentsAndSearchForTerm(documents, "fox");
        //then
        List<IndexEntry> expected = new ArrayList<>();
        expected.add(new IndexEntryImplementation("Document 3", TFIDFCalculator.calculateScoreTFIDF(1, 2, 3, 2)));
        expected.add(new IndexEntryImplementation("Document 1", TFIDFCalculator.calculateScoreTFIDF(1, 2, 3, 2)));

        assertThat(result).containsExactlyElementsOf(expected);
    }

    @Test
    void shouldSearchExampleFromTaskDescription() {
        //given
        ((SearchEngineImplementation) searchEngine).addNewDocumentsToIndex(documents);
        //when
        List<IndexEntry> result = searchEngine.search("brown");
        //then
        List<IndexEntry> expected = new ArrayList<>();
        expected.add(new IndexEntryImplementation("Document 1", TFIDFCalculator.calculateScoreTFIDF(2, 2, 3, 2)));
        expected.add(new IndexEntryImplementation("Document 2", TFIDFCalculator.calculateScoreTFIDF(1, 2, 3, 2)));

        assertThat(result).containsExactlyElementsOf(expected);
    }

    @Test
    void shouldSearchExampleFromTaskDescriptionWithPunctuation() {
        //given
        documents = new ArrayList<>();
        documents.add(new Document("Document 1", "the brown, fox jumped> over< the  ^brown % dog @"));
        documents.add(new Document("Document 2", "the lazy. .brown , dog sat       in  - -  --- the }corner"));
        documents.add(new Document("Document 3", "the red fox bit,, the lazy: ; \" dog"));
        ((SearchEngineImplementation) searchEngine).addNewDocumentsToIndex(documents);
        //when
        List<IndexEntry> result = searchEngine.search("brown");
        //then
        List<IndexEntry> expected = new ArrayList<>();
        expected.add(new IndexEntryImplementation("Document 1", TFIDFCalculator.calculateScoreTFIDF(2, 2, 3, 2)));
        expected.add(new IndexEntryImplementation("Document 2", TFIDFCalculator.calculateScoreTFIDF(1, 2, 3, 2)));

        assertThat(result).containsExactlyElementsOf(expected);
    }

    @Test
    void shouldSearchExampleForEmptyListFail() {
        //given
        List<Document> documents = new ArrayList<>();
        ((SearchEngineImplementation) searchEngine).addNewDocumentsToIndex(documents);
        //when

        //then
        assertThatThrownBy(() -> searchEngine.search("brown")).isInstanceOf(TermNotFoundException.class).hasMessage("Sorry, no document with searched term was found");
    }

    @Test
    void shouldSearchMethodFailIfTermIsNull() {
        //given

        //when

        //then
        assertThatThrownBy(() -> searchEngine.search(null)).isInstanceOf(NullPointerException.class).hasMessage("Term provided for indexDocumentsAndSearchForTerm method equals null");
    }
}