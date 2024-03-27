package fr.gdd;

import fr.gdd.sage.generics.LazyIterator;
import fr.gdd.sage.interfaces.SPOC;
import fr.gdd.sage.jena.JenaBackend;
import org.apache.jena.atlas.lib.tuple.Tuple;
import org.apache.jena.dboe.trans.bplustree.ProgressJenaIterator;
import org.apache.jena.tdb2.store.NodeId;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Random;

public class QB {
    static JenaBackend backend = null;

    static NodeId any = null;
    public static void QB2(String PathToTDB2Dataset, String outputFile, Integer samplesize) throws IOException {
        ProgressJenaIterator.rng = new Random(2);
        ProgressJenaIterator.NB_WALKS = 1;
        JenaBackend backend = new JenaBackend(PathToTDB2Dataset);
        try (PrintWriter writer = new PrintWriter(new FileWriter(outputFile))) {
            NodeId is_a = backend.getId("<http://www.w3.org/1999/02/22-rdf-syntax-ns#type>", SPOC.PREDICATE);
            ProgressJenaIterator sac = (ProgressJenaIterator) ((LazyIterator) backend.search(backend.any(), is_a, backend.any())).iterator;
            double N = sac.cardinality();
            int i = 0;
            double sum_c = 0.;
            while (i < samplesize) {
                Tuple<NodeId> sacrwR = sac.getUniformRandomSPO();
                String classValue = backend.getValue(sacrwR.get(2));
                LazyIterator c = (LazyIterator) backend.search(backend.any(), is_a, sacrwR.get(2));
                ProgressJenaIterator cR = (ProgressJenaIterator) c.iterator;
                sum_c += (1 / cR.cardinality());
                double estimateC = (N / i) * sum_c;
                writer.printf("%s,%f%n", classValue, estimateC);
                i++;
            }
        }
    }
    public static void QB3(String PathToTDB2Dataset, String outputFile, Integer samplesize) throws IOException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(outputFile))) {
            writer.println("Subject,CD");
            JenaBackend backend = new JenaBackend(PathToTDB2Dataset);
            ProgressJenaIterator.rng = new Random(2);
            ProgressJenaIterator.NB_WALKS = 1;
            ProgressJenaIterator spo = (ProgressJenaIterator) ((LazyIterator) backend.search(backend.any(), backend.any(), backend.any())).iterator;
            double N = spo.cardinality();
            int i = 0;
            double sum_s = 0.;
            while(i<samplesize){
                Tuple<NodeId> sporwR = spo.getUniformRandomSPO();
                String subject = backend.getValue(sporwR.get(0));
                LazyIterator s = (LazyIterator) backend.search(sporwR.get(0), backend.any(), backend.any());
                ProgressJenaIterator sR = (ProgressJenaIterator) s.iterator;
                sum_s += (1/ sR.cardinality());
                double estimateS = (N/i) * sum_s ;
                writer.printf("%s,%f%n", subject, estimateS);
                i++;
            }

        }
    }
    public static void QB4(String PathToTDB2Dataset, String outputFile, Integer samplesize) throws IOException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(outputFile))) {
            writer.println("Predicate,CD");
            JenaBackend backend = new JenaBackend(PathToTDB2Dataset);
            ProgressJenaIterator.rng = new Random(2);
            ProgressJenaIterator.NB_WALKS = 1;
            ProgressJenaIterator spo = (ProgressJenaIterator) ((LazyIterator) backend.search(backend.any(), backend.any(), backend.any())).iterator;
            double N = spo.cardinality();
            int i = 0;
            double sum_p = 0.;
            while(i<samplesize){
                Tuple<NodeId> sporwR = spo.getUniformRandomSPO();
                String predicate = backend.getValue(sporwR.get(1));
                LazyIterator p = (LazyIterator) backend.search(backend.any(),sporwR.get(1), backend.any());
                ProgressJenaIterator pR = (ProgressJenaIterator) p.iterator;
                sum_p += (1/ pR.cardinality());
                double estimateP = (N/i) * sum_p;
                writer.printf("%s,%f%n", predicate, estimateP);
                i++;
            }

        }
    }
    public static void QB5(String PathToTDB2Dataset, String outputFile, Integer samplesize) throws IOException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(outputFile))) {
            writer.println("Object,CD");
            JenaBackend backend = new JenaBackend(PathToTDB2Dataset);
            ProgressJenaIterator.rng = new Random(2);
            ProgressJenaIterator.NB_WALKS = 1;
            ProgressJenaIterator spo = (ProgressJenaIterator) ((LazyIterator) backend.search(backend.any(), backend.any(), backend.any())).iterator;
            double N = spo.cardinality();
            int i = 0;
            double sum_o = 0.;
            while(i<samplesize){
                Tuple<NodeId> sporwR = spo.getUniformRandomSPO();
                String object = backend.getValue(sporwR.get(2));
                LazyIterator o = (LazyIterator) backend.search(backend.any(), backend.any(),sporwR.get(2));
                ProgressJenaIterator oR = (ProgressJenaIterator) o.iterator;
                sum_o += (1/ oR.cardinality());
                double estimateO = (N/i) * sum_o;
                writer.printf("%s,%f%n", object, estimateO);
                i++;
            }
        }
    }
}
