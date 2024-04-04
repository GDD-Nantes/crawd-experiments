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
    runs = runs.strip().split(",")

estimators = config.get("estimators")
if estimators is None:
    estimators = ["chao_lee","horvitz_thompson","smoothed_jackknife","ndv","method_of_moments_v3"]
else:
    estimators = estimators.strip().split(",")

# Define input and output file paths for each dataset and query
rule all:
    input:
        expand("{dataset}/figures/{query}.png",
               dataset=datasets,
               query=queries
        )

rule merge_and_plot:
    input:
        expand("{{dataset}}/compared_errors/{estimator}/Run_{attempt}/{{query}}.json",
            estimator=estimators,
            attempt=runs
        )
    output:
        "{dataset}/figures/{query}.csv",
        "{dataset}/figures/{query}.png"
    run:
        ground_truth=f"{wildcards.dataset}/GT/{wildcards.query}_formatted.csv"
        shell(f"""
        python python_scripts/main.py merge-and-plot {input} {output[0]} {output[1]} \
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


