package searchEngine;

public class TFIDFCalculator {
    public static double calculateScoreTFIDF(long rawFrequencyOfTerm, long rawFrequencyOfOfMostOccurringTerm, long numberOfAllDocuments, long numberOfDocumentsWithGivenTerm) {
        System.out.println(calculateTF(rawFrequencyOfTerm, rawFrequencyOfOfMostOccurringTerm));
        System.out.println(calculateIDF(numberOfAllDocuments, numberOfDocumentsWithGivenTerm));
        return calculateTF(rawFrequencyOfTerm, rawFrequencyOfOfMostOccurringTerm) * calculateIDF(numberOfAllDocuments, numberOfDocumentsWithGivenTerm);
    }

    private static double calculateTF(long rawFrequencyOfTerm, long rawFrequencyOfOfMostOccurringTerm) {
        return 0.5 + 0.5 * ((double) rawFrequencyOfTerm / rawFrequencyOfOfMostOccurringTerm);
    }

    private static double calculateIDF(long numberOfAllDocuments, long numberOfDocumentsWithGivenTerm) {
        return Math.log10((double) numberOfAllDocuments / numberOfDocumentsWithGivenTerm);
    }
}
