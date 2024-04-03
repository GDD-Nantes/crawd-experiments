import argparse
import json
import numpy as np
import matplotlib.pyplot as plt
import os

def plot_results_and_save(path_to_compared_errors):
    # Load the results
    with open(path_to_compared_errors, 'r') as f:
        all_relative_errors_json = json.load(f)

    # Convert lists back to NumPy arrays for all_relative_errors
    all_relative_errors = {float(key): {estimator: np.array(val) for estimator, val in value.items()}
                           for key, value in all_relative_errors_json.items()}

    # Plotting
    sample_sizes = list(all_relative_errors.keys())
    plt.figure(figsize=(10, 6))
    for estimator, errors in all_relative_errors[sample_sizes[0]].items():
        plt.plot(sample_sizes, [error[estimator] for error in all_relative_errors.values()], label=estimator)

    plt.xlabel('Sample Size')
    # Set y-axis limit to focus on the range from 0 to 10
    plt.ylim(0, 100)
    plt.xticks(np.arange(min(sample_sizes)+0.005, max(sample_sizes) + 0.01, 0.01))
    # Set grid linestyle to dashed and reduce its thickness
    plt.grid(True, linestyle='--', linewidth=0.5)
    plt.ylabel('Relative Error (%)')

    # Extract the name from the file path for the title and figure name
    file_name = os.path.basename(path_to_compared_errors)
    title_name = os.path.splitext(file_name)[0]  # Remove extension

    plt.title(f'Performance of CD and other estimators for {title_name}')
    plt.legend()
    # Extracting directory path and create figures directory
    directory_path = os.path.dirname(path_to_compared_errors)
    figures_directory = os.path.join(directory_path, 'figures')
    os.makedirs(figures_directory, exist_ok=True)

    # Save figure with the desired name and directory
    figure_path = os.path.join(figures_directory, f'{title_name}.png')
    plt.savefig(figure_path)
    plt.show()

if __name__ == "__main__":
    # Argument parser
    parser = argparse.ArgumentParser(description='Plot and save results from compared errors JSON file.')
    parser.add_argument('--input_file', type=str, help='Path to the compared errors JSON file')

    # Parse arguments
    args = parser.parse_args()

    # Call the plot function with the provided input file path
    plot_results_and_save(args.input_file)
