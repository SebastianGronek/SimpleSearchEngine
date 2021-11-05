package searchEngine;

import com.findwise.IndexEntry;
import com.findwise.SearchEngine;
import errors.TermNotFoundException;
import index.IndexEntryImplementation;
import model.Document;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SearchEngineImplementationTest {
    private static SearchEngine searchEngine;
    private List<Document> documents;

    @BeforeAll
    private static void instantiatingSearchEngine() {
        searchEngine = new SearchEngineImplementation();
    }

    @BeforeEach
    private void instantiateDocumentList() {
        documents = new ArrayList<>();
    }

    @Test
    void shouldIndexDocumentAndSearch() {
        //given
        documents.add(new Document("Document 1", "the brown fox jumped over the brown dog"));
        documents.add(new Document("Document 2", "the lazy brown dog sat in the corner"));
        documents.add(new Document("Document 3", "the red fox bit the lazy dog"));
        //when
        List<IndexEntry> result = SearchEngineImplementation.indexDocumentsAndSearchForTerm(documents, "fox");
        //then
        List<IndexEntry> expected = new ArrayList<>();
        expected.add(new IndexEntryImplementation("Document 3", TFIDFCalculator.calculateScoreTFIDF(1, 2, 3, 2)));
        expected.add(new IndexEntryImplementation("Document 1", TFIDFCalculator.calculateScoreTFIDF(1, 2, 3, 2)));

        assertThat(result).containsExactlyElementsOf(expected);
    }

    @Test
    void shouldSearchExampleFromTaskDescription() {
        //given
        documents.add(new Document("Document 1", "the brown fox jumped over the brown dog"));
        documents.add(new Document("Document 2", "the lazy brown dog sat in the corner"));
        documents.add(new Document("Document 3", "the red fox bit the lazy dog"));
        ((SearchEngineImplementation) searchEngine).indexAllDocuments(documents);
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
        documents.add(new Document("Document 1", "the brown, fox jumped> over< the  ^brown % dog @"));
        documents.add(new Document("Document 2", "the lazy. .brown , dog sat       in  - -  --- the }corner"));
        documents.add(new Document("Document 3", "the red fox bit,, the lazy: ; \" dog"));
        ((SearchEngineImplementation) searchEngine).indexAllDocuments(documents);
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
        ((SearchEngineImplementation) searchEngine).indexAllDocuments(documents);
        //when
        //then
        assertThatThrownBy(() -> searchEngine.search("brown")).isInstanceOf(TermNotFoundException.class).hasMessage("Sorry, no document with searched term was found");
    }

    @Test
    void shouldSearchExampleForDocumentsWithNoContentFail() {
        //given
        documents.add(new Document("Document 1", ""));
        documents.add(new Document("Document 2", ""));
        documents.add(new Document("Document 3", ""));
        ((SearchEngineImplementation) searchEngine).indexAllDocuments(documents);
        //when
        //then
        assertThatThrownBy(() -> searchEngine.search("brown")).isInstanceOf(TermNotFoundException.class).hasMessage("Sorry, no document with searched term was found");
    }
}