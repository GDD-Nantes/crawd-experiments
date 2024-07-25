import re
from pathlib import Path
import statistics

result_pattern = re.compile(r"{\?count->\s*\"\"(.*?)\"\^\^.*")

def extract_counts_from_file(path_to_dat):
    content = Path(path_to_dat).read_text()
    
    results = re.findall(result_pattern, content)
    results = [float(x) for x in results]
    result = statistics.mean(results) 
    
    print(result)


extract_counts_from_file("/Users/nedelec-b-2/Desktop/Projects/anonymous-count-distinct/cd_bad_queries_wdbench/results/query_637.sparql-10000-1.dat")
