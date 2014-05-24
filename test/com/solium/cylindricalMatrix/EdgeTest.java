package com.solium.cylindricalMatrix;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.solium.cylindricalMatrix.Edge;
import com.solium.cylindricalMatrix.Node;

/**
 * Tests Edge constructor, getters and setters
 */
public class EdgeTest {

	@Test
	public void buildEdge() {
		Node node1 = new Node(makeNodeId(0,0));
		Node node2 = new Node(makeNodeId(1,1));
		Edge edge = new Edge(node1, node2, 1);
		assertEquals(edge.getSource(), node1);
		assertEquals(edge.getDestination(), node2);
		assertEquals(edge.getWeight(), 1);
	}
	
	private String makeNodeId(int row, int col) {
		return Integer.toString(row+1) + "," + Integer.toString(col+1);
	}
}
