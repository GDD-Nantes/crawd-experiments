import seaborn as sns
import pandas as pd
import matplotlib.pyplot as plt
import numpy as np
%matplotlib inline


# Assuming you have a dictionary or list of classes with their estimated cardinality and ground truth
class_estimations_exact = {
    "Role0": {"CRWD":405819 ,"chao_lee_N_j":138094, "GT": 402344},
    "Role1": {"CRWD": 203703,"chao_lee_N_j":116417, "GT": 204871},
    "Role2": {"CRWD": 175092,"chao_lee_N_j":105882, "GT": 176466},
    "ProductCategory14": {"CRWD":22237 ,"chao_lee_N_j":7539, "GT": 22413},
    "ProductCategory2": {"CRWD":29016 ,"chao_lee_N_j":10136, "GT": 28194},
    "ProductCategory4": {"CRWD":19545 ,"chao_lee_N_j":6596, "GT": 19170},
    "ProductCategory3": {"CRWD":20024 ,"chao_lee_N_j":6631, "GT": 19934},
    "ProductCategory1": {"CRWD":17966 ,"chao_lee_N_j":5960, "GT": 18010},
    "ProductCategory11": {"CRWD": 15788,"chao_lee_N_j":5140, "GT": 15843},
    "ProductCategory5": {"CRWD": 15152,"chao_lee_N_j":4050, "GT": 15392}

}
class_estimations_runs = {
    "Role0": {"CRWD":[403973,408483,407489,404954,404048] ,"chao_lee_N_j":[137610,138094,140736,137658,138234], "GT": 402344},
    "Role1": {"CRWD": [208234,211912,211925,208761,206039],"chao_lee_N_j":[117544,116417,116404,116312,117586], "GT": 204871},
    "Role2": {"CRWD": [180790,183813,182267,182771,181732],"chao_lee_N_j":[106079,105882,106129,106783,104440], "GT": 176466},
    "ProductCategory14": {"CRWD":[22322,22233,22483,22558,22624] ,"chao_lee_N_j":[7421,7539,7237,6928,7136], "GT": 22413},
    "ProductCategory2": {"CRWD":[28404,27754,28585,27966,28196] ,"chao_lee_N_j":[9452,10136,10946,7890,11081], "GT": 28194},
    "ProductCategory4": {"CRWD":[18746,19380,18733,19425,19066] ,"chao_lee_N_j":[4116,6596,6047,4899,6391], "GT": 19170},
    "ProductCategory3": {"CRWD":[20621,19940,19792,19917,19571] ,"chao_lee_N_j":[6803,6631,6751,8169,8153], "GT": 19934},
    "ProductCategory1": {"CRWD":[17973,17879,18351,18452,18050] ,"chao_lee_N_j":[5466,5960,6574,5088,5719], "GT": 18010},
    "ProductCategory11": {"CRWD": [16056,15969,16308,15794,15915],"chao_lee_N_j":[3575,5140,5102,5306,5695], "GT": 15843},
    "ProductCategory5": {"CRWD": [15943,15601,15503,16582,15676],"chao_lee_N_j":[4621,4050,4021,3777,4726], "GT": 15392}

}
# Create DataFrame
df = pd.DataFrame(class_estimations_runs).T
df['mean_CRWD'] = df['CRWD'].apply(np.mean)
df['mean_chao_lee_N_j'] = df['chao_lee_N_j'].apply(np.mean)

# Error bars for CRWD and chao_lee_N_j
df['std_CRWD'] = df['CRWD'].apply(np.std)
df['std_chao_lee_N_j'] = df['chao_lee_N_j'].apply(np.std)

# Sort DataFrame by GT (Ground Truth) in descending order
df_sorted = df.sort_values(by='GT', ascending=False)

plt.figure(figsize=(14, 10))

# Calculate the width of each bar
bar_width = 0.27

# Calculate the x positions for the bars
index = np.arange(len(df_sorted))
bar_positions_GT = index
bar_positions_CRWD = index + bar_width
bar_positions_chao_lee_N_j = index + 2 * bar_width

# Plot the bars with error bars
plt.bar(bar_positions_GT, df_sorted['GT'], width=bar_width, color='lightgreen', label='exact value')
plt.bar(bar_positions_CRWD, df_sorted['mean_CRWD'], yerr=df_sorted['std_CRWD'], width=bar_width, color='blue', label='CRWD', capsize=2)
plt.bar(bar_positions_chao_lee_N_j, df_sorted['mean_chao_lee_N_j'], yerr=df_sorted['std_chao_lee_N_j'], width=bar_width, color='orange', label='chao_lee_N_j',capsize=2)

# Set the x-axis labels
plt.xlabel('top 10 classes')
plt.ylabel('count-distinct objects')
"""
# Put mean values on top of each column
for i, value in enumerate(df_sorted['GT']):
    plt.text(bar_positions_GT[i], value, f"{value:.0f}", ha='left', va='bottom', fontsize=8, rotation=45)
for i, value in enumerate(df_sorted['mean_CRWD']):
    plt.text(bar_positions_CRWD[i], value, f"{value:.0f}", ha='left', va='bottom', fontsize=8, rotation=45)

for i, value in enumerate(df_sorted['mean_chao_lee_N_j']):
    plt.text(bar_positions_chao_lee_N_j[i], value, f"{value:.1f}", ha='left', va='bottom', fontsize=8, rotation=45)"""

# Set the x-axis ticks and labels
plt.xticks(bar_positions_CRWD, df_sorted.index, rotation=45,ha='right')
#plt.tick_params(axis='x', which='major')
# Add vertical grid lines
plt.grid(axis='x', linestyle='--',alpha = 0)
plt.tight_layout()
plt.legend(fontsize='large')


plt.show()
#plt.savefig('/GDD/Thi/count-distinct-sampling/watdiv/watdiv_CDo_GBc_Run_1.eps', format='eps')
#plt.savefig('/GDD/Thi/count-distinct-sampling/watdiv/watdiv_CDo_GBc_Run_1.png')