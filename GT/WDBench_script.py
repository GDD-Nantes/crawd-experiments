import subprocess
import time
import os
# Define your query names and queries in arrays
QUERY_NAMES = ["QB2", "QB3", "QB4", "QB5", "QC3", "QC4", "QC5", "QC6", "QD3", "QD4", "QE3", "QE4","QF1","QF2","QF3","QF4","QF5"]
QUERIES = [
    'SELECT (COUNT(DISTINCT ?o) AS ?x) WHERE { ?s <http://www.wikidata.org/prop/direct/P131> ?o }',
    'SELECT (COUNT(DISTINCT ?p) AS ?x) WHERE { ?s ?p ?o }',
    'SELECT (COUNT(DISTINCT ?s) AS ?x) WHERE { ?s ?p ?o }',
    'SELECT (COUNT(DISTINCT ?o) AS ?x) WHERE { ?s ?p ?o }',
    'SELECT (COUNT(DISTINCT ?d) AS ?x) ?c WHERE { ?s <http://www.wikidata.org/prop/direct/P131> ?c , ?d } GROUP BY ?c',
    'SELECT (COUNT(DISTINCT ?p) AS ?x) ?c WHERE { ?s <http://www.wikidata.org/prop/direct/P131> ?c ; ?p ?o } GROUP BY ?c',
    'SELECT (COUNT(DISTINCT ?s) AS ?x) ?c WHERE { ?s <http://www.wikidata.org/prop/direct/P131> ?c } GROUP BY ?c',
    'SELECT (COUNT(DISTINCT ?o) AS ?x) ?c WHERE { ?s <http://www.wikidata.org/prop/direct/P131> ?c ; ?p ?o } GROUP BY ?c',
    'SELECT (COUNT(DISTINCT ?s) AS ?x) ?p WHERE { ?s ?p ?o } GROUP BY ?p',
    'SELECT (COUNT(DISTINCT ?o) AS ?x) ?p WHERE { ?s ?p ?o } GROUP BY ?p',
    'SELECT (COUNT(DISTINCT ?s) AS ?x) ?c ?p WHERE { ?s <http://www.wikidata.org/prop/direct/P131> ?c ; ?p ?o } GROUP BY ?c ?p',
    'SELECT (COUNT(DISTINCT ?o) AS ?x) ?c ?p WHERE { ?s <http://www.wikidata.org/prop/direct/P131> ?c ; ?p ?o } GROUP BY ?c ?p',
    'SELECT (COUNT(DISTINCT ?s ) AS ?x) WHERE { ?s ?p ?o  FILTER(isIri(?s))}',
    'SELECT (COUNT(DISTINCT ?s) AS ?x) WHERE { ?s ?p ?o FILTER(isBlank(?s))}',
    'SELECT (COUNT(DISTINCT ?o ) AS ?x) WHERE { ?s ?p ?o FILTER(isIri(?o))}',
    'SELECT (COUNT(DISTINCT ?o ) AS ?x) WHERE { ?s ?p ?o FILTER(isLiteral(?o))}',
    'SELECT (COUNT(DISTINCT ?o ) AS ?x) WHERE { ?s ?p ?o FILTER(isBlank(?o))}'
]

# Loop through each query
for query_name, query in zip(QUERY_NAMES, QUERIES):
    result_file = f"/GDD/Thi/count-distinct-sampling/GT/wdbench/{query_name}.txt"
    # Check if the result file already exists
    if os.path.exists(result_file):
        print(f"Result file {result_file} already exists. Skipping query...")
        continue
    # Run the command and measure the time
    start_time = time.time()
    result = subprocess.run(['curl', '-s', '-X', 'POST', '--data-urlencode', f'query={query}', 'http://localhost:3030/WDBENCH/query'], capture_output=True, text=True)
    end_time = time.time()

    # Calculate the execution time
    elapsed_time = (end_time - start_time) * 1000  # milliseconds
    print(f'Done with query {query_name}. Execution time: {elapsed_time:.6f} milliseconds')
    # Save the result to a file
    with open(f"/GDD/Thi/count-distinct-sampling/GT/wdbench/{query_name}.txt", "w") as result_file:
        result_file.write(result.stdout)

    # Save the execution time to a file
    with open("/GDD/Thi/count-distinct-sampling/GT/wdbench/execution_times.txt", "a") as execution_times_file:
        execution_times_file.write(f"Query: {query}\n")
        execution_times_file.write(f"Execution time: {elapsed_time:.6f} milliseconds\n")
        execution_times_file.write("-------------------------------------\n")
