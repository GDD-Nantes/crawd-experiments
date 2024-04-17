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
import matplotlib.ticker as mtick

# Configure logging
logging.basicConfig(level=logging.INFO)# Set logging level to INFO

# Define a logger
logger = logging.getLogger(__name__)

@click.group
def cli():
    pass

DATASET_SIZE = {'watdiv':10916457,'largerdf_dbpedia':42849609,'wdbench':1257169959}

ndv = ndvEstimator()

def calc_relative_error(gt, estimate):
    return (estimate - gt) / gt * 100

def read_csv_data(file_path, dictionary):
    logger.info(f"Reading data from {file_path}")
    data = []
    with open(file_path, 'r', encoding='utf-8') as f:
        reader = csv.reader(f, delimiter=' ')
        next(reader)  # Skip the header if exists
        for row in reader:
            # Check if the row has two values
            if len(row) == 3:
                #logger.info(f"Row: {row}")
                # Unpack the values
                value, cd,chao = row
                #logger.info(f"Value: {value}, cd: {cd}, chao: {chao}")
                value_id = dictionary.setdefault(value, len(dictionary))
                try:
                    cd = float(cd)
                    chao = float(chao)
                    data.append((value_id, cd,chao))
                except ValueError:
                    print(f"Warning: Skipping line with invalid cd value: {row}")
    return np.array(data)

def ndv_estimator(values, n_pop):
    try:
        return ndv.sample_predict(values, n_pop)
    except:
        return np.nan

def horvitz_thompson(values, n_pop):
    try:
        return horvitz_thompson_estimator(values, n_pop)
    except:
        return np.nan

def smoothed_jackknife(values,n_pop = None):
    try:
        return smoothed_jackknife_estimator(values)
    except:
        return np.nan


def method_of_moments_v3(values, n_pop=None):
    try:
        return method_of_moments_v3_estimator(values)
    except:
        return np.nan

def chao_lee(values, n_pop=None):
    try:
        return chao_lee_estimator(values)
    except:
        return np.nan

def run_estimator(estimator_func, table, num_rows, n_pop, gt):
    values = table[:num_rows, 0]
    estimator_result = estimator_func(values, n_pop)
    if not np.isnan(estimator_result):
        relative_error_value = calc_relative_error( gt, estimator_result)
        return estimator_result, relative_error_value
    else:
        return estimator_result, np.nan



ESTIMATORS = {
        'ndv': ndv_estimator,
        'horvitz_thompson': horvitz_thompson,
        'smoothed_jackknife': smoothed_jackknife,
        'method_of_moments_v3': method_of_moments_v3,
        'crawd': None,
        'chao_lee_N_j': None,
        'chao_lee':chao_lee
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
def run_estimators(input_file, ground_truth, compared_results, compared_errors, estimator, dataset,N = None):

    start = 0
    stop = 0.1# wdbench 0.01, watdiv 0.1, largerdf_dbpedia 0.1
    step_small = 0.0005
    step_large = 0.0005
    sample_sizes_small_step = [round(start + step_small * i, 4) for i in range(int(0.05 / step_small) + 1)]
    sample_sizes_large_step = [round(0.05 + step_large * i, 4) for i in range(int((stop - 0.05) / step_large) + 1)]
    sample_sizes = sample_sizes_small_step + sample_sizes_large_step[1:]
    # Read the input file
    dictionary = {}
    sample_table = read_csv_data(input_file, dictionary)
    # Read the ground truth

    gt = read_gt_file(ground_truth)

    # Get the dataset size
    if N is not None:
        N = N
    else:
        if (dataset == 'watdiv'):
            N = DATASET_SIZE['watdiv']
        elif (dataset == 'wdbench'):
            N = DATASET_SIZE['wdbench']
            sample_sizes = [i / 10000 for i in range(0, 101)]
        elif (dataset == 'largerdf_dbpedia'):
            N = DATASET_SIZE['largerdf_dbpedia']
        else:
            print("Error: Dataset not recognized.")
            return
    logger.info(f"Dataset size: {N}")
    logger.info(f"Ground truth: {gt}")

    estimator_func = ESTIMATORS[estimator]

    all_results = {}
    all_relative_errors = {}
    for sample_size in tqdm(sample_sizes):
        if estimator in ["ndv", "horvitz_thompson", "smoothed_jackknife", "chao_lee"]:
            results, relative_errors = run_estimator(estimator_func, sample_table, int(sample_size * N), N, gt)
        elif estimator == "crawd":
            results = sample_table[int(sample_size * N),1]
            relative_errors = calc_relative_error(gt, results)
        elif estimator == "chao_lee_N_j":
            results = sample_table[int(sample_size * N),2]
            relative_errors = calc_relative_error(gt, results)
            #logger.info(f"Sample size: {sample_size}, Estimator: {estimator}, Result: {results}, Relative Error: {relative_errors}")
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
@click.option("--dataset",type=click.STRING)
@click.option("--ground-truth",type= click.Path(exists = True, file_okay=True, dir_okay=False))
@click.option("--query",type=click.STRING)
def merge_data(input_files,output_data_file,dataset, ground_truth,query):
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

    df = pd.DataFrame.from_records(records)
    df = df[['sample_size', 'relative_error', 'estimator', 'run']]

    # Convert columns to the correct data types if necessary
    df['sample_size'] = df['sample_size'].astype(float)
    df['relative_error'] = df['relative_error'].astype(float)
    df['estimator'] = df['estimator'].astype(str)

    df.to_csv(output_data_file, index=False)
    logger.info(f"Written data to {output_data_file}")


@cli.command()
@click.argument("input-data-file", type=click.Path(exists=True, file_okay=True, dir_okay=False))
@click.argument("output-plot-file", type=click.Path(exists=False, file_okay=True, dir_okay=False))
def plot_data(input_data_file, output_plot_file):
    df = pd.read_csv(input_data_file)
    estimator_colors = {
        'ndv': 'black',
        'horvitz_thompson': 'red',
        'smoothed_jackknife': 'green',
        'chao_lee': 'pink',
        'crawd': 'blue',
        'chao_lee_N_j': 'orange'
    }
    # Format x-axis ticks as percentages
    def format_percent_x(x, pos):
        return '{:.1%}'.format(x) # fix .1 if wdbench
    # Format y-axis ticks as percentages
    def format_percent_y(y, pos):
        return str(int(y)) + '%'
    plt.clf()
    plt.figure(figsize=(10, 8))

    sns.set_style("whitegrid", {'grid.linestyle': '--', 'grid.linewidth': 0.1})
    p=sns.lineplot(data=df,x='sample_size', y='relative_error', hue='estimator',estimator='mean',err_style="band",errorbar="sd",palette=estimator_colors, linewidth=1, alpha=0.7,markers=False, markersize=4, dashes=False, style='estimator')
    p.set(xlabel='sample size', ylabel='relative error')
    for line in p.lines:
        line.set_linewidth(2)
    plt.xlim(0, 0.01)#fix if wdbench
    plt.ylim(-100, 100)
    # Add a note just under the legend
    note_text = '13M samples'
    plt.figtext(0.9, 0.05, note_text, ha='right')
    note_text_1 = 'exact value = 304,967,140'
    plt.figtext(0.9, 0.5, note_text_1, ha='right')


    plt.gca().xaxis.set_major_formatter(mtick.FuncFormatter(format_percent_x))
    plt.gca().yaxis.set_major_formatter(mtick.FuncFormatter(format_percent_y))
    plt.legend()
    plt.savefig(output_plot_file)




if __name__ == "__main__":
    cli()














