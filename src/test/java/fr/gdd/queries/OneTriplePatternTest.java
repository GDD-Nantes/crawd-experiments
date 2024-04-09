package fr.gdd.queries;

import fr.gdd.estimators.CRWD;
import fr.gdd.estimators.ChaoLee;
import fr.gdd.sage.databases.persistent.Watdiv10M;
import fr.gdd.sage.interfaces.SPOC;
import fr.gdd.sage.jena.JenaBackend;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.Set;

@Disabled
class OneTriplePatternTest {

    public static Double WATDIV_DISTINCT_S = 521_585.;
    public static Double WATDIV_DISTINCT_P = 86.;
    public static Double WATDIV_DISTINCT_O = 1_005_832.;

    @Test
    public void distinct_s() {
        Watdiv10M watdiv10M = new Watdiv10M(Optional.empty());
        JenaBackend backend = new JenaBackend(watdiv10M.dbPath_asStr);

        OneTriplePattern spoExperiment = (OneTriplePattern) new OneTriplePattern(backend, Set.of(SPOC.PREDICATE))
                .setEstimator(new CRWD())
                .setStep(10)
                .setUniform(false)
                .setSeed(2)
                .setEstimatedCount(1)
                .fixN();

        while(spoExperiment.getNbSteps() < 10_000_000) {
            Double estimate = spoExperiment.sample().get(Set.of());
            System.out.printf("%s %.2f %f%n", spoExperiment.getNbSteps(), estimate, getRelativeError(WATDIV_DISTINCT_P, estimate));
        }
    }

    /**
     * @param truth The true value that we expect our estimator to reach when the sample is infinite
     * @param actual The actual estimated value.
     * @return The relative error. Careful: it can be higher than 1.
     */
    public static Double getRelativeError(Double truth, Double actual) {
        return Math.abs(truth-actual) / truth;
    }

    public static Double getRelativeErrorPercent(Double truth, Double actual) {
        return getRelativeError(truth, actual) * 100.;
    }
}