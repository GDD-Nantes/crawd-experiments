package fr.gdd.estimators;

import java.util.Objects;

/**
 * Count-(using Random Walks)Distinct. Our home-made count-distinct estimator inspired
 * by Wander Join, and the "Coverage" of Chaos Lee.
 * In addition, CRWD avoids the trap of the birthday paradox.
 */
public class CRWD implements CountDistinctEstimator<CRWD.CRWDSample> {

    // #A Wander Join
    Double bigN = null;
    Double sumBigN = 0.;
    Double fullSampleSize = 0.;

    // #B Count-distinct
    Double sumOfProbas = 0.;
    Double sumOfProbaOverNj = 0.;

    /**
     * To fix the N once and for all, useful to debug and test.
     * @param bigN The fixed big N that states the size of the full data.
     * @return this for convenience
     */
    public CRWD fixN (Double bigN) {
        this.bigN = bigN;
        return this;
    }

    @Override
    public CountDistinctEstimator<CRWDSample> add(CRWDSample newSample) {
        sumBigN += 1./newSample.probaOfElement;
        fullSampleSize += 1;

        if (Objects.nonNull(newSample.frequency)) {
            sumOfProbas += 1./newSample.probaOfElement;
            sumOfProbaOverNj += (newSample.probaOfElement / newSample.frequency);
        }
        return this;
    }

    @Override
    public Double getEstimate() {
        if (fullSampleSize == 0.) return 0.;
        if (sumOfProbas == 0.) return Objects.nonNull(bigN) ? bigN : sumBigN/fullSampleSize;

        if (Objects.nonNull(bigN)) {
            return (bigN/sumOfProbas) * (sumOfProbaOverNj);
        }
        return ((sumBigN/fullSampleSize)/sumOfProbas) * (sumOfProbaOverNj);
    }

    public record CRWDSample(Double probaOfElement, Double frequency) {}
}
