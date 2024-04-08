package fr.gdd;

import fr.gdd.estimators.ChaoLee;
import fr.gdd.sage.generics.LazyIterator;
import fr.gdd.sage.interfaces.BackendIterator;
import fr.gdd.sage.interfaces.SPOC;
import fr.gdd.sage.jena.JenaBackend;
import org.apache.jena.atlas.lib.tuple.Tuple;
import org.apache.jena.base.Sys;
import org.apache.jena.dboe.trans.bplustree.ProgressJenaIterator;
import org.apache.jena.tdb2.store.NodeId;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Random;
public class SACSPO {

    public  static void getSampleSaCSPO_CDs_GROUPBY_CLASS(String pathToTDB2dataset, String outputfile, Integer samplesize, String predicateValue) throws IOException {;
        try (PrintWriter writer = new PrintWriter(new FileWriter(outputfile))) {
            writer.println("Subject CRWD Chao_Lee");
            ProgressJenaIterator.NB_WALKS = 1;
            JenaBackend backend = new JenaBackend(pathToTDB2dataset);
            NodeId is_a = backend.getId(predicateValue, SPOC.PREDICATE);

            int i = 0;
            HashMap<NodeId,Values> mapS = new HashMap<>();
            ChaoLee chaoLee = new ChaoLee();
            while (i < samplesize){
                //run the full query {?s a class ; ?p ?o .}
                ProgressJenaIterator s_isa_cl = (ProgressJenaIterator) ((LazyIterator) backend.search(backend.any(), is_a, backend.any())).iterator;
                Tuple<NodeId> s_isa_clRecord = s_isa_cl.getRandomSPO();

                NodeId sId = s_isa_clRecord.get(SPOC.SUBJECT);
                NodeId cl = s_isa_clRecord.get(SPOC.OBJECT);

                String subject = backend.getValue(sId);

                // Check if the cl is already in the map
                if (!mapS.containsKey(cl)) {
                    mapS.put(cl, new Values(0, 0, 0,0));
                }
                Values valsS = mapS.get(cl);
                ProgressJenaIterator sac = (ProgressJenaIterator) ((LazyIterator) backend.search(backend.any(), is_a, cl)).iterator;

                ProgressJenaIterator spo2 = (ProgressJenaIterator) ((LazyIterator) backend.search(sId, backend.any(), backend.any())).iterator;
                Tuple<NodeId> spo2Record = spo2.getRandomSPO();


                valsS.update(sac.cardinality()* spo2.cardinality(),sac.cardinality()* spo2.cardinality(),i,((sac.cardinality()* spo2.cardinality())/ spo2.cardinality()));
                mapS.put(cl,valsS);

                double estimateS = ((valsS.getSum_one_over_p_i_for_N()/ valsS.getS())/ valsS.getSum_one_over_p_i_for_fix_value())* valsS.getSum_ratio();

                chaoLee.fixN(valsS.getSum_one_over_p_i_for_N()/i).add(new ChaoLee.ChaoLeeSample(sId,sac.cardinality()* spo2.cardinality()));
                writer.printf("%s %f %f%n", subject,estimateS, chaoLee.getEstimate());
                i++;
            }
        }
    }
    public  static void getSampleSaCSPO_CDo_GROUPBY_CLASS(String pathToTDB2dataset, String outputfile, Integer samplesize, String predicateValue) throws IOException {;

        try (PrintWriter writer = new PrintWriter(new FileWriter(outputfile))) {
            writer.println("Class Object CRWD Chao_Lee");
            ProgressJenaIterator.NB_WALKS = 1;
            JenaBackend backend = new JenaBackend(pathToTDB2dataset);
            NodeId is_a = backend.getId(predicateValue, SPOC.PREDICATE);

            int i = 0;
            HashMap<NodeId,Values> mapO = new HashMap<>();
            HashMap<NodeId,ChaoLee> mapChaoLee = new HashMap<>();
            while (i < samplesize){
                //run the full query {?s a class ; ?p ?o .}
                ProgressJenaIterator s_isa_cl = (ProgressJenaIterator) ((LazyIterator) backend.search(backend.any(), is_a, backend.any())).iterator;
                Tuple<NodeId> s_isa_clRecord = s_isa_cl.getRandomSPO();

                NodeId sId = s_isa_clRecord.get(SPOC.SUBJECT);
                NodeId cl = s_isa_clRecord.get(SPOC.OBJECT);
                String classValue = backend.getValue(cl);

                if (!mapO.containsKey(cl)) {
                    mapO.put(cl, new Values(0, 0, 0,0));
                }
                Values valsO = mapO.get(cl);
                ProgressJenaIterator sac = (ProgressJenaIterator) ((LazyIterator) backend.search(backend.any(), is_a, cl)).iterator;

                ProgressJenaIterator spo = (ProgressJenaIterator) ((LazyIterator) backend.search(backend.any(), backend.any(), backend.any())).iterator;
                Tuple<NodeId> spoRecord = spo.getRandomSPO();

                NodeId oId = spoRecord.get(SPOC.OBJECT);
                String object = backend.getValue(oId);

                ProgressJenaIterator spo2 = (ProgressJenaIterator) ((LazyIterator) backend.search(backend.any(), backend.any(), oId)).iterator;
                double sac_count = sac.count();
                double spo_count = spo.count();
                double spo2_count = spo2.count();
                valsO.update((sac_count* spo_count),(sac_count* spo2_count),i,1);
                mapO.put(cl,valsO);

                double estimateO = ((valsO.getSum_one_over_p_i_for_N()/ valsO.getS())/ valsO.getSum_one_over_p_i_for_fix_value())* valsO.getSum_ratio();
                if (!mapChaoLee.containsKey(cl)) {
                    mapChaoLee.put(cl, new ChaoLee());
                }
                mapChaoLee.get(cl).fixN(valsO.getSum_one_over_p_i_for_N()/i).add(new ChaoLee.ChaoLeeSample(oId, sac_count*spo2_count));
                writer.printf("%s %s %f %f%n",classValue, object,estimateO, mapChaoLee.get(cl).getEstimate());
                i++;
            }
        }
    }

