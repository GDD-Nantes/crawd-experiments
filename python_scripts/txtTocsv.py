import json
import os

def extract_and_format_values(json_data):
    data = json.loads(json_data)
    formatted_values = []
    for entry in data['results']['bindings']:
        x_value = entry['x']['value']
        if 'c' in entry:  # Check if "c" key is present
            c_value = entry['c']['value']
            formatted_value = f'CD,{int(x_value)},{c_value}'
        elif 'p' in entry:  # Check if "p" key is present
            p_value = entry['p']['value']
            formatted_value = f'CD,{int(x_value)},{p_value}'
        elif 'c' in entry and 'p' in entry:  # Check if both "c" and "p" keys are present
            c_value = entry['c']['value']
            p_value = entry['p']['value']
            formatted_value = f'CD,{int(x_value)},{c_value},{p_value}'
        else:
            formatted_value = f'CD,{int(x_value)}'  # Handle the case when "c" is not present
        formatted_values.append(formatted_value)

    return formatted_values

def process_file(file_path):
    # Read JSON data from file
    with open(file_path, 'r') as file:
        json_data = file.read()
    formatted_values = extract_and_format_values(json_data)
    formatted_values = sorted(formatted_values, key=lambda x: int(x.split(',')[1]))
    file_name = os.path.splitext(file_path)[0]
    output_file_path = f'{file_name}_formatted.csv'
    with open(output_file_path, 'w') as output_file:
        for value in formatted_values:
            output_file.write(value + '\n')


watdiv_path = '/count-distinct-sampling/watdiv/GT'
wdbench_path = '/count-distinct-sampling/wdbench/GT'
dbpedia_path = '/count-distinct-sampling/largerdf_dbpedia/GT'
# Process all files in the directory
for path in [watdiv_path,wdbench_path, dbpedia_path]:
    for filename in os.listdir(path):
        if filename.endswith(".txt"):
            file_path = os.path.join(path, filename)
            process_file(file_path)
