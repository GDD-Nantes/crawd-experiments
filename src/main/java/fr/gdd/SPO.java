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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class SPO {
    static JenaBackend backend = null;
    public static void CDs(String PathToTDB2Dataset, String outputFile, Integer samplesize) throws IOException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(outputFile))) {
            writer.println("Subject CRWD Chao_Lee");
            JenaBackend backend = new JenaBackend(PathToTDB2Dataset);
            ProgressJenaIterator.NB_WALKS = 1;
            ProgressJenaIterator spo = (ProgressJenaIterator) ((LazyIterator) backend.search(backend.any(), backend.any(), backend.any())).iterator;


            int i = 1;
            double sum_s = 0.;
            double sum_p_for_N = 0.;
            ChaoLee chaoLee = new ChaoLee();
            while(i<(samplesize+1)){

                Pair<Tuple<NodeId>, Double> sporwR = spo.getUniformRandomSPOWithProbability();
                Tuple<NodeId> sporw = sporwR.getLeft();
                NodeId sub = sporw.get(SPOC.SUBJECT);
                String subject = backend.getValue(sub);

                LazyIterator s = (LazyIterator) backend.search(sub, backend.any(), backend.any());
                ProgressJenaIterator sR = (ProgressJenaIterator) s.iterator;
                double count = sR.cardinality();
                sum_s += (1/ count);
                sum_p_for_N += (1/ sporwR.getRight());
                double estimateS = ((sum_p_for_N/i)/i) * sum_s ;

                chaoLee.fixN(sum_p_for_N/i).add(new ChaoLee.ChaoLeeSample(Set.of(sub), sporwR.getRight(), count));


                writer.printf("%s %f %f%n", subject,estimateS, chaoLee.getEstimate());
                i++;
            }

        }
    }
    public static void CDs_dbpedia(String PathToTDB2Dataset, String outputFile, Integer samplesize) throws IOException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(outputFile))) {
            writer.println("Subject CRWD Chao_Lee");
            JenaBackend backend = new JenaBackend(PathToTDB2Dataset);

            ProgressJenaIterator.NB_WALKS = 1;
            NodeId graph = backend.getId("<http://example.com/dbpedia>", SPOC.GRAPH);
            ProgressJenaIterator spo = (ProgressJenaIterator) ((LazyIterator) backend.search(backend.any(), backend.any(), backend.any(), graph)).iterator;

            int i = 1;
            double sum_s = 0.;
            double sum_p_for_N = 0.;
            ChaoLee chaoLee = new ChaoLee();
            while (i<(samplesize+1)) {
                Pair<Tuple<NodeId>, Double> sporwR = spo.getUniformRandomSPOWithProbability();
                Tuple<NodeId> sporw = sporwR.getLeft();
                NodeId sub = sporw.get(SPOC.SUBJECT);
                String subject = backend.getValue(sub);

                LazyIterator s = (LazyIterator) backend.search(sub, backend.any(), backend.any(),graph);
                ProgressJenaIterator sR = (ProgressJenaIterator) s.iterator;
                double count = sR.cardinality();
                sum_s += (1/ count);
                sum_p_for_N += (1/ sporwR.getRight());
                double estimateS = ((sum_p_for_N/i)/i) * sum_s ;
                chaoLee.fixN(sum_p_for_N/i).add(new ChaoLee.ChaoLeeSample(Set.of(sporw.get(1)),sporwR.getRight(), count));

                writer.printf("%s %f %f%n", subject,estimateS, chaoLee.getEstimate());
                i++;
            }
        }
    }
    public static void CDp(String PathToTDB2Dataset, String outputFile, Integer samplesize) throws IOException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(outputFile))) {
            writer.println("Predicate CRWD Chao_Lee");
            JenaBackend backend = new JenaBackend(PathToTDB2Dataset);
            ProgressJenaIterator.NB_WALKS = 1;
            ProgressJenaIterator spo = (ProgressJenaIterator) ((LazyIterator) backend.search(backend.any(), backend.any(), backend.any())).iterator;

            int i = 1;
            double sum_p = 0.;
            double sum_p_for_N = 0.;
            ChaoLee chaoLee = new ChaoLee();
            double count = 0;
            HashMap<NodeId,Double> predicateCount = new HashMap<>();
            while(i<(samplesize+1)){
                Pair<Tuple<NodeId>, Double> sporwR = spo.getUniformRandomSPOWithProbability();
                NodeId pId = sporwR.getLeft().get(1);
                String predicate = backend.getValue(pId);
                if (predicateCount.containsKey(pId)) {
                    count = predicateCount.get(pId);
                }else{
                    LazyIterator p = (LazyIterator) backend.search(backend.any(),pId, backend.any());
                    ProgressJenaIterator pR = (ProgressJenaIterator) p.iterator;
                    count =  pR.count();
                    predicateCount.put(pId,count);
                }
                sum_p += (1/ count);
                sum_p_for_N += (1 / sporwR.getRight());
                double estimateP = ((sum_p_for_N/i)/i) * sum_p;
                chaoLee.fixN(sum_p_for_N/i).add(new ChaoLee.ChaoLeeSample(Set.of(pId),sporwR.getRight(), count));
                writer.printf("%s %f %f%n", predicate, estimateP, chaoLee.getEstimate());
                i++;
            }

        }
    }


    public static void CDp_dbpedia(String PathToTDB2Dataset, String outputFile, Integer samplesize) throws IOException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(outputFile))) {
            writer.println("Predicate CRWD Chao_Lee");
            JenaBackend backend = new JenaBackend(PathToTDB2Dataset);
            ProgressJenaIterator.NB_WALKS = 10;
            NodeId graph = backend.getId("<http://example.com/dbpedia>", SPOC.GRAPH);
            ProgressJenaIterator spo = (ProgressJenaIterator) ((LazyIterator) backend.search(backend.any(), backend.any(), backend.any(),graph)).iterator;
            int i = 1;
            double sum_p = 0.;
            double sum_p_for_N = 0.;
            ChaoLee chaoLee = new ChaoLee();
            HashMap<NodeId,Double> predicateCount = new HashMap<>();
            double count = 0;
            while(i<(samplesize+1)){
                Pair<Tuple<NodeId>, Double> sporwR = spo.getUniformRandomSPOWithProbability();
                NodeId pId = sporwR.getLeft().get(1);
                String predicate = backend.getValue(pId);
                if (predicateCount.containsKey(pId)) {
                    count = predicateCount.get(pId);
                }else{
                LazyIterator p = (LazyIterator) backend.search(backend.any(),pId, backend.any(),graph);
                ProgressJenaIterator pR = (ProgressJenaIterator) p.iterator;
                count =  pR.count();
                predicateCount.put(pId,count);
                }
                sum_p += (1/ count);
                sum_p_for_N += (1 / sporwR.getRight());
                double estimateP = ((sum_p_for_N/i)/i) * sum_p;
                chaoLee.fixN(sum_p_for_N/i).add(new ChaoLee.ChaoLeeSample(Set.of(pId),sporwR.getRight(), count));
                writer.printf("%s %f %f%n", predicate, estimateP, chaoLee.getEstimate());
                i++;
            }

        }
    }
    public static void CDo(String PathToTDB2Dataset, String outputFile, Integer samplesize) throws IOException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(outputFile))) {
            writer.println("Object CRWD Chao_Lee");
            JenaBackend backend = new JenaBackend(PathToTDB2Dataset);
            ProgressJenaIterator.NB_WALKS = 1;
            ProgressJenaIterator spo = (ProgressJenaIterator) ((LazyIterator) backend.search(backend.any(), backend.any(), backend.any())).iterator;
            int i = 1;
            double sum_o = 0.;
            double sum_o_for_N = 0.;
            ChaoLee chaoLee = new ChaoLee();
            while(i<(samplesize+1)){
                Pair<Tuple<NodeId>, Double> sporwR = spo.getUniformRandomSPOWithProbability();
                NodeId oId = sporwR.getLeft().get(2);
                String object = backend.getValue(oId);
                LazyIterator o = (LazyIterator) backend.search(backend.any(), backend.any(),oId);
                ProgressJenaIterator oR = (ProgressJenaIterator) o.iterator;
                double count =  oR.cardinality();
                sum_o += (1/ count);
                sum_o_for_N += (1 / sporwR.getRight());
                double estimateO = ((sum_o_for_N/i)/i) * sum_o;
                chaoLee.fixN(sum_o_for_N/i).add(new ChaoLee.ChaoLeeSample(Set.of(oId),sporwR.getRight(), count));
                writer.printf("%s %f %f%n", object, estimateO, chaoLee.getEstimate());
                i++;
            }
        }
    }
    public static void CDo_dbpedia(String PathToTDB2Dataset, String outputFile, Integer samplesize) throws IOException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(outputFile))) {
            writer.println("Object CRWD Chao_Lee");
            JenaBackend backend = new JenaBackend(PathToTDB2Dataset);

            ProgressJenaIterator.NB_WALKS = 1;
            NodeId graph = backend.getId("<http://example.com/dbpedia>", SPOC.GRAPH);
            ProgressJenaIterator spo = (ProgressJenaIterator) ((LazyIterator) backend.search(backend.any(), backend.any(), backend.any(),graph)).iterator;
            int i = 1;
            double sum_o = 0.;
            double sum_o_for_N = 0.;
            ChaoLee chaoLee = new ChaoLee();

            while(i<(samplesize+1)){
                Pair<Tuple<NodeId>, Double> sporwR = spo.getUniformRandomSPOWithProbability();
                NodeId oId = sporwR.getLeft().get(2);
                String object = backend.getValue(oId);

                LazyIterator o = (LazyIterator) backend.search(backend.any(), backend.any(),oId,graph);
                ProgressJenaIterator oR = (ProgressJenaIterator) o.iterator;
                double count =  oR.cardinality();
                sum_o += (1/ count);
                sum_o_for_N += (1 / sporwR.getRight());
                double estimateO = ((sum_o_for_N/i)/i) * sum_o;
                chaoLee.fixN(sum_o_for_N/i).add(new ChaoLee.ChaoLeeSample(Set.of(oId),sporwR.getRight(), count));
                writer.printf("%s %f %f%n", object, estimateO, chaoLee.getEstimate());
                i++;
            }
        }
    }

}
