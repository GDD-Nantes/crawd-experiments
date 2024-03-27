package fr.gdd;

import fr.gdd.sage.generics.LazyIterator;
import fr.gdd.sage.jena.JenaBackend;
import org.apache.jena.atlas.lib.tuple.Tuple;
import org.apache.jena.dboe.trans.bplustree.ProgressJenaIterator;
import org.apache.jena.graph.*;
import org.apache.jena.tdb2.store.NodeId;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Random;

public class QF {


   //SELECT (COUNT(DISTINCT ?s ) AS ?x) WHERE { ?s ?p ?o  FILTER(isIri(?s))}
   public static void QF1(String PathToTDB2Dataset, String outputFile, Integer samplesize) throws IOException {
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
               NodeId s = sporwR.get(0);
               String subject = backend.getValue(s);
               // Create a Jena Node from the subject string
               Node node = NodeFactory.createURI(subject);
               // Check the type of the Node
               if (node instanceof Node_URI) {
                   LazyIterator sI = (LazyIterator) backend.search(s, backend.any(), backend.any());
                   ProgressJenaIterator sR = (ProgressJenaIterator) sI.iterator;
                   sum_s += (1/ sR.cardinality());
                   double estimateS = (N/i) * sum_s ;
                   writer.printf("%sI,%f%n", subject, estimateS);
                   i++;
               }
           }

       }
   }

   //SELECT (COUNT(DISTINCT ?s) AS ?x) WHERE { ?s ?p ?o FILTER(isBlank(?s))}
   public static void QF2(String PathToTDB2Dataset, String outputFile, Integer samplesize) throws IOException {
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
               NodeId s = sporwR.get(0);
               String subject = backend.getValue(s);
               // Create a Jena Node from the subject string
               Node node = NodeFactory.createURI(subject);
               // Check the type of the Node
               if (node instanceof Node_Blank) {
                   LazyIterator sI = (LazyIterator) backend.search(s, backend.any(), backend.any());
                   ProgressJenaIterator sR = (ProgressJenaIterator) sI.iterator;
                   sum_s += (1/ sR.cardinality());
                   double estimateS = (N/i) * sum_s ;
                   writer.printf("%s,%f%n", subject, estimateS);
                   i++;
               }
           }

       }
   }
   //SELECT (COUNT(DISTINCT ?o ) AS ?x) WHERE { ?s ?p ?o FILTER(isIri(?o))}
   public static void QF3(String PathToTDB2Dataset, String outputFile, Integer samplesize) throws IOException {
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
               NodeId o = sporwR.get(2);
               String object = backend.getValue(o);
               // Create a Jena Node from the subject string
               Node node = NodeFactory.createURI(object);
               // Check the type of the Node
               if (node instanceof Node_URI) {
                   LazyIterator oI = (LazyIterator) backend.search(backend.any(), backend.any(), o);
                   ProgressJenaIterator oR = (ProgressJenaIterator) oI.iterator;
                   sum_o += (1/ oR.cardinality());
                   double estimateO = (N/i) * sum_o ;
                   writer.printf("%s,%f%n", object, estimateO);
                   i++;
               }
           }

       }
   }
   //SELECT (COUNT(DISTINCT ?o ) AS ?x) WHERE { ?s ?p ?o FILTER(isLiteral(?o))}
   public static void QF4(String PathToTDB2Dataset, String outputFile, Integer samplesize) throws IOException {
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
               NodeId o = sporwR.get(2);
               String object = backend.getValue(o);
               // Create a Jena Node from the subject string
               Node node = NodeFactory.createURI(object);
               // Check the type of the Node
               if (node instanceof Node_Literal) {
                   LazyIterator oI = (LazyIterator) backend.search(backend.any(), backend.any(), o);
                   ProgressJenaIterator oR = (ProgressJenaIterator) oI.iterator;
                   sum_o += (1/ oR.cardinality());
                   double estimateO = (N/i) * sum_o ;
                   writer.printf("%s,%f%n", object, estimateO);
                   i++;
               }
           }

       }
   }
   //SELECT (COUNT(DISTINCT ?o ) AS ?x) WHERE { ?s ?p ?o FILTER(isBlank(?o))}
   public static void QF5(String PathToTDB2Dataset, String outputFile, Integer samplesize) throws IOException {
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
               NodeId o = sporwR.get(2);
               String object = backend.getValue(o);
               // Create a Jena Node from the subject string
               Node node = NodeFactory.createURI(object);
               // Check the type of the Node
               if (node instanceof Node_Blank) {
                   LazyIterator oI = (LazyIterator) backend.search(backend.any(), backend.any(), o);
                   ProgressJenaIterator oR = (ProgressJenaIterator) oI.iterator;
                   sum_o += (1/ oR.cardinality());
                   double estimateO = (N/i) * sum_o ;
                   writer.printf("%s,%f%n", object, estimateO);
                   i++;
               }
           }

       }
   }
}
