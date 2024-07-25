# CRAWD: Sampling-Based Estimation of Count-Distinct SPARQL Queries
This repository contains the code and data for the paper "CRAWD: Sampling-Based Estimation of Count-Distinct SPARQL Queries" 

## Abstract
Count-distinct SPARQL queries compute the number of unique values in the results of a query executed on a Knowledge Graph. 
However, counting the exact number of distinct values is often computationally demanding and time-consuming. 
As a result, these queries often fail on public SPARQL endpoints due to fair use policies. 
In this paper, we propose CRAWD, a new sampling-based approach designed to approximate count-distinct SPARQL queries. 
CRAWD significantly improves sampling efficiency and allows feasible execution of count-distinct SPARQL queries on public SPARQL endpoints, considerably improving existing methods.

* [WatDiv10M Count Distinct Queries](/count-distinct-watdiv/queries/top5_cd_original)
* [WatDiv10M CD Void Queries](count-distinct-watdiv/queries/VOID)
* [WDBench Count Distinct Queries](/count-distinct-wdbench/top30q)
* [WDBench CD Void Queries](/count-distinct-wdbench/void_queries)

## Methodology for benchmarking
As there is no dedicated benchmark for count-distinct aggregate queries, we used
the synthetic benchmark  WatDiv10M and the real-world benchmark WDBench to build two workloads.
The WatDiv10M workload consists of 60  queries, and  WDBench workload consists of 43 queries.
WatDiv10M queries range from 1 to 12 triple patterns, while  WDBench queries have 1 to 5 triple patterns.

### For [Watdiv](https://github.com/dsg-uwaterloo/watdiv)
From the original 12400 queries, we selected queries as follows:

- We transformed WatDiv's original queries into count aggregate queries.
- We pruned queries that time out after 30 seconds.
- We grouped queries per number of triple patterns and kept only the top 5 queries regarding the number of results, as these are the most challenging for count-distinct.
- We transformed these queries into count-distinct aggregate queries. Using query variables, we rewrote the queries into several count-distinct aggregate queries and computed the number of distinct results per variable.
- For each group of queries, we selected the top 5 queries in terms of distinct results.

### For [WDBench](https://github.com/MillenniumDB/WDBench)
From the 681 queries with multiple BPGs, we selected 30
queries as follows:
-  Since WDBench queries could have a cartesian product,  we pruned these queries, resulting in 260 queries.
-  We transformed the 260 queries into count aggregate queries. We selected the top 30 queries regarding the number of results.
   As for WatDiv, we transformed queries into count-distinct aggregate queries and selected those with the highest number of distinct results.


For both workloads, we added single triple pattern queries from
SPORTAL: QB3 (count-distinct predicates), QB4
(count-distinct subjects),  and QB5 (count-distinct objects), which are the most basic VoID (https://www.w3.org/TR/void/).  From SPORTAL, we
also added 10 queries that count distinct objects per class for the
top 10 classes with the highest number of distinct objects per class.



## Repository Structure

You can find detail for each subdirectory in their README. The repository is structured as follows:
- [count-distinct-watdiv](./count-distinct-watdiv): instruction, queries, results for the experiments on the WatDiv benchmark.
- [count-distinct-wdbench](./count-distinct-wdbench): instruction, queries, results for the experiments on the WDBench benchmark.
- [sage-jena/rawer](./sage-jena/rawer) : the implementation of the CRAWD algorithm in blazegraph.
- [plots_paper](./plots_paper): the plots used in the paper and more plots for the experiments.
- [cd_bad_queries](./cd_bad_queries_wdbench): details of the queries that have problem on JOIN's order in WDBench.
- [cd_motivating_example](./cd_motivating_examples): results for the motivating example in the introduction of the paper.
- [frequencies_of_frequency](./frequencies_of_frequencies): the code and data for the experiments on the frequencies of frequency of single triple patterns.
- [data_analysis](./data_analysis.ipynb): the notebook for the data analysis and plotting of the results.

### Requirements
You can install all the required Python libraries by running the following command:

``` pip install -r requirements.txt```

- Recommended:
  - Python version: >= 3.7
  - JDK version:  21
  - Jena version: 4.9.0
  - Maven version: 3.8.1
  - Blazegraph version: 2.1.5

### Steps to reproduce the experiments
1. Clone the repository
2. Install the requirements
3.
```sh
cd sage-jena

# Install it in your local maven repository
mvn clean install -Dmaven.test.skip=true
```
4. Now, you can continue with README.md in [count-distinct-watdiv](./count-distinct-watdiv/README.md) and [count-distinct-wdbench](./count-distinct-wdbench/README.md) directories.

