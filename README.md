# CRAWD: Sampling-Based Estimation of Count-Distinct SPARQL Queries
This repository contains the code and data for the paper "CRAWD: Sampling-Based Estimation of Count-Distinct SPARQL Queries" 

## Abstract
Count-distinct SPARQL queries compute the number of unique values in the results of a query executed on a Knowledge Graph. 
However, counting the exact number of distinct values is often computationally demanding and time-consuming. 
As a result, these queries often fail on public SPARQL endpoints due to fair use policies. 
In this paper, we propose CRAWD, a new sampling-based approach designed to approximate count-distinct SPARQL queries.
CRAWD significantly improves sampling efficiency and allows feasible execution of count-distinct SPARQL queries on public SPARQL endpoints, 
considerably improving existing methods.

## User Guide
### Installation

1. Download the datasets from the following links:
- [WatDiv10M](https://github.com/dsg-uwaterloo/watdiv)
- [DBpedia](https://github.com/dice-group/LargeRDFBench)
- [WDBench](https://github.com/MillenniumDB/WDBench)

2. Load the datasets into TDB2 Apache Jena:
You can find the instructions on how to load the datasets into TDB2 [here](https://jena.apache.org/documentation/tdb2/tdb2_cmds.html).
3. Install Sage-Jena:
You can find the instructions on how to install Sage-Jena [here](https://github.com/Chat-Wane/sage-jena).



