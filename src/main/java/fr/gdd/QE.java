package fr.gdd;

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
import java.util.Random;

public class QE {
    static JenaBackend backend = null;
    static boolean watdiv = true;

    //SELECT (COUNT(DISTINCT ?s) AS ?x) ?c ?p WHERE { ?s a ?c ; ?p ?o } GROUP BY ?c ?p
    public static void QE4(String PathToTDB2Dataset, String outputFile, Integer samplesize, String classvalue,String predicatevalue) throws IOException {
        ProgressJenaIterator.rng = new Random(2);
        JenaBackend backend = new JenaBackend(PathToTDB2Dataset);
        ProgressJenaIterator.NB_WALKS = 10;
        try(PrintWriter writer = new PrintWriter(new FileWriter(outputFile))) {
            writer.println("Subject,CD");
            NodeId is_a = backend.getId("<http://www.w3.org/1999/02/22-rdf-syntax-ns#type>", SPOC.PREDICATE);
            if (watdiv == false){
                is_a = backend.getId("<http://www.wikidata.org/prop/direct/P131>", SPOC.PREDICATE);
            }
            NodeId classNode = backend.getId(classvalue);
            NodeId predicate = backend.getId(predicatevalue);
            ProgressJenaIterator sac = (ProgressJenaIterator) ((LazyIterator) backend.search(backend.any(), is_a, classNode)).iterator;

            int i = 0;
            double sumProb = 0;
            double sumCard = 0;
            while(i<samplesize){
                Pair<Tuple<NodeId>, Double> sacR = sac.getRandomSPOWithProbability();
                NodeId s = sacR.getLeft().get(0);
                double firstProb = sacR.getRight();
                ProgressJenaIterator spo = (ProgressJenaIterator) ((LazyIterator) backend.search(s, predicate, backend.any())).iterator;
                Pair<Tuple<NodeId>, Double> spoR = spo.getRandomSPOWithProbability();
                double N = spo.cardinality();
                double secondProb = spoR.getRight();

                double finalProb = firstProb * secondProb;
                sumProb += 1/finalProb;
                sumCard += finalProb/spo.cardinality();
                double estimateS =( N / sumProb) * sumCard;
                writer.printf("%s,%f%n", backend.getValue(s), estimateS);
                i++;
            }
        }

    }
    //SELECT (COUNT(DISTINCT ?o) AS ?x) ?c ?p WHERE { ?s a ?c ; ?p ?o } GROUP BY ?c ?p
    public static void QE5(String PathToTDB2Dataset, String outputFile, Integer samplesize, String classvalue,String predicatevalue) throws IOException {
        ProgressJenaIterator.rng = new Random(2);
        JenaBackend backend = new JenaBackend(PathToTDB2Dataset);
        ProgressJenaIterator.NB_WALKS = 10;
        try(PrintWriter writer = new PrintWriter(new FileWriter(outputFile))) {
            writer.println("Subject,CD");
            NodeId is_a = backend.getId("<http://www.w3.org/1999/02/22-rdf-syntax-ns#type>", SPOC.PREDICATE);
            if (watdiv == false){
                is_a = backend.getId("<http://www.wikidata.org/prop/direct/P131>", SPOC.PREDICATE);
            }
            NodeId classNode = backend.getId(classvalue);
            NodeId predicate = backend.getId(predicatevalue);
            ProgressJenaIterator sac = (ProgressJenaIterator) ((LazyIterator) backend.search(backend.any(), is_a, classNode)).iterator;

            int i = 0;
            double sumProb = 0;
            double sumCard = 0;
            while(i<samplesize){
                Pair<Tuple<NodeId>, Double> sacR = sac.getRandomSPOWithProbability();
                NodeId o = sacR.getLeft().get(2);
                double firstProb = sacR.getRight();
                ProgressJenaIterator spo = (ProgressJenaIterator) ((LazyIterator) backend.search(backend.any(), predicate, o)).iterator;
                Pair<Tuple<NodeId>, Double> spoR = spo.getRandomSPOWithProbability();
                double N = spo.cardinality();
                double secondProb = spoR.getRight();

                double finalProb = firstProb * secondProb;
                sumProb += 1/finalProb;
                sumCard += finalProb/spo.cardinality();
                double estimateO =( N / sumProb) * sumCard;
                writer.printf("%s,%f%n", backend.getValue(o), estimateO);
                i++;
            }
        }

    }

}
