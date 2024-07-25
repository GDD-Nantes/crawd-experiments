# CRAWD: Sampling-Based Estimation of Count-Distinct SPARQL Queries
This repository contains the code and data for the paper "CRAWD: Sampling-Based Estimation of Count-Distinct SPARQL Queries" 

## Abstract
Count-distinct SPARQL queries compute the number of unique values in the results of a query executed on a Knowledge Graph. 
However, counting the exact number of distinct values is often computationally demanding and time-consuming. 
As a result, these queries often fail on public SPARQL endpoints due to fair use policies. 
In this paper, we propose CRAWD, a new sampling-based approach designed to approximate count-distinct SPARQL queries. 
CRAWD significantly im-proves sampling efficiency and allows feasible execution of count-distinct SPARQL queries on public SPARQL endpoints, considerably improving existing methods.


## Repository Structure

You can find detail for each subdirectory in their README. The repository is structured as follows:
- count-distinct-watdiv: instruction, queries, results for the experiments on the WatDiv benchmark.
- count-distinct-wdbench: instruction, queries, results for the experiments on the WDBench benchmark.
- sage-jena/rawer : the implementation of the CRAWD algorithm in blazegraph.
- plots_paper: the plots used in the paper and more plots for the experiments.
- cd_bad_queries: details of the queries that have problem on JOIN's order.
- cd_motivating_example: results for the motivating example in the introduction of the paper.
- frequencies_of_frequency: the code and data for the experiments on the frequencies of frequency of single triple patterns.



### Requirements
You can install all the required Python libraries by running the following command:

``` pip install -r requirements.txt```

- Recommended:
    - Python version: >= 3.7
    - JDK version:  21
    - Jena version: 4.9.0
    - Maven version: 3.8.1
    - Blazegraph version: 2.1.5

