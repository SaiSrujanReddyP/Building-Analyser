package com.example.buildinganalyzer6;

import java.util.*;

public class GraphAlgorithms {

    static class Node {
        int id;
        double x, y; // Coordinates of the node

        Node(){}

        Node(int id, double x, double y) {
            this.id = id;
            this.x = x;
            this.y = y;
        }
    }

    static class Edge {
        int from, to;
        double weight;

        Edge(int from, int to, double weight) {
            this.from = from;
            this.to = to;
            this.weight = weight;
        }
    }

    static class Graph {
        List<Node> nodes = new ArrayList<>();
        Map<Integer, List<Edge>> adjList = new HashMap<>();

        void addNode(Node node) {
            nodes.add(node);
            adjList.put(node.id, new ArrayList<>());
        }

        void addEdge(int from, int to, double weight) {
            adjList.get(from).add(new Edge(from, to, weight));
            adjList.get(to).add(new Edge(to, from, weight)); // Assuming undirected graph
        }

        // Method to calculate the Euclidean distance between two nodes
        static double distance(Node n1, Node n2) {
            return Math.sqrt(Math.pow(n1.x - n2.x, 2) + Math.pow(n1.y - n2.y, 2));
        }

        // Method to count the number of nodes within view range of a given position
        static int countNodesInViewRange(double x, double y, List<Node> nodes, double viewRange) {
            int count = 0;
            for (Node node : nodes) {
                if (distance(new Node(-1, x, y), node) <= viewRange) {
                    count++;
                }
            }
            return count;
        }

        // Method to find the optimal position for the camera using a grid traversal
        Node findOptimalCameraPosition(double viewRange, double gridStep) {
            double minX = Double.MAX_VALUE, minY = Double.MAX_VALUE;
            double maxX = Double.MIN_VALUE, maxY = Double.MIN_VALUE;

            // Determine the boundaries of the grid based on the farthest nodes
            for (Node node : nodes) {
                if (node.x < minX) minX = node.x;
                if (node.y < minY) minY = node.y;
                if (node.x > maxX) maxX = node.x;
                if (node.y > maxY) maxY = node.y;
            }

            // Calculate the center of the grid
            double centerX = (minX + maxX) / 2;
            double centerY = (minY + maxY) / 2;

            Node optimalNode = null;
            int maxCoveredNodes = 0;

            // Traverse each point on the grid
            for (double x = minX; x <= maxX; x += gridStep) {
                for (double y = minY; y <= maxY; y += gridStep) {
                    int coveredNodes = countNodesInViewRange(x, y, nodes, viewRange);
                    // Check if this point has more coverage or if it is closer to the center in case of a tie
                    if (coveredNodes > maxCoveredNodes ||
                            (coveredNodes == maxCoveredNodes && (optimalNode == null || distance(new Node(-1, x, y), new Node(-1, centerX, centerY)) <
                                    distance(optimalNode, new Node(-1, centerX, centerY))))) {
                        maxCoveredNodes = coveredNodes;
                        optimalNode = new Node(-1, x, y);
                    }
                }
            }

            return optimalNode;
        }

        // Dijkstra's algorithm to find the shortest path
        List<Integer> dijkstra(int start, int destination) {
            Map<Integer, Double> distances = new HashMap<>();
            Map<Integer, Integer> previous = new HashMap<>();
            PriorityQueue<NodeDistance> pq = new PriorityQueue<>(Comparator.comparingDouble(nd -> nd.distance));

            for (Node node : nodes) {
                distances.put(node.id, Double.MAX_VALUE);
                previous.put(node.id, null);
            }

            distances.put(start, 0.0);
            pq.add(new NodeDistance(start, 0.0));

            while (!pq.isEmpty()) {
                NodeDistance current = pq.poll();
                int currentNode = current.node;

                if (currentNode == destination) {
                    break;
                }

                for (Edge edge : adjList.get(currentNode)) {
                    double newDist = distances.get(currentNode) + edge.weight;
                    if (newDist < distances.get(edge.to)) {
                        distances.put(edge.to, newDist);
                        previous.put(edge.to, currentNode);
                        pq.add(new NodeDistance(edge.to, newDist));
                    }
                }
            }

            // Reconstruct the path
            List<Integer> path = new ArrayList<>();
            for (Integer at = destination; at != null; at = previous.get(at)) {
                path.add(at);
            }
            Collections.reverse(path);

            if (path.isEmpty() || path.get(0) != start) {
                return Collections.emptyList(); // No path found
            }
            return path;
        }

        static class NodeDistance {
            int node;
            double distance;

            NodeDistance(int node, double distance) {
                this.node = node;
                this.distance = distance;
            }
        }

        // Method to convert latitude and longitude to Cartesian coordinates
        static Node latLonToCartesian(int id, double lat, double lon) {
            final double R = 6371; // Earth's radius in km
            double x = R * Math.cos(Math.toRadians(lat)) * Math.cos(Math.toRadians(lon));
            double y = R * Math.cos(Math.toRadians(lat)) * Math.sin(Math.toRadians(lon));
            return new Node(id, x, y);
        }
    }

   /* public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Graph graph = new Graph();

        // Adding nodes to the graph (latitude, longitude)
        double[][] coordinates = {
                {0, 0},
                {2, 2},
                {3, 1},
                {5, 4},
                {7, 3}
        };

        for (int i = 0; i < coordinates.length; i++) {
            graph.addNode(Graph.latLonToCartesian(i + 1, coordinates[i][0], coordinates[i][1]));
        }

        //Adding edges to the graph
        graph.addEdge(1, 2, 2.0);
        graph.addEdge(2, 3, 1.0);
        graph.addEdge(3, 4, 2.0);
        graph.addEdge(4, 5, 3.0);
        graph.addEdge(1, 3, 2.5);
        graph.addEdge(2, 4, 2.2);
        graph.addEdge(3, 5, 2.8);

        System.out.println("Finding optimal camera position...");
        double viewRange = 3.0; // Example view range
        double gridStep = 0.5;  // Example grid step size
        Node optimalNode = graph.findOptimalCameraPosition(viewRange, gridStep);

        if (optimalNode != null) {
            System.out.println("Optimal camera position is at coordinates: (" + optimalNode.x + ", " + optimalNode.y + ")");
        } else {
            System.out.println("No optimal position found.");
        }

        System.out.println("Enter start node ID:");
        int start = scanner.nextInt();
        System.out.println("Enter destination node ID:");
        int destination = scanner.nextInt();

        List<Integer> shortestPath = graph.dijkstra(start, destination);

        if (shortestPath.isEmpty()) {
            System.out.println("No path found between node " + start + " and node " + destination);
        } else {
            System.out.println("Shortest path from node " + start + " to node " + destination + " is:");
            for (int nodeId : shortestPath) {
                System.out.print(nodeId + " ");
            }
        }

        scanner.close();
    }*/
}
