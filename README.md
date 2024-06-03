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
### Requirements
- For SOTA algorithms, 2 main libraries that you need to install are:
1. Pydistinct: 
``` pip install pydistinct```
[https://github.com/chanedwin/pydistinct](https://github.com/chanedwin/pydistinct)
2. Learned NDV estimator
``` pip install estndv```
 [https://github.com/wurenzhi/learned_ndv_estimator](https://github.com/wurenzhi/learned_ndv_estimator)

- Overall, you can install all the required libraries by running the following command:
``` pip install -r requirements.txt```

### Installation

1. Download the datasets from the following links:
- [WatDiv10M](https://github.com/dsg-uwaterloo/watdiv)
- [DBpedia](https://github.com/dice-group/LargeRDFBench)
- [WDBench](https://github.com/MillenniumDB/WDBench)

2. Load the datasets into TDB2 Apache Jena:
You can find the instructions on how to load the datasets into TDB2 [here](https://jena.apache.org/documentation/tdb2/tdb2_cmds.html).

### Running the code

1. For getting the sample and running the CRAWD algorithm in SPO queries, you can run the SPOTest.java file in the `src` folder.
2. For getting the sample and running the CRAWD algorithm in groupby queries, you can run the TwoTriplePatternsTest.java file in the `src` folder.
3. For evaluating CRAWD and other algorithms, you can check out the snakemake file `evaluate.smk`.


### Note:
1. All ground truth files of 3 datasets for groupby queries are located in the `res` folder.

