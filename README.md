# Simple Search Engine

Simple search engine created as a recruitment task. 
The search engine supports searches for single terms in the document set and returns a list of IndexEntry objects. IndexEntry object contains name of document with desired term and score, calculated as TF-IDF (Term Frequency - Inverse Document Frequency). 

## Table of contents
* [Technologies](#technologies)
* [Setup](#setup)
* [TFIDF algorithm](#TFIDF-algorithm)
* [Sorting of result list](#Sorting-of-result-list)
* [Example](#Example)

### Technologies
* Java SE 11
* JUnit
* AssertJ

### Setup
Download code to your IDE and run main method. To use search engine, use static method 
```
indexDocumentsAndSearchForTerm(List<Document> documents, String term);
```
where *documents* is list of texts to be indexed, which are wrapped in simple container Class (with Strings name and content).
### TFIDF algorithm
This search engine uses TF-IDF calculation scheme with augmented frequency (double normalization 0.5), which could be found on Wikipedia.

TF: (0.5 + 0.5(f/g))

IDF: log(N/n)

TF-IDF: TF * IDF

**where**:

f - raw frequency of term

g - raw frequency of most occurring term in text

N - number of all documents

n- number of documents containing searched term

### Sorting of result list
List of IndexEntry objects is sorted in descending order by score. If two IndexEntries have the same score, the one referring shorter document comes first.
### Example
Creating list of documents to be indexed:
```aidl
List<Document> documents = new ArrayList<>();
        documents.add(new Document("Document 1", "the brown fox jumped over    the brown dog"));
        documents.add(new Document("Document 2", "the lazy brown dog sat, in the corner"));
        documents.add(new Document("Document 3", "the red fox bit the ... lazy dog"));
```
Searching for term "brown":
```aidl
indexDocumentsAndSearchForTerm(documents, "brown");
```
Result:
```aidl
[IndexEntryImplementation{id='Document 1', score=0.17609125905568124}, 
IndexEntryImplementation{id='Document 2', score=0.13206844429176093}]
```
