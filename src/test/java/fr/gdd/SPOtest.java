package fr.gdd;

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
import org.apache.jena.dboe.trans.bplustree.ProgressJenaIterator;

import java.util.List;
import java.util.Map;
import java.util.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SPOtest {

    static Dataset dataset = null;
    static JenaBackend backend = null;

    static NodeId predicate = null;
    static NodeId any = null;


    public static class CsvUtil {
        public static List<Map<String, Object>> convertToMapList(List<String[]> rows, String[] headers) {
            List<Map<String, Object>> dataList = new ArrayList<>();

            for (String[] row : rows) {
                Map<String, Object> data = new HashMap<>();
                for (int i = 0; i < headers.length && i < row.length; i++) {
                    switch (headers[i]) {
                        case "Class":
                        case "Predicate":
                            data.put(headers[i], "<" + row[i] + ">");
                            break;
                        case "Distinct Object Count":
                            try {
                                data.put(headers[i], Integer.parseInt(row[i]));
                            } catch (NumberFormatException e) {
                                e.printStackTrace();
                            }
                            break;
                        case "Distinct Subject Count":
                            try {
                                data.put(headers[i], Integer.parseInt(row[i]));
                            } catch (NumberFormatException e) {
                                e.printStackTrace();
                            }
                            break;
                        default:
                            data.put(headers[i], row[i]);
                            break;
                    }
                }
                dataList.add(data);
            }

            return dataList;
        }
    }
    // we have script to get the ground truth. No need to run this test
    @Disabled
    @Test
    public void find_ground_truth_in_SPO() throws IOException {
        String outputfile = "sample_results/ground_truth_SPO.csv";
        try (PrintWriter writer = new PrintWriter(new FileWriter(outputfile))){
            writer.println("CDs,CDp,CDo");
            ProgressJenaIterator.NB_WALKS = 1000;
            JenaBackend backend = new JenaBackend("/GDD/WATDIV");
            BackendIterator<NodeId, ?> spo = (BackendIterator) backend.search(backend.any(), backend.any(), backend.any());
            HashMap<NodeId,Integer> frequencySubject = new HashMap<>();
            HashMap<NodeId,Integer> frequencyPredicate = new HashMap<>();
            HashMap<NodeId,Integer> frequencyObject = new HashMap<>();

            while (spo.hasNext()) {
                spo.next();
                // ?s ?p ?o
                BackendIterator<NodeId, ?> sR = backend.search(spo.getId(SPOC.SUBJECT), backend.any(), backend.any());
                while (sR.hasNext()) {
                    sR.next();
                    if (!frequencySubject.containsKey(sR.getId(SPOC.SUBJECT))){
                        frequencySubject.put(sR.getId(SPOC.SUBJECT),0);}
                    frequencySubject.put(sR.getId(SPOC.SUBJECT),frequencySubject.get(sR.getId(SPOC.SUBJECT))+1);

                }
                BackendIterator<NodeId, ?> pR = backend.search(backend.any(), spo.getId(SPOC.PREDICATE), backend.any());
                while (pR.hasNext()) {
                    pR.next();
                    if (!frequencyPredicate.containsKey(pR.getId(SPOC.PREDICATE))){
                        frequencyPredicate.put(pR.getId(SPOC.PREDICATE),0);}
                    frequencyPredicate.put(pR.getId(SPOC.PREDICATE),frequencyPredicate.get(pR.getId(SPOC.PREDICATE))+1);
                }
                BackendIterator<NodeId, ?> oR = backend.search(backend.any(), backend.any(), spo.getId(SPOC.OBJECT));
                while (oR.hasNext()) {
                    oR.next();
                    if (!frequencyObject.containsKey(oR.getId(SPOC.OBJECT))){
                        frequencyObject.put(oR.getId(SPOC.OBJECT),0);}
                    frequencyObject.put(oR.getId(SPOC.OBJECT),frequencyObject.get(oR.getId(SPOC.OBJECT))+1);
                }
            }
            writer.printf("%d,%d,%d%n",frequencySubject.size(),frequencyPredicate.size(),frequencyObject.size());
        }
    }


    // DATASET LEVEL - VoID

    //QB3,4,5
    @Disabled
    @Test
    public void getSampleSPO_CDSPO() throws IOException {
        String outputfileS = "sample_results/CDs_in_spo.csv";
        String outputfileP = "sample_results/CDp_in_spo.csv";
        String outputfileO = "sample_results/CDo_in_spo.csv";

        try (PrintWriter writerS = new PrintWriter(new FileWriter(outputfileS));
             PrintWriter writerP = new PrintWriter(new FileWriter(outputfileP));
             PrintWriter writerO = new PrintWriter(new FileWriter(outputfileO))) {

            writerS.println("Subject,1/Probability,CDsubject");
            writerP.println("Predicate,1/Probability,CDpredicate");
            writerO.println("Object,1/Probability,CDobject");

            ProgressJenaIterator.NB_WALKS = 1000;
            JenaBackend backend = new JenaBackend("/GDD/WATDIV");
            LazyIterator spo = (LazyIterator) backend.search(backend.any(), backend.any(), backend.any());
            ProgressJenaIterator spoR = (ProgressJenaIterator) spo.iterator;
            int i = 0;
            double sum_s = 0.;
            double sum_p = 0.;
            double sum_o = 0.;
            double sum_probas = 0.;
            double N = spoR.count();

            while (i < N) {
                Pair<Tuple<NodeId>, Double> rWp = spoR.getRandomSPOWithProbability();
                Tuple<NodeId> ids = rWp.getLeft();
                Double proba = rWp.getRight();
                //get sample
                String subject = backend.getValue(ids.get(0));
                String predicate = backend.getValue(ids.get(1));
                String object = backend.getValue(ids.get(2));

                // perform formula
                LazyIterator s = (LazyIterator) backend.search(ids.get(0), backend.any(), backend.any());
                ProgressJenaIterator sR = (ProgressJenaIterator) s.iterator;
                LazyIterator p = (LazyIterator) backend.search(backend.any(), ids.get(1), backend.any());
                ProgressJenaIterator pR = (ProgressJenaIterator) p.iterator;
                LazyIterator o = (LazyIterator) backend.search(backend.any(), backend.any(), ids.get(2));
                ProgressJenaIterator oR = (ProgressJenaIterator) o.iterator;
                sum_s += (1/proba) / sR.count();
                sum_p += (1/proba) / pR.count();
                sum_o += (1/proba) / oR.count();
                sum_probas += (1/proba);
                //count distinct for non uniform sample
                double estimateS = (N/ sum_probas)* sum_s;
                double estimateP = (N/ sum_probas)* sum_p;
                double estimateO = (N/ sum_probas)* sum_o;

                //write to file
                writerS.printf("%s,%f,%f%n", subject, 1/proba, estimateS);
                writerP.printf("%s,%f,%f%n", predicate, 1/proba, estimateP);
                writerO.printf("%s,%f,%f%n", object, 1/proba, estimateO);

                i++;
            }
        }
    }
    //QB2
    @Disabled
    @Test
    public void CDc_in_sac() throws IOException {
        String outputfile = "sample_results/CDc_in_sac.csv";
        try (PrintWriter writer = new PrintWriter(new FileWriter(outputfile))){
            writer.println("Class,1/Probability,CDclass");
            ProgressJenaIterator.NB_WALKS = 1000;
            JenaBackend backend = new JenaBackend("/GDD/WATDIV");
            NodeId is_a = backend.getId("<http://www.w3.org/1999/02/22-rdf-syntax-ns#type>", SPOC.PREDICATE);
            ProgressJenaIterator SPO = (ProgressJenaIterator) ((LazyIterator) backend.search(backend.any(), is_a, backend.any())).iterator;
            double N = SPO.count();
            double sum_c = 0;
            double sum_probas = 0;
            int i = 0;
            while (i < N) {
                Pair<Tuple<NodeId>, Double> spoR = SPO.getRandomSPOWithProbability();
                NodeId cId = spoR.getLeft().get(SPOC.OBJECT);
                String classValue = backend.getValue(cId);
                Double firstTripleProba = spoR.getRight();
                sum_probas += (1 / firstTripleProba);
                LazyIterator c = (LazyIterator) backend.search(backend.any(), is_a, cId);
                ProgressJenaIterator cR = (ProgressJenaIterator) c.iterator;
                sum_c += (1 / firstTripleProba) / cR.count();
                double estimateC = (N/ sum_probas) * sum_c;
                writer.printf("%s,%.1f,%f%n", classValue, 1 / firstTripleProba, estimateC);
                i += 1;
            }
        }
    }

    @Disabled
    @Test
    public void CDc_in_sac_TEST() throws IOException {
        String outputfile = "sample_results/CDc_in_sac_test.csv";
        try (PrintWriter writer = new PrintWriter(new FileWriter(outputfile))){
            writer.println("Class,1/Probability,CDclass");
            ProgressJenaIterator.NB_WALKS = 1000;
            JenaBackend backend = new JenaBackend("/GDD/WATDIV");
            NodeId is_a = backend.getId("<http://www.w3.org/1999/02/22-rdf-syntax-ns#type>", SPOC.PREDICATE);
            ProgressJenaIterator SPO = (ProgressJenaIterator) ((LazyIterator) backend.search(backend.any(), is_a, backend.any())).iterator;
            double N = SPO.count();
            double sum_c = 0;
            double sum_probas = 0;
            int i = 0;
            while (i < N) {
                Pair<Tuple<NodeId>, Double> spoR = SPO.getUniformRandomSPOWithProbability();
                NodeId cId = spoR.getLeft().get(SPOC.OBJECT);
                String classValue = backend.getValue(cId);
                Double firstTripleProba = spoR.getRight();
                sum_probas += (1 / firstTripleProba);
                LazyIterator c = (LazyIterator) backend.search(backend.any(), is_a, cId);
                ProgressJenaIterator cR = (ProgressJenaIterator) c.iterator;
                sum_c += (1 / firstTripleProba) / cR.count();
                double estimateC = (N/ sum_probas) * sum_c;
                writer.printf("%s,%.1f,%f%n", classValue, 1 / firstTripleProba, estimateC);
                i += 1;
            }
        }
    }
}
