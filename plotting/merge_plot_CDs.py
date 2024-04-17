import pandas as pd
import matplotlib.pyplot as plt
import seaborn as sns
import matplotlib.ticker as mtick

def plot_data(input_data_files, note_texts_1,note_texts_2, output_plot_file):
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
        return '{:.0%}'.format(x)

    # Format y-axis ticks as percentages
    def format_percent_y(y, pos):
        return str(int(y)) + '%'

    fontsize = 18
    sns.set_style("whitegrid", {'grid.linestyle': '--', 'grid.linewidth': 0.1})

    fig, axes = plt.subplots(nrows=1, ncols=len(input_data_files), figsize=(6.7*3, 2.5*3 ), gridspec_kw={'wspace': 0.1})


    for i, input_data_file in enumerate(input_data_files):
        df = pd.read_csv(input_data_file)
        df = df[~df.apply(lambda row: row.astype(str).str.contains('method_of_moments_v3').any(), axis=1)]

        ax = axes[i] if len(input_data_files) > 1 else axes

        p = sns.lineplot(data=df, x='sample_size', y='relative_error', hue='estimator', legend=True,
                         hue_order=["horvitz_thompson", "smoothed_jackknife", "ndv", "chao_lee", "chao_lee_N_j", "crawd"],
                         estimator='mean', err_style="band", errorbar="sd", palette=estimator_colors,
                         linewidth=3, alpha=0.7, markers=False, markersize=4, dashes=False, style='estimator', ax=ax)
        ax.set_ylim(-100, 100)
        if i==0:
            ax.text(1, 0, note_texts_1[i], ha='right', va='bottom', transform=ax.transAxes, fontsize=fontsize)
            ax.set_xlim(0, 0.1)
            ax.xaxis.set_major_formatter(mtick.FuncFormatter(format_percent_x))
            ax.tick_params(axis='x', labelsize=fontsize,rotation=45)
            ax.text(1, 0.45, note_texts_2[i], ha='right', va='bottom', transform=ax.transAxes, fontsize=fontsize)

            ax.text(0.85, 0.95, "WatDiv10M", ha='center', va='bottom', transform=ax.transAxes, fontsize=fontsize-2, weight='bold')
            ax.text(0.1, 0.95, "SUBJECT", ha='center', va='bottom', transform=ax.transAxes, fontsize=fontsize-2, weight='bold')
            ax.yaxis.set_major_formatter(mtick.FuncFormatter(format_percent_y))
            ax.tick_params(axis='y', labelsize=fontsize)
            ax.set_ylabel('relative error', fontsize=fontsize)
            ax.set_xlabel('sample size', fontsize=fontsize).set_visible(False)
            p.legend().set_visible(False)
        elif i ==1:
            ax.text(1, 0, note_texts_1[i], ha='right', va='bottom', transform=ax.transAxes, fontsize=fontsize)
            ax.set_xlim(0, 0.1)
            ax.xaxis.set_major_formatter(mtick.FuncFormatter(format_percent_x))
            ax.tick_params(axis='x', labelsize=fontsize,rotation=45)
            ax.text(1, 0.5, note_texts_2[i], ha='right', va='bottom', transform=ax.transAxes, fontsize=fontsize)
            ax.text(0.9, 0.95, "DBpedia", ha='center', va='bottom', transform=ax.transAxes, fontsize=fontsize-2, weight='bold')
            p.legend(loc='upper left', fontsize=16)
            ax.tick_params(labelleft = False)
            ax.set_ylabel('relative error', fontsize=fontsize).set_visible(False)
            ax.set_xlabel('sample size', fontsize=fontsize).set_visible(False)

        elif i == 2:
            ax.text(1, 0, note_texts_1[i], ha='right', va='bottom', transform=ax.transAxes, fontsize=fontsize)
            ax.set_xlim(0, 0.01)
            ax.xaxis.set_major_formatter(mtick.FuncFormatter(lambda x, pos: '{:.1%}'.format(x)))
            ax.tick_params(axis='x', labelsize=fontsize,rotation=45)
            ax.text(1, 0.5, note_texts_2[i], ha='right', va='bottom', transform=ax.transAxes, fontsize=fontsize)
            ax.text(0.86, 0.95, "WDBench", ha='center', va='bottom', transform=ax.transAxes, fontsize=fontsize-2, weight='bold')
            p.legend().set_visible(False)
            ax.tick_params(labelleft = False)
            ax.set_ylabel('relative error', fontsize=fontsize).set_visible(False)
            ax.set_xlabel('sample size', fontsize=fontsize).set_visible(False)

    #plt.tight_layout()
    plt.subplots_adjust(left=0.07, right=0.98, top=0.97, bottom=0.13)
    plt.savefig(output_plot_file)


input_data_files = ['/GDD/Thi/count-distinct-sampling/watdiv/final_csv/SPO_CDs.csv', '/GDD/Thi/count-distinct-sampling/largerdf_dbpedia/final_csv/SPO_CDs.csv', '/GDD/Thi/count-distinct-sampling/wdbench/final_csv/SPO_CDs.csv']
note_texts_1 = ['1.1M samples', '4.5M samples', '13M samples']
note_texts_2 = ['exact value = 521,585', 'exact value = 9,494,331', 'exact value = 92,498,623']
output_plot_file = '/GDD/Thi/count-distinct-sampling/CDs_merged.png'
plot_data(input_data_files, note_texts_1,note_texts_2, output_plot_file)