package fr.gdd.estimators;

/**
 * The interface of sample-based count-distinct estimators.
 * @param <T> The type of the input to feed the statistics.
 */
public interface CountDistinctEstimator<T> {

    /**
     * Update the internal state of the estimator to take into account the new
     * element of the sample.
     * @param newSample The new sampled element.
     * @return the estimator itself, for convenience.
     */
    CountDistinctEstimator<T> add(T newSample);

    /**
     * @return The estimate based on gathered data.
     */
    Double getEstimate();

    /**
     * Most estimators work better when N is known.
     * @param n The total number of elements including duplicates.
     */
    CountDistinctEstimator<T> fixN(Double n);

}
