# CRAWD vs Chao_lee  
## On single triple pattern queries( VoID queries)
### QB3: count distinct predicates in dataset
![spo_p](/plots_paper/CDp_exact_merged.png)
### QB4: count distinct subjects in dataset
![spo_s](/plots_paper/CDs_merged.png)
### QB5: count distinct objects in dataset
![spo_o](/plots_paper/CDo_merged.png)
## with BGP queries 
### QC6

We rely on queries like [QC6](/count-distinct-watdiv/queries/VOID/c6_pc10.sparql)

CRAWD vs Chao_lee_Fµ on top ten classes of WatDiv,DBPedia and WDBench
Results after 10% sampling of datasize for WatDiv and DBpedia, 1% for WDBnech

![WC6_o](/plots_paper/aggregate_top_10.png)

# Main BGP experiment

* Variation of relative error according the budget configuration for WatDiv10M:

![wagg2](/plots_paper/watdiv_agg_2.png)

* WDBench:

![wdagg](/plots_paper/wdbench_agg_2.png)

* Relative error queries on config 9 for WatDiv10M: b=1M / bµ= 10*#tp

![wperq](/plots_paper/watdiv_per_query.png)

* WDBench:
![wdperq](/plots_paper/wdbench_per_query.png)

