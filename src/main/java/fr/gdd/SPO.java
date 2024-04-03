package fr.gdd;

import com.github.jsonldjava.core.RDFDataset;
import fr.gdd.sage.generics.LazyIterator;
import fr.gdd.sage.interfaces.BackendIterator;
import fr.gdd.sage.interfaces.SPOC;
import fr.gdd.sage.jena.JenaBackend;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.jena.atlas.lib.tuple.Tuple;
import org.apache.jena.base.Sys;
import org.apache.jena.dboe.base.record.Record;
import org.apache.jena.dboe.trans.bplustree.ProgressJenaIterator;
import org.apache.jena.sparql.core.Quad;
import org.apache.jena.tdb2.store.NodeId;
import org.eclipse.jetty.server.handler.ContextHandler;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Random;

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
            while(i<(samplesize+1)){
                Pair<Tuple<NodeId>, Double> sporwR = spo.getUniformRandomSPOWithProbability();
                Tuple<NodeId> sporw = sporwR.getLeft();
                String subject = backend.getValue(sporw.get(0));

                LazyIterator s = (LazyIterator) backend.search(sporw.get(SPOC.SUBJECT), backend.any(), backend.any());
                ProgressJenaIterator sR = (ProgressJenaIterator) s.iterator;
                sum_s += (1/ sR.count());
                sum_p_for_N += (1/ sporwR.getRight());
                double estimateS = ((sum_p_for_N/i)/i) * sum_s ;
                //double estimateS = (10_916_457./i) * sum_s ;
                writer.printf("%s,%f%n", subject, estimateS);
                //System.out.println(estimateS);
                i++;
            }

        }
    }
    public static void CDs_dbpedia(String PathToTDB2Dataset, String outputFile, Integer samplesize) throws IOException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(outputFile))) {
            writer.println("Subject,CD");
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
                String subject = backend.getValue(sporw.get(0));

                LazyIterator s = (LazyIterator) backend.search(sporw.get(SPOC.SUBJECT), backend.any(), backend.any(), graph);
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
            writer.println("Predicate,CD");
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
            writer.println("Predicate,CD");
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

                String predicate = backend.getValue(sporwR.getLeft().get(1));
                LazyIterator p = (LazyIterator) backend.search(backend.any(),sporwR.getLeft().get(SPOC.PREDICATE), backend.any(),graph);
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
            writer.println("Object,CD");
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
            writer.println("Object,CD");
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

}
