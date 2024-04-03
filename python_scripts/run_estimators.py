import os
import json
import csv
import numpy as np
import pandas as pd
import io
import argparse
from tqdm import tqdm
from estndv import ndvEstimator
from pydistinct.stats_estimators import *


ndv = ndvEstimator()

def relative_error(gt, estimate):
    return abs(gt - estimate) / gt * 100

def read_csv_data(file_path, dictionary):
    data = []
    with open(file_path, 'r', encoding='utf-8') as f:
        reader = csv.reader(f, delimiter=',')
        next(reader)  # Skip the header if exists
        for row in reader:
            # Check if the row has two values
            if len(row) == 2:
                # Unpack the values
                value, cd = row
                value_id = dictionary.setdefault(value, len(dictionary))
                try:
                    cd = float(cd)
                    data.append((value_id, cd))
                except ValueError:
                    print(f"Warning: Skipping line with invalid cd value: {row}")
    return np.array(data)

def ndv_estimator(values, n_pop):
    try:
        return ndv.sample_predict(values, n_pop)
    except:
        return 0

def horvitz_thompson_estimator(values, n_pop):
    return horvitz_thompson_estimator(values, n_pop)

def smoothed_jackknife_estimator(values):
    return smoothed_jackknife_estimator(values)

def chao_lee_estimator(values):
    return chao_lee_estimator(values)

def method_of_moments_v3_estimator(values):
    return method_of_moments_v3_estimator(values)

def run_estimator(estimator_func, table, num_rows, n_pop, GT):
    values = table[:num_rows, 0]
    estimator_result = estimator_func(values, n_pop)
    relative_error_value = relative_error(GT, estimator_result)

    return {'estimate': estimator_result}, relative_error_value

def main(args):
    path_to_sample = args.input_file
    start = 0.005
    stop = 0.1
    step = 0.005
    sample_sizes = [round(start + step * i, 3) for i in range(int((stop - start) / step) + 1)]

    dictionary = {}
    sample_table = read_csv_data(path_to_sample, dictionary)

    gt = read_gt_file(args.ground_truth)

    dataset_size = {'watdiv':10916457,'largerdf_dbpedia':42849609,'wdbench':1257169959}
    dataset = args.input_file.split('/')[4]
    if (dataset == 'watdiv'):
            N = dataset_sizes['watdiv']
        elif (dataset == 'wdbench'):
            N = dataset_sizes['wdbench']
        elif (dataset == 'largerdf_dbpedia'):
            N = dataset_sizes['largerdf_dbpedia']
        else:
            print("Error: Dataset not recognized.")
            return
    print(f"Dataset size: {N}")
    print(f"Ground truth: {gt}")
    estimators = {
        'ndv': ndv_estimator,
        'horvitz_thompson': horvitz_thompson_estimator,
        'smoothed_jackknife': smoothed_jackknife_estimator,
        'method_of_moments_v3': method_of_moments_v3_estimator,
        'chao_lee': chao_lee_estimator
    }

    for estimator_name, estimator_func in estimators.items():
        all_results = {}
        all_relative_errors = {}
        print(f"Running {estimator_name} estimator")
        for sample_size in tqdm(sample_sizes):
            results, relative_errors = run_estimator(estimator_func, sample_table, int(sample_size * N), N, gt)
            all_results[sample_size] = results
            all_relative_errors[sample_size] = relative_errors


        all_results_json = {key: {estimator_name: val} for key, val in all_results.items()}
        all_relative_errors_json = {key: {estimator_name: val} for key, val in all_relative_errors.items()}

        results_file_path = os.path.join(args.compared_results, estimator_name + '_results.json')
        errors_file_path = os.path.join(args.compared_errors, estimator_name + '_errors.json')

        with open(results_file_path, 'w') as f:
            json.dump(all_results_json, f)

        with open(errors_file_path, 'w') as f:
            json.dump(all_relative_errors_json, f)

if __name__ == "__main__":
    parser = argparse.ArgumentParser(description='Run estimators on a given sample and save the results and errors into JSON files.')
    parser.add_argument('--input_file', type=str, help='Path to the input sample CSV file')
    parser.add_argument('--ground_truth', type=str, help='Path to the ground truth CSV file')
    parser.add_argument('--compared_results', type=str, help='Path to save the compared results JSON files')
    parser.add_argument('--compared_errors', type=str, help='Path to save the compared errors JSON files')
    args = parser.parse_args()
    main(args)
