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

public class QC {


    static boolean watdiv = true;
    // SELECT (COUNT(DISTINCT ?d) AS ?x) ?c WHERE { ?s a ?c , ?d } GROUP BY ?c
    public static void QC3(String PathToTDB2Dataset, String outputFile, Integer samplesize, String classvalue) throws IOException {
        ProgressJenaIterator.rng = new Random(2);
        ProgressJenaIterator.NB_WALKS = 10;
        JenaBackend backend = new JenaBackend(PathToTDB2Dataset);
        try(PrintWriter writer = new PrintWriter(new FileWriter(outputFile))) {
            writer.println("d,CD");
            NodeId classNode = backend.getId(classvalue);
            NodeId is_a = backend.getId("<http://www.w3.org/1999/02/22-rdf-syntax-ns#type>", SPOC.PREDICATE);
            if (watdiv == false){
                is_a = backend.getId("<http://www.wikidata.org/prop/direct/P131>", SPOC.PREDICATE);
            }
            ProgressJenaIterator sac = (ProgressJenaIterator) ((LazyIterator) backend.search(backend.any(), is_a, classNode)).iterator;

            int i = 0;
            double sumProb = 0;
            while(i<samplesize){
                Pair<Tuple<NodeId>, Double> sacR = sac.getRandomSPOWithProbability();
                NodeId s = sacR.getLeft().get(0);
                double firstProb = sacR.getRight();
                ProgressJenaIterator sad = (ProgressJenaIterator) ((LazyIterator) backend.search(s, is_a, backend.any())).iterator;
                Pair<Tuple<NodeId>, Double> sadR = sad.getRandomSPOWithProbability();
                double N = sad.cardinality();
                NodeId d = sadR.getLeft().get(2);
                String dString = backend.getValue(d);
                ProgressJenaIterator sadCard = (ProgressJenaIterator) ((LazyIterator) backend.search(s, is_a, d)).iterator;
                double secondProb = sadR.getRight();
                double finalProb = firstProb * secondProb;
                sumProb += 1/finalProb;
                double estimated =( N / sumProb) * (sumProb/sadCard.cardinality());
                writer.printf("%s,%f%n", dString, estimated);
                i++;
            }



        }

    }
    //SELECT (COUNT(DISTINCT ?p) AS ?x) ?c WHERE { ?s a ?c ; ?p ?o } GROUP BY ?c
    public static void QC4(String PathToTDB2Dataset, String outputFile, Integer samplesize, String classvalue) throws IOException {
        ProgressJenaIterator.rng = new Random(2);
        JenaBackend backend = new JenaBackend(PathToTDB2Dataset);
        ProgressJenaIterator.NB_WALKS = 10;
        try(PrintWriter writer = new PrintWriter(new FileWriter(outputFile))) {
            writer.println("Predicate,CD");
            NodeId is_a = backend.getId("<http://www.w3.org/1999/02/22-rdf-syntax-ns#type>", SPOC.PREDICATE);
            if (watdiv == false){
                is_a = backend.getId("<http://www.wikidata.org/prop/direct/P131>", SPOC.PREDICATE);
            }
            NodeId classNode = backend.getId(classvalue);
            ProgressJenaIterator sac = (ProgressJenaIterator) ((LazyIterator) backend.search(backend.any(), is_a, classNode)).iterator;

            int i = 0;
            double sumProb = 0;
            double sumCard = 0;
            while(i<samplesize){
                Pair<Tuple<NodeId>, Double> sacR = sac.getRandomSPOWithProbability();
                NodeId s = sacR.getLeft().get(0);
                double firstProb = sacR.getRight();
                ProgressJenaIterator spo = (ProgressJenaIterator) ((LazyIterator) backend.search(s, backend.any(), backend.any())).iterator;
                Pair<Tuple<NodeId>, Double> spoR = spo.getRandomSPOWithProbability();
                double N = spo.cardinality();
                double secondProb = spoR.getRight();

                NodeId p = spoR.getLeft().get(1);
                String pString = backend.getValue(p);

                ProgressJenaIterator spoCard = (ProgressJenaIterator) ((LazyIterator) backend.search(s, p, backend.any())).iterator;

                double finalProb = firstProb * secondProb;
                sumProb += 1/finalProb;
                sumCard += finalProb/spoCard.cardinality();
                double estimateP =( N / sumProb) * sumCard;
                writer.printf("%s,%f%n", pString, estimateP);
                i++;
            }
        }

    }
    //SELECT (COUNT(DISTINCT ?s) AS ?x) ?c WHERE { ?s a ?c } GROUP BY ?c
    public static void QC5(String PathToTDB2Dataset, String outputFile, Integer samplesize, String classvalue) throws IOException {
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
            ProgressJenaIterator sac = (ProgressJenaIterator) ((LazyIterator) backend.search(backend.any(), is_a, classNode)).iterator;
            double N = sac.cardinality();
            int i = 0;
            double sumProb = 0;
            while(i<samplesize){
                Tuple<NodeId> sacR = sac.getUniformRandomSPO();
                NodeId s = sacR.get(0);
                ProgressJenaIterator sIter = (ProgressJenaIterator) ((LazyIterator) backend.search(s , is_a, classNode)).iterator;
                sumProb += 1/sIter.cardinality();
                double estimateS =( N/i) * sumProb;
                writer.printf("%s,%f%n", backend.getValue(s), estimateS);
                i++;
            }
        }
    }
    //SELECT (COUNT(DISTINCT ?o) AS ?x) ?c WHERE { ?s a ?c ; ?p ?o } GROUP BY ?c
    public static void QC6(String PathToTDB2Dataset, String outputFile, Integer samplesize, String classValue) throws IOException {
        ProgressJenaIterator.rng = new Random(2);
        JenaBackend backend = new JenaBackend(PathToTDB2Dataset);
        ProgressJenaIterator.NB_WALKS = 10;
        NodeId is_a = backend.getId("<http://www.w3.org/1999/02/22-rdf-syntax-ns#type>", SPOC.PREDICATE);
        if (watdiv == false){
            is_a = backend.getId("<http://www.wikidata.org/prop/direct/P131>", SPOC.PREDICATE);
        }
        NodeId classNode = backend.getId(classValue);
        ProgressJenaIterator sac = (ProgressJenaIterator) ((LazyIterator) backend.search(backend.any(), is_a, classNode)).iterator;
        try(PrintWriter writer = new PrintWriter(new FileWriter(outputFile))) {
            writer.println("Object,CD");
            int i = 0;
            double sumProb = 0;
            double sumCard = 0;
            while(i<samplesize){
                Pair<Tuple<NodeId>, Double> sacR = sac.getRandomSPOWithProbability();
                NodeId s = sacR.getLeft().get(0);
                double firstProb = sacR.getRight();
                ProgressJenaIterator spo = (ProgressJenaIterator) ((LazyIterator) backend.search(s, backend.any(), backend.any())).iterator;
                Pair<Tuple<NodeId>, Double> spoR = spo.getRandomSPOWithProbability();
                double N = spo.cardinality();
                double secondProb = spoR.getRight();
                NodeId o = spoR.getLeft().get(2);
                String oString = backend.getValue(o);
                ProgressJenaIterator spoCard = (ProgressJenaIterator) ((LazyIterator) backend.search(s, backend.any(), o)).iterator;
                double finalProb = firstProb * secondProb;
                sumProb += 1/finalProb;
                sumCard += finalProb/spoCard.cardinality();
                double estimateO =( N / sumProb) * sumCard;
                writer.printf("%s,%f%n", oString, estimateO);
                i++;
            }
        }

    }
}
