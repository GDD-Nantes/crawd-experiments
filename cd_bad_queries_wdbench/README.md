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

To run the experiment, do not forget to symlink
`rawer-jar-with-dependencies.jar` and `wdbench.jnl` in this directory. Then, we use
[snakemake](https://snakemake.github.io/):
`snakemake -c1 just_do_it.dat`. 
The results are located in the [results directory](./results).

Results are much better, even on the smallest 10k-scans budget:

- [X] For `query_407`, the expected value is 66053832. 
      CRAWD's estimate is 65254568 with a sublimit of 4 scans (1.2% mean relative error (vs 96% without force order)).
- [X] For `query_637`, the expected value is 27994204.
      CRAWD's estimate is 27814624 with a sublimit of 4 scans (0.64% mean relative error (vs 100% without force order)).
