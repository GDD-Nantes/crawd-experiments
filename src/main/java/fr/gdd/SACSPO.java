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

public class SACSPO {

    public  static void getSampleSaCSPO_CDs_GROUPBY_CLASS(String pathToTDB2dataset, String outputfile, Integer samplesize, String predicateValue) throws IOException {;
        ProgressJenaIterator.rng = new Random(2);
        try (PrintWriter writer = new PrintWriter(new FileWriter(outputfile))) {
            writer.println("Subject,Class,Predicate,Object,CD(s)");
            ProgressJenaIterator.NB_WALKS = 1;
            JenaBackend backend = new JenaBackend(pathToTDB2dataset);
            NodeId is_a = backend.getId(predicateValue, SPOC.PREDICATE);

            int i = 0;
            HashMap<NodeId,Values> mapS = new HashMap<>();
            while (i < samplesize){
                //run the full query {?s a class ; ?p ?o .}
                ProgressJenaIterator s_isa_cl = (ProgressJenaIterator) ((LazyIterator) backend.search(backend.any(), is_a, backend.any())).iterator;
                Tuple<NodeId> s_isa_clRecord = s_isa_cl.getRandomSPO();

                NodeId sId = s_isa_clRecord.get(SPOC.SUBJECT);
                NodeId cl = s_isa_clRecord.get(SPOC.OBJECT);
                String classValue = backend.getValue(cl);
                String subject = backend.getValue(sId);

                // Check if the cl is already in the map
                if (!mapS.containsKey(cl)) {
                    mapS.put(cl, new Values(0, 0, 0,0));
                }
                Values valsS = mapS.get(cl);
                ProgressJenaIterator sac = (ProgressJenaIterator) ((LazyIterator) backend.search(backend.any(), is_a, cl)).iterator;

                ProgressJenaIterator spo2 = (ProgressJenaIterator) ((LazyIterator) backend.search(sId, backend.any(), backend.any())).iterator;
                Tuple<NodeId> spo2Record = spo2.getRandomSPO();

                NodeId pId = spo2Record.get(SPOC.PREDICATE);
                String predicate = backend.getValue(pId);
                NodeId oId = spo2Record.get(SPOC.OBJECT);
                String object = backend.getValue(oId);

                valsS.update(sac.cardinality()* spo2.cardinality(),sac.cardinality()* spo2.cardinality(),i,((sac.cardinality()* spo2.cardinality())/ spo2.cardinality()));
                mapS.put(cl,valsS);

                double estimateS = ((valsS.getSum_one_over_p_i_for_N()/ valsS.getS())/ valsS.getSum_one_over_p_i_for_fix_value())* valsS.getSum_ratio();
                writer.println(subject + "," + classValue + "," + predicate + "," + object + "," + estimateS );
                i++;
            }
        }
    }
    public  static void getSampleSaCSPO_CDo_GROUPBY_CLASS(String pathToTDB2dataset, String outputfile, Integer samplesize, String predicateValue) throws IOException {;
        ProgressJenaIterator.rng = new Random(2);
        try (PrintWriter writer = new PrintWriter(new FileWriter(outputfile))) {
            writer.println("Subject,Class,Predicate,Object,CD(o)");
            ProgressJenaIterator.NB_WALKS = 1;
            JenaBackend backend = new JenaBackend(pathToTDB2dataset);
            NodeId is_a = backend.getId(predicateValue, SPOC.PREDICATE);

            int i = 0;
            HashMap<NodeId,Values> mapO = new HashMap<>();
            while (i < samplesize){
                //run the full query {?s a class ; ?p ?o .}
                ProgressJenaIterator s_isa_cl = (ProgressJenaIterator) ((LazyIterator) backend.search(backend.any(), is_a, backend.any())).iterator;
                Tuple<NodeId> s_isa_clRecord = s_isa_cl.getRandomSPO();

                NodeId sId = s_isa_clRecord.get(SPOC.SUBJECT);
                NodeId cl = s_isa_clRecord.get(SPOC.OBJECT);
                String classValue = backend.getValue(cl);
                String subject = backend.getValue(sId);
                if (!mapO.containsKey(cl)) {
                    mapO.put(cl, new Values(0, 0, 0,0));
                }
                Values valsO = mapO.get(cl);
                ProgressJenaIterator sac = (ProgressJenaIterator) ((LazyIterator) backend.search(backend.any(), is_a, cl)).iterator;

                ProgressJenaIterator spo2 = (ProgressJenaIterator) ((LazyIterator) backend.search(sId, backend.any(), backend.any())).iterator;
                Tuple<NodeId> spo2Record = spo2.getRandomSPO();

                NodeId pId = spo2Record.get(SPOC.PREDICATE);
                String predicate = backend.getValue(pId);
                NodeId oId = spo2Record.get(SPOC.OBJECT);
                String object = backend.getValue(oId);

                ProgressJenaIterator spo3 = (ProgressJenaIterator) ((LazyIterator) backend.search(backend.any(), backend.any(), oId)).iterator;
                valsO.update((sac.cardinality()* spo2.cardinality()),(sac.cardinality()* spo2.cardinality()),i,((sac.cardinality()* spo2.cardinality())/ spo3.cardinality()));
                mapO.put(cl,valsO);

                double estimateO = ((valsO.getSum_one_over_p_i_for_N()/ valsO.getS())/ valsO.getSum_one_over_p_i_for_fix_value())* valsO.getSum_ratio();

                writer.println(subject + "," + classValue + "," + predicate + "," + object + "," + estimateO);
                i++;
            }
        }
    }







