package com.accolite.HierarchicalGraphSolver.service;
import com.accolite.HierarchicalGraphSolver.exception.NodeNotFoundException;
import com.accolite.HierarchicalGraphSolver.model.*;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

@Service
public class GraphService {
    private final Graph graph = new Graph();

    public void loadGraphFromJson() {
        try (BufferedReader reader = new BufferedReader(new FileReader("C:\\Users\\AnuragKumar\\Downloads\\HierarchicalGraphSolver\\HierarchicalGraphSolver\\src\\main\\resources\\graph-data.json")))
        {
            ObjectMapper objectMapper = new ObjectMapper();
            GraphData graphData = objectMapper.readValue(reader, GraphData.class);

            graphData.getNodes().forEach(graph::addNode);

            graphData.getRelationships().forEach(relationship ->
                    graph.addRelationship(relationship.getParentId(), relationship.getChildId()));
        } catch (IOException e) {
            throw new RuntimeException("Failed to load graph data from JSON", e);
        }
    }

    public Node getNode(String nodeId) {
        Node node = graph.getNodes().get(nodeId);
        if (node == null) {
            throw new NodeNotFoundException("Node with ID " + nodeId + " not found.");
        }
        return node;
    }

    public List<String> findPath(String startNodeId, String endNodeId) {
        List<String> path = new ArrayList<>();
        if (!dfs(startNodeId, endNodeId, path, new HashSet<>())) {
            throw new IllegalArgumentException("No path found between the nodes.");
        }
        return path;
    }

    private boolean dfs(String current, String target, List<String> path, Set<String> visited) {
        if (visited.contains(current)) return false;
        visited.add(current);
        path.add(current);
        if (current.equals(target)) return true;
        for (String neighbor : graph.getAdjacencyList().getOrDefault(current, Collections.emptyList())) {
            if (dfs(neighbor, target, path, visited)) return true;
        }
        path.remove(path.size() - 1);
        return false;
    }

    public int calculateDepth(String nodeId) {
        Node node = graph.getNodes().get(nodeId);
        if (node == null) throw new IllegalArgumentException("Node not found.");
        int depth = 0;
        while (node.getParentId() != null) {
            depth++;
            node = graph.getNodes().get(node.getParentId());
        }
        return depth;
    }

    public String findCommonAncestor(String nodeId1, String nodeId2) {
        Set<String> ancestors1 = getAncestors(nodeId1);
        Set<String> ancestors2 = getAncestors(nodeId2);

        for (String ancestor : ancestors1) {
            if (ancestors2.contains(ancestor)) {
                return ancestor;
            }
        }

        throw new IllegalArgumentException("No common ancestor found.");
    }

    private Set<String> getAncestors(String nodeId) {
        Set<String> ancestors = new HashSet<>();
        Node node = graph.getNodes().get(nodeId);
        while (node != null && node.getParentId() != null) {
            ancestors.add(node.getParentId());
            node = graph.getNodes().get(node.getParentId());
        }
        return ancestors;
    }

    // Add a new node
    public void addNode(String nodeId, String parentId) {
        if (graph.getNodes().containsKey(nodeId)) {
            throw new IllegalArgumentException("Node with ID " + nodeId + " already exists.");
        }

        Node newNode = new Node();
        newNode.setId(nodeId);
        newNode.setParentId(parentId);

        graph.getNodes().put(nodeId, newNode);

        if (parentId != null) {
            addRelationship(parentId, nodeId);
        }
    }

    // Update a node's parent
    public void updateNode(String nodeId, String newParentId) {
        Node node = graph.getNodes().get(nodeId);
        if (node == null) {
            throw new NodeNotFoundException("Node with ID " + nodeId + " not found.");
        }
        String oldParentId = node.getParentId();
        node.setParentId(newParentId);

        // Remove the node from the old parent's children list
        if (oldParentId != null) {
            graph.getAdjacencyList().get(oldParentId).remove(nodeId);
        }

        // Add the node under the new parent
        if (newParentId != null) {
            addRelationship(newParentId, nodeId);
        }
    }

    // Delete a node
    public void deleteNode(String nodeId) {
        Node node = graph.getNodes().get(nodeId);
        if (node == null) {
            throw new NodeNotFoundException("Node with ID " + nodeId + " not found.");
        }

        // Delete all relationships to this node
        for (String parentId : graph.getAdjacencyList().keySet()) {
            graph.getAdjacencyList().get(parentId).remove(nodeId);
        }

        // Remove the node from the graph
        graph.getNodes().remove(nodeId);
    }

    // Add a relationship between two nodes
    public void addRelationship(String parentId, String childId) {
        Node parent = graph.getNodes().get(parentId);
        Node child = graph.getNodes().get(childId);
        if (parent == null || child == null) {
            throw new IllegalArgumentException("Parent or child node not found.");
        }
        graph.getAdjacencyList().putIfAbsent(parentId, new ArrayList<>());
        graph.getAdjacencyList().get(parentId).add(childId);
        child.setParentId(parentId);
    }

    // Delete a relationship between two nodes
    public void deleteRelationship(String parentId, String childId) {
        Node parent = graph.getNodes().get(parentId);
        Node child = graph.getNodes().get(childId);
        if (parent == null || child == null) {
            throw new IllegalArgumentException("Parent or child node not found.");
        }
        List<String> children = graph.getAdjacencyList().get(parentId);
        if (children != null) {
            children.remove(childId);
        }
        child.setParentId(null);
    }
}
