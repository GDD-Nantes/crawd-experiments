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

import java.util.Objects;
import java.util.Set;
import java.util.function.Function;

/**
 * This time, two triples patterns. Not that complicated either.
 */
public class TwoTriplePatterns extends ConfigCountDistinctQuery {

    static final Integer SS = 4;
    static final Integer PP = 5;
    static final Integer OO = 6;

    Function<Tuple<NodeId>, NodeId> boundS;
    Function<Tuple<NodeId>, NodeId> boundP;
    Function<Tuple<NodeId>, NodeId> boundO;
    Function<Tuple<NodeId>, NodeId> boundSS;
    Function<Tuple<NodeId>, NodeId> boundPP;
    Function<Tuple<NodeId>, NodeId> boundOO;

    Double bigN = null;

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
    public TwoTriplePatterns fixN() {
        this.estimator.fixN(getBigN());
        return this;
    }

    public Double getBigN() {
        if (Objects.nonNull(bigN)) {
            return bigN;
        }

        NodeId s = boundS.apply(null);
        NodeId p = boundP.apply(null);
        NodeId o = boundO.apply(null);

        ProgressJenaIterator firstTP =  getProgressJenaIterator(s, p, o);
        PreemptJenaIterator itFirst = (PreemptJenaIterator) firstTP;
        double sum = 0.;
        while (itFirst.hasNext()) {
            itFirst.next();
            Tuple<NodeId> spo = itFirst.getCurrentTuple();

            NodeId ss = boundSS.apply(spo);
            NodeId pp = boundPP.apply(spo);
            NodeId oo = boundOO.apply(spo);

            ProgressJenaIterator secondTP = getProgressJenaIterator(ss, pp, oo);
            PreemptJenaIterator itSecond = (PreemptJenaIterator) secondTP;

            sum += itSecond.count();
        }
        this.bigN = sum;
        return bigN;
    }

    @Override
    public Double sample() {
        ProgressJenaIterator firstTP =  getProgressJenaIterator(boundS.apply(null), boundP.apply(null), boundO.apply(null));
        for (int i = 0; i < step; ++i) {
            nbSteps += 1;
            Pair<Tuple<NodeId>, Double> firstRandom = getRandomAndProba(firstTP);

            ProgressJenaIterator secondTP = getProgressJenaIterator(
                    boundSS.apply(firstRandom.getLeft()),
                    boundPP.apply(firstRandom.getLeft()),
                    boundOO.apply(firstRandom.getLeft()));

            Pair<Tuple<NodeId>, Double> secondRandom = getRandomAndProba(secondTP);

            this.addSample(firstRandom, secondRandom, getCount(firstRandom.getLeft(), secondRandom.getLeft()));
        }
        return estimator.getEstimate();
    }

    protected void addSample(Pair<Tuple<NodeId>, Double> first, Pair<Tuple<NodeId>, Double> second, Double frequency) {
        switch (estimator) {
            // TODO state if the random walk failed for more general use-cases
            // TODO case ChaoLee cl -> cl.add(new ChaoLee.ChaoLeeSample(getNodeIds(spo.getLeft()), spo.getRight(), frequency));
            case CRWD crwd -> crwd.add(new CRWD.CRWDSample(first.getRight() * second.getRight(),
                    Math.max(1,frequency)));
            default -> throw new UnsupportedOperationException("The estimator is not supported: "+estimator.getClass().getSimpleName());
        }
    }

