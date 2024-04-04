package fr.gdd.queries;

import fr.gdd.sage.jena.JenaBackend;
import org.apache.jena.dboe.trans.bplustree.ProgressJenaIterator;
import org.apache.jena.tdb2.store.NodeId;

import java.util.List;
import java.util.Random;

/**
 * Distinct of ?s or ?p or ?o in simple pattern like SPO
 */
public class SPObis {

    boolean isUniform = false;
    final JenaBackend backend;
    NodeId graph = null;

    public SPObis(JenaBackend backend) {
        this.backend = backend;
    }

    public List<String> getSample(NodeId id) {
        return null;
    }


    /* ********************************************************************* */

    /**
     * @param uniform True to get uniform sampling of 1 triple pattern.
     * @return this.
     */
    public SPObis setUniform(boolean uniform) {
        isUniform = uniform;
        return this;
    }

    /**
     * @param graph The NodeId of the graph to look for, in the dataset.
     * @return this.
     */
    public SPObis setGraph(NodeId graph) {
        this.graph = graph;
        return this;
    }

    /**
     * @param seed The seed used for this run.
     * @return this.
     */
    public SPObis setSeed(Integer seed) {
        ProgressJenaIterator.rng = new Random(seed);
        return this;
    }
}
