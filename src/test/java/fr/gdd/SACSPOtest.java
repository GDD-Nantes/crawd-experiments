package org.apache.jena.dboe.trans.bplustree;

import fr.gdd.sage.generics.LazyIterator;
import fr.gdd.sage.interfaces.BackendIterator;
import fr.gdd.sage.interfaces.SPOC;
import fr.gdd.sage.jena.JenaBackend;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.jena.atlas.lib.tuple.Tuple;
import org.apache.jena.query.Dataset;
import org.apache.jena.tdb2.store.NodeId;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SACSPOtest {

    static Dataset dataset = null;
    static JenaBackend backend = null;

    static NodeId predicate = null;
    static NodeId any = null;

    // Just getting sample is not really efficient, we need to get the sample and then perform the count distinct
    @Disabled
    @Test
    public void getSampleSaCAPO() throws IOException {
        String outputfile = "sample_results/non_uniform_sac_spo_CD.csv";
        try (PrintWriter writer = new PrintWriter(new FileWriter(outputfile))) {
            writer.println("Subject,A,Class,Predicate,Object,RAWProbability,CDsubject,CDobject");
            ProgressJenaIterator.NB_WALKS = 1000;
            JenaBackend backend = new JenaBackend("/GDD/WATDIV");
            NodeId is_a = backend.getId("<http://www.w3.org/1999/02/22-rdf-syntax-ns#type>", SPOC.PREDICATE);
            int i = 0;
            double sum_s = 0;
            double sum_o = 0;
            double sum_probas = 0;
            while (i < 1_000_000){
                //run the full query {?s a class ; ?p ?o .}
                ProgressJenaIterator s_isa_cl = (ProgressJenaIterator) ((LazyIterator) backend.search(backend.any(), is_a, backend.any())).iterator;
                Pair<Tuple<NodeId>, Double> s_isa_clRecord = s_isa_cl.getRandomSPOWithProbability();
                Double firstTripleProba = s_isa_clRecord .getRight();

                NodeId sId = s_isa_clRecord.getLeft().get(SPOC.SUBJECT);
                NodeId cl = s_isa_clRecord.getLeft().get(SPOC.OBJECT);
                String classValue = backend.getValue(cl);
                String subject = backend.getValue(sId);

                ProgressJenaIterator spo2 = (ProgressJenaIterator) ((LazyIterator) backend.search(sId, backend.any(), backend.any())).iterator;
                Pair<Tuple<NodeId>,Double> spoRecord = spo2.getRandomSPOWithProbability();
                Double spoProba = spoRecord.getRight();
                Double finalProba = firstTripleProba * spoProba;

                NodeId pId = spoRecord.getLeft().get(SPOC.PREDICATE);
                String predicate = backend.getValue(pId);
                NodeId oId = spoRecord.getLeft().get(SPOC.OBJECT);
                String object = backend.getValue(oId);

                // perform count distinct formular
                Tuple<NodeId> ids = spoRecord.getLeft();
                LazyIterator s = (LazyIterator) backend.search(ids.get(0), backend.any(), backend.any());
                ProgressJenaIterator sR = (ProgressJenaIterator) s.iterator;
                LazyIterator o = (LazyIterator) backend.search(backend.any(), backend.any(), ids.get(2));
                ProgressJenaIterator oR = (ProgressJenaIterator) o.iterator;
                sum_s += (1/finalProba) / sR.count();
                sum_o += (1/finalProba) / oR.count();
                sum_probas += (1/finalProba);
                //count distinct for non uniform sample
                double estimateS = (spo2.count()/ sum_probas)* sum_s;
                double estimateO = (spo2.count()/ sum_probas)* sum_o;

                writer.printf("%s,%s,%s,%s,%s,%f,%f,%f%n", subject, "a", classValue, predicate, object, 1/finalProba, estimateS, estimateO);
                i++;
            }
        }
    }

    // CLASS-BASED LEVEL
    //QC4, QC6
    @Disabled
    @Test
    public void CDp_o_group_by_class() throws IOException {
        String predicateOutputFile = "sample_results/CDp_groupby_class_in_sac_spo.csv";
        String objectOutputFile = "sample_results/CDo_groupby_class_in_sac_spo.csv";

        try(PrintWriter predicateWriter = new PrintWriter(new FileWriter(predicateOutputFile));
            PrintWriter objectWriter = new PrintWriter(new FileWriter(objectOutputFile))) {
            predicateWriter.println("Predicate,1/Probability,CDpredicate");
            objectWriter.println("Object,1/Probability,CDobject");

            ProgressJenaIterator.NB_WALKS = 1000;
            JenaBackend backend = new JenaBackend("/GDD/WATDIV");
            NodeId is_a = backend.getId("<http://www.w3.org/1999/02/22-rdf-syntax-ns#type>", SPOC.PREDICATE);

            // getting list of class
            Set<NodeId> classes = new HashSet<>();
            LazyIterator classIterator = (LazyIterator) backend.search(backend.any(), is_a, backend.any());
            BackendIterator<NodeId, ?> classR = (BackendIterator) classIterator.iterator;
            while (classR.hasNext()) {
                classR.next();
                NodeId cl = classR.getId(SPOC.OBJECT);
                classes.add(cl);
            }
            // for each class
            for(NodeId cl : classes){
                double sum_p = 0;
                double sum_o = 0;
                double sum_probas = 0;
                int i = 0;
                while (i < 100_000){
                    //run the full query {?s a class ; ?p ?o .}
                    ProgressJenaIterator s_isa_cl = (ProgressJenaIterator) ((LazyIterator) backend.search(backend.any(), is_a, cl)).iterator;
                    Pair<Tuple<NodeId>, Double> s_isa_clRecord = s_isa_cl.getRandomSPOWithProbability();
                    Double firstTripleProba = s_isa_clRecord .getRight();

                    NodeId sId = s_isa_clRecord.getLeft().get(SPOC.SUBJECT);
                    String subject = backend.getValue(sId);

                    ProgressJenaIterator spo2 = (ProgressJenaIterator) ((LazyIterator) backend.search(sId, backend.any(), backend.any())).iterator;
                    Pair<Tuple<NodeId>,Double> spoRecord = spo2.getRandomSPOWithProbability();
                    Double spoProba = spoRecord.getRight();
                    Double finalProba = firstTripleProba * spoProba;

                    NodeId oId = spoRecord.getLeft().get(SPOC.OBJECT);
                    String object = backend.getValue(oId);
                    NodeId pId = spoRecord.getLeft().get(SPOC.PREDICATE);
                    String predicate = backend.getValue(pId);

                    // perform count distinct formular
                    Tuple<NodeId> ids = spoRecord.getLeft();
                    LazyIterator p = (LazyIterator) backend.search(backend.any(), ids.get(1), backend.any());
                    ProgressJenaIterator pR = (ProgressJenaIterator) p.iterator;
                    LazyIterator o = (LazyIterator) backend.search(backend.any(), backend.any(), ids.get(2));
                    ProgressJenaIterator oR = (ProgressJenaIterator) o.iterator;
                    sum_p += (1/finalProba) / pR.count();
                    sum_o += (1/finalProba) / oR.count();
                    sum_probas += (1/finalProba);
                    //count distinct for non uniform sample
                    double estimateP = (spo2.count()/ sum_probas)* sum_p;
                    double estimateO = (spo2.count()/ sum_probas)* sum_o;

                    // Write data to appropriate files
                    predicateWriter.printf("%s,%f,%f%n", predicate, 1/finalProba, estimateP);
                    objectWriter.printf("%s,%f,%f%n", object, 1/finalProba, estimateO);
                    i++;
                }
            }

        }
    }
    //QC5
    @Disabled
    @Test
    public void CDs_in_class() throws IOException {
        String outputfile = "sample_results/CDs_in_class.csv";
        try (PrintWriter writer = new PrintWriter(new FileWriter(outputfile))) {
            writer.println("Subject,Class,RAWProbability,CDsubject");
            ProgressJenaIterator.NB_WALKS = 1000;
            JenaBackend backend = new JenaBackend("/GDD/WATDIV");
            NodeId is_a = backend.getId("<http://www.w3.org/1999/02/22-rdf-syntax-ns#type>", SPOC.PREDICATE);

            // getting list of class
            Set<NodeId> classes = new HashSet<>();
            LazyIterator classIterator = (LazyIterator) backend.search(backend.any(), is_a, backend.any());
            BackendIterator<NodeId, ?> classR = (BackendIterator) classIterator.iterator;
            int N = 0;
            while (classR.hasNext()) {
                classR.next();
                NodeId cl = classR.getId(SPOC.OBJECT);
                classes.add(cl);
                N++;
            }
            // for each class
            for (NodeId cl : classes) {
                double sum_s = 0;
                double sum_probas = 0;
                int i = 0;
                while (i < N) {
                    ProgressJenaIterator s_isa_cl = (ProgressJenaIterator) ((LazyIterator) backend.search(backend.any(), is_a, cl)).iterator;
                    Pair<Tuple<NodeId>, Double> s_isa_clRecord = s_isa_cl.getRandomSPOWithProbability();
                    Tuple<NodeId> ids = s_isa_clRecord.getLeft();
                    String classValue = backend.getValue(cl);
                    String subject = backend.getValue(ids.get(0));
                    Double firstTripleProba = s_isa_clRecord.getRight();
                    sum_probas += (1 / firstTripleProba);
                    LazyIterator s = (LazyIterator) backend.search(ids.get(0), is_a, cl);
                    ProgressJenaIterator sR = (ProgressJenaIterator) s.iterator;
                    sum_s += (1 / firstTripleProba) / sR.count();
                    double estimateS =  ((N / sum_probas) * sum_s);
                    writer.printf("%s,%s,%f,%f%n", subject, classValue, 1 / firstTripleProba, estimateS);
                    i++;
                }

            }
        }
    }

    // QC3
    @Disabled
    @Test
    public void CD_d_in_class() throws IOException {
        String outputfile = "sample_results/CDd_groupby_class.csv";
        try (PrintWriter writer = new PrintWriter(new FileWriter(outputfile))) {
            writer.println("d,Class,RAWProbability,CDd");
            ProgressJenaIterator.NB_WALKS = 1000;
            JenaBackend backend = new JenaBackend("/GDD/WATDIV");
            NodeId is_a = backend.getId("<http://www.w3.org/1999/02/22-rdf-syntax-ns#type>", SPOC.PREDICATE);
            // getting list of class
            Set<NodeId> classes = new HashSet<>();
            LazyIterator classIterator = (LazyIterator) backend.search(backend.any(), is_a, backend.any());
            BackendIterator<NodeId, ?> classR = (BackendIterator) classIterator.iterator;

            while (classR.hasNext()) {
                classR.next();
                NodeId cl = classR.getId(SPOC.OBJECT);
                classes.add(cl);

            }
            // for each class
            for (NodeId cl : classes) {
                double sum_d = 0;
                double sum_probas = 0;
                int i = 0;
                while (i < 100_000) {
                    ProgressJenaIterator s_isa_cl = (ProgressJenaIterator) ((LazyIterator) backend.search(backend.any(), is_a, cl)).iterator;
                    Pair<Tuple<NodeId>, Double> s_isa_clRecord = s_isa_cl.getRandomSPOWithProbability();
                    Tuple<NodeId> ids = s_isa_clRecord.getLeft();
                    String classValue = backend.getValue(cl);
                    Double firstTripleProba = s_isa_clRecord.getRight();

                    ProgressJenaIterator s_isa_d = (ProgressJenaIterator) ((LazyIterator) backend.search(ids.get(0), is_a, backend.any())).iterator;
                    Pair<Tuple<NodeId>, Double> s_isa_dRecord = s_isa_d.getRandomSPOWithProbability();
                    Double s_isa_dProba = s_isa_dRecord.getRight();
                    Double finalProba = firstTripleProba * s_isa_dProba;

                    Tuple<NodeId> ids2 = s_isa_dRecord.getLeft();
                    String d_string = backend.getValue(ids2.get(2));
                    sum_probas += (1 / finalProba);
                    LazyIterator d = (LazyIterator) backend.search(backend.any(), is_a, ids2.get(2));
                    ProgressJenaIterator dR = (ProgressJenaIterator) d.iterator;
                    sum_d += (1 / finalProba) / dR.count();
                    double estimateD =  ((s_isa_d.count() / sum_probas) * sum_d);
                    writer.printf("%s,%s,%f,%f%n", d_string, classValue, 1 / finalProba, estimateD);
                }
            }
        }
    }
    // Property-based Level
    //QD3, QD4
    @Disabled
    @Test
    public void CD_s_o_groupby_p_in_spo() throws IOException {
        String subjectOutputFile = "sample_results/CDs_groupby_predicate.csv";
        String objectOutputFile = "sample_results/CDo_groupby_predicate.csv";

        try (PrintWriter subjectWriter = new PrintWriter(new FileWriter(subjectOutputFile));
             PrintWriter objectWriter = new PrintWriter(new FileWriter(objectOutputFile))) {
            subjectWriter.println("Subject,Predicate,1/Probability,CDsubject");
            objectWriter.println("Object,Predicate,1/Probability,CDobject");

            ProgressJenaIterator.NB_WALKS = 1000;
            JenaBackend backend = new JenaBackend("/GDD/WATDIV");
            Set<NodeId> predicates = new HashSet<>();
            LazyIterator predicateIter = (LazyIterator) backend.search(backend.any(), backend.any(), backend.any());
            BackendIterator<NodeId, ?> predR = (BackendIterator) predicateIter.iterator;

            while (predR.hasNext()) {
                predR.next();
                NodeId pred = predR.getId(SPOC.PREDICATE);
                predicates.add(pred);
            }
            for(NodeId pred:predicates){
                double sum_s = 0;
                double sum_o = 0;
                double sum_probas = 0;
                int i = 0;
                while (i < 1_000) {
                    ProgressJenaIterator spo = (ProgressJenaIterator) ((LazyIterator) backend.search(backend.any(), pred, backend.any())).iterator;
                    Pair<Tuple<NodeId>, Double> spoRecord = spo.getRandomSPOWithProbability();
                    Double firstTripleProba = spoRecord.getRight();

                    NodeId sId = spoRecord.getLeft().get(SPOC.SUBJECT);
                    String subject = backend.getValue(sId);

                    NodeId oId = spoRecord.getLeft().get(SPOC.OBJECT);
                    String object = backend.getValue(oId);

                    String predicate = backend.getValue(pred);

                    // perform count distinct formular
                    Tuple<NodeId> ids = spoRecord.getLeft();
                    LazyIterator s = (LazyIterator) backend.search(ids.get(0), pred, backend.any());
                    ProgressJenaIterator sR = (ProgressJenaIterator) s.iterator;
                    LazyIterator o = (LazyIterator) backend.search(backend.any(), pred, ids.get(2));
                    ProgressJenaIterator oR = (ProgressJenaIterator) o.iterator;
                    sum_s += (1 / firstTripleProba) / sR.count();
                    sum_o += (1 / firstTripleProba) / oR.count();
                    sum_probas += (1 / firstTripleProba);
                    //count distinct for non uniform sample
                    double estimateS = (spo.count() / sum_probas) * sum_s;
                    double estimateO = (spo.count() / sum_probas) * sum_o;

                    // Write data to files
                    subjectWriter.printf("%s,%s,%.1f,%f%n", subject,predicate, 1 / firstTripleProba, estimateS);
                    objectWriter.printf("%s,%s,%.1f,%f%n", object,predicate, 1 / firstTripleProba, estimateO);
                    i++;
                }
            }
        }
    }
    // Nested class-predicate level
    //QE3,4
    @Disabled
    @Test
    public void CD_s_o_groupby_class_predicate_in_sac_spo() throws IOException {
        String subjectOutputFile = "sample_results/CDs_groupby_class_predicate.csv";
        String objectOutputFile = "sample_results/CDo_groupby_class_predicate.csv";

        try (PrintWriter subjectWriter = new PrintWriter(new FileWriter(subjectOutputFile));
             PrintWriter objectWriter = new PrintWriter(new FileWriter(objectOutputFile))) {
            subjectWriter.println("Subject,Class,Predicate,1/Probability,CDsubject");
            objectWriter.println("Object,Class,Predicate,1/Probability,CDobject");

            ProgressJenaIterator.NB_WALKS = 1000;
            JenaBackend backend = new JenaBackend("/GDD/WATDIV");
            NodeId is_a = backend.getId("<http://www.w3.org/1999/02/22-rdf-syntax-ns#type>", SPOC.PREDICATE);
            Set<NodeId> classes = new HashSet<>();
            LazyIterator classIterator = (LazyIterator) backend.search(backend.any(), is_a, backend.any());
            BackendIterator<NodeId, ?> classR = (BackendIterator) classIterator.iterator;
            double N = 0;
            Set<NodeId> predicates = new HashSet<>();
            while (classR.hasNext()) {
                classR.next();
                NodeId cl = classR.getId(SPOC.OBJECT);
                classes.add(cl);
                BackendIterator<NodeId, ?> predicateIter = (BackendIterator) backend.search(classR.getId(0), backend.any(), backend.any());

                while (predicateIter.hasNext()) {
                    predicateIter.next();
                    N += 1;
                    NodeId pred = predicateIter.getId(SPOC.PREDICATE);
                    predicates.add(pred);
                }
            }
            for(NodeId cl:classes) {
                for (NodeId pred : predicates) {
                    double sum_s = 0;
                    double sum_o = 0;
                    double sum_probas = 0;
                    int i = 0;
                    while (i < 1_000) {
                        ProgressJenaIterator s_isa_cl = (ProgressJenaIterator) ((LazyIterator) backend.search(backend.any(), is_a, cl)).iterator;
                        Pair<Tuple<NodeId>, Double> s_isa_clRecord = s_isa_cl.getRandomSPOWithProbability();
                        Double firstTripleProba = s_isa_clRecord.getRight();

                        NodeId sId = s_isa_clRecord.getLeft().get(SPOC.SUBJECT);
                        String subject = backend.getValue(sId);

                        ProgressJenaIterator spo = (ProgressJenaIterator) ((LazyIterator) backend.search(sId, pred, backend.any())).iterator;
                        Pair<Tuple<NodeId>, Double> spoRecord = spo.getRandomSPOWithProbability();
                        Double spoProba = spoRecord.getRight();
                        Double finalProba = firstTripleProba * spoProba;

                        NodeId oId = spoRecord.getLeft().get(SPOC.OBJECT);
                        String object = backend.getValue(oId);

                        // perform count distinct formular
                        Tuple<NodeId> ids = spoRecord.getLeft();
                        LazyIterator s = (LazyIterator) backend.search(ids.get(0), pred, backend.any());
                        ProgressJenaIterator sR = (ProgressJenaIterator) s.iterator;
                        LazyIterator o = (LazyIterator) backend.search(backend.any(), pred, ids.get(2));
                        ProgressJenaIterator oR = (ProgressJenaIterator) o.iterator;
                        sum_s += (1 / finalProba) / sR.count();
                        sum_o += (1 / finalProba) / oR.count();
                        sum_probas += (1 / finalProba);
                        //count distinct for non uniform sample
                        double estimateS = (N / sum_probas) * sum_s;
                        double estimateO = (N / sum_probas) * sum_o;

                        // Write data to files
                        subjectWriter.printf("%s,%s,%s,%f,%f%n", subject, backend.getValue(cl), backend.getValue(pred), 1 / finalProba, estimateS);
                        objectWriter.printf("%s,%s,%s,%f,%f%n", object, backend.getValue(cl), backend.getValue(pred), 1 / finalProba, estimateO);
                        i++;
                    }
                }
            }
        }
    }
    // TO get class for running script for getting ground truth
    @Disabled
    @Test
    public void get_graph_largeRDF() throws IOException {
        String outputfile = "sample_results/graph_largeRDF.csv";
        try (PrintWriter writer = new PrintWriter(new FileWriter(outputfile))){
            JenaBackend backend = new JenaBackend("/GDD/RSFB/engines/FedUP-experiments/backup/summaries/largerdfbench/fedup-id");
            BackendIterator<NodeId, ?> graphs = (BackendIterator) backend.search(backend.any(), backend.any(), backend.any(), backend.any());
            HashSet<NodeId> graphSet = new HashSet<>();
            while (graphs.hasNext()) {
                graphs.next();
                NodeId graph = graphs.getId(SPOC.GRAPH);
                graphSet.add(graph);
            }
            for (NodeId graph : graphSet) {
                writer.printf("%s%n", backend.getValue(graph));
            }
        }
    }

}