    public  static void getSampleSaCSPO_CDo_GROUPBY_CLASS_dbpedia(String pathToTDB2dataset, String outputfile, Integer samplesize, String predicateValue) throws IOException {;

        try (PrintWriter writer = new PrintWriter(new FileWriter(outputfile))) {
            writer.println("Object CRWD Chao_Lee");
            ProgressJenaIterator.NB_WALKS = 1;
            JenaBackend backend = new JenaBackend(pathToTDB2dataset);
            NodeId is_a = backend.getId(predicateValue, SPOC.PREDICATE);
            NodeId graph = backend.getId("<http://example.com/dbpedia>", SPOC.GRAPH);
            int i = 0;
            HashMap<NodeId,Values> mapO = new HashMap<>();
            ChaoLee chaoLee = new ChaoLee();
            while (i < samplesize){
                //run the full query {?s a class ; ?p ?o .}
                ProgressJenaIterator s_isa_cl = (ProgressJenaIterator) ((LazyIterator) backend.search(backend.any(), is_a, backend.any(),graph)).iterator;
                Tuple<NodeId> s_isa_clRecord = s_isa_cl.getRandomSPO();

                NodeId sId = s_isa_clRecord.get(SPOC.SUBJECT);
                NodeId cl = s_isa_clRecord.get(SPOC.OBJECT);
                String classValue = backend.getValue(cl);
                String subject = backend.getValue(sId);
                if (!mapO.containsKey(cl)) {
                    mapO.put(cl, new Values(0, 0, 0,0));
                }
                Values valsO = mapO.get(cl);
                ProgressJenaIterator sac = (ProgressJenaIterator) ((LazyIterator) backend.search(backend.any(), is_a, cl,graph)).iterator;

                ProgressJenaIterator spo2 = (ProgressJenaIterator) ((LazyIterator) backend.search(graph,sId, backend.any(), backend.any())).iterator;
                Tuple<NodeId> spo2Record = spo2.getRandomSPO();

                NodeId pId = spo2Record.get(2);
                String predicate = backend.getValue(pId);
                NodeId oId = spo2Record.get(3);
                String object = backend.getValue(oId);

                ProgressJenaIterator spo3 = (ProgressJenaIterator) ((LazyIterator) backend.search(graph,backend.any(), backend.any(), oId)).iterator;
                valsO.update((sac.cardinality()* spo2.cardinality()),(sac.cardinality()* spo2.cardinality()),i,((sac.cardinality()* spo2.cardinality())/ spo3.cardinality()));
                mapO.put(cl,valsO);

                double estimateO = ((valsO.getSum_one_over_p_i_for_N()/ valsO.getS())/ valsO.getSum_one_over_p_i_for_fix_value())* valsO.getSum_ratio();

                chaoLee.fixN(valsO.getSum_one_over_p_i_for_N()/i).add(new ChaoLee.ChaoLeeSample(oId,sac.count()* spo3.count()));
                writer.printf("%s %f %f%n", object,estimateO, chaoLee.getEstimate());
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

                double N = (sac.cardinality()* spo2.cardinality());

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

    public  static void Accept_Reject_SaCSPO_CDo_Role0(String pathToTDB2dataset, String outputfile, Integer samplesize) throws IOException {
        ;

        try (PrintWriter writer = new PrintWriter(new FileWriter(outputfile))) {
            writer.println("Object Olken CRWD Chao_Lee");
            ProgressJenaIterator.NB_WALKS = 10;
            JenaBackend backend = new JenaBackend(pathToTDB2dataset);
            NodeId is_a = backend.getId("<http://www.w3.org/1999/02/22-rdf-syntax-ns#type>", SPOC.PREDICATE);
            NodeId role0 = backend.getId("<http://db.uwaterloo.ca/~galuc/wsdbm/Role0>", SPOC.OBJECT);

            //iterate over the all s in Role0
            BackendIterator<NodeId,?> s_isa_role0 = backend.search(backend.any(), is_a, role0);
            HashMap<NodeId,Double> frequencySubject = new HashMap<>();
            while(s_isa_role0.hasNext()){
                s_isa_role0.next();
                NodeId sId = s_isa_role0.getId(SPOC.SUBJECT);
                frequencySubject.get(sId);
                BackendIterator<NodeId,?> s_p_o = backend.search(sId, backend.any(), backend.any());
                while (s_p_o.hasNext()){
                    s_p_o.next();
                    if(!frequencySubject.containsKey(sId)){
                        frequencySubject.put(sId,0.0);
                    }
                    frequencySubject.put(sId,frequencySubject.get(sId)+1);
                }

            }
            System.out.println(frequencySubject.size());

            ChaoLee chaoLee = new ChaoLee();
            double sum_for_N = 0;
            int i = 0;
            double sum_p = 0;
            double sum_total = 0;
            while (i < samplesize){
                //run the full query {?s a class ; ?p ?o .}
                ProgressJenaIterator s_isa_Role0 = (ProgressJenaIterator) ((LazyIterator) backend.search(backend.any(), is_a,role0)).iterator;
                Tuple<NodeId> s_isa_clRecord = s_isa_Role0.getRandomSPO();

                NodeId sId = s_isa_clRecord.get(SPOC.SUBJECT);

                ProgressJenaIterator Spo = (ProgressJenaIterator) ((LazyIterator) backend.search(sId, backend.any(), backend.any())).iterator;
                Tuple<NodeId> spoRecord = Spo.getRandomSPO();

                NodeId oId = spoRecord.get(SPOC.OBJECT);
                String object = backend.getValue(oId);

                ProgressJenaIterator SpO = (ProgressJenaIterator) ((LazyIterator) backend.search(sId, backend.any(), oId)).iterator;
                double s_isa_Role0_count = s_isa_Role0.count();
                double Spo_count = Spo.count();
                double SpO_count = SpO.count();
                sum_for_N+=s_isa_Role0_count*Spo_count;
                sum_p += (s_isa_Role0_count*Spo_count);
                sum_total += (s_isa_Role0_count*Spo_count)/(s_isa_Role0_count*SpO_count);
                chaoLee.fixN(sum_for_N/i).add(new ChaoLee.ChaoLeeSample(oId,s_isa_Role0_count*SpO_count));
                double estimateO = ((sum_for_N/i)/sum_p)*sum_total;
                double proba = 1/(s_isa_Role0_count*SpO_count);
                double alpha = frequencySubject.get(sId)/(frequencySubject.size()*319);
                if(proba < alpha){
                    writer.printf("%s accept %f %f%n", object,estimateO, chaoLee.getEstimate());
                    System.out.println("accept");
                }else{
                    writer.printf("%s reject %f %f%n", object,estimateO, chaoLee.getEstimate());
                    System.out.println("reject");
                }
                System.out.println("At i:"+i+", object: "+object+" crwd: "+estimateO+" chao_lee: "+chaoLee.getEstimate());
                i++;
            }
        }

    }
}
