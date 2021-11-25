package searchEngine;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;

class TFIDFCalculatorTest {

    @ParameterizedTest
    @CsvSource({"1, 10, 9,1, 0.55","1, 5, 4, 4, 0.1806179973983887", "2,2,7,5, 0.38021124171160603", "2, 11, 24, 6, 0.41302772983492025"})
    void shouldCalculateProperTFIDF(long rawFrequencyOfTerm, long rawFrequencyOfOfMostOccurringTerm, long numberOfAllDocuments, long numberOfDocumentsWithGivenTerm, double expectedResult) {
        //given

        //when
        double result = TFIDFCalculator.calculateScoreTFIDF(rawFrequencyOfTerm, rawFrequencyOfOfMostOccurringTerm, numberOfAllDocuments, numberOfDocumentsWithGivenTerm);
        //then
        assertThat(result).isEqualTo(expectedResult);
    }

}