import csv
import os
import re
import argparse

base_dirs = ["./count-distinct-wdbench/wdbench/CRAWD", "./count-distinct-wdbench/wdbench/CHAOLEE"]
csv_output_dir = "1query_1csv"

def collect_data_from_1_query_CRAWD(query_file, csv_file):
    with open(csv_file, 'w', newline='') as f:
        writer = csv.writer(f)
        writer.writerow([
             'query_name','Config', 'Run',
            'WJ_SampleSize', 'CRAWD_SampleSize', '∑Fµ_SampleSize', '∑Fµ_success', '∑Fµ_fail',
            'N̂', 'Nb_Total_Scans', 'Execution_time', 'cd'
        ])
        path_parts = query_file.split(os.path.sep)
        config_dir = path_parts[-2]
        config = int(config_dir.replace("config", ""))
        query_name = path_parts[-1].split('.')[0]
        print(f"Processing {query_file}")

        with open(query_file, 'r') as result_file:
            contents = result_file.read()
            block_pattern = r"\[fr\.gdd\.sage\.rawer\.cli\.RawerCLI\.main\(\)\] DEBUG CountDistinctCRAWD - (?:WJ SampleSize|CRAWD SampleSize|∑Fµ SampleSize|∑Fµ success|∑Fµ fail|N̂|Nb Total Scans): .+?\n\[fr\.gdd\.sage\.rawer\.cli\.RawerCLI\.main\(\)\] DEBUG CountDistinctCRAWD - (?:WJ SampleSize|CRAWD SampleSize|∑Fµ SampleSize|∑Fµ success|∑Fµ fail|N̂|Nb Total Scans): .+?\n\[fr\.gdd\.sage\.rawer\.cli\.RawerCLI\.main\(\)\] DEBUG CountDistinctCRAWD - (?:WJ SampleSize|CRAWD SampleSize|∑Fµ SampleSize|∑Fµ success|∑Fµ fail|N̂|Nb Total Scans): .+?\n\[fr\.gdd\.sage\.rawer\.cli\.RawerCLI\.main\(\)\] DEBUG CountDistinctCRAWD - (?:WJ SampleSize|CRAWD SampleSize|∑Fµ SampleSize|∑Fµ success|∑Fµ fail|N̂|Nb Total Scans): .+?\n\[fr\.gdd\.sage\.rawer\.cli\.RawerCLI\.main\(\)\] DEBUG CountDistinctCRAWD - (?:WJ SampleSize|CRAWD SampleSize|∑Fµ SampleSize|∑Fµ success|∑Fµ fail|N̂|Nb Total Scans): .+?\n\[fr\.gdd\.sage\.rawer\.cli\.RawerCLI\.main\(\)\] DEBUG CountDistinctCRAWD - (?:WJ SampleSize|CRAWD SampleSize|∑Fµ SampleSize|∑Fµ success|∑Fµ fail|N̂|Nb Total Scans): .+?\n\[fr\.gdd\.sage\.rawer\.cli\.RawerCLI\.main\(\)\] DEBUG CountDistinctCRAWD - (?:WJ SampleSize|CRAWD SampleSize|∑Fµ SampleSize|∑Fµ success|∑Fµ fail|N̂|Nb Total Scans): .+?\n\{.+\nExecution time:.+\nNumber of Results:.+\n"
            blocks = re.findall(block_pattern, contents)
            run_data_blocks = blocks[:5]

            for run_number, run_data_block in enumerate(run_data_blocks, 1):
                run_data = run_data_block.strip()
                #print(run_data)
                WJ_SampleSize = re.search(r'\[fr\.gdd\.sage\.rawer\.cli\.RawerCLI\.main\(\)\] DEBUG CountDistinctCRAWD - WJ SampleSize: (\d+\.?\d*)',run_data).group(1)
                CRAWD_SampleSize = re.search(r'\[fr\.gdd\.sage\.rawer\.cli\.RawerCLI\.main\(\)\] DEBUG CountDistinctCRAWD - CRAWD SampleSize: (\d+\.?\d*)',run_data).group(1)
                Sum_F_mu_SampleSize = re.search(r'\[fr\.gdd\.sage\.rawer\.cli\.RawerCLI\.main\(\)\] DEBUG CountDistinctCRAWD - ∑Fµ SampleSize: (\d+\.?\d*)',run_data).group(1)
                Sum_F_mu_success = re.search(r'\[fr\.gdd\.sage\.rawer\.cli\.RawerCLI\.main\(\)\] DEBUG CountDistinctCRAWD - ∑Fµ success: (\d+\.?\d*)',run_data).group(1)
                Sum_F_mu_fail = re.search(r'\[fr\.gdd\.sage\.rawer\.cli\.RawerCLI\.main\(\)\] DEBUG CountDistinctCRAWD - ∑Fµ fail: (\d+\.?\d*)',run_data).group(1)
                N_hat = re.search( r'\[fr\.gdd\.sage\.rawer\.cli\.RawerCLI\.main\(\)\] DEBUG CountDistinctCRAWD - N̂:\s*(\d+\.\d+([eE][+-]?\d+)?)',run_data).group(1)
                Nb_Total_Scans = re.search(r'\[fr\.gdd\.sage\.rawer\.cli\.RawerCLI\.main\(\)\] DEBUG CountDistinctCRAWD - Nb Total Scans: (\d+\.?\d*)',run_data).group(1)
                Execution_time = re.search(r"Execution time:\s*(\d+) ms", run_data).group(1).strip()
                Result_cd = re.search(r"{\?cd->\s*\"(.*?)\"\^\^.*", run_data).group(1).strip()
                writer.writerow([
                    query_name,config, run_number,
                    float(WJ_SampleSize), float(CRAWD_SampleSize), float(Sum_F_mu_SampleSize), float(Sum_F_mu_success), float(Sum_F_mu_fail),
                    N_hat, Nb_Total_Scans, Execution_time,round(float(Result_cd[1:]))
                ])
                #print(WJ_SampleSize, CRAWD_SampleSize, Sum_F_mu_SampleSize, Sum_F_mu_success, Sum_F_mu_fail, N_hat, Nb_Total_Scans, Execution_time, round(float(Result_cd[1:])))