    // TODO approximated count
    protected Double count(Tuple<NodeId> first, Tuple<NodeId> second) {
        double result = 0.;
        ProgressJenaIterator firstTP =  getProgressJenaIterator(
                vars.contains(SPOC.SUBJECT) ? first.get(SPOC.SUBJECT) : boundS.apply(null),
                vars.contains(SPOC.PREDICATE) ? first.get(SPOC.PREDICATE) : boundP.apply(null),
                vars.contains(SPOC.OBJECT) ? first.get(SPOC.OBJECT) : boundO.apply(null));

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

    protected Double estimatedCountTP1xTP2(Tuple<NodeId> first, Tuple<NodeId> second, Integer nbWalks) {
        ProgressJenaIterator firstTP =  getProgressJenaIterator(
                vars.contains(SPOC.SUBJECT) ? first.get(SPOC.SUBJECT) : boundS.apply(null),
                vars.contains(SPOC.PREDICATE) ? first.get(SPOC.PREDICATE) : boundP.apply(null),
                vars.contains(SPOC.OBJECT) ? first.get(SPOC.OBJECT) : boundO.apply(null));

        double sum = 0.;
        for (int i = 0; i < nbWalks; ++i) {
            Pair<Tuple<NodeId>, Double> randomFirst = getRandomAndProba(firstTP);

            NodeId s = vars.contains(SS) ? second.get(SPOC.SUBJECT) : boundSS.apply(randomFirst.getLeft());
            NodeId p = vars.contains(PP) ? second.get(SPOC.PREDICATE) : boundPP.apply(randomFirst.getLeft());
            NodeId o = vars.contains(OO) ? second.get(SPOC.OBJECT) : boundOO.apply(randomFirst.getLeft());

            String ss = NodeId.isAny(s) ? "any" : backend.getValue(s);
            String pp = NodeId.isAny(p) ? "any" : backend.getValue(p);
            String oo = NodeId.isAny(o) ? "any" : backend.getValue(o);

            ProgressJenaIterator secondTP = getProgressJenaIterator(
                    vars.contains(SS) ? second.get(SPOC.SUBJECT) : boundSS.apply(randomFirst.getLeft()),
                    vars.contains(PP) ? second.get(SPOC.PREDICATE) : boundPP.apply(randomFirst.getLeft()),
                    vars.contains(OO) ? second.get(SPOC.OBJECT) : boundOO.apply(randomFirst.getLeft()));

            Pair<Tuple<NodeId>, Double> randomSecond = getRandomAndProba(secondTP);

            sum += Objects.isNull(randomSecond.getLeft()) ?
                    0: // (does nothing if the walk fails)
                    1./(randomFirst.getRight() * randomSecond.getRight()); // or add the proba
        }

        return sum/nbWalks;
    }

    protected Double estimatedCountTP2xTP1(Tuple<NodeId> first, Tuple<NodeId> second, Integer nbWalks) {
        var foundS = backend.getValue(first.get(SPOC.SUBJECT));
        var foundP = backend.getValue(first.get(SPOC.PREDICATE));
        var foundO = backend.getValue(first.get(SPOC.OBJECT));

        var foundSS = backend.getValue(second.get(SPOC.SUBJECT));
        var foundPP = backend.getValue(second.get(SPOC.PREDICATE));
        var foundOO = backend.getValue(second.get(SPOC.OBJECT));

        double sum = 0.;

        for (int i = 0; i < nbWalks; ++i) {
            NodeId s = vars.contains(SS)? second.get(SPOC.SUBJECT): Objects.isNull(boundSS.apply(null))? backend.any(): boundSS.apply(null);
            NodeId p = vars.contains(PP)? second.get(SPOC.PREDICATE): Objects.isNull(boundPP.apply(null))? backend.any(): boundPP.apply(null);
            NodeId o = vars.contains(OO)? second.get(SPOC.OBJECT): Objects.isNull(boundOO.apply(null))? backend.any(): boundOO.apply(null);

            String ssss = NodeId.isAny(s) ? "any" : backend.getValue(s);
            String pppp = NodeId.isAny(p) ? "any" : backend.getValue(p);
            String oooo = NodeId.isAny(o) ? "any" : backend.getValue(o);

            ProgressJenaIterator secondTP =  getProgressJenaIterator(s, p, o);
            // Double cardSecond = secondTP.cardinality(1000);

            Pair<Tuple<NodeId>, Double> randomSecond = getRandomAndProba(secondTP);

            NodeId ss = vars.contains(SPOC.SUBJECT) ? first.get(SPOC.SUBJECT) : boundS.apply(randomSecond.getLeft());
            NodeId pp = vars.contains(SPOC.PREDICATE) ? first.get(SPOC.PREDICATE) : boundP.apply(randomSecond.getLeft());
            NodeId oo = vars.contains(SPOC.OBJECT) ? first.get(SPOC.OBJECT) : boundO.apply(randomSecond.getLeft());

            String sss = NodeId.isAny(ss) ? "any" : backend.getValue(ss);
            String ppp = NodeId.isAny(pp) ? "any" : backend.getValue(pp);
            String ooo = NodeId.isAny(oo) ? "any" : backend.getValue(oo);

            ProgressJenaIterator firstTP = getProgressJenaIterator(ss, pp, oo);

            Pair<Tuple<NodeId>, Double> randomFirst = getRandomAndProba(firstTP);

            // Double cardFirst = firstTP.cardinality(1000);

            sum += Objects.isNull(randomFirst.getLeft()) ?
                    0: // (does nothing if the walk fails)
                    1./(randomFirst.getRight() * randomSecond.getRight()); // or add the proba
        }

        //System.out.println(foundOO);
        //System.out.println("card = " + sum/nbWalks);

        return sum/nbWalks;
    }

    private Double getCount(Tuple<NodeId> first, Tuple<NodeId> second) {
        return Objects.isNull(nbWalks) ? count(first, second) : estimatedCountTP2xTP1(first, second, nbWalks);
    }

    private Pair<Tuple<NodeId>, Double> getRandomAndProba(ProgressJenaIterator tp) {
        return isUniform ? tp.getUniformRandomSPOWithProbability(): tp.getRandomSPOWithProbability();
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
        boundSS = (e) -> Objects.isNull(e) ? null : e.get(spo);
        return this;
    }

    /**
     * @param spo It depends on the first triple pattern.
     * @return this.
     */
    public TwoTriplePatterns bindPP(Integer spo) {
        boundPP = (e) -> Objects.isNull(e) ? null : e.get(spo);
        return this;
    }

    /**
     * @param spo It depends on the first triple pattern.
     * @return this.
     */
    public TwoTriplePatterns bindOO(Integer spo) {
        boundOO = (e) -> Objects.isNull(e) ? null : e.get(spo);
        return this;
    }

    /**
     * @param spo It depends on the first triple pattern.
     * @return this.
     */
    public TwoTriplePatterns bindS(Integer spo) {
        boundS = (e) -> Objects.isNull(e) ? null : e.get(spo);
        return this;
    }

    /**
     * @param spo It depends on the first triple pattern.
     * @return this.
     */
    public TwoTriplePatterns bindP(Integer spo) {
        boundP = (e) -> Objects.isNull(e) ? null : e.get(spo);
        return this;
    }

    /**
     * @param spo It depends on the first triple pattern.
     * @return this.
     */
    public TwoTriplePatterns bindO(Integer spo) {
        boundO = (e) -> Objects.isNull(e) ? null : e.get(spo);
        return this;
    }

    public TwoTriplePatterns bindS(NodeId s) {
        this.boundS = (e) -> s;
        return this;
    }

    public TwoTriplePatterns bindP(NodeId p) {
        this.boundP = (e) -> p;
        return this;
    }

    public TwoTriplePatterns bindO(NodeId o) {
        this.boundO = (e) -> o;
        return this;
    }

    public TwoTriplePatterns bindS(Node s) {
        return this.bindS(this.backend.getId(s));
    }

    public TwoTriplePatterns bindP(Node p) {
        return this.bindP(this.backend.getId(p));
    }

    public TwoTriplePatterns bindO(Node o) {
        return this.bindO(this.backend.getId(o));
    }

}
