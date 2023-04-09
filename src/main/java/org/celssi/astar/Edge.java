package org.celssi.astar;

public class Edge {

    private final Node a;
    private final Node b;
    private double g;

    public Edge(double g, Node a, Node b) {
        this.g = g;
        this.a = a;
        this.b = b;
    }

    public double getG() {
        return g;
    }

    public void setG(double g) {
        this.g = g;
    }

    public Node getA() {
        return a;
    }

    public Node getB() {
        return b;
    }

    public Node getOppositeNode(Node thisNode) {
        if (thisNode == a) {
            return b;
        } else if (thisNode == b) {
            return a;
        }
        return null;
    }

    @Override
    public String toString() {
        return "Edge{" + "g=" + g + ", a=" + a + ", b=" + b + '}';
    }

}
