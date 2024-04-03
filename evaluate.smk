# Define the base path
base_path = "/GDD/Thi/count-distinct-sampling/"

# Define datasets and queries
datasets = ["watdiv","largerdf_dbpedia", "wdbench"]
queries = ["SPO_CDs", "SPO_CDp", "SPO_CDo"]
run = [1,2,3,4,5]

# Define input and output file paths for each dataset and query
rule all:
    input:
        expand(base_path + "{dataset}/figures/Run_{run}/{query}.png",
               dataset=datasets[0],
               query=queries[0]
               )

# Rule to run_estimators.py
rule run_estimators:
    input:
        input_file=base_path + "{dataset}/sample/Run_{run}/{query}.csv",
        ground_truth=base_path + "{dataset}/GT/{query}_formatted.csv"
    output:
        compared_results=base_path + "{dataset}/compared_results/Run_{run}/{query}.json",
        compared_errors=base_path + "{dataset}/compared_errors/Run_{run}/{query}.json"
    shell:
        """
        source /GDD/miniconda3/etc/profile.d/conda.sh &&
        conda activate ndv &&
        python {base_path}python_scripts/run_estimators.py \
            --input_file {input.input_file} \
            --ground_truth {input.ground_truth} \
            --compared_results {output.compared_results} \
            --compared_errors {output.compared_errors}
        """

# Rule to visualize.py
rule visualize:
    input:
        errors_file="{base_path}/{dataset}/compared_errors/Run_{run}/{query}.json"
    output:
        figure="{base_path}/{dataset}/figures/Run_{run}/{query}.png"
    shell:
        """
        python {base_path}/python_scripts/visualize.py --input_file {input.errors_file}
        """
