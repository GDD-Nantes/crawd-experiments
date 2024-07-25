import os
from glob import glob
from snakemake.utils import validate
import json

# Directory for input SPARQL files
QUERY_DIR = "./count-distinct-wdbench/wdbench/void_queries"
QUERY_FILES = [os.path.splitext(f)[0] for f in os.listdir(QUERY_DIR) if f.endswith(".sparql")]
TRIPLE_COUNT_FILE = "./count-distinct-wdbench/wdbench/triple_count.json"
RESULT_DIR = "./count-distinct-wdbench/wdbench/CRAWD/void_queries"

with open("./count-distinct-wdbench/wdbench/wdbench_config.json") as f1:
    CONFIG = json.load(f1)
with open(TRIPLE_COUNT_FILE) as f2:
    TRIPLE_COUNTS = json.load(f2)
rule all:
    input:
        expand(
            f"{RESULT_DIR}/{{config}}/{{query}}.result",
            config=CONFIG.keys(),
            query=QUERY_FILES
        )

rule prepare_result_directories:
    output:
        [f"{RESULT_DIR}/{config_name}" for config_name in CONFIG.keys()]
    run:
        for config_name in CONFIG.keys():
            os.makedirs(f"{RESULT_DIR}/{config_name}", exist_ok=True)


rule run_sparql_query:
    input:
        query_file=f"{QUERY_DIR}/{{query}}.sparql",
        triple_counts=TRIPLE_COUNT_FILE,
        config_file="./count-distinct-wdbench/wdbench/wdbench_config.json"
    output:
        f"{RESULT_DIR}/{{config}}/{{query}}.result"
    run:
        with open(input.config_file) as f:
            config = json.load(f)
        limit = config[wildcards.config]["limit"]
        sl = TRIPLE_COUNTS.get(wildcards.query, 1) * config[wildcards.config]["sl"]
        result_dir = f"{RESULT_DIR}/{wildcards.config}"
        # Replace path to the database with the actual path
        shell(
            f"""
            mvn exec:java -pl rawer \
                -Dexec.args="\
                --database=path/to/your/run_blazegraph_wdbench/wdbench-blaze/blazegraph.jnl \
                --file={input.query_file} \
                --limit={limit} \
                -sl={sl}\
                --threads=13 -n=5\
                --report" \
                &> {output}
            """
        )