package fr.gdd.queries;

import fr.gdd.SPO;
import fr.gdd.estimators.CRWD;
import fr.gdd.estimators.ChaoLee;
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
    public void distinct_p() {
        JenaBackend backend = new JenaBackend("/GDD/WATDIV");

        OneTriplePattern spoExperiment = (OneTriplePattern) new OneTriplePattern(backend, Set.of(SPOC.PREDICATE))
                .setEstimator(new CRWD())
                .setStep(1000)
                .setUniform(true)
                .setSeed(1)
                .setExactCount();
                //.setEstimatedCount(1000);
                //.fixN();

        while(spoExperiment.getNbSteps() < 1_100_000) {
            Double estimate = spoExperiment.sample().get(Set.of());
            double relativeError = getRelativeErrorPercent(WATDIV_DISTINCT_P, estimate);
            System.out.printf("%s %.2f %f%n", spoExperiment.getNbSteps(), estimate, relativeError);

        }
    }

    /**
     * @param truth The true value that we expect our estimator to reach when the sample is infinite
     * @param actual The actual estimated value.
     * @return The relative error. Careful: it can be higher than 1.
     */
    public static Double getRelativeError(Double truth, Double actual) {
        return (truth-actual) / truth;
    }

    public static Double getRelativeErrorPercent(Double truth, Double actual) {
        return getRelativeError(truth, actual) * 100.;
    }
}