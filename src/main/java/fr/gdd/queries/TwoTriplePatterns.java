package fr.gdd.queries;

import fr.gdd.estimators.CRWD;
import fr.gdd.sage.interfaces.SPOC;
import fr.gdd.sage.jena.JenaBackend;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.jena.atlas.lib.tuple.Tuple;
import org.apache.jena.dboe.trans.bplustree.PreemptJenaIterator;
import org.apache.jena.dboe.trans.bplustree.ProgressJenaIterator;
import org.apache.jena.graph.Node;
import org.apache.jena.tdb2.store.NodeId;

import java.util.Set;
import java.util.function.Function;

/**
 * This time, two triples patterns. Not that complicated either.
 */
public class TwoTriplePatterns extends OneTriplePattern {

    static final Integer SS = 4;
    static final Integer PP = 5;
    static final Integer OO = 6;

    Function<Tuple<NodeId>, NodeId> boundSS;
    Function<Tuple<NodeId>, NodeId> boundPP;
    Function<Tuple<NodeId>, NodeId> boundOO;

    /**
     * @param backend The backend to look into.
     * @param vars    The variable that we want to know the distinct number of values.
     */
    public TwoTriplePatterns(JenaBackend backend, Set<Integer> vars) {
        super(backend, vars);
        boundSS = (t) -> backend.any();
        boundPP = (t) -> backend.any();
        boundOO = (t) -> backend.any();
    }

    @Override
    public Double sample() {
        ProgressJenaIterator firstTP =  getProgressJenaIterator(boundS, boundP, boundO);
        for (int i = 0; i < step; ++i) {
            nbSteps += 1;
            Pair<Tuple<NodeId>, Double> firstRandom = isUniform ?
                    firstTP.getUniformRandomSPOWithProbability():
                    firstTP.getRandomSPOWithProbability();

            ProgressJenaIterator secondTP = getProgressJenaIterator(
                    boundSS.apply(firstRandom.getLeft()),
                    boundPP.apply(firstRandom.getLeft()),
                    boundOO.apply(firstRandom.getLeft()));

            Pair<Tuple<NodeId>, Double> secondRandom = isUniform ?
                    secondTP.getUniformRandomSPOWithProbability():
                    secondTP.getRandomSPOWithProbability();

            this.addSample(firstRandom, secondRandom, count(firstRandom.getLeft(), secondRandom.getLeft()));
        }
        return estimator.getEstimate();
    }

    protected void addSample(Pair<Tuple<NodeId>, Double> first, Pair<Tuple<NodeId>, Double> second, Double frequency) {
        switch (estimator) {
            // TODO state if the random walk failed for more general use-cases
            // TODO case ChaoLee cl -> cl.add(new ChaoLee.ChaoLeeSample(getNodeIds(spo.getLeft()), spo.getRight(), frequency));
            case CRWD crwd -> crwd.add(new CRWD.CRWDSample(first.getRight() * second.getRight(), frequency));
            default -> throw new UnsupportedOperationException("The estimator is not supported: "+estimator.getClass().getSimpleName());
        }
    }

    // TODO approximated count
    protected Double count(Tuple<NodeId> first, Tuple<NodeId> second) {
        Double result = 0.;
        ProgressJenaIterator firstTP =  getProgressJenaIterator(
                vars.contains(SPOC.SUBJECT) ? first.get(SPOC.SUBJECT) : boundS,
                vars.contains(SPOC.PREDICATE) ? first.get(SPOC.PREDICATE) : boundP,
                vars.contains(SPOC.OBJECT) ? first.get(SPOC.OBJECT) : boundO);

        PreemptJenaIterator firstIt = (PreemptJenaIterator) firstTP;

        while (firstIt.hasNext()) {
            firstIt.next();
            Tuple<NodeId> currentTuple = firstIt.getCurrentTuple();

            ProgressJenaIterator secondTP =  getProgressJenaIterator(
                    vars.contains(SS) ? second.get(SPOC.SUBJECT) : boundSS.apply(currentTuple),
                    vars.contains(PP) ? second.get(SPOC.PREDICATE) : boundPP.apply(currentTuple),
                    vars.contains(OO) ? second.get(SPOC.OBJECT) : boundOO.apply(currentTuple));
            PreemptJenaIterator secondIt = (PreemptJenaIterator) secondTP;
            result += secondIt.count();
        }

        return result;
    }

    /* ************************************************************************** */

    public TwoTriplePatterns bindSS(NodeId n) {
        boundSS = (e) -> n;
        return this;
    }

    public TwoTriplePatterns bindPP(NodeId n) {
        boundPP = (e) -> n;
        return this;
    }

    public TwoTriplePatterns bindOO(NodeId n) {
        boundOO = (e) -> n;
        return this;
    }

    public TwoTriplePatterns bindSS(Node n) {
        return bindSS(backend.getId(n));
    }

    public TwoTriplePatterns bindPP(Node n) {
        return bindPP(backend.getId(n));
    }

    public TwoTriplePatterns bindOO(Node n) {
        return bindOO(backend.getId(n));
    }

    /**
     * @param spo It depends on the first triple pattern.
     * @return this.
     */
    public TwoTriplePatterns bindSS(Integer spo) {
        boundSS = (e) -> e.get(spo);
        return this;
    }

    /**
     * @param spo It depends on the first triple pattern.
     * @return this.
     */
    public TwoTriplePatterns bindPP(Integer spo) {
        boundPP = (e) -> e.get(spo);
        return this;
    }

    /**
     * @param spo It depends on the first triple pattern.
     * @return this.
     */
    public TwoTriplePatterns bindOO(Integer spo) {
        boundOO = (e) -> e.get(spo);
        return this;
    }
}
