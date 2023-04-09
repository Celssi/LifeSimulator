package org.celssi.astar;

public interface Heuristic {

    double calculate(Node start, Node target, Node current);

}
