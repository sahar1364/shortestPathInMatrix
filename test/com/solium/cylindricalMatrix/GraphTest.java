package com.solium.cylindricalMatrix;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.solium.cylindricalMatrix.Edge;
import com.solium.cylindricalMatrix.Graph;
import com.solium.cylindricalMatrix.Node;

/**
 * Tests Graph's public methods (finding shortest path method which uses Dijkstra algorithm)
 */
public class GraphTest {
	Node node1 = new Node(makeNodeId(0,0));
	Node node2 = new Node(makeNodeId(1, 1));
	Node node3 = new Node(makeNodeId(0, 1));
	Node node4 = new Node(makeNodeId(1, 0));
	
	//Standard version setting
	Edge edge1 = new Edge(node1, node3, 1);
	Edge edge2 = new Edge(node1, node2, 1);
	Edge edge3 = new Edge(node4, node2, 1);
	Edge edge4 = new Edge(node4, node3, 1);
	
	private String makeNodeId(int row, int col) {
		return Integer.toString(row+1) + "," + Integer.toString(col+1);
	}
	
	@Test
	public void shortestDistanceShouldReturnNullIfEdgesEmpty() {
		Graph graph = new Graph(new ArrayList<Edge>());
		assertEquals(Integer.MAX_VALUE, graph.getMinimumWeight());
	}
	
	@Test
	public void minimumWeightFromSourceToDestinationTestCase() {
		List<Edge> edges = new ArrayList<Edge>();
		edges.add(edge1);
		edges.add(edge2);
		edges.add(edge3);
		edges.add(edge4);
		Graph graph = new Graph(edges);
		List<Node> sources = new ArrayList<Node>();
		sources.add(node1);
		List<Node> destinations = new ArrayList<Node>();
		destinations.add(node2);
		List<Integer> sourceWeights = new ArrayList<Integer>();
		sourceWeights.add(1);
		graph.setSources(sources);
		graph.setDestinations(destinations);
		graph.setSourceWeights(sourceWeights);
		graph.findShortestPathFromSourcesAndDestinations();
		assertEquals(2, graph.getMinimumWeight());
	}
	
	@Test
	public void shortestPathShouldReturnMaxIfSourceOrDestinationNotSet() {
		List<Edge> edges = new ArrayList<Edge>();
		edges.add(edge1);
		edges.add(edge2);
		edges.add(edge3);
		edges.add(edge4);
		Graph graph = new Graph(edges);
		graph.findShortestPathFromSourcesAndDestinations();
		assertEquals(Integer.MAX_VALUE, graph.getMinimumWeight());		
	}
}
