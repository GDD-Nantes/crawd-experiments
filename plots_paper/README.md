# CRAWD vs Chao_lee  with BGP queries 

## QC6 experiment 

We rely on queries like [QC6](/count-distinct-watdiv/queries/VOID/c6_pc10.sparql)

CRAWD vs Chao_lee_Fµ on top ten classes of WatDiv,DBPedia and WDBench
Results after 10% sampling of datasize for WatDiv and DBpedia, 1% for WDBnech

![WC6_o](/plots_paper/aggregate_top_10.png)

# Main BGP experiment

* Variation of relative error according the budget configuration for WatDiv10M and WDBench:

![wagg2](/plots_paper/watdiv_agg_2.png)
![wdagg](/plots_paper/wdbench_agg_2.png)

* Relative error queries on config 9: b=1M / bµ= 10*#tp

![wperq](/plots_paper/watdiv_per_query.png)
![wdperq](/plots_paper/wdbench_per_query.png)

