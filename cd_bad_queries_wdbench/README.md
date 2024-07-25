# Bad queries of WDBench

CRAWD works well on most queries of WDBench. While increasing the
budget would eventually be enough to solve all issues, some of the
problematic queries only need to find a better join ordering:
[`query_407`](./queries/query_407.sparql) and
[`query_637`](./queries/query_637.sparql).

Currently, CRAWD exploits a basic heuristic based on each triple
pattern cardinality. By adding a `--force-order` parameter, we are
able to force the join order of the count-distinct query and
subsequent count queries to that of the provided `--query` or
`--file`.

To run the experiment, we use [snakemake](https://snakemake.github.io/):
`snakemake -c1 just_do_it.dat`. The results are located in the [results directory](./results).
