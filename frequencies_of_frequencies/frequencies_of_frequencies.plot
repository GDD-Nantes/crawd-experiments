# gnuplot -e "set output 'watdiv_fof_spo_o.png'" -e "datafile='watdiv_fof_spo_o.csv'" frequencies_of_frequencies.plot  
set terminal png size 1350,800
set termoption enhanced
#set output "frequencies_of_frequencies.png"


set datafile separator " "

stats datafile using 1 nooutput
max_y = STATS_max + 0.5
sum_x = STATS_sum

stats datafile using 0 nooutput
max_x = STATS_max + 1

y_tics = ceil(max_y/10**int(log10(max_y))*10/5) *10**int(log10(max_y))/10
y_tics = y_tics < 1 ? 1 : y_tics

set ytics y_tics
set grid ytics
set yrange [0:max_y]
set xrange [0:max_x]
# set boxwidth 1
set style fill solid 0.5

set xlabel "n^t^h distinct frequency"
set ylabel "number of terms that have this frequency"

# plot "watdiv_fof_spo_o.csv" u ($0+1):1 w lp
plot datafile u ($0+0.5):1 w boxes  title sprintf("Query with %i distinct frequencies for %i distinct elements.", max_x, sum_x)