import pandas as pd
import os

def extract_name1(filename):
    return filename.split('/')[-1]

def extract_name2(filename):
    # Extract last part of each element in the tuple
    parts = [url.rsplit('/', 1)[-1] for url in filename]
    parts = [part.replace('/', '_').replace('.', '_') for part in parts]
    result = '_'.join(parts)
    return result
def groupby_class(filename):

    df = pd.read_csv(filename)
    input_parent_dir = os.path.dirname(filename)

    grouped_by_class = df.groupby('Class', sort=False)
    for name, group in grouped_by_class:
        class_name = extract_name1(name)
        output_filename = os.path.join(input_parent_dir, f'{class_name}.csv')
        group.to_csv(output_filename, index=False)

def groupby_class_predicate(filename):

    df = pd.read_csv(filename)
    input_parent_dir = os.path.dirname(filename)

    grouped_by_class = df.groupby(['Class','Predicate'], sort=False)
    for name, group in grouped_by_class:
        new_name = extract_name2(name)

        output_filename = os.path.join(input_parent_dir, f'{new_name}.csv')
        group.to_csv(output_filename, index=False)
if __name__ == '__main__':
    #groupby_class('/GDD/Thi/count-distinct-sampling/watdiv/sample/groupby_class/CDs_proba/SACSPO_CDs_proba.csv')

    groupby_class('/GDD/Thi/count-distinct-sampling/watdiv/sample/groupby_class/CDs/SACSPO_CDs.csv')
    groupby_class('/GDD/Thi/count-distinct-sampling/watdiv/sample/groupby_class/CDo/SACSPO_CDo.csv')

    #groupby_class_predicate('/GDD/Thi/count-distinct-sampling/watdiv/sample/groupby_class_predicate/CDo/SACSPO_CDo.csv')
    #groupby_class_predicate('/GDD/Thi/count-distinct-sampling/watdiv/sample/groupby_class_predicate/CDs/SACSPO_CDs.csv')
