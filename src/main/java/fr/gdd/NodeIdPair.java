package fr.gdd;
import org.apache.jena.tdb2.store.NodeId;
public class NodeIdPair {
    private NodeId first;
    private NodeId second;

    public NodeIdPair(NodeId first, NodeId second) {
        this.first = first;
        this.second = second;
    }

}
