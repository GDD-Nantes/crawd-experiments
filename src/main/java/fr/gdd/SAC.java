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
import java.util.HashMap;
import java.util.Random;

public class SAC {
    static JenaBackend backend = null;
    public static void SAC_CDc(String PathToTDB2Dataset, String outputFile, Integer samplesize, String predicate) throws IOException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(outputFile))) {
            writer.println("Class,CD");
            JenaBackend backend = new JenaBackend(PathToTDB2Dataset);
            NodeId is_a = backend.getId(predicate, SPOC.PREDICATE);
            ProgressJenaIterator sac = (ProgressJenaIterator) ((LazyIterator) backend.search(backend.any(), is_a, backend.any())).iterator;
            double N = sac.cardinality();
            int i = 0;
            double sum_c = 0.;
            while (i < samplesize) {
                Tuple<NodeId> sacrwR = sac.getUniformRandomSPO();
                NodeId cl = sacrwR.get(2);
                String classValue = backend.getValue(cl);
                LazyIterator c = (LazyIterator) backend.search(backend.any(), is_a, cl);
                ProgressJenaIterator cR = (ProgressJenaIterator) c.iterator;
                sum_c += (1 / cR.cardinality());
                double estimateC = (N / i) * sum_c;
                writer.printf("%s,%f%n", classValue, estimateC);
                i++;
            }
        }
    }
    public static void SAC_CDc_dbpedia(String PathToTDB2Dataset, String outputFile, Integer samplesize, String predicate) throws IOException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(outputFile))) {
            writer.println("Class,CD");
            JenaBackend backend = new JenaBackend(PathToTDB2Dataset);
            NodeId is_a = backend.getId(predicate, SPOC.PREDICATE);
            NodeId graph = backend.getId("<http://example.com/dbpedia>", SPOC.GRAPH);
            ProgressJenaIterator sac = (ProgressJenaIterator) ((LazyIterator) backend.search(backend.any(), is_a, backend.any(),graph)).iterator;
            double N = sac.cardinality();
            int i = 0;
            double sum_c = 0.;
            while (i < samplesize) {
                Tuple<NodeId> sacrwR = sac.getUniformRandomSPO();
                NodeId cl = sacrwR.get(2);
                String classValue = backend.getValue(cl);
                LazyIterator c = (LazyIterator) backend.search(backend.any(), is_a, cl);
                ProgressJenaIterator cR = (ProgressJenaIterator) c.iterator;
                sum_c += (1 / cR.cardinality());
                double estimateC = (N / i) * sum_c;
                writer.printf("%s,%f%n", classValue, estimateC);
                i++;
            }
        }
    }
}