def collect_data_from_1_query_CHAOLEE(query_file, csv_file):
    with open(csv_file, 'w', newline='') as f:
        writer = csv.writer(f)
        writer.writerow([
             'query_name','Config', 'Run',
            'BigN_SampleSize', 'CHAOLEE_SampleSize', 'Nb_Total_Scans', 'Execution_time', 'cd'
        ])
        path_parts = query_file.split(os.path.sep)
        config_dir = path_parts[-2]
        config = int(config_dir.replace("config", ""))
        query_name = path_parts[-1].split('.')[0]
        print(f"Processing {query_file}")

        with open(query_file, 'r') as result_file:
            contents = result_file.read()
            block_pattern = re.compile(r"\[fr\.gdd\.sage\.rawer\.cli\.RawerCLI\.main\(\)\] DEBUG CountDistinctChaoLee - BigN SampleSize: \d+\.\d+\n\[fr\.gdd\.sage\.rawer\.cli\.RawerCLI\.main\(\)\] DEBUG CountDistinctChaoLee - ChaoLee SampleSize: \d+\n\[fr\.gdd\.sage\.rawer\.cli\.RawerCLI\.main\(\)\] DEBUG CountDistinctChaoLee - Nb Total Scans: \d+\n\{.+\nExecution time:\s+\d+ ms\nNumber of Results:\s+\d+")
            blocks = re.findall(block_pattern, contents)
            run_data_blocks = blocks[:5]
            #print(run_data_blocks)
            for run_number, run_data_block in enumerate(run_data_blocks, 1):
                run_data = run_data_block.strip()
                #print(run_data)
                BigN_SampleSize = re.search(r'\[fr\.gdd\.sage\.rawer\.cli\.RawerCLI\.main\(\)\] DEBUG CountDistinctChaoLee - BigN SampleSize: (\d+\.?\d*)',run_data).group(1)
                CHAOLEE_SampleSize = re.search(r'\[fr\.gdd\.sage\.rawer\.cli\.RawerCLI\.main\(\)\] DEBUG CountDistinctChaoLee - ChaoLee SampleSize: (\d+\.?\d*)',run_data).group(1)
                #Sum_F_mu_SampleSize = re.search(r'\[fr\.gdd\.sage\.rawer\.cli\.RawerCLI\.main\(\)\] DEBUG CountDistinctChaoLee - ∑Fµ SampleSize: (\d+\.?\d*)',run_data).group(1)
                #Sum_F_mu_success = re.search(r'\[fr\.gdd\.sage\.rawer\.cli\.RawerCLI\.main\(\)\] DEBUG CountDistinctChaoLee - ∑Fµ success: (\d+\.?\d*)',run_data).group(1)
                #Sum_F_mu_fail = re.search(r'\[fr\.gdd\.sage\.rawer\.cli\.RawerCLI\.main\(\)\] DEBUG CountDistinctChaoLee - ∑Fµ fail: (\d+\.?\d*)',run_data).group(1)
                #N_hat = re.search( r'\[fr\.gdd\.sage\.rawer\.cli\.RawerCLI\.main\(\)\] DEBUG CountDistinctChaoLee - N̂:\s*(\d+\.\d+([eE][+-]?\d+)?)',run_data).group(1)
                Nb_Total_Scans = re.search(r'\[fr\.gdd\.sage\.rawer\.cli\.RawerCLI\.main\(\)\] DEBUG CountDistinctChaoLee - Nb Total Scans: (\d+\.?\d*)',run_data).group(1)
                Execution_time = re.search(r"Execution time:\s*(\d+) ms", run_data).group(1).strip()
                Result_cd = re.search(r"{\?cd->\s*\"(.*?)\"\^\^.*", run_data).group(1).strip()
                writer.writerow([
                    query_name,config, run_number,
                    float(BigN_SampleSize), float(CHAOLEE_SampleSize), \
                    #float(Sum_F_mu_SampleSize), float(Sum_F_mu_success), float(Sum_F_mu_fail),N_hat,
                     Nb_Total_Scans, Execution_time,round(float(Result_cd[1:]))
                ])

