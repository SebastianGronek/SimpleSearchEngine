# Simple Search Engine

Simple search engine created as a recruitment task. The search engine supports searches for single terms in the document
set and returns a list of IndexEntry objects. IndexEntry object contains name of document with desired term and score,
calculated as TF-IDF (Term Frequency - Inverse Document Frequency). Search engine ignores the difference between upper
and lower case. Provided with empty map or null, search engine throws exception with proper message.

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

Download code to your IDE and run main method in SearchEngineImplementation class. To use search engine, create SimpleSearchEngine using constructor

```
new SearchEngineImplementation(Map<String, String> documentsNameAndContent);
```

where *documentsNamesAndContents* is map of documents' names and contents. Then use *search()* method with desired term
as parameter

```
public List<IndexEntry> search(String term);
```

which returns list of IndexEntry objects.

### TFIDF algorithm

This search engine uses TF-IDF calculation scheme with augmented frequency (double normalization 0.5), which could be
found on Wikipedia. Argument with value of one in IDF calculation was added so that result would never be zero 
(which was the case when the searched term was the most common term in document).

TF: (0.5 + 0.5(f/g))

IDF: log((N/n)+1)

TF-IDF: TF * IDF

**where**:

f - raw frequency of term

g - raw frequency of most occurring term in text

N - number of all documents

n- number of documents containing searched term

### Sorting of result list

List of IndexEntry objects is sorted in descending order by score. If two IndexEntries have the same score, the one
referring shorter document comes first.

### Example

Creating map of documents to be indexed:

```
 documentsNamesAndContents = new HashMap<>();
        documentsNamesAndContents.put("Document 1", "the brown fox jumped over the brown dog");
        documentsNamesAndContents.put("Document 2", "the lazy brown dog sat in the corner");
        documentsNamesAndContents.put("Document 3", "the red fox bit the lazy dog");
```

Creating instance of SimpleSearchEngine class:

```
searchEngine = new SearchEngineImplementation(documentsNameAndContent);
```

Searching for desired term:

```
List<IndexEntry> result = searchEngine.search("brown");
```

Result:

```
[IndexEntryImplementation{id='Document 1', score=0.3979400086720376}, 
IndexEntryImplementation{id='Document 2', score=0.29845500650402823}]
```
