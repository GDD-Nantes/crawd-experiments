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
You can install all the required Python libraries by running the following command:

``` pip install -r requirements.txt```

- Recommended:
    - Python version: >= 3.7
    - JDK version:  21
    - Jena version: 4.9.0
### Installation

1. The instruction for downloading the datasets can be found from the following links:
- [WatDiv10M](https://github.com/dsg-uwaterloo/watdiv)
- [DBpedia](https://github.com/dice-group/LargeRDFBench)
- [WDBench](https://github.com/MillenniumDB/WDBench)

2. Load the datasets into TDB2 Apache Jena:
You can find the instructions on how to load the datasets into TDB2 [here](https://jena.apache.org/documentation/tdb2/tdb2_cmds.html).

3. Download the CRAWD code from the anonymous repository
4. Unzip the downloaded file and navigate to the folder containing the code
5. Install to the local maven repository by running the following command:

```mvn clean install -Dmaven.test.skip=true```


### Note:
1. All ground truth files of 3 datasets for groupby queries are located in the `res` folder.
2. CRAWD algorithm is implemented by JAVA in the `src` folder. While the state-of-the-art algorithms are implemented by Python.
Therefore, the experiments are conducted by 2 main processes: 
    - Getting the sample and running the CRAWD algorithm in JAVA into the `sample` folder in each dataset directory.
    - Reading CRAWD's estimated count-distinct values and running other algorithms in Python in `main.py` file in `python_scripts` folder.

### Running the code

1. For getting the sample and running the CRAWD algorithm in SPO queries, you can run the SPOTest.java file in the `src` folder.
2. For getting the sample and running the CRAWD algorithm in groupby queries, you can run the TwoTriplePatternsTest.java file in the `src` folder.
3. For evaluating CRAWD and other algorithms, you can check out the snakemake file `evaluate.smk`.

