import sys
import pandas as pd
import argparse
def main(input_file, output_file,chaolee=False):
    # Load the data
    df = pd.read_csv(input_file)
    gt_file="./count-distinct-wdbench/wdbench/GT/GT_top30q_max_countall_max_cd_var_and_voidq.csv"
    gt_df = pd.read_csv(gt_file)

    columns_to_drop = ['WJ_SampleSize', 'CRAWD_SampleSize', '∑Fµ_SampleSize', '∑Fµ_success', '∑Fµ_fail', 'N̂']
    if chaolee:
        columns_to_drop= ['Nb_Total_Scans']
    df.drop(columns=columns_to_drop, inplace=True)
    pivot_df = df.pivot_table(
        index='query_name',
        columns='Config',
        values=['cd', 'Execution_time'],
        aggfunc='mean',
        fill_value=0  # Fill missing values (if any) with 0
    )

    # Flatten the column names (e.g., from ('cd', 1) to 'cd_1')
    pivot_df.columns = ["_".join(map(str, col)) for col in pivot_df.columns]
    pivot_df = pivot_df.reset_index()
    # Merge the pivot DataFrame with the ground truth DataFrame
    merged_df = pd.merge(pivot_df, gt_df, on='query_name', how='inner')
    # Calculate relative error for each 'cd' column
    cd_columns = [col for col in merged_df.columns if col.startswith('cd_')]
    for cd_col in cd_columns:
        merged_df[f'RE_{cd_col}'] = ((merged_df[cd_col] - merged_df['CD_GT']).abs() / merged_df['CD_GT']) * 100

    # Save the results to a new CSV file
    merged_df.to_csv(output_file, index=False)
    print(f"Results saved to {output_file}")

if __name__ == '__main__':
    main(sys.argv[1], sys.argv[2],sys.argv[3] if len(sys.argv) > 3 else False)