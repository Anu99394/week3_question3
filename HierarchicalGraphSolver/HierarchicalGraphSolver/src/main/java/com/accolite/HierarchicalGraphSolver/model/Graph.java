package com.accolite.HierarchicalGraphSolver.model;

import java.util.*;

public class Graph {
    private final Map<String, Node> nodes = new HashMap<>();
    private final Map<String, List<String>> adjacencyList = new HashMap<>();

    public void addNode(Node node) {
        nodes.put(node.getId(), node);
        adjacencyList.putIfAbsent(node.getId(), new ArrayList<>());
    }

    public void addRelationship(String parentId, String childId) {
        if (nodes.containsKey(parentId) && nodes.containsKey(childId)) {
            adjacencyList.get(parentId).add(childId);
            nodes.get(childId).setParentId(parentId);
        } else {
            throw new IllegalArgumentException("Parent or child node not found.");
        }
    }

    public Map<String, Node> getNodes() {
        return nodes;
    }

    public Map<String, List<String>> getAdjacencyList() {
        return adjacencyList;
    }
}