def main():
    parser = argparse.ArgumentParser(description="Data collection script.")
    parser.add_argument("--dataset", choices=["CRAWD", "CHAOLEE", "BOTH"], default="BOTH",
                        help="The dataset to process (CRAWD, CHAOLEE, or BOTH - default)")
    args = parser.parse_args()

    for base_dir in base_dirs:
        for sub_dir in ["top30q", "void_queries"]:
            for config in range(1, 10):
                config_dir = f"config{config}"
                path = os.path.join(base_dir, sub_dir, config_dir)
                os.makedirs(os.path.join( base_dir,csv_output_dir, sub_dir, config_dir), exist_ok=True)
                for filename in os.listdir(path):
                    if filename.endswith(".result"):
                        query_file = os.path.join(path, filename)
                        query_name = filename.split('.')[0]
                        csv_file = os.path.join(base_dir,csv_output_dir, sub_dir, config_dir, f"{query_name}.csv")
                        if args.dataset in ["CRAWD", "BOTH"] and "CRAWD" in base_dir:
                            collect_data_from_1_query_CRAWD(query_file, csv_file)
                        if args.dataset in ["CHAOLEE", "BOTH"] and "CHAOLEE" in base_dir:
                            collect_data_from_1_query_CHAOLEE(query_file, csv_file)


if __name__ == "__main__":
    main()
