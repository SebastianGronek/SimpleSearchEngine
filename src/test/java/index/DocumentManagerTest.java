package index;

import model.Document;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;

class DocumentManagerTest {
    private static List<Document> documents;

    @BeforeAll
    private static void createDocumentManager() {
        documents = new ArrayList<>();
        documents.add(new Document("Document 1", "the brown fox jumped over the brown dog"));
        documents.add(new Document("Document 2", "the lazy brown dog sat in the corner"));
        documents.add(new Document("Document 3", "the red fox bit the lazy dog"));
    }

    @Test
    void shouldAddDocumentsToLengthsOfAllDocumentsMap() {
        //given
        DocumentManager documentManager = DocumentManager.DocumentManagerFactory(documents);
        //when
        documentManager.addDocumentsToLengthsOfAllDocumentsMap(documents);
        //then
        Map<String, Long> expected = new HashMap<>();
        expected.put("Document 1", 8L);
        expected.put("Document 2", 8L);
        expected.put("Document 3", 7L);
        assertThat(documentManager.getLengthsOfAllDocuments()).containsExactlyEntriesOf(expected);
    }
}