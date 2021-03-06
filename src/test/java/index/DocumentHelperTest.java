package index;

import errors.InvalidInputException;
import model.Document;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DocumentHelperTest {
    @Test
    void shouldSplitDocumentIntoWordsAndCountOccurrences() {
//given
        String content = "Winter is coming, Ours is the fury!";
//when
        Map<String, Long> result = DocumentHelper.splitDocumentIntoWordsAndCountOccurrences(content);
//then
        Map<String, Long> expected = new HashMap<>();
        expected.put("winter", 1L);
        expected.put("is", 2L);
        expected.put("coming", 1L);
        expected.put("ours", 1L);
        expected.put("the", 1L);
        expected.put("fury", 1L);
        assertThat(result).isEqualTo(expected);
    }

    @Test
    void shouldThrowExceptionWhenSplittingIntoWordsDocumentWithNoContent() {
        //given

        //when

        //then
        assertThatThrownBy(() -> DocumentHelper.splitContentIntoStreamOfWords(null))
                .isInstanceOf(NullPointerException.class).hasMessage("Content of the document equals null");
        assertThatThrownBy(() -> DocumentHelper.splitContentIntoStreamOfWords(""))
                .isInstanceOf(InvalidInputException.class).hasMessage("The document is empty - no content provided or contains only punctuation");
        assertThatThrownBy(() -> DocumentHelper.splitContentIntoStreamOfWords("     "))
                .isInstanceOf(InvalidInputException.class).hasMessage("The document is empty - no content provided or contains only punctuation");
    }

    @ParameterizedTest
    @CsvSource({"\";][];];.,?/+\"", "][;.,,,..:", "_+-=[]{}::'><,.?/", "_+-=[ ]{}: :'><,. ?/"})
    void shouldThrowExceptionWhenSplittingIntoWordsDocumentWithOnlyPunctuation(String input) {
        //given

        //when

        //then
        assertThatThrownBy(() -> DocumentHelper.splitContentIntoStreamOfWords(input)).isInstanceOf(InvalidInputException.class)
                .hasMessage("The document is empty - no content provided or contains only punctuation");
    }

    @ParameterizedTest
    @CsvSource({"Stark motto, Winter is coming, 3", "Baratheon motto, Ours is the fury, 4", "Java, Java, 1",
            "7327 " + "3231, 124123 21312 , 2"})
    void shouldCalculateLengthOfDocuments(String name, String content, long expected) {
        //given

        // when
        Document document = new Document(name, content);
        //then
        assertThat(document.getLengthOfADocument()).isEqualTo(expected);
    }

}