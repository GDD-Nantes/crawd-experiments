# Experiment for Count-Distinct with WDBench

- The name of each folder explains itself.
- To run the experiment for both estimators CRAWD and Chao-Lee, you can run the snakemake file in `snakemake_files`.
  For example, with CRAWD estimator, you can run the following command in the `sage-jena` folder:
```bash
snakemake -F -p -s ./count-distinct-wdbench/snakemake_files/crawd_config.smk -c1
```

- The final results,aggregated analysis for each estimator are saved in `results` folder.
- The ground truth for each query is saved in `GT` folder.