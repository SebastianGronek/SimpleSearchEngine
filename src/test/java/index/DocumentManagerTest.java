package index;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class DocumentManagerTest {
    private static DocumentManager documentManager;

    @BeforeAll
    private static void createDocumentManager() {
        documentManager = new DocumentManager();
    }

    @Test
    void shouldSplitDocumentIntoWordsAndCountOccurrences() {
//given
        String content = "Winter is coming, Ours is the fury!";
//when
        Map<String, Long> result = documentManager.splitDocumentIntoWordsAndCountOccurrences(content);
//then
        Map<String, Long> expected = new HashMap<>();
        expected.put("Winter", 1L);
        expected.put("is", 2L);
        expected.put("coming", 1L);
        expected.put("Ours", 1L);
        expected.put("the", 1L);
        expected.put("fury", 1L);
        assertThat(result).isEqualTo(expected);
    }
}