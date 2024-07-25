print(config)

# Define datasets and queries
datasets = config.get("datasets")
if datasets is None:
    datasets = ["watdiv", "largerdf_dbpedia", "wdbench"]
else:
    datasets = datasets.strip().split(",")

queries = config.get("queries")
if queries is None:
    queries = ["SPO_CDs", "SPO_CDp", "SPO_CDo"]
else:
    queries = queries.strip().split(",")

runs = config.get("runs")
if runs is None:
    runs = [1, 2, 3, 4, 5]
else:
    runs = runs

estimators = config.get("estimators")
if estimators is None:
    estimators = ["horvitz_thompson","smoothed_jackknife","ndv","chao_lee","chao_lee_N_j","crawd"]
else:
    estimators = estimators.strip().split(",")

# Define input and output file paths for each dataset and query
rule all:
    input:
        expand("{dataset}/figures/{query}.png",
               dataset=datasets,
               query=queries
        )

rule plot_data:
    input:
        "{dataset}/final_csv/{query}.csv"
    output:
        "{dataset}/figures/{query}.png"
    run:
        shell(f"""
        python python_scripts/main.py plot-data {input} {output}
        """)

rule merge_data:
    input:
        expand("{{dataset}}/compared_errors/{estimator}/Run_{attempt}/{{query}}.json",
            estimator=estimators,
            attempt=runs
        )
    output:
        "{dataset}/final_csv/{query}.csv"
    run:
        ground_truth=f"{wildcards.dataset}/GT/{wildcards.query}_formatted.csv"
        shell(f"""
        python python_scripts/main.py merge-data {input} {output}  \
            --dataset={wildcards.dataset} \
            --ground-truth={ground_truth} \
            --query={wildcards.query}

        """)

# Rule to run_estimators.py
rule run_estimators:
    input:
        input_file="{dataset}/sample/Run_{attempt}/{query}.csv",
        ground_truth="{dataset}/GT/{query}_formatted.csv"
    output:
        compared_results="{dataset}/compared_results/{estimator}/Run_{attempt}/{query}.json",
        compared_errors="{dataset}/compared_errors/{estimator}/Run_{attempt}/{query}.json"
    shell:
        """
        python python_scripts/main.py run-estimators \
            --input-file {input.input_file} \
            --ground-truth {input.ground_truth} \
            --compared-results {output.compared_results} \
            --compared-errors {output.compared_errors} \
            --estimator {wildcards.estimator} \
            --dataset {wildcards.dataset}
        """


