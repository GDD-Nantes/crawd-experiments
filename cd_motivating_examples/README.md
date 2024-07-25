# Motivating examples

_How many people live in Europe? How many books have been written by Europeans? How many women head cities in Europe? How many distinct objects are in a knowledge graph?_

These queries are expressed as SPARQL queries in the [queries
folder](./queries). For instance, the first query about european people is:

```sparql
PREFIX wd: <http://www.wikidata.org/entity/>
PREFIX wdt: <http://www.wikidata.org/prop/direct/>

SELECT(COUNT(DISTINCT ?person) AS ?cd_persons )
WHERE {
  ?person wdt:P31 wd:Q5 .
  ?person wdt:P19 ?city .
  ?city wdt:P17 ?country .
  ?country wdt:P30 wd:Q46 .
}
```

Such queries time out on the public [SPARQL endpoints of
Wikidata](https://query.wikidata.org/) that comprises more than 16B
triples. However, executing them locally on WDBench that only
comprises 1.2B triples, they may not time out anymore.

To execute a query using Blazegraph without running a server, we use the following:
```java -jar embedded-blazegraph-jar-with-dependencies.jar --database=wdbench-blaze.jnl --file=./queries/books.sparql```

Blazegraph using 16 threads evaluates these queries:
- `books.sparql`: 841ms
- `women.sparql`: 27.7s
- `people.sparql`: 39.3s

They do not timeout but are already quite challenging. Therefore we
constrain Blazegraph to use only one 1 process using `docker run --cpuset-cpus="0" -m=55g`.
- `books.sparql`: 3s
- `women.sparql`: 105s
- `people.sparql`: 170s

Therefore, with a timeout threshold set to 60s, the end-user would
have had no result for her queries. Using approximate query
processing, we are guaranteed to get an approximate result before
60s. Using CRAWD, the approximate results of these queries are
available in the [results folder](./results/). With the configuration
involving #tp x 100 scans for each sampled result, and a single
thread, CRAWD provides at 60s:
- `books.sparql`: estimated 3509 vs 3542 expected
- `women.sparql`: estimated 2849 vs 2877 expected
- `people.sparql`: estimated 1796949 vs 1804559 expected

While these estimate are already good, a end-user could decide to
further refine them by asking for additional rounds of sampling. 
