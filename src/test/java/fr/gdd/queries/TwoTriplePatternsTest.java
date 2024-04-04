package fr.gdd.queries;

import fr.gdd.estimators.CRWD;
import fr.gdd.estimators.ChaoLee;
import fr.gdd.sage.databases.persistent.Watdiv10M;
import fr.gdd.sage.interfaces.SPOC;
import fr.gdd.sage.jena.JenaBackend;
import org.apache.jena.tdb2.store.NodeId;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@Disabled
class TwoTriplePatternsTest {

    @Test
    public void two_triple_pattern() {
        Watdiv10M watdiv10M = new Watdiv10M(Optional.empty());
        JenaBackend backend = new JenaBackend(watdiv10M.dbPath_asStr);

        NodeId is_a = backend.getId("<http://www.w3.org/1999/02/22-rdf-syntax-ns#type>", SPOC.PREDICATE);

        TwoTriplePatterns twoTPs = (TwoTriplePatterns) new TwoTriplePatterns(backend, Set.of(TwoTriplePatterns.OO))
                .bindSS(SPOC.SUBJECT)
                .setEstimator(new CRWD())
                .setStep(10)
                .setUniform(false)
                .setSeed(1)
                .fixN()
                .bindP(is_a);

        while(twoTPs.getNbSteps() < 10_000_000) {
            Double estimate = twoTPs.sample();
            System.out.printf("%s %.2f%n", twoTPs.getNbSteps(), estimate);
        }
    }

}