package fr.gdd.queries;

import fr.gdd.estimators.CRWD;
import fr.gdd.estimators.ChaoLee;
import fr.gdd.estimators.CountDistinctEstimator;
import fr.gdd.sage.generics.LazyIterator;
import fr.gdd.sage.interfaces.SPOC;
import fr.gdd.sage.jena.JenaBackend;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.jena.atlas.lib.tuple.Tuple;
import org.apache.jena.dboe.trans.bplustree.ProgressJenaIterator;
import org.apache.jena.graph.Node;
import org.apache.jena.tdb2.store.NodeId;

import java.util.Objects;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Distinct of ?s or ?p or ?o in simple pattern like SPO.
 * TODO
 */
public class OneTriplePattern {

    final JenaBackend backend;
    boolean isUniform = false;
    NodeId graph = null;
    CountDistinctEstimator<?> estimator;
    Integer step = 1;
    Integer nbSteps = 0;
    Set<Integer> vars;

    NodeId boundS;
    NodeId boundP;
    NodeId boundO;

    /**
     * @param backend The backend to look into.
     * @param vars The variable that we want to know the distinct number of values.
     */
    public OneTriplePattern(JenaBackend backend, Set<Integer> vars) {
        this.backend = backend;
        this.vars = vars;
        this.boundS = backend.any();
        this.boundP = backend.any();
        this.boundO = backend.any();
    }

    /**
     * Perform a sampling based on current configuration.
     * @return The estimated count-distinct.
     */
    public Double sample() {
        ProgressJenaIterator spo =  getProgressJenaIterator(boundS, boundP, boundO);
        for (int i = 0; i < step; ++i) {
            nbSteps += 1;
            Pair<Tuple<NodeId>, Double> randomAndProba = isUniform ? spo.getUniformRandomSPOWithProbability() : spo.getRandomSPOWithProbability();
            ProgressJenaIterator it4Fi = getProgressJenaIterator(
                    vars.contains(SPOC.SUBJECT) ? randomAndProba.getLeft().get(SPOC.SUBJECT) : boundS,
                    vars.contains(SPOC.PREDICATE) ? randomAndProba.getLeft().get(SPOC.PREDICATE) : boundP,
                    vars.contains(SPOC.OBJECT) ? randomAndProba.getLeft().get(SPOC.OBJECT) : boundO);
            this.addSample(randomAndProba, it4Fi.count());
        }
        return estimator.getEstimate();
    }

    public OneTriplePattern fixN() {
        ProgressJenaIterator spo =  getProgressJenaIterator(boundS, boundP, boundO);
        estimator.fixN(spo.count());
        return this;
    }

    private ProgressJenaIterator getProgressJenaIterator(NodeId s, NodeId p, NodeId o) {
        return (ProgressJenaIterator) ((LazyIterator<NodeId, ?>)
                (Objects.isNull(graph) ?
                        backend.search(s, p, o) :
                        backend.search(s, p, o, graph))).getWrapped();
    }

    /**
     * Feed the estimator depending on its type
     * @param spo the element found.
     * @param frequency The number of occurrences of this NodeId in the results, at specified position.
     */
    private void addSample(Pair<Tuple<NodeId>, Double> spo, Double frequency) {
        switch (estimator) {
            case ChaoLee cl -> cl.add(new ChaoLee.ChaoLeeSample(getNodeIds(spo.getLeft()), spo.getRight(), frequency));
            case CRWD crwd -> crwd.add(new CRWD.CRWDSample(spo.getRight(), frequency));
            default -> throw new UnsupportedOperationException("The estimator is not supported: "+estimator.getClass().getSimpleName());
        }
    }

    private Set<NodeId> getNodeIds(Tuple<NodeId> triple) {
        return vars.stream().map(triple::get).collect(Collectors.toSet());
    }

    /* ********************************************************************* */

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
    public OneTriplePattern setUniform(boolean uniform) {
        isUniform = uniform;
        return this;
    }

    /**
     * @param graph The NodeId of the graph to look for, in the dataset.
     * @return this.
     */
    public OneTriplePattern setGraph(NodeId graph) {
        this.graph = graph;
        return this;
    }

    /**
     * @param seed The seed used for this run.
     * @return this.
     */
    public OneTriplePattern setSeed(Integer seed) {
        ProgressJenaIterator.rng = new Random(seed);
        return this;
    }

    /**
     * @param estimator The estimator to run at each step.
     * @return this.
     */
    public OneTriplePattern setEstimator(CountDistinctEstimator<?> estimator) {
        this.estimator = estimator;
        return this;
    }

    /**
     * @param step The number of iterations of random sample before returning a result.
     * @return this.
     */
    public OneTriplePattern setStep(Integer step) {
        this.step = step;
        return this;
    }

    public OneTriplePattern bindS(NodeId s) {
        this.boundS = s;
        return this;
    }

    public OneTriplePattern bindP(NodeId p) {
        this.boundP = p;
        return this;
    }

    public OneTriplePattern bindO(NodeId o) {
        this.boundO = o;
        return this;
    }

    public OneTriplePattern bindS(Node s) {
        return this.bindS(this.backend.getId(s));
    }

    public OneTriplePattern bindP(Node p) {
        return this.bindP(this.backend.getId(p));
    }

    public OneTriplePattern bindO(Node o) {
        return this.bindO(this.backend.getId(o));
    }
}
