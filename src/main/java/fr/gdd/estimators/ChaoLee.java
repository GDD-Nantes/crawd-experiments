package fr.gdd.estimators;

import org.apache.jena.tdb2.store.NodeId;

import java.util.HashSet;
import java.util.Set;

/**
 * Chao Lee estimator for count-distinct when Nj and N are available.
 */
public class ChaoLee implements CountDistinctEstimator<ChaoLee.ChaoLeeSample> {

    Set<NodeId> distincts = new HashSet<>();
    Double sumOfNj = 0.;
    Double bigN;

    public ChaoLee() {}

    /**
     * To fix the N once and for all, useful to debug and test.
     * @param bigN The fixed big N that states the size of the full data.
     * @return this for convenience
     */
    public ChaoLee fixN (Double bigN) {
        this.bigN = bigN;
        return this;
    }

    @Override
    public CountDistinctEstimator<ChaoLeeSample> add(ChaoLeeSample newSample) {
        if (!distincts.contains(newSample.element)) {
            this.distincts.add(newSample.element);
            sumOfNj += newSample.frequency;
        }
        return this;
    }


    @Override
    public Double getEstimate() {
        return sumOfNj == 0. ? 0. : distincts.size()/(sumOfNj/this.bigN);
    }

    public record ChaoLeeSample(NodeId element, Double frequency){}
}
