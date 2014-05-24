package com.solium.cylindricalMatrix;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 *Graph defined by its edges 
 * 
 */

public class Graph {
	
	private  List<Edge> edges;
	
	//sources and destinations are set outside this class. If the problem is Standard, then we need to have
	//the list of all sources in the first column and the list of all destinations in the last column to find
	//the minimum weight for going from the first column to the last column
	private List<Node> sources;
	private List<Node> destinations;
	private List<Integer> sourceWeights; //Keeps the weight for the sources to be added to the sum of the weight from source to dest since we do not take into account the source weight in the algorithm
	private int minimumWeight = Integer.MAX_VALUE; //Keeps the minimal weight to go from source(s) to destination(s)

	//These variables are needed for finding the shortest path between a source and a destination using Dijkstra
	private Set<Node> solvedNodes = new HashSet<Node>();
	private Set<Node> unSolvedNodes = new HashSet<Node>();
	private Map<Node, Node> pathTrack = new HashMap<Node, Node>();
	private Map<Node, Integer> distance = new HashMap<Node, Integer>();
	
	public Graph(List<Edge> edges) {
		this.edges = edges;
	}

	public List<Edge> getEdges() {
		return edges;
	}
	
	public int getMinimumWeight() {
		return minimumWeight;
	}
	
	public void setSources(List<Node> sources) {
		this.sources = sources;
	}
	
	public void setDestinations(List<Node> destinations) {
		this.destinations = destinations;
	}
	
	public void setSourceWeights(List<Integer> sourceWeights) {
		this.sourceWeights = sourceWeights;
	}
	
	/**
	 * This method uses Dijkstra's algorithm to find the shortest path (possibly more than one) between 
	 * source and destination
	 * @param source 
	 * @param destination
	 * @return A list of nodes to go from source to the destination from the shortest path
	 * 
	 * NOTE: The implementation of Dijkstra is inspired from http://www.vogella.com/tutorials/JavaAlgorithmsDijkstra/article.html
	 */
	private LinkedList<Node> findShortestPathFromSourceToDestination(Node source, Node destination) {
	    LinkedList<Node> path = new LinkedList<Node>();
	    distance.put(source, 0);
	    unSolvedNodes.add(source);
	    while (unSolvedNodes.size() > 0) {
	      Node node = getMinimum(unSolvedNodes);
	      solvedNodes.add(node);
	      unSolvedNodes.remove(node);
	      findMinimalDistances(node);
	    }
	    Node step = destination;
	    // check if a path exists
	    if (pathTrack.get(step) == null)
	      return null;
	    path.add(step);
	    while (pathTrack.get(step) != null) {
	      step = pathTrack.get(step);
	      path.add(step);
	    }
	    // reverse the list to get the right order of nodes from source to destination
	    Collections.reverse(path);
	    return path;
	}
	
	/**
	 * Finds the total weight for the shortest path between source and the destination
	 * @param sourceWeight
	 * @param destination
	 * @return
	 */
	private Integer shortestPathWeight(int sourceWeight, Node destination) {
		if (distance.get(destination) == null)
			return null;
		return distance.get(destination) + sourceWeight;
	}
	
	/**
	 * @param node
	 * Given a node it sets the distance for the neighbor nodes(targets), if the distance from node to the neighbor 
	 * is less than or equal to the distance value for the target(neighbor) -in case of a tie it will do a lexicoGraphical check
	 */
	private void findMinimalDistances(Node node) {
	    List<Node> adjacentNodes = getNeighbors(node);
	    for (Node target : adjacentNodes) {
	    	int currentDistance = getShortestDistance(target);
	    	int nodeDistance = getShortestDistance(node);
	    	int nodeTargetDistance = getDistance(node, target);
	    	if ((currentDistance > nodeDistance + nodeTargetDistance) || ((currentDistance == nodeDistance + nodeTargetDistance) && (node.getId().compareTo(pathTrack.get(target).getId()) < 0))) {
		        distance.put(target, nodeDistance + getDistance(node, target));
		        pathTrack.put(target, node);
		        unSolvedNodes.add(target);
	    	}
	    }
	}
	
	/**
	 * @param node
	 * @param target
	 * @return gets the weight for an edge which is the distance between the input nodes
	 */
	private int getDistance(Node node, Node target) {
	    for (Edge edge : edges) {
	      if (edge.getSource().equals(node) && edge.getDestination().equals(target))
	        return edge.getWeight();
	    }
	    throw new RuntimeException("Should not happen");
    }
	
	/**
	 * @param node
	 * @return finds the adjacent nodes to a node
	 */
	private List<Node> getNeighbors(Node node) {
	    List<Node> neighbors = new ArrayList<Node>();
	    for (Edge edge : edges) {
	      if (edge.getSource().equals(node)
	          && !isSolved(edge.getDestination())) {
	        neighbors.add(edge.getDestination());
	      }
	    }
	    return neighbors;
    }
	
	/**
	 * @param nodes
	 * @return Among a set of nodes the node that has the shortest distance from source is returned
	 */
	private Node getMinimum(Set<Node> nodes) {
	    Node minimum = null;
	    for (Node node : nodes) {
	      if (minimum == null) 
	        minimum = node;
	      else if ((getShortestDistance(node) < getShortestDistance(minimum)) || (getShortestDistance(node) == getShortestDistance(minimum) && node.getId().compareTo(minimum.getId()) < 0))
	          minimum = node;
	    }
	    return minimum;
    }
	
	/**
	 * @param node
	 * @return
	 */
	private boolean isSolved(Node node) {
	    return solvedNodes.contains(node);
    }
	
	/**
	 * @param destination
	 * @return The shortest distance so far to get from source to a node
	 */
	private int getShortestDistance(Node destination) {
	    Integer d = distance.get(destination);
	    if (d == null)
	      return Integer.MAX_VALUE;
	    else 
	      return d;
    }
	
	/**
	 * this method clears the graph from attributes values that are used for finding shortest path
	 */
	private void clearGraph() {
		solvedNodes.clear();
		unSolvedNodes.clear(); 
		pathTrack.clear();
		distance.clear();
	}
	
	/**
	 * Finds the minimal weight given a set of sources and destinations
	 * @return the list of nodes indicating the shortest path
	 */
	public LinkedList<Node> findShortestPathFromSourcesAndDestinations() {
		if (sources == null || destinations == null)
			return null;
		LinkedList<Node> minimumPath = new LinkedList<Node>();
		for (int i = 0; i < sources.size(); i++) {
			for (int j = 0; j < destinations.size(); j++) {
				LinkedList<Node> potentialShortestPath = findShortestPathFromSourceToDestination(sources.get(i), destinations.get(j));
				int potentialMinWeight = shortestPathWeight(sourceWeights.get(i), destinations.get(j));
				if (potentialMinWeight < minimumWeight) {
					minimumWeight = potentialMinWeight;
					minimumPath.clear();
					for (Node node: potentialShortestPath)
						minimumPath.add(node);
				}
				clearGraph();
			}
		}
		return minimumPath;
	}
	
	/**
	 * Lexicographically sorts the list of nodes in the path and return the smallest path string for display
	 * @return a string of the shortest path
	 */
	public String lexicographicallySmallestForDisplay() {
		LinkedList<Node> minimumPath = findShortestPathFromSourcesAndDestinations();
		String stringArray = Arrays.toString(minimumPath.toArray());
		String pathForDisplay = stringArray.substring(1, stringArray.length()-1);
		return pathForDisplay.replace("),", ")");
	}

} 

