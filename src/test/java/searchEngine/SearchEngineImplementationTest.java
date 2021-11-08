package searchEngine;

import com.findwise.IndexEntry;
import com.findwise.SearchEngine;
import errors.InvalidInputException;
import index.IndexEntryImplementation;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SearchEngineImplementationTest {
    private SearchEngine searchEngine;
    private static Map<String, String> documentsNamesAndContents;

    @BeforeAll
    private static void instantiateDocumentMap() {
        documentsNamesAndContents = new HashMap<>();
        documentsNamesAndContents.put("Document 1", "the brown fox jumped over the brown dog");
        documentsNamesAndContents.put("Document 2", "the lazy brown dog sat in the corner");
        documentsNamesAndContents.put("Document 3", "the red fox bit the lazy dog");
    }

    @BeforeEach
    private void instantiatingSearchEngine() {
        searchEngine = new SearchEngineImplementation(documentsNamesAndContents);
    }

    @Test
    void shouldIndexDocumentAndSearch() {
        //given

        //when
        List<IndexEntry> result = searchEngine.search("fox");
        //then
        List<IndexEntry> expected = new ArrayList<>();
        expected.add(new IndexEntryImplementation("Document 3", TFIDFCalculator.calculateScoreTFIDF(1, 2, 3, 2)));
        expected.add(new IndexEntryImplementation("Document 1", TFIDFCalculator.calculateScoreTFIDF(1, 2, 3, 2)));

        assertThat(result).containsExactlyElementsOf(expected);
    }

    @Test
    void shouldSearchExampleFromTaskDescription() {
        //given

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
        documentsNamesAndContents = new HashMap<>();
        documentsNamesAndContents.put("Document 1", "the brown, fox jumped> over< the  ^brown % dog @");
        documentsNamesAndContents.put("Document 2", "the lazy. .brown , dog sat       in  - -  --- the }corner");
        documentsNamesAndContents.put("Document 3", "the red fox bit,, the lazy: ; \" dog");
        searchEngine = new SearchEngineImplementation(documentsNamesAndContents);
        //when
        List<IndexEntry> result = searchEngine.search("brown");
        //then
        List<IndexEntry> expected = new ArrayList<>();
        expected.add(new IndexEntryImplementation("Document 1", TFIDFCalculator.calculateScoreTFIDF(2, 2, 3, 2)));
        expected.add(new IndexEntryImplementation("Document 2", TFIDFCalculator.calculateScoreTFIDF(1, 2, 3, 2)));

        assertThat(result).containsExactlyElementsOf(expected);
    }

    @Test
    void shouldSearchExampleForNullListFail() {
        //given
        Map<String, String> documents = null;

        //when

        //then
        assertThatThrownBy(() -> searchEngine = new SearchEngineImplementation(documents)).isInstanceOf(NullPointerException.class).hasMessage("Provided map of documents is null");
    }

    @Test
    void shouldSearchExampleForEmptyListFail() {
        //given
        Map<String, String> documents = new HashMap<>();

        //when

        //then
        assertThatThrownBy(() -> searchEngine = new SearchEngineImplementation(documents)).isInstanceOf(InvalidInputException.class).hasMessage("Provided map of documents is empty");
    }


    @Test
    void shouldSearchMethodFailIfTermIsNull() {
        //given

        //when

        //then
        assertThatThrownBy(() -> searchEngine.search(null)).isInstanceOf(NullPointerException.class).hasMessage("Term provided for indexDocumentsAndSearchForTerm method equals null");
    }
}