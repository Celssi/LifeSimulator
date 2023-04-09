package org.celssi.astar;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;

import static org.celssi.astar.Node.State.*;

public class Graph {

    private final List<Node> nodes = new ArrayList<>();
    private final Heuristic heuristic;

    public Graph(Heuristic heuristic) {
        this.heuristic = heuristic;
    }

    public void addNode(Node n) {
        nodes.add(n);
    }

    public List<Node> getNodes() {
        return nodes;
    }

    public Heuristic getHeuristic() {
        return heuristic;
    }

    public void link(Node a, Node b, double cost) {
        Edge edge = new Edge(cost, a, b);
        a.addEdge(edge);
        b.addEdge(edge);
    }

    public List<Node> findPath(Node start, Node target) {
        List<Node> path = new ArrayList<>();

        nodes.forEach(node -> {
            node.setState(UNVISITED);
            node.setBackPathNode(null);
            node.setG(Double.MAX_VALUE);
        });

        start.setG(0);
        start.setH(heuristic.calculate(start, target, start));

        PriorityQueue<Node> activeNodes = new PriorityQueue<>();
        activeNodes.add(start);

        while (!activeNodes.isEmpty()) {
            Node currentNode = activeNodes.poll();
            currentNode.setState(CLOSED);

            // target node found!
            if (currentNode == target) {
                target.retrievePath(path);
                return path;
            }

            currentNode.getEdges().forEach((edge) -> {
                Node neighborNode = edge.getOppositeNode(currentNode);
                double neighborG = currentNode.getG() + edge.getG();
                if (!neighborNode.isBlocked() && neighborG < neighborNode.getG()) {

                    neighborNode.setBackPathNode(currentNode);
                    neighborNode.setG(neighborG);
                    double h = heuristic.calculate(start, target, neighborNode);
                    neighborNode.setH(h);
                    if (!activeNodes.contains(neighborNode)) {
                        activeNodes.add(neighborNode);
                        neighborNode.setState(OPEN);
                    }
                }
            });
        }

        return path;
    }

}