    public  static void getSampleSaCSPO_CDo_GROUPBY_CLASS_PREDICATE(String pathToTDB2dataset, String outputfile, Integer samplesize, String predicateValue) throws IOException {;
        try (PrintWriter writer = new PrintWriter(new FileWriter(outputfile))) {
            writer.println("Subject,Class,Predicate,Object,CD(o)");
            ProgressJenaIterator.NB_WALKS = 1;
            JenaBackend backend = new JenaBackend(pathToTDB2dataset);
            NodeId is_a = backend.getId(predicateValue, SPOC.PREDICATE);

            int i = 0;
            HashMap<NodeIdPair,Values> mapO = new HashMap<>();
            while (i < samplesize){
                ProgressJenaIterator s_isa_cl = (ProgressJenaIterator) ((LazyIterator) backend.search(backend.any(), is_a, backend.any())).iterator;
                Tuple<NodeId> s_isa_clRecord = s_isa_cl.getRandomSPO();
                NodeId cl = s_isa_clRecord.get(SPOC.OBJECT);
                NodeId sId = s_isa_clRecord.get(SPOC.SUBJECT);

                ProgressJenaIterator sac = (ProgressJenaIterator) ((LazyIterator) backend.search(backend.any(), is_a, cl)).iterator;

                ProgressJenaIterator spo1 = (ProgressJenaIterator) ((LazyIterator) backend.search(sId, backend.any(), backend.any())).iterator;
                Tuple<NodeId> spo1Record = spo1.getRandomSPO();
                NodeId pId = spo1Record.get(SPOC.PREDICATE);
                NodeId oId = spo1Record.get(SPOC.OBJECT);

                ProgressJenaIterator spo2 = (ProgressJenaIterator) ((LazyIterator) backend.search(backend.any(), pId, backend.any())).iterator;

                NodeIdPair key = new NodeIdPair(cl, pId);
                if(!mapO.containsKey(key)){
                    mapO.put(key, new Values(0, 0, 0,0));
                }
                Values valsO = mapO.get(key);

                ProgressJenaIterator spo3 = (ProgressJenaIterator) ((LazyIterator) backend.search(sId, pId, backend.any())).iterator;
                ProgressJenaIterator spo4 = (ProgressJenaIterator) ((LazyIterator) backend.search(sId, pId,oId)).iterator;
                //khong co s sao co p
                double N = sac.cardinality()* spo2.cardinality();

                double sum_one_over_p_i = 1/(sac.cardinality()* spo3.cardinality());

                double sum_one_over_p_i_over_F_i = (sum_one_over_p_i/ spo4.cardinality());
                valsO.update(N,sum_one_over_p_i,i,sum_one_over_p_i_over_F_i);
                mapO.put(key, valsO);

                String classValue = backend.getValue(cl);
                String predicate = backend.getValue(pId);
                String subject = backend.getValue(sId);
                String object = backend.getValue(oId);
                double estimateO = ((valsO.getSum_one_over_p_i_for_N()/ valsO.getS())/ valsO.getSum_one_over_p_i_for_fix_value())* valsO.getSum_ratio();
                writer.println(subject + "," + classValue + "," + predicate + "," + object + "," + estimateO);
                i++;
            }
        }
    }
    public  static void getSampleSaCSPO_CDs_GROUPBY_CLASS_PREDICATE(String pathToTDB2dataset, String outputfile, Integer samplesize, String predicateValue) throws IOException {;
        try (PrintWriter writer = new PrintWriter(new FileWriter(outputfile))) {
            writer.println("Subject,Class,Predicate,Object,CD(s)");
            ProgressJenaIterator.NB_WALKS = 1;
            JenaBackend backend = new JenaBackend(pathToTDB2dataset);
            NodeId is_a = backend.getId(predicateValue, SPOC.PREDICATE);

            int i = 0;
            HashMap<NodeIdPair,Values> mapS = new HashMap<>();
            while (i < samplesize){
                ProgressJenaIterator s_isa_cl = (ProgressJenaIterator) ((LazyIterator) backend.search(backend.any(), is_a, backend.any())).iterator;
                Tuple<NodeId> s_isa_clRecord = s_isa_cl.getRandomSPO();
                NodeId cl = s_isa_clRecord.get(SPOC.OBJECT);

                ProgressJenaIterator sac = (ProgressJenaIterator) ((LazyIterator) backend.search(backend.any(), is_a, cl)).iterator;
                Tuple<NodeId> sacRecord = sac.getRandomSPO();
                NodeId sId = sacRecord.get(SPOC.SUBJECT);

                ProgressJenaIterator spo1 = (ProgressJenaIterator) ((LazyIterator) backend.search(sId, backend.any(), backend.any())).iterator;
                NodeId pId = spo1.getRandomSPO().get(SPOC.PREDICATE);

                ProgressJenaIterator spo2 = (ProgressJenaIterator) ((LazyIterator) backend.search(backend.any(), pId, backend.any())).iterator;
                Tuple<NodeId> spo2Record = spo2.getRandomSPO();
                NodeIdPair key = new NodeIdPair(cl, pId);
                if(!mapS.containsKey(key)){
                    mapS.put(key, new Values(0, 0, 0,0));
                }
                Values valsS = mapS.get(key);
                ProgressJenaIterator spo3 = (ProgressJenaIterator) ((LazyIterator) backend.search(sId, pId, backend.any())).iterator;

                double N = (sac.cardinality()* spo2.cardinality();

                double sum_one_over_p_i = (sac.cardinality()* spo3.cardinality());

                double sum_one_over_p_i_over_F_i = sac.cardinality();
                valsS.update(N,sum_one_over_p_i,i,sum_one_over_p_i_over_F_i);
                mapS.put(key, valsS);

                String classValue = backend.getValue(cl);
                String predicate = backend.getValue(pId);
                String subject = backend.getValue(sId);
                String object = backend.getValue(spo2Record.get(SPOC.OBJECT));
                double estimateS = ((valsS.getSum_one_over_p_i_for_N()/ valsS.getS())/ valsS.getSum_one_over_p_i_for_fix_value())* valsS.getSum_ratio();
                writer.println(subject + "," + classValue + "," + predicate + "," + object + "," + estimateS);
                i++;
            }
        }
    }
}
