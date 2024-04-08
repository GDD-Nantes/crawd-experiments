package fr.gdd.queries;

import fr.gdd.estimators.CountDistinctEstimator;
import fr.gdd.sage.generics.LazyIterator;
import fr.gdd.sage.jena.JenaBackend;
import org.apache.jena.atlas.lib.tuple.Tuple;
import org.apache.jena.dboe.trans.bplustree.ProgressJenaIterator;
import org.apache.jena.tdb2.store.NodeId;

import java.util.Objects;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

public class ConfigCountDistinctQuery {

    final JenaBackend backend;
    boolean isUniform = false;
    NodeId graph = null;
    CountDistinctEstimator<?> estimator;
    Integer step = 1;
    Integer nbSteps = 0;
    final Set<Integer> vars;

    Integer nbWalks;

    public ConfigCountDistinctQuery(JenaBackend backend, Set<Integer> vars) {
        this.vars = vars;
        this.backend = backend;
    }

    public Double sample() {throw new UnsupportedOperationException("sample");}
    public ConfigCountDistinctQuery fixN() {throw new UnsupportedOperationException("fixN");}

    protected ProgressJenaIterator getProgressJenaIterator(NodeId s, NodeId p, NodeId o) {
        return (ProgressJenaIterator) ((LazyIterator<NodeId, ?>)
                (Objects.isNull(graph) ?
                        backend.search(s, p, o) :
                        backend.search(s, p, o, graph))).getWrapped();
    }

    /**
     * @return The sample size so far.
     */
    public Integer getNbSteps() {
        return nbSteps;
    }

    /**
     * @param uniform True to get uniform sampling of 1 triple pattern.
     * @return this.
     */
    public ConfigCountDistinctQuery setUniform(boolean uniform) {
        isUniform = uniform;
        return this;
    }

    /**
     * @param graph The NodeId of the graph to look for, in the dataset.
     * @return this.
     */
    public ConfigCountDistinctQuery setGraph(NodeId graph) {
        this.graph = graph;
        return this;
    }

    /**
     * @param seed The seed used for this run.
     * @return this.
     */
    public ConfigCountDistinctQuery setSeed(Integer seed) {
        ProgressJenaIterator.rng = new Random(seed);
        return this;
    }

    // TODO multiple estimators
    /**
     * @param estimator The estimator to run at each step.
     * @return this.
     */
    public ConfigCountDistinctQuery setEstimator(CountDistinctEstimator<?> estimator) {
        this.estimator = estimator;
        return this;
    }

    /**
     * @param step The number of iterations of random sample before returning a result.
     * @return this.
     */
    public ConfigCountDistinctQuery setStep(Integer step) {
        this.step = step;
        return this;
    }

    /**
     * @param nbWalks The number of walks to process the estimate on 2 tps using Wander Join
     * @return this.
     */
    public ConfigCountDistinctQuery setEstimatedCount(Integer nbWalks) {
        this.nbWalks = nbWalks;
        return this;
    }

    /**
     * Configure to use the perfect count. It takes more time but it's useful for debug purposes.
     * @return this.
     */
    public ConfigCountDistinctQuery setExactCount() {
        this.nbWalks = null;
        return this;
    }

    public CountDistinctEstimator<?> getEstimator() {
        return estimator;
    }

    protected Set<NodeId> getNodeIds(Tuple<NodeId> triple) {
        return vars.stream().map(triple::get).collect(Collectors.toSet());
    }

}
