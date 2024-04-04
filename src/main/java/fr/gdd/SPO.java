package fr.gdd;

import fr.gdd.estimators.ChaoLee;
import fr.gdd.sage.generics.LazyIterator;
import fr.gdd.sage.interfaces.SPOC;
import fr.gdd.sage.jena.JenaBackend;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.jena.atlas.lib.tuple.Tuple;
import org.apache.jena.dboe.trans.bplustree.ProgressJenaIterator;
import org.apache.jena.tdb2.store.NodeId;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class SPO {
    static JenaBackend backend = null;
    public static void CDs(String PathToTDB2Dataset, String outputFile, Integer samplesize) throws IOException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(outputFile))) {
            writer.println("Subject,CD");
            JenaBackend backend = new JenaBackend(PathToTDB2Dataset);
            ProgressJenaIterator.rng = new Random(2);
            ProgressJenaIterator.NB_WALKS = 1;
            ProgressJenaIterator spo = (ProgressJenaIterator) ((LazyIterator) backend.search(backend.any(), backend.any(), backend.any())).iterator;
            //double N = spo.cardinality();

            int i = 1;
            double sum_s = 0.;
            double sum_p_for_N = 0.;
            ChaoLee chaoLee = new ChaoLee();
            while(i<(samplesize+1)){
                //perform CRWD
                Pair<Tuple<NodeId>, Double> sporwR = spo.getUniformRandomSPOWithProbability();
                Tuple<NodeId> sporw = sporwR.getLeft();
                NodeId sub = sporw.get(SPOC.SUBJECT);
                String subject = backend.getValue(sub);

                LazyIterator s = (LazyIterator) backend.search(sub, backend.any(), backend.any());
                ProgressJenaIterator sR = (ProgressJenaIterator) s.iterator;
                sum_s += (1/ sR.count());
                sum_p_for_N += (1/ sporwR.getRight());
                double estimateS = ((sum_p_for_N/i)/i) * sum_s ;

                chaoLee.fixN(sum_p_for_N/i).add(new ChaoLee.ChaoLeeSample(Set.of(sub), sporwR.getRight(), sR.count()));

                writer.printf("%s,%f%n", subject, chaoLee.getEstimate());
                //System.out.println(estimateS);
                i++;
            }

        }
    }
    public static void CDs_dbpedia(String PathToTDB2Dataset, String outputFile, Integer samplesize) throws IOException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(outputFile))) {
            writer.println("Subject,CRWD,Chao_Lee");
            JenaBackend backend = new JenaBackend(PathToTDB2Dataset);
            ProgressJenaIterator.rng = new Random(2);
            ProgressJenaIterator.NB_WALKS = 1;
            NodeId graph = backend.getId("<http://example.com/dbpedia>", SPOC.GRAPH);
            ProgressJenaIterator spo = (ProgressJenaIterator) ((LazyIterator) backend.search(backend.any(), backend.any(), backend.any(), graph)).iterator;
            //double N = spo.cardinality();
            int i = 1;
            double sum_s = 0.;
            double sum_p_for_N = 0.;
            while (i<(samplesize+1)) {
                Pair<Tuple<NodeId>, Double> sporwR = spo.getUniformRandomSPOWithProbability();
                Tuple<NodeId> sporw = sporwR.getLeft();
                String subject = backend.getValue(sporw.get(1));

                LazyIterator s = (LazyIterator) backend.search(graph,sporw.get(SPOC.SUBJECT), backend.any(), backend.any());
                ProgressJenaIterator sR = (ProgressJenaIterator) s.iterator;
                sum_s += (1 / sR.count());
                sum_p_for_N += (1 / sporwR.getRight());
                double estimateS = ((sum_p_for_N / i) / i) * sum_s;
                writer.printf("%s,%f%n", subject, estimateS);
                i++;
            }
        }
    }
    public static void CDp(String PathToTDB2Dataset, String outputFile, Integer samplesize) throws IOException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(outputFile))) {
            writer.println("Predicate,CRWD,Chao_Lee");
            JenaBackend backend = new JenaBackend(PathToTDB2Dataset);
            ProgressJenaIterator.rng = new Random(2);
            ProgressJenaIterator.NB_WALKS = 1;
            ProgressJenaIterator spo = (ProgressJenaIterator) ((LazyIterator) backend.search(backend.any(), backend.any(), backend.any())).iterator;
            //double N = spo.cardinality();
            int i = 1;
            double sum_p = 0.;
            double sum_p_for_N = 0.;
            while(i<(samplesize+1)){
                Pair<Tuple<NodeId>, Double> sporwR = spo.getUniformRandomSPOWithProbability();

                String predicate = backend.getValue(sporwR.getLeft().get(1));
                LazyIterator p = (LazyIterator) backend.search(backend.any(),sporwR.getLeft().get(SPOC.PREDICATE), backend.any());
                ProgressJenaIterator pR = (ProgressJenaIterator) p.iterator;
                sum_p += (1/ pR.cardinality());
                sum_p_for_N += (1 / sporwR.getRight());
                double estimateP = ((sum_p_for_N/i)/i) * sum_p;
                writer.printf("%s,%f%n", predicate, estimateP);
                i++;
            }

        }
    }
    public static void CDp_dbpedia(String PathToTDB2Dataset, String outputFile, Integer samplesize) throws IOException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(outputFile))) {
            writer.println("Predicate,CRWD,Chao_Lee");
            JenaBackend backend = new JenaBackend(PathToTDB2Dataset);
            ProgressJenaIterator.rng = new Random(2);
            ProgressJenaIterator.NB_WALKS = 1;
            NodeId graph = backend.getId("<http://example.com/dbpedia>", SPOC.GRAPH);
            ProgressJenaIterator spo = (ProgressJenaIterator) ((LazyIterator) backend.search(backend.any(), backend.any(), backend.any(),graph)).iterator;
            int i = 1;
            double sum_p = 0.;
            double sum_p_for_N = 0.;
            while(i<(samplesize+1)){
                Pair<Tuple<NodeId>, Double> sporwR = spo.getUniformRandomSPOWithProbability();

                String predicate = backend.getValue(sporwR.getLeft().get(2));
                LazyIterator p = (LazyIterator) backend.search(graph,backend.any(),sporwR.getLeft().get(SPOC.PREDICATE), backend.any());
                ProgressJenaIterator pR = (ProgressJenaIterator) p.iterator;
                sum_p += (1/ pR.cardinality());
                sum_p_for_N += (1 / sporwR.getRight());
                double estimateP = ((sum_p_for_N/i)/i) * sum_p;
                writer.printf("%s,%f%n", predicate, estimateP);
                i++;
            }

        }
    }
    public static void CDo(String PathToTDB2Dataset, String outputFile, Integer samplesize) throws IOException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(outputFile))) {
            writer.println("Object,CRWD,Chao_Lee");
            JenaBackend backend = new JenaBackend(PathToTDB2Dataset);
            ProgressJenaIterator.rng = new Random(2);
            ProgressJenaIterator.NB_WALKS = 1;
            ProgressJenaIterator spo = (ProgressJenaIterator) ((LazyIterator) backend.search(backend.any(), backend.any(), backend.any())).iterator;
            //double N = spo.cardinality();
            int i = 1;
            double sum_o = 0.;
            double sum_o_for_N = 0.;
            while(i<(samplesize+1)){
                Pair<Tuple<NodeId>, Double> sporwR = spo.getUniformRandomSPOWithProbability();

                String object = backend.getValue(sporwR.getLeft().get(2));
                LazyIterator o = (LazyIterator) backend.search(backend.any(), backend.any(),sporwR.getLeft().get(2));
                ProgressJenaIterator oR = (ProgressJenaIterator) o.iterator;
                sum_o += (1/ oR.cardinality());
                sum_o_for_N += (1 / sporwR.getRight());
                double estimateO = ((sum_o_for_N/i)/i) * sum_o;
                writer.printf("%s,%f%n", object, estimateO);
                i++;
            }
        }
    }
    public static void CDo_dbpedia(String PathToTDB2Dataset, String outputFile, Integer samplesize) throws IOException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(outputFile))) {
            writer.println("Object,CRWD,Chao_Lee");
            JenaBackend backend = new JenaBackend(PathToTDB2Dataset);
            ProgressJenaIterator.rng = new Random(2);
            ProgressJenaIterator.NB_WALKS = 1;
            NodeId graph = backend.getId("<http://example.com/dbpedia>", SPOC.GRAPH);
            ProgressJenaIterator spo = (ProgressJenaIterator) ((LazyIterator) backend.search(backend.any(), backend.any(), backend.any(),graph)).iterator;
            //double N = spo.cardinality();
            int i = 1;
            double sum_o = 0.;
            double sum_o_for_N = 0.;
            while(i<(samplesize+1)){
                Pair<Tuple<NodeId>, Double> sporwR = spo.getUniformRandomSPOWithProbability();

                String object = backend.getValue(sporwR.getLeft().get(3));
                LazyIterator o = (LazyIterator) backend.search(graph,backend.any(), backend.any(),sporwR.getLeft().get(3));
                ProgressJenaIterator oR = (ProgressJenaIterator) o.iterator;
                sum_o += (1/ oR.cardinality());
                sum_o_for_N += (1 / sporwR.getRight());
                double estimateO = ((sum_o_for_N/i)/i) * sum_o;
                writer.printf("%s,%f%n", object, estimateO);
                i++;
            }
        }
    }

}
