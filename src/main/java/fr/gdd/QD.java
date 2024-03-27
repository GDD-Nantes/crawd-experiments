package fr.gdd;

import fr.gdd.sage.generics.LazyIterator;
import fr.gdd.sage.jena.JenaBackend;
import org.apache.jena.atlas.lib.tuple.Tuple;
import org.apache.jena.dboe.trans.bplustree.ProgressJenaIterator;
import org.apache.jena.tdb2.store.NodeId;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Random;

public class QD {
    static JenaBackend backend = null;

    // SELECT (COUNT(DISTINCT ?s) AS ?x) ?p WHERE { ?s ?p ?o } GROUP BY ?p
    public static void QD3(String PathToTDB2Dataset, String outputFile, Integer samplesize, String predicate) throws IOException {
        ProgressJenaIterator.rng = new Random(2);
        ProgressJenaIterator.NB_WALKS = 1;
        JenaBackend backend = new JenaBackend(PathToTDB2Dataset);
        try (PrintWriter writer = new PrintWriter(new FileWriter(outputFile))) {
            writer.println("Subject,CD");
            NodeId p = backend.getId(predicate);
            ProgressJenaIterator spo = (ProgressJenaIterator) ((LazyIterator) backend.search(backend.any(), p, backend.any())).iterator;
            double N = spo.cardinality();
            int i = 0;
            double sum = 0;
            while (i<samplesize){
                Tuple<NodeId> spoR = spo.getUniformRandomSPO();
                NodeId s = spoR.get(0);
                ProgressJenaIterator sp = (ProgressJenaIterator) ((LazyIterator) backend.search(s, p, backend.any())).iterator;
                sum += 1/sp.cardinality();
                double estimateS = (N/i)*sum;
                writer.printf("%s,%f%n", backend.getValue(s), estimateS);
                i++;
            }
        }
    }
    //SELECT (COUNT(DISTINCT ?o) AS ?x) ?p WHERE { ?s ?p ?o } GROUP BY ?p
    public static void QD4(String PathToTDB2Dataset, String outputFile, Integer samplesize, String predicate) throws IOException {
        ProgressJenaIterator.rng = new Random(2);
        ProgressJenaIterator.NB_WALKS = 1;
        JenaBackend backend = new JenaBackend(PathToTDB2Dataset);
        try (PrintWriter writer = new PrintWriter(new FileWriter(outputFile))) {
            writer.println("Object,CD");
            NodeId p = backend.getId(predicate);
            ProgressJenaIterator spo = (ProgressJenaIterator) ((LazyIterator) backend.search(backend.any(), p, backend.any())).iterator;
            double N = spo.cardinality();
            int i = 0;
            double sum = 0;
            while (i<samplesize){
                Tuple<NodeId> spoR = spo.getUniformRandomSPO();
                NodeId o = spoR.get(2);
                ProgressJenaIterator op = (ProgressJenaIterator) ((LazyIterator) backend.search(backend.any(), p, o)).iterator;
                sum += 1/op.cardinality();
                double estimateO = (N/i)*sum;
                writer.printf("%s,%f%n", backend.getValue(o), estimateO);
                i++;
            }
        }
    }
}
