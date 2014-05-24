package com.solium.cylindricalMatrix.parser;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.solium.cylindricalMatrix.Edge;
import com.solium.cylindricalMatrix.Graph;
import com.solium.cylindricalMatrix.Node;
import com.solium.cylindricalMatrix.parser.CylindricalMatrixInputParser;
import com.solium.cylindricalMatrix.parser.CylindricalMatrixInputParserException;

/**
 * Tests CylindricalMatrixInputParser to make sure it throws appropriate messages for malformed input files
 */
public class CylindricalMatrixInputParserTest {
	
    @Test (expected = CylindricalMatrixInputParserException.class)
    public void shouldNotAllowMalformedHeader() throws IOException, CylindricalMatrixInputParserException {
        String config = "M";
        new CylindricalMatrixInputParser().buildGraph(new StringReader(config));
    }
    
    @Test (expected = CylindricalMatrixInputParserException.class)
    public void shouldNotAllowWrongNumberOfColumns() throws IOException, CylindricalMatrixInputParserException {
        String config = "S\n1 2\n1 2 3";
        new CylindricalMatrixInputParser().buildGraph(new StringReader(config));
    }
    
    @Test (expected = CylindricalMatrixInputParserException.class)
    public void shouldNotAllowWrongNumberOfRows() throws IOException, CylindricalMatrixInputParserException {
        String config = "S\n2 2\n1 2";
        new CylindricalMatrixInputParser().buildGraph(new StringReader(config));
    }
    
    @Test (expected = CylindricalMatrixInputParserException.class)
    public void shouldNotAllowMissingSourceCoordinatesForB1AndB2() throws IOException, CylindricalMatrixInputParserException {
        String config = "B1\n2 2\n1 2\n3 4";
        new CylindricalMatrixInputParser().buildGraph(new StringReader(config));
    }
    
    @Test (expected = CylindricalMatrixInputParserException.class)
    public void shouldNotAllowMissingDestinationCoordinatesForB1AndB2() throws IOException, CylindricalMatrixInputParserException {
        String config = "B2\n2 2 3,4\n1 2\n3 4";
        new CylindricalMatrixInputParser().buildGraph(new StringReader(config));
    }
    
    @Test
    public void graphIsBuiltProperly() throws IOException, CylindricalMatrixInputParserException {
		Node node_1 = new Node(makeNodeId(0,0));
		Node node_2 = new Node(makeNodeId(0,1));
		Node node_3 = new Node(makeNodeId(0,2));
		Node node_4 = new Node(makeNodeId(1,0));
		Node node_5 = new Node(makeNodeId(1,1));
		Node node_6 = new Node(makeNodeId(1,2));
		Node node_7 = new Node(makeNodeId(2,0));
		Node node_8 = new Node(makeNodeId(2,1));
		Node node_9 = new Node(makeNodeId(2,2));
		
		Edge edge_1 = new Edge(node_1, node_2, 1);
		Edge edge_2 = new Edge(node_1, node_5, 1);
		Edge edge_3 = new Edge(node_1, node_8, 9);
		Edge edge_4 = new Edge(node_2, node_3, 9);
		Edge edge_5 = new Edge(node_2, node_6, 9);
		Edge edge_6 = new Edge(node_2, node_9, 1);
		Edge edge_7 = new Edge(node_4, node_5, 1);
		Edge edge_8 = new Edge(node_4, node_8, 9);
		Edge edge_9 = new Edge(node_4, node_2, 1);
		Edge edge_10 = new Edge(node_5, node_6, 9);
		Edge edge_11 = new Edge(node_5, node_3, 9);
		Edge edge_12 = new Edge(node_5, node_9, 1);
		Edge edge_13 = new Edge(node_7, node_8, 9);
		Edge edge_14 = new Edge(node_7, node_5, 1);
		Edge edge_15 = new Edge(node_7, node_2, 1);
		Edge edge_16 = new Edge(node_8, node_9, 1);
		Edge edge_17 = new Edge(node_8, node_6, 9);
		Edge edge_18 = new Edge(node_8, node_3, 9);
		
		List<Edge> edges = new ArrayList<Edge>();
		edges.add(edge_1);
		edges.add(edge_2);
		edges.add(edge_3);
		edges.add(edge_4);
		edges.add(edge_5);
		edges.add(edge_6);
		edges.add(edge_7);
		edges.add(edge_8);
		edges.add(edge_9);
		edges.add(edge_10);
		edges.add(edge_11);
		edges.add(edge_12);
		edges.add(edge_13);
		edges.add(edge_14);
		edges.add(edge_15);
		edges.add(edge_16);
		edges.add(edge_17);
		edges.add(edge_18);
		
        String config = "S\n3 3\n1 1 9\n9 1 9\n9 9 1";
		
		Graph graph = new CylindricalMatrixInputParser().buildGraph(new StringReader(config));
		assertEquals(edges.size(), graph.getEdges().size());
    }
    
	private String makeNodeId(int row, int col) {
		return Integer.toString(row+1) + "," + Integer.toString(col+1);
	}
	
}
