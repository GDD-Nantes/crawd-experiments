import os
import pandas as pd
import re

# Function to get the last line of a CSV file
def get_last_line(csv_file):
    with open(csv_file, 'r') as file:
        lines = file.readlines()
        last_line = lines[-1].strip()
    return last_line

# Function to extract key-value pairs from the data
def extract_key_value_pairs(data):
    pairs = re.findall(r'\[(.*?)\] => (.*?)[ ;|$]', data)
    return pairs

# Directory containing the runs
runs_dir = "/GDD/Thi/count-distinct-sampling/wdbench/sample/groupby_class/"
# Dictionary to store key-value pairs for each key across 5 runs
key_value_dict_CDo = {}
key_value_dict_chao_lee_CDo = {}

# Iterate over each run
for i in range(1, 6):  # Assuming 5 runs
    run_dir = os.path.join(runs_dir, f"Run_{i}")

    # Check if the run directory exists
    if os.path.exists(run_dir):
        CDo_csv = os.path.join(run_dir, "CDo.csv")
        chao_lee_CDo_csv = os.path.join(run_dir, "chao_lee_CDo.csv")

        # Check if the files exist
        if os.path.exists(CDo_csv):
            last_line_CDo = get_last_line(CDo_csv)
            pairs_CDo = extract_key_value_pairs(last_line_CDo)
            for key, value in pairs_CDo:
                if key not in key_value_dict_CDo:
                    key_value_dict_CDo[key] = []
                key_value_dict_CDo[key].append(float(value))

        if os.path.exists(chao_lee_CDo_csv):
            last_line_chao_lee_CDo = get_last_line(chao_lee_CDo_csv)
            pairs_chao_lee_CDo = extract_key_value_pairs(last_line_chao_lee_CDo)
            for key, value in pairs_chao_lee_CDo:
                if key not in key_value_dict_chao_lee_CDo:
                    key_value_dict_chao_lee_CDo[key] = []
                key_value_dict_chao_lee_CDo[key].append(float(value))

# Write the key-value pairs for CDo to a CSV file
output_filename_CDo = os.path.join(runs_dir, "final_CDo.csv")
with open(output_filename_CDo, 'w') as output_file:
    for key, values in key_value_dict_CDo.items():
        output_file.write(f"{key},{','.join(map(str, values))}\n")

# Write the key-value pairs for chao_lee_CDo to a CSV file
output_filename_chao_lee_CDo = os.path.join(runs_dir, "final_chao_lee_CDo.csv")
with open(output_filename_chao_lee_CDo, 'w') as output_file:
    for key, values in key_value_dict_chao_lee_CDo.items():
        output_file.write(f"{key},{','.join(map(str, values))}\n")
