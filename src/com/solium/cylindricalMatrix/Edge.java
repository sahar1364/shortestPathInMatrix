package com.solium.cylindricalMatrix;

/**
 * An Edge has a source node and a destination node (directional edge)
 * Edge also has a weight attribute specifying the weight to go from source to destination
 */

public class Edge {
	private final Node source;
	private final Node destination;
	private final int weight;
	
	public Edge(Node source, Node destination, int weight) {
		this.source = source;
		this.destination = destination;
		this.weight = weight;
	}

	public Node getSource() {
		return source;
	}

	public Node getDestination() {
		return destination;
	}

	public int getWeight() {
		return weight;
	}
	
	@Override
	public String toString() {
		return "Edge: " + source + " --> " + destination + ": " + weight;
	}
}
