import click
import json
import numpy as np
import matplotlib.pyplot as plt
import os
import seaborn as sns
import pandas as pd
import matplotlib.ticker as mtick
@click.command()
@click.argument("input-data-file", type=click.Path(exists=True, file_okay=True, dir_okay=False))
@click.argument("output-plot-file", type=click.Path(exists=False, file_okay=True, dir_okay=False))
def plot_data(input_data_file, output_plot_file):
    df = pd.read_csv(input_data_file)
    # Filter rows where the specified value is not present
    df = df[~df.apply(lambda row: row.astype(str).str.contains('method_of_moments_v3').any(), axis=1)]

    estimator_colors = {
        'horvitz_thompson': 'red',
        'smoothed_jackknife': 'green',
        'ndv': 'black',
        'chao_lee': 'pink',
        'chao_lee_N_j': 'orange',
        'crawd': 'blue'
    }
    # Format x-axis ticks as percentages
    def format_percent_x(x, pos):
        return '{:.0%}'.format(x) # fix .1 if wdbench
    # Format y-axis ticks as percentages
    def format_percent_y(y, pos):
        return str(int(y)) + '%'
    plt.clf()
    plt.figure(figsize=(10, 8))

    sns.set_style("whitegrid", {'grid.linestyle': '--', 'grid.linewidth': 0.1})
    p=sns.lineplot(data=df,x='sample_size', y='relative_error', hue='estimator',
    hue_order=["horvitz_thompson","smoothed_jackknife","ndv","chao_lee","chao_lee_N_j","crawd"],estimator='mean',err_style="band",errorbar="sd",palette=estimator_colors, linewidth=1, alpha=0.7,markers=False, markersize=4, dashes=False, style='estimator')
    p.set(xlabel='sample size', ylabel='relative error')
    for line in p.lines:
        line.set_linewidth(2)
    plt.xlim(0, 0.1)#fix if wdbench
    plt.ylim(-100, 100)
    # Add a note just under the legend
    note_text = '4.5M samples'
    plt.figtext(0.9, 0.05, note_text, ha='right')
    note_text_1 = 'exact value = 13,619,093'
    plt.figtext(0.9, 0.5, note_text_1, ha='right')

    plt.gca().xaxis.set_major_formatter(mtick.FuncFormatter(format_percent_x))
    plt.gca().yaxis.set_major_formatter(mtick.FuncFormatter(format_percent_y))
    plt.legend()
    plt.savefig(output_plot_file)



if __name__ == "__main__":
    plot_data()