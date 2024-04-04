package fr.gdd.estimators;

import com.github.jsonldjava.utils.Obj;
import org.apache.jena.tdb2.store.NodeId;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Chao Lee estimator for count-distinct when Nj and N are available.
 */
public class ChaoLee implements CountDistinctEstimator<ChaoLee.ChaoLeeSample> {


    Set<Set<NodeId>> distincts = new HashSet<>();
    Double sumOfNj = 0.;

    // Wander Join
    Double sampleSize = 0.;
    Double sumOfProba = 0.;
    Double bigN;

    // Debug
    Long nbDuplicates = 0L;

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
        ++sampleSize;
        sumOfProba += 1./newSample.proba;
        if (!distincts.contains(newSample.element)) {
            this.distincts.add(newSample.element);
            sumOfNj += newSample.frequency;
        } else {
            nbDuplicates += 1;
        }
        return this;
    }

    @Override
    public Double getEstimate() {
        if (sampleSize == 0.) return 0.;

        Double n = Objects.nonNull(bigN) ? bigN : (sumOfProba/sampleSize);

        return sumOfNj==0. ? 0. : distincts.size()/(sumOfNj/n);
    }

    public record ChaoLeeSample(Set<NodeId> element, Double proba, Double frequency){}
}
