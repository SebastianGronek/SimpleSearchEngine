package index;

import errors.InvalidInputException;
import model.Document;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DocumentManagerTest {
    private static List<Document> documents;
    private static DocumentManager documentManager;

    @BeforeAll
    private static void createDocumentManager() {
        documents = new ArrayList<>();
        documents.add(new Document("Document 1", "the brown fox"));
        documents.add(new Document("Document 2", "The lazy the"));
        documents.add(new Document("Document 3", "the red"));
        documentManager = DocumentManager.DocumentManagerFactory(documents);
    }

    @Test
    void shouldCreateADocumentManager() {
        //given

        //when

        //then
        Map<String, Long> expectedLengthsOfAllDocuments = new HashMap<>();
        expectedLengthsOfAllDocuments.put("Document 1", 3L);
        expectedLengthsOfAllDocuments.put("Document 2", 3L);
        expectedLengthsOfAllDocuments.put("Document 3", 2L);

        Map<String, Map<String, Long>> expectedInvertedIndex = new HashMap<>();
        Map<String, Long> mapOfThe = new HashMap<>();
        mapOfThe.put("Document 1", 1L);
        mapOfThe.put("Document 2", 2L);
        mapOfThe.put("Document 3", 1L);

        Map<String, Long> mapOfBrown = new HashMap<>();
        mapOfBrown.put("Document 1", 1L);

        Map<String, Long> mapOfFox = new HashMap<>();
        mapOfFox.put("Document 1", 1L);


        Map<String, Long> mapOfLazy = new HashMap<>();
        mapOfLazy.put("Document 2", 1L);


        Map<String, Long> mapOfRed = new HashMap<>();
        mapOfRed.put("Document 3", 1L);

        expectedInvertedIndex.put("the", mapOfThe);
        expectedInvertedIndex.put("brown", mapOfBrown);
        expectedInvertedIndex.put("fox", mapOfFox);
        expectedInvertedIndex.put("lazy", mapOfLazy);
        expectedInvertedIndex.put("red", mapOfRed);

        List<Map.Entry<String, Long>> expectedListOfAllEntries = new ArrayList<>();
        expectedListOfAllEntries.addAll(mapOfThe.entrySet());
        expectedListOfAllEntries.addAll(mapOfBrown.entrySet());
        expectedListOfAllEntries.addAll(mapOfFox.entrySet());
        expectedListOfAllEntries.addAll(mapOfLazy.entrySet());
        expectedListOfAllEntries.addAll(mapOfRed.entrySet());

        assertThat(documentManager)
                .hasFieldOrPropertyWithValue("lengthsOfAllDocuments", expectedLengthsOfAllDocuments)
                .hasFieldOrPropertyWithValue("invertedIndex", expectedInvertedIndex);
        assertThat(documentManager.getAllEntriesInIndex()).containsExactlyInAnyOrderElementsOf(expectedListOfAllEntries);
    }

    @Test
    void shouldDocumentManagerFactoryFailWhenListOfDocumentsIsNull() {
        //given
        List<Document> documents = null;
        //when

        //then
        assertThatThrownBy(() -> DocumentManager.DocumentManagerFactory(documents)).isInstanceOf(NullPointerException.class).hasMessage("Provided map of documents is null");
    }

    @Test
    void shouldDocumentManagerFactoryFailWhenListOfDocumentsIsEmpty() {
        //given
        List<Document> documents = new ArrayList<>();
        //when

        //then
        assertThatThrownBy(() -> DocumentManager.DocumentManagerFactory(documents)).isInstanceOf(InvalidInputException.class).hasMessage("Provided map of documents is empty");
    }
}