package com.accolite.HierarchicalGraphSolver.controller;
import com.accolite.HierarchicalGraphSolver.model.Node;
import com.accolite.HierarchicalGraphSolver.service.GraphService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/graph")
public class GraphController {

    @Autowired
    private GraphService graphService;

    @PostMapping("/load")
    public String loadGraph() {
        graphService.loadGraphFromJson();
        return "Graph loaded successfully!";
    }

    //to get a specific node by ID
    @GetMapping("/nodes/{id}")
    public Object getNode(@PathVariable String id) {
        return graphService.getNode(id);
    }

    // to find the path between two nodes
    @GetMapping("/paths")
    public List<String> findPath(@RequestParam String startNodeId, @RequestParam String endNodeId) {
        return graphService.findPath(startNodeId, endNodeId);
    }

    // to calculate the depth of a node
    @GetMapping("/nodes/{id}/depth")
    public int calculateDepth(@PathVariable String id) {
        return graphService.calculateDepth(id);
    }

    // to find the common ancestor of two nodes
    @GetMapping("/common-ancestor")
    public String findCommonAncestor(@RequestParam String nodeId1, @RequestParam String nodeId2) {
        return graphService.findCommonAncestor(nodeId1, nodeId2);
    }

    // Add a new node
    @PostMapping("/nodes")
    public String addNode(@RequestBody Node node) {
        graphService.addNode(node.getId(), node.getParentId());
        return "Node added successfully!";
    }

    // Update a node
    @PutMapping("/nodes/{id}")
    public String updateNode(@PathVariable String id, @RequestParam String newParentId) {
        graphService.updateNode(id, newParentId);
        return "Node updated successfully!";
    }

    // Delete a node
    @DeleteMapping("/nodes/{id}")
    public String deleteNode(@PathVariable String id) {
        graphService.deleteNode(id);
        return "Node deleted successfully!";
    }

    // Add a relationship between nodes
    @PostMapping("/relationships")
    public String addRelationship(@RequestParam String parentId, @RequestParam String childId) {
        graphService.addRelationship(parentId, childId);
        return "Relationship added successfully!";
    }

    // Delete a relationship
    @DeleteMapping("/relationships")
    public String deleteRelationship(@RequestParam String parentId, @RequestParam String childId) {
        graphService.deleteRelationship(parentId, childId);
        return "Relationship deleted successfully!";
    }
}
