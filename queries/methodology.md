# Queries
As there is no dedicated benchmark for count-distinct aggregate queries, we used
the synthetic benchmark  WatDiv and the real-world benchmark WDBench to build two workloads.
The WatDiv workload consists of 60  queries, and  WDBench workload consists of 43 queries. 
   Watdiv queries range from 1 to 12 triple patterns, while  WDBench queries have 1 to 5 triple patterns.

## For Watdiv
From the original 12400 queries, we selected queries as follows:
  
 - We transformed WatDiv's original queries into count aggregate queries.
- We pruned queries that time out after 30 seconds.
- We grouped queries per number of triple patterns and kept only the top 5 queries regarding the number of results, as these are the most challenging for count-distinct.
 - We transformed these queries into count-distinct aggregate queries. Using query variables, we rewrote the queries into several count-distinct aggregate queries and computed the number of distinct results per variable.
 - For each group of queries, we selected the top 5 queries in terms of distinct results.

 ## For WDBench
 From the 681 queries with multiple BPGs, we selected 30
  queries as follows:
-  Since WDBench queries could have a cartesian product,  we pruned these queries, resulting in 260 queries.
 -  We transformed the 260 queries into count aggregate queries. We selected the top 30 queries regarding the number of results.
   As for WatDiv, we transformed queries into count-distinct aggregate queries and selected those with the highest number of distinct results.


  For both workloads, we added single triple pattern queries from
  SPORTAL: QB3 (count-distinct predicates), QB4
  (count-distinct subjects),  and QB5 (count-distinct objects), which are the most basic VoID (https://www.w3.org/TR/void/).  From SPORTAL, we
  also added 10 queries that count distinct objects per class for the
  top 10 classes with the highest number of distinct objects per class.
