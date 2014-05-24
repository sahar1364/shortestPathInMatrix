package com.solium.cylindricalMatrix;

import java.util.List;


/**
 * A node in a graph has an id, 
 * where the id is constructed by the location of the node in the input matrix (row, column)
 * A node has outboundEdges which make it easy to find its adjacent nodes 
 */

public class Node {

	private String id;
	//TODO: setting outbound edges on the node helps the performance for finding adjacent nodes in Dijkstra algorithm 
	private List<Edge> outboundEdges;
	
	public Node(String id) {
		if (!doesIdFollowConventions(id))
			throw new IllegalArgumentException();
		this.id = id;
	}

	public String getId() {
		return id;
	}

	public List<Edge> getOutboundEdges() {
		return outboundEdges;
	}

	public void setOutboundEdges(List<Edge> outgoingEdges) {
		this.outboundEdges = outgoingEdges;
	}
	
	/**
	 * For this project, we use i,j format for the ids where i is the row number and j is the column number
	 * @param idForCheck
	 * @return
	 */
	private boolean doesIdFollowConventions(String idForCheck) {
		if (idForCheck == null)
			return false;
		int commaIndex = idForCheck.indexOf(",");
		if (commaIndex == -1)
			return false;
		String i = idForCheck.substring(0, commaIndex);
		String j = idForCheck.substring(commaIndex+1, idForCheck.length());
		try {
			Double.parseDouble(i);
			Double.parseDouble(j);
		}
		catch(NumberFormatException nfe) {  
			return false;  
		}  
		return true;
	}
	
	@Override
	public String toString() {
		return "("+id+")";
	}
}
