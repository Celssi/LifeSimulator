package org.celssi.utils;

import org.celssi.GameState;
import org.celssi.astar.Graph;
import org.celssi.astar.Node;
import org.celssi.astar.Tile;
import org.celssi.constants.Settings;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class MapUtils {
    public static boolean IsAllowedPosition(int newX, int newY) {
        if (newX < 0 || newY < 0 || newY > (GameState.MAP.length - 1) || newX > GameState.MAP[newY].length - 1) {
            return false;
        }

        return !GameState.MAP[newY][newX].equals("*");
    }

    public static int[] GetRandomFreePosition() {
        int[] freePosition = new int[2];
        String character;

        do {
            int randomRowIndex = MathUtils.RandomGenerator.nextInt(GameState.MAP.length);
            String[] randomRow = GameState.MAP[randomRowIndex];
            int randomColumnIndex = MathUtils.RandomGenerator.nextInt(randomRow.length);

            character = randomRow[randomColumnIndex];
            freePosition[0] = randomColumnIndex;
            freePosition[1] = randomRowIndex;
        } while (!character.equals(" "));

        return freePosition;
    }

    public static Graph GetMapAsAGraph() {
        Graph graph = new Graph((start, target, current) -> {
            // --- implement your heuristic here ---

            // heuristic = manhattan distance
            //int dx = Math.abs(target.getObj().x - current.getObj().x);
            //int dy = Math.abs(target.getObj().y - current.getObj().y);
            //return dx + dy;

            // heuristic = linear distance
            int dx = target.getTile().column() - current.getTile().column();
            int dy = target.getTile().row() - current.getTile().row();
            return Math.sqrt(dx * dx + dy * dy);

            //heuristic = 0 -> equivalent to Dijkstra
            //return 0;
        });

        Node[][] nodes = createNodesAndAddToGraph(graph);
        createLinks(graph, nodes);

        return graph;
    }

    public static Node GetNodeFromCoordinates(List<Node> nodes, int x, int y) {
        Optional<Node> foundNode = nodes.stream().filter(node -> node.getTile().row() == y && node.getTile().column() == x).findFirst();
        return foundNode.orElse(null);
    }

    private static Node[][] createNodesAndAddToGraph(Graph graph) {
        Node[][] nodes = new Node[Settings.MAP_HEIGHT][Settings.MAP_WIDTH];

        for (int i = 0; i < GameState.MAP.length; i++) {
            for (int j = 0; j < GameState.MAP[i].length; j++) {
                Tile tile = new Tile(i, j);
                Node node = new Node(tile);
                node.setBlocked(Objects.equals(GameState.MAP[i][j], "*"));

                nodes[i][j] = node;
                graph.addNode(node);
            }
        }

        return nodes;
    }

    private static void createLinks(Graph graph, Node[][] nodes) {
        for (int i = 0; i < nodes.length; i++) {
            for (int j = 0; j < nodes[i].length; j++) {
                // Should have link to next on the right
                if (j < nodes[i].length - 1) {
                    graph.link(nodes[i][j], nodes[i][j + 1], 1);
                }

                // Should have link to the one below
                if (i < nodes.length - 1) {
                    graph.link(nodes[i][j], nodes[i + 1][j], 1);
                }

                // Should have link to the one diagonally down left
                if (i < nodes.length - 1 && j < nodes[i].length - 1) {
                    graph.link(nodes[i][j], nodes[i + 1][j + 1], Math.sqrt(2));
                }

                // Should have link to the one diagonally down right
                if (i < nodes.length - 1 && j > 0) {
                    graph.link(nodes[i][j], nodes[i + 1][j - 1], Math.sqrt(2));
                }
            }
        }
    }

    public static int[] GetNearestSafePosition(double positionX, double positionY) {
        int roundedX = Math.round((float) positionX);
        int roundedY = Math.round((float) positionY);

        if (IsAllowedPosition(roundedX, roundedY)) {
            return new int[]{roundedX, roundedY};
        }

        int flooredX = (int) Math.floor(positionX);
        int flooredY = (int) Math.floor(positionY);
        int ceiledX = (int) Math.ceil(positionX);
        int ceiledY = (int) Math.ceil(positionY);

        int[][] nearByPositions = new int[][]{new int[]{flooredX, flooredY}, new int[]{roundedX, flooredY}, new int[]{flooredX, roundedY}, new int[]{ceiledX, ceiledY}, new int[]{roundedX, ceiledY}, new int[]{ceiledX, roundedY}};
        return Arrays.stream(nearByPositions).filter(p -> IsAllowedPosition(p[0], p[1])).findFirst().orElse(new int[]{roundedX, roundedY});
    }
}
