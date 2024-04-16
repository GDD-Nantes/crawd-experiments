package fr.gdd;

import fr.gdd.estimators.ChaoLee;
import fr.gdd.sage.generics.LazyIterator;
import fr.gdd.sage.interfaces.BackendIterator;
import fr.gdd.sage.interfaces.SPOC;
import fr.gdd.sage.jena.JenaBackend;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.jena.atlas.lib.tuple.Tuple;
import org.apache.jena.dboe.trans.bplustree.ProgressJenaIterator;
import org.apache.jena.tdb2.store.NodeId;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Random;
import java.util.Set;

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
                Pair<Tuple<NodeId>, Double> sacRecord = sac.getRandomSPOWithProbability();

                ProgressJenaIterator spo2 = (ProgressJenaIterator) ((LazyIterator) backend.search(sId, backend.any(), backend.any())).iterator;
                Pair<Tuple<NodeId>, Double> spo2Record = spo2.getRandomSPOWithProbability();
                double proba_N = (sacRecord.getRight()* spo2Record.getRight());

                valsS.update(1/proba_N,1/proba_N,i,(1/proba_N)/ (1/ spo2Record.getRight()));
                mapS.put(cl,valsS);

                double estimateS = ((valsS.getSum_one_over_p_i_for_N()/ valsS.getS())/ valsS.getSum_one_over_p_i_for_fix_value())* valsS.getSum_ratio();
                chaoLee.fixN(valsS.getSum_one_over_p_i_for_N()/i).add(new ChaoLee.ChaoLeeSample(Set.of(sId),proba_N,1/proba_N));
                writer.printf("%s %f %f%n", subject,estimateS, chaoLee.getEstimate());
                i++;
            }
        }
    }
    public  static void getSampleSaCSPO_CDo_GROUPBY_CLASS(String pathToTDB2dataset, String outputfile, Integer samplesize, String predicateValue) throws IOException {
        ;

        try (PrintWriter writer = new PrintWriter(new FileWriter(outputfile))) {
            writer.println("Class Object CRWD Chao_Lee");
            ProgressJenaIterator.NB_WALKS = 10;
            JenaBackend backend = new JenaBackend(pathToTDB2dataset);
            NodeId is_a = backend.getId(predicateValue, SPOC.PREDICATE);

            int i = 0;
            HashMap<NodeId, Values> mapO = new HashMap<>();
            HashMap<NodeId, ChaoLee> mapChaoLee = new HashMap<>();
            while (i < samplesize) {
                //run the full query {?s a class ; ?p ?o .}
                ProgressJenaIterator s_isa_cl = (ProgressJenaIterator) ((LazyIterator) backend.search(backend.any(), is_a, backend.any())).iterator;
                Tuple<NodeId> s_isa_clRecord = s_isa_cl.getRandomSPO();

                NodeId sId = s_isa_clRecord.get(SPOC.SUBJECT);
                NodeId cl = s_isa_clRecord.get(SPOC.OBJECT);
                String classValue = backend.getValue(cl);

                if (!mapO.containsKey(cl)) {
                    mapO.put(cl, new Values(0, 0, 0, 0));
                }
                Values valsO = mapO.get(cl);
                ProgressJenaIterator sac = (ProgressJenaIterator) ((LazyIterator) backend.search(backend.any(), is_a, cl)).iterator;
                Pair<Tuple<NodeId>, Double> sacRecord = sac.getRandomSPOWithProbability();
                ProgressJenaIterator Spo = (ProgressJenaIterator) ((LazyIterator) backend.search(sId, backend.any(), backend.any())).iterator;
                Pair<Tuple<NodeId>, Double> SpoRecord = Spo.getRandomSPOWithProbability();

                NodeId oId = SpoRecord.getLeft().get(SPOC.OBJECT);
                String object = backend.getValue(oId);
                ProgressJenaIterator SpO = (ProgressJenaIterator) ((LazyIterator) backend.search(sId, backend.any(), oId)).iterator;
                Pair<Tuple<NodeId>, Double> SpORecord = SpO.getRandomSPOWithProbability();

                ProgressJenaIterator spO = (ProgressJenaIterator) ((LazyIterator) backend.search(backend.any(), backend.any(), oId)).iterator;
                Pair<Tuple<NodeId>, Double> spORecord = spO.getRandomSPOWithProbability();
                NodeId s2Id = spORecord.getLeft().get(SPOC.SUBJECT);
                double one_over_proba = 0;
                if (sId != s2Id) {
                    one_over_proba = 0;
                } else {
                    one_over_proba = 1/(sacRecord.getRight() * spORecord.getRight());
                }

                double proba_N = (sacRecord.getRight() * SpoRecord.getRight());
                valsO.update(1 / proba_N,  one_over_proba, i, ( one_over_proba) / (1 / (sacRecord.getRight() * SpORecord.getRight())));

                mapO.put(cl, valsO);
                if (!mapChaoLee.containsKey(cl)) {
                    mapChaoLee.put(cl, new ChaoLee());
                }
                ChaoLee chaoLee = mapChaoLee.get(cl);
                chaoLee.fixN(valsO.getSum_one_over_p_i_for_N() / i).add(new ChaoLee.ChaoLeeSample(Set.of(oId), proba_N, (1 / (sacRecord.getRight() * SpORecord.getRight()))));
                double estimateO = ((valsO.getSum_one_over_p_i_for_N() / valsO.getS()) / valsO.getSum_one_over_p_i_for_fix_value()) * valsO.getSum_ratio();

                writer.printf("%s %s %f %f%n", classValue, object, estimateO, mapChaoLee.get(cl).getEstimate());
                System.out.println("At i:" + i + ", class: " + classValue + ", object: " + object + " crwd: " + estimateO + " chao_lee: " + mapChaoLee.get(cl).getEstimate());
                i++;
            }
        }
    }

    public  static void Accept_Reject_SaCSPO_CDo_Role0(String pathToTDB2dataset, String outputfile, Integer samplesize) throws IOException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(outputfile))) {
            writer.println("Object Olken CRWD Chao_Lee");
            ProgressJenaIterator.NB_WALKS = 10;
            JenaBackend backend = new JenaBackend(pathToTDB2dataset);
            NodeId is_a = backend.getId("<http://www.w3.org/1999/02/22-rdf-syntax-ns#type>", SPOC.PREDICATE);
            NodeId role0 = backend.getId("<http://db.uwaterloo.ca/~galuc/wsdbm/Role0>", SPOC.OBJECT);

            //iterate over the all s in Role0
            BackendIterator<NodeId, ?> s_isa_role0 = backend.search(backend.any(), is_a, role0);
            HashMap<NodeId, Double> frequencySubject = new HashMap<>();
            while (s_isa_role0.hasNext()) {
                s_isa_role0.next();
                NodeId sId = s_isa_role0.getId(SPOC.SUBJECT);
                frequencySubject.get(sId);
                BackendIterator<NodeId, ?> s_p_o = backend.search(sId, backend.any(), backend.any());
                while (s_p_o.hasNext()) {
                    s_p_o.next();
                    if (!frequencySubject.containsKey(sId)) {
                        frequencySubject.put(sId, 0.0);
                    }
                    frequencySubject.put(sId, frequencySubject.get(sId) + 1);
                }

            }
            System.out.println(frequencySubject.size());

            ChaoLee chaoLee = new ChaoLee();
            double sum_for_N = 0;
            int i = 1;
            double sum_p = 0;
            double sum_total = 0;
            while (i < samplesize) {
                //run the full query {?s a class ; ?p ?o .}
                ProgressJenaIterator s_isa_Role0 = (ProgressJenaIterator) ((LazyIterator) backend.search(backend.any(), is_a, role0)).iterator;
                Pair<Tuple<NodeId>, Double> s_isa_Role0Record = s_isa_Role0.getRandomSPOWithProbability();

                NodeId sId = s_isa_Role0Record.getLeft().get(SPOC.SUBJECT);

                ProgressJenaIterator Spo = (ProgressJenaIterator) ((LazyIterator) backend.search(sId, backend.any(), backend.any())).iterator;
                Pair<Tuple<NodeId>, Double> spoRecord = Spo.getRandomSPOWithProbability();
            }
        }

    }
}
