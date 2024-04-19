package fr.gdd.queries;

import fr.gdd.estimators.CRWD;
import fr.gdd.estimators.ChaoLee;
import fr.gdd.sage.interfaces.SPOC;
import fr.gdd.sage.jena.JenaBackend;
import org.apache.jena.tdb2.store.NodeId;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Disabled
class TwoTriplePatternsTest {

    public static String WATDIV_MOST_FREQUENT_CLASS = "<http://db.uwaterloo.ca/~galuc/wsdbm/Role0>";
    //public static Double WATDIV_MOST_FREQUENT_CLASS_DISTINCT_O = 402344.;
    @Test
    public void sac_spo_gb_c_CD_o_watdiv() {
        JenaBackend backend = new JenaBackend("/WATDIV");

        NodeId is_a = backend.getId("<http://www.w3.org/1999/02/22-rdf-syntax-ns#type>", SPOC.PREDICATE);
        NodeId c = backend.getId(WATDIV_MOST_FREQUENT_CLASS, SPOC.OBJECT);

        TwoTriplePatterns twoTPs = ((TwoTriplePatterns) new TwoTriplePatterns(backend, Set.of(TwoTriplePatterns.OO))
                .bindSS(SPOC.SUBJECT)
                .bindS(SPOC.SUBJECT)
                .bindP(is_a)
                .bindO(c)
                .groupBy(SPOC.OBJECT)
                .setEstimator(new CRWD())
                //.setEstimator(new ChaoLee())
                .setStep(10)
                .setUniform(false)
                .setSeed(1)
                //.setExactCount())
                .setEstimatedCount(100));

        while(twoTPs.getNbSteps() < (twoTPs.getBigN() / 1000)) {
            // Double estimate = twoTPs.sample().get(Set.of());
            // System.out.printf("%s %.2f %.2f%n", twoTPs.getNbSteps(), estimate,
            //        OneTriplePatternTest.getRelativeErrorPercent(WATDIV_MOST_FREQUENT_CLASS_DISTINCT_O, estimate));
            Map<Set<NodeId>, Double> group2estimate = twoTPs.sample();
            StringBuilder formatted = new StringBuilder();
            formatted.append(twoTPs.getNbSteps() + ": ");
            for (Set<NodeId> group : group2estimate.keySet()) {
                formatted.append(group.stream().map(backend::getValue).collect(Collectors.toList())).append(" => ").append(group2estimate.get(group)).append(" ; ");
            }
            System.out.println(formatted);
        }
    }

    @Test
    public void sac_spo_gb_c_CD_o_dbpedia() {
        JenaBackend backend = new JenaBackend("/LargeRDFBench");

        NodeId is_a = backend.getId("<http://www.w3.org/1999/02/22-rdf-syntax-ns#type>", SPOC.PREDICATE);
        NodeId g = backend.getId("<http://example.com/dbpedia>", SPOC.GRAPH);
        NodeId c = backend.getId("<http://dbpedia.org/ontology/MusicalWork>", SPOC.OBJECT);
        TwoTriplePatterns twoTPs = ((TwoTriplePatterns) new TwoTriplePatterns(backend, Set.of(TwoTriplePatterns.OO))
                .bindSS(SPOC.SUBJECT)
                .bindS(SPOC.SUBJECT)
                .bindP(is_a)
                .bindO(c)
                .groupBy(SPOC.OBJECT)
                .setGraph(g)
                .setEstimator(new CRWD())
                //.setEstimator(new ChaoLee())
                .setStep(10)
                .setUniform(false)
                .setSeed(5)
                //.setExactCount())
                .setEstimatedCount(100))
                ;

        while(twoTPs.getNbSteps() < (twoTPs.getBigN()/1000)) {
          Map<Set<NodeId>, Double> group2estimate = twoTPs.sample();
            StringBuilder formatted = new StringBuilder();
            formatted.append(twoTPs.getNbSteps() + ": ");
            for (Set<NodeId> group : group2estimate.keySet()) {
                formatted.append(group.stream().map(backend::getValue).collect(Collectors.toList())).append(" => ").append(group2estimate.get(group)).append(" ; ");
            }
            System.out.println(formatted);
        }
    }

    @Test
    public void sac_spo_gb_c_CD_o_wdbench() {
        JenaBackend backend = new JenaBackend("/WDBench");

        NodeId a = backend.getId("<http://www.wikidata.org/prop/direct/P131>", SPOC.PREDICATE);
        NodeId c = backend.getId("<http://www.wikidata.org/entity/Q1085>", SPOC.OBJECT);
        TwoTriplePatterns twoTPs = ((TwoTriplePatterns) new TwoTriplePatterns(backend, Set.of(TwoTriplePatterns.OO))
                .bindSS(SPOC.SUBJECT)
                .bindS(SPOC.SUBJECT)
                .bindP(a)
                .bindO(c)
                .groupBy(SPOC.OBJECT)
                //.setEstimator(new CRWD())
                .setEstimator(new ChaoLee())
                .setStep(100)
                .setUniform(false)
                .setSeed(5)
                //.setExactCount())
                .setEstimatedCount(1000));

        while(twoTPs.getNbSteps() < (twoTPs.getBigN() / 10000)) {
            Map<Set<NodeId>, Double> group2estimate = twoTPs.sample();
            StringBuilder formatted = new StringBuilder();
            formatted.append(twoTPs.getNbSteps() + ": ");
            for (Set<NodeId> group : group2estimate.keySet()) {
                formatted.append(group.stream().map(backend::getValue).collect(Collectors.toList())).append(" => ").append(group2estimate.get(group)).append(" ; ");
            }
            System.out.println(formatted);
        }
    }


    @Test
    public void sac_spo_gb_c_CD_s () {
        //Watdiv10M watdiv10M = new Watdiv10M(Optional.empty());
        JenaBackend backend = new JenaBackend("/WATDIV");

        TwoTriplePatterns twoTPs = ((TwoTriplePatterns) new TwoTriplePatterns(backend, Set.of(SPOC.SUBJECT))
                .bindSS(SPOC.SUBJECT)
                .bindS(SPOC.SUBJECT)
                .bindP(backend.getId("<http://www.w3.org/1999/02/22-rdf-syntax-ns#type>", SPOC.PREDICATE))
                //.bindO(c)
                .groupBy(SPOC.OBJECT)
                .setEstimator(new CRWD())
                .setStep(10)
                .setUniform(false)
                .setSeed(1)
                .setExactCount())
                // .setEstimatedCount(10_000))
                .fixN(); // must be after binds and estimator


        // System.out.println(10*twoTPs.getBigN()/100);
        // while(twoTPs.getNbSteps() < 10*twoTPs.getBigN()/100) {
        while(twoTPs.getNbSteps() < 1_000_000) {
            // Double estimate = twoTPs.sample().get(Set.of());
            // System.out.printf("%s %.2f %.2f%n", twoTPs.getNbSteps(), estimate,
            //        OneTriplePatternTest.getRelativeErrorPercent(WATDIV_MOST_FREQUENT_CLASS_DISTINCT_O, estimate));

            Map<Set<NodeId>, Double> group2estimate = twoTPs.sample();
            StringBuilder formatted = new StringBuilder();
            formatted.append(twoTPs.getNbSteps() + ": ");
            for (Set<NodeId> group : group2estimate.keySet()) {
                formatted.append(group.stream().map(backend::getValue).collect(Collectors.toList())).append(" => ").append(group2estimate.get(group)).append(" ; ");
            }
            System.out.println(formatted);
        }
    }



}