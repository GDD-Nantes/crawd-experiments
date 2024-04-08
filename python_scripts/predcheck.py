import os
import pandas as pd
import matplotlib.pyplot as plt
from tqdm import tqdm
import seaborn as sns
import matplotlib.ticker as mtick
import numpy as np
import multiprocessing
# Define the parent directory
parent_dir = '/GDD/Thi/count-distinct-sampling/watdiv/sample/predcheck'

errors = []
ground_truth_values = 89
final_df = pd.DataFrame()

for i in tqdm(range(1, 11)):
    run_dir = os.path.join(parent_dir, f'Run_{i}')
    csv_file = os.path.join(run_dir, 'SPO_CDp.csv')

    # Read the CSV file
    df = pd.read_csv(csv_file, delimiter=' ')
    column_i= df['CRWD']
    final_df[f'run_{i}'] = column_i

# Calculate row-wise average
final_df['average'] = final_df.mean(axis=1)
final_df.to_csv('/GDD/Thi/count-distinct-sampling/watdiv/sample/predcheck/predchek_10_runs_df.csv', index=False)
