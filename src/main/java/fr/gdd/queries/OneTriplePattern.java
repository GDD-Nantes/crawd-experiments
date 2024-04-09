package fr.gdd.queries;

import fr.gdd.estimators.CRWD;
import fr.gdd.estimators.ChaoLee;
import fr.gdd.sage.interfaces.SPOC;
import fr.gdd.sage.jena.JenaBackend;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.jena.atlas.lib.tuple.Tuple;
import org.apache.jena.dboe.trans.bplustree.ProgressJenaIterator;
import org.apache.jena.graph.Node;
import org.apache.jena.tdb2.store.NodeId;

import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * Distinct of ?s or ?p or ?o in simple pattern like SPO.
 * TODO
 */
public class OneTriplePattern extends ConfigCountDistinctQuery {

    NodeId boundS;
    NodeId boundP;
    NodeId boundO;

    /**
     * @param backend The backend to look into.
     * @param vars The variable that we want to know the distinct number of values.
     */
    public OneTriplePattern(JenaBackend backend, Set<Integer> vars) {
        super(backend, vars);
        this.boundS = backend.any();
        this.boundP = backend.any();
        this.boundO = backend.any();
    }

    @Override
    public Map<Set<NodeId>, Double> sample() {
        ProgressJenaIterator spo =  getProgressJenaIterator(boundS, boundP, boundO);
        for (int i = 0; i < step; ++i) {
            nbSteps += 1;
            Pair<Tuple<NodeId>, Double> randomAndProba = isUniform ? spo.getUniformRandomSPOWithProbability() : spo.getRandomSPOWithProbability();
            ProgressJenaIterator it4Fi = getProgressJenaIterator(
                    vars.contains(SPOC.SUBJECT) ? randomAndProba.getLeft().get(SPOC.SUBJECT) : boundS,
                    vars.contains(SPOC.PREDICATE) ? randomAndProba.getLeft().get(SPOC.PREDICATE) : boundP,
                    vars.contains(SPOC.OBJECT) ? randomAndProba.getLeft().get(SPOC.OBJECT) : boundO);

            double count = Objects.isNull(this.nbWalks) ? it4Fi.count() : it4Fi.cardinality(this.nbWalks);

            this.addSample(randomAndProba, count);
        }
        return Map.of(Set.of(), estimator.getEstimate());
    }

    public OneTriplePattern fixN() {
        ProgressJenaIterator spo =  getProgressJenaIterator(boundS, boundP, boundO);
        estimator.fixN(spo.count());
        return this;
    }

    /**
     * Feed the estimator depending on its type
     * @param spo the element found.
     * @param frequency The number of occurrences of this NodeId in the results, at specified position.
     */
    protected void addSample(Pair<Tuple<NodeId>, Double> spo, Double frequency) {
        switch (estimator) {
            case ChaoLee cl -> cl.add(new ChaoLee.ChaoLeeSample(getNodeIds(vars, spo.getLeft()), spo.getRight(), frequency));
            case CRWD crwd -> crwd.add(new CRWD.CRWDSample(spo.getRight(), frequency));
            default -> throw new UnsupportedOperationException("The estimator is not supported: "+estimator.getClass().getSimpleName());
        }
    }

    /* ********************************************************************* */

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
