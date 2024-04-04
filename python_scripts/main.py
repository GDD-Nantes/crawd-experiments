import os
from pathlib import Path
import json
import csv
import numpy as np
import pandas as pd
import io
import argparse
from tqdm import tqdm
from estndv import ndvEstimator
from pydistinct.stats_estimators import *
import click
import seaborn as sns
import re
import logging
import matplotlib.pyplot as plt

# Configure logging
logging.basicConfig(level=logging.INFO)  # Set logging level to INFO

# Define a logger
logger = logging.getLogger(__name__)

@click.group
def cli():
    pass

DATASET_SIZE = {'watdiv':10916457,'largerdf_dbpedia':42849609,'wdbench':1257169959}

ndv = ndvEstimator()

def calc_relative_error(gt, estimate):
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

def horvitz_thompson(values, n_pop):
    try:
        return horvitz_thompson_estimator(values, n_pop)
    except:
        return 0

def smoothed_jackknife(values,n_pop = None):
    try:
        return smoothed_jackknife_estimator(values)
    except:
        return 0

def chao_lee(values, n_pop=None):
    return chao_lee_estimator(values)

def method_of_moments_v3(values, n_pop=None):
    try:
        return method_of_moments_v3_estimator(values)
    except:
        return 0


def run_estimator(estimator_func, table, num_rows, n_pop, GT):
    values = table[:num_rows, 0]
    estimator_result = estimator_func(values, n_pop)
    relative_error_value = calc_relative_error(GT, estimator_result)

    return estimator_result, relative_error_value

ESTIMATORS = {
        'ndv': ndv_estimator,
        'horvitz_thompson': horvitz_thompson,
        'smoothed_jackknife': smoothed_jackknife,
        'method_of_moments_v3': method_of_moments_v3,
        'chao_lee': chao_lee
    }


def read_gt_file(file_path):
    with open(file_path, 'r', encoding='utf-8') as f:
        line = f.read()
        stri,value = line.strip().split(',')
        return int(value)

@cli.command()
@click.option("--input-file",type= click.Path(exists = True, file_okay=True, dir_okay=False))
@click.option("--ground-truth",type= click.Path(exists = True, file_okay=True, dir_okay=False))
@click.option("--compared-results",type= click.Path(exists = False, file_okay=True, dir_okay=False))
@click.option("--compared-errors",type= click.Path(exists = False, file_okay=True, dir_okay=False))
@click.option("--estimator",type=click.STRING)
@click.option("--dataset",type=click.STRING)
def run_estimators(input_file, ground_truth, compared_results, compared_errors, estimator, dataset):
    start = 0.005
    stop = 0.1
    step = 0.005
    sample_sizes = [round(start + step * i, 3) for i in range(int((stop - start) / step) + 1)]
    dictionary = {}
    sample_table = read_csv_data(input_file, dictionary)

    gt = read_gt_file(ground_truth)


    if (dataset == 'watdiv'):
        N = DATASET_SIZE['watdiv']
    elif (dataset == 'wdbench'):
        N = DATASET_SIZE['wdbench']
    elif (dataset == 'largerdf_dbpedia'):
        N = DATASET_SIZE['largerdf_dbpedia']
    else:
        print("Error: Dataset not recognized.")
        return
    print(f"Dataset size: {N}")
    print(f"Ground truth: {gt}")

    estimator_func = ESTIMATORS[estimator]

    all_results = {}
    all_relative_errors = {}
    print(f"Running {estimator} estimator")
    for sample_size in tqdm(sample_sizes):
        results, relative_errors = run_estimator(estimator_func, sample_table, int(sample_size * N), N, gt)
        logger.info(f"Sample size: {sample_size}, Result: {results}, Relative Error: {relative_errors}")
        if isinstance(results, np.ndarray):
            all_results[sample_size] = results.item()
        else:
            all_results[sample_size] = results

        if isinstance(relative_errors, np.ndarray):
            all_relative_errors[sample_size] = relative_errors.item()
        else:
            all_relative_errors[sample_size] = relative_errors
    with open(compared_results, 'w') as f:
        json.dump(all_results, f)

    with open(compared_errors, 'w') as f:
        json.dump(all_relative_errors, f)

@cli.command()
@click.argument("input-files", nargs=-1, type=click.Path(exists = True, file_okay=True, dir_okay=False))
@click.argument("output-data-file", type=click.Path(exists=False, file_okay=True, dir_okay=False))
@click.argument("output-plot-file", type=click.Path(exists=False, file_okay=True, dir_okay=False))
@click.option("--dataset",type=click.STRING)
@click.option("--ground-truth",type= click.Path(exists = True, file_okay=True, dir_okay=False))
@click.option("--query",type=click.STRING)
def merge_and_plot(input_files,output_data_file, output_plot_file,dataset, ground_truth,query):
    records = []
    for input_file in tqdm(input_files):
        _, _, estimator, run, _ = input_file.split('/')
        run = int(re.search("Run_(\d+)", run).group(1))

        with open(input_file, 'r') as f:
            data = json.load(f)
            for sample_size, relative_error in data.items():
                records.append({
                    'sample_size': sample_size,
                    'relative_error': relative_error,
                    'estimator': estimator,
                    'run': run
                })

        gt = read_gt_file(ground_truth)
        cd_path = f"{dataset}/sample/Run_{run}/{query}.csv"
        cd_df = pd.read_csv(cd_path).reset_index()
        cd_df = cd_df.rename(columns={'index':'nb_samples'})
        cd_df['nb_samples'] = cd_df['nb_samples'] + 1
        cd_df['sample_size'] = cd_df['nb_samples']/DATASET_SIZE[dataset]
        cd_df["relative_error"] = cd_df["CD"].apply(lambda x: calc_relative_error(gt, x))
        cd_df["estimator"] = "crwd"
        cd_df["run"] = run
        cd_df = cd_df[['sample_size', 'relative_error', 'estimator', 'run']]
        records.extend(cd_df.to_dict('records'))

    df = pd.DataFrame.from_records(records)
    logger.info(f"Written data to {output_data_file}")
    df.to_csv(output_data_file, index=False)
    # Check the data types of the columns
    logger.info(f"Types of df {df.dtypes}")

    # Convert columns to the correct data types if necessary
    df['sample_size'] = df['sample_size'].astype(float)
    df['relative_error'] = df['relative_error'].astype(float)
    df['estimator'] = df['estimator'].astype(str)
    sns.set_theme(style="whitegrid")
    # Group by 'estimator' and 'sample_size', calculate mean and standard deviation
    grouped_df = df.groupby(['estimator', 'sample_size']).agg({'relative_error': ['mean', 'std']}).reset_index()
    grouped_df.columns = ['estimator', 'sample_size', 'mean_relative_error', 'std_dev_relative_error']

    # Merge mean and standard deviation back to original DataFrame
    df = pd.merge(df, grouped_df, on=['estimator', 'sample_size'], how='left')

    # Plotting with Seaborn
    sns.set_theme(style="whitegrid")
    plt.figure(figsize=(10, 6))

    # Use seaborn lineplot with error bars
    sns.lineplot(data=df, x="sample_size", y="mean_relative_error", hue="estimator")

    plt.xlabel('Sample Size')
    plt.ylabel('Relative Error (%)')
    plt.legend()
    plt.savefig(output_plot_file)
    plt.show()


if __name__ == "__main__":
    cli()
