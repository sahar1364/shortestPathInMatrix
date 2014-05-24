package com.solium.cylindricalMatrix.parser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import com.solium.cylindricalMatrix.Edge;
import com.solium.cylindricalMatrix.Graph;
import com.solium.cylindricalMatrix.Node;

/**
 * Class to create a Graph based on a matrix configuration in a text file.  
 * The configuration must consist of the following:
 *
 * 1. A header row The first line of each specification will indicate which criteria will be used to find the path.  
 * ‘S’ for the standard question, ‘B1’ for bonus 1 and ‘B2’ for bonus 2.
 * 2. Two integers where m is the row size and n is the column size for the standard version
 * 	For Bonus1 and Bonus2 versions, this line is followed by the source and the destination
 * 3. The rest of the file includes corresponding weight for each cell in the matrix
 *
 * Example of a configuration for standard version:
 *S
 *5 6
 *3 4 1 2 8 6
 *6 1 8 2 7 4
 *5 9 3 9 9 5
 *8 4 1 3 2 6
 *3 7 2 8 6 4
 *
 *Example of a configuration for Bonus1 version:
 *B1
 *5 6 1,1 5,6
 *3 4 1 2 8 6
 *6 1 8 2 7 4
 *5 9 3 9 9 5
 *8 4 1 3 2 6
 *3 7 2 8 6 4
 *
 *Example of a configuration for Bonus2 version:
 *B2
 *5 6 1,1 5,6
 *3 4 1 2 8 6
 *6 1 8 2 7 4
 *5 9 3 9 9 5
 *8 4 1 3 2 6
 *3 7 2 8 6 4

 */

public class CylindricalMatrixInputParser {
	
	Graph graph = new Graph(new ArrayList<Edge>());
	Map<String, Node> nodesMap = new HashMap<String, Node>();
	
	/**
     * Given a connection to an input file, builds the graph.
     *
     * @param input A reader currently pointing to a matrix input file.
     * @return The Graph constructed based on the matrix in the provided input file.
     * @throws IOException If there are any errors reading from the input Reader.
     * @throws CylindricalMatrixInputParserException If the input file is malformed
     * 
     */
    public Graph buildGraph(Reader input) throws IOException, CylindricalMatrixInputParserException {
        BufferedReader bin = new BufferedReader(input);
        String header = bin.readLine().trim();
        
        if (!isStandard(header) && !isBonus1(header) && !isBonus2(header))
        	throw new CylindricalMatrixInputParserException("The input file is missing a valid header (S, B1, B2)");
        
        String matrixDimensionsLine = bin.readLine();
        StringTokenizer tokenizer = new StringTokenizer(matrixDimensionsLine, " ");
        
        String rowsStr = tokenizer.nextToken().trim();
        if (!tokenizer.hasMoreElements())
        	throw new CylindricalMatrixInputParserException("Please provide two numbers for matrix dimensions in the input file");
        String columnsStr = tokenizer.nextToken().trim();
        if (rowsStr == null || columnsStr == null)
        	throw new CylindricalMatrixInputParserException("Please provide valid numbers for matrix dimensions in the input file");
        
        int rows = Integer.parseInt(rowsStr);
        int cols = Integer.parseInt(columnsStr);
        createAllNodesOfGraph(rows, cols);
        
        int[][] weightMatrix = new int[rows][cols];
        
        /*
         * if the input file is a standard version
         */
        if ("S".equals(header)) {
            for (int row = 0; row < rows; row++) {
            	String line = bin.readLine();
            	if (line == null)
            		throw new CylindricalMatrixInputParserException("Invalid number of rows");
            	
            	String[] tokens = line.split("[ ]+");
            	if (tokens.length != cols)
            		throw new CylindricalMatrixInputParserException("Invalid number of columns");
            	
            	for (int col = 0; col < cols; col++) {
            		
            		weightMatrix[row][col] = Integer.parseInt(tokens[col]);
            		
            		if (col > 0) {
            			
            			// If top row, only add horizontal edges
            			if (row == 0) 
            				buildEdgesFirstRowStandard(row, col,  weightMatrix);
            			
            			else if (row < rows-1) 
            				buildEdgesForMiddleRowsStandard(row, col, weightMatrix);
            			
            			// If bottom row, add top edges
            			else 
            				buildEdgesForLastRowStandard(row, col, weightMatrix);
            			
            		}
            	}
            }
            List<Integer> sourceWeights = new ArrayList<Integer>();
            List<Node> sources = new ArrayList<Node>();
            List<Node> destinations = new ArrayList<Node>();
            for (int i = 0; i < rows; i++) {
            	sourceWeights.add(weightMatrix[i][0]);
            	sources.add(nodesMap.get(makeNodeId(i, 0)));
            	destinations.add(nodesMap.get(makeNodeId(i, cols-1)));
            }
            graph.setSourceWeights(sourceWeights);
            graph.setSources(sources);
            graph.setDestinations(destinations);
        }
        
        /*
         * if the input file is the Bonus1 version
         */
        else if ("B1".equals(header)) {
        	if (!tokenizer.hasMoreElements())
        		throw new CylindricalMatrixInputParserException("Source coordinates missing");
        	String sourceNodeStr = tokenizer.nextToken().trim();
        	if (!tokenizer.hasMoreElements())
        		throw new CylindricalMatrixInputParserException("Destination coordinates missing");
        	String destNodeStr = tokenizer.nextToken().trim();
        	List<Node> sources = new ArrayList<Node>();
        	sources.add(nodesMap.get(sourceNodeStr));
        	List<Node> destinations = new ArrayList<Node>();
        	destinations.add(nodesMap.get(destNodeStr));
        	graph.setSources(sources);
        	graph.setDestinations(destinations);
        	
            for (int row = 0; row < rows; row++) {
            	String line = bin.readLine();
            	if (line == null)
            		throw new CylindricalMatrixInputParserException("Invalid number of rows");

            	String[] tokens = line.split(" ");
            	if (tokens.length != cols)
            		throw new CylindricalMatrixInputParserException("Invalid number of columns");
            	
            	for (int col = 0; col < cols; col++) {
            		
            		weightMatrix[row][col] = Integer.parseInt(tokens[col]);
            		
            		if (col == 0) {
            			//middle rows first column
            			if (row > 0 && row < rows -1) 
            				buildEdgesForMiddleRowsFirstColumnBonus1(row, col, weightMatrix);
            			
            			//last row first column
            			else if (row == rows -1) 
            				buildEdgesForLastRowsFirstColumnBonus1(row, col, weightMatrix);
            		}
            		
            		if (col > 0) {
            			// If top row, only add horizontal edges
            			if (row == 0) 
            				buildEdgesForFirstRowBonus1(row, col, weightMatrix);

            			else if (row < rows-1) 
            				buildEdgesForMiddleRowsBonus1(row, col, weightMatrix);
            			
            			// If bottom row, add top edges
            			else 
            				buildEdgesForLastRowBonus1(row, col, weightMatrix);
            		}
            	}
            }
            int[] dimensions = sourceDimensions(sourceNodeStr);
            List<Integer> sourceWeights = new ArrayList<Integer>();
            sourceWeights.add(weightMatrix[dimensions[0]-1][dimensions[1]-1]);
            graph.setSourceWeights(sourceWeights);
        }
        
        /*
         * if the input file is the Bonus2 version
         */
        else if ("B2".equals(header)) {
        	if (!tokenizer.hasMoreElements())
        		throw new CylindricalMatrixInputParserException("Source coordinates missing");
        	String sourceNodeStr = tokenizer.nextToken().trim();
        	if (!tokenizer.hasMoreElements())
        		throw new CylindricalMatrixInputParserException("Destination coordinates missing");
        	String destNodeStr = tokenizer.nextToken().trim(); 
        	List<Node> sources = new ArrayList<Node>();
        	sources.add(nodesMap.get(sourceNodeStr));
        	List<Node> destinations = new ArrayList<Node>();
        	destinations.add(nodesMap.get(destNodeStr));
        	graph.setSources(sources);
        	graph.setDestinations(destinations);
        	
            for (int row = 0; row < rows; row++) {
            	String line = bin.readLine();
            	if (line == null)
            		throw new CylindricalMatrixInputParserException("Invalid number of rows");
            	
            	String[] tokens = line.split(" ");
            	if (tokens.length != cols)
            		throw new CylindricalMatrixInputParserException("Invalid number of columns");
            	
            	for (int col = 0; col < cols; col++) {
            		
            		weightMatrix[row][col] = Integer.parseInt(tokens[col]);
            		
            		if (col == 0) {
            			//middle rows first column
            			if (row > 0 && row < rows -1) 
            				buildEdgesForMiddleRowsFirstColumnBonus2(row, col, weightMatrix);
            			
            			//last row first column
            			else if (row == rows -1) 
            				buildEdgesForLastRowsFirstColumnBonus2(row, col, weightMatrix);
            		}     
            		
            		if (col > 0 && col < cols-1) {
            			
            			// If top row, only add horizontal edges
            			if (row == 0) 
            				buildEdgesForFirstRowBonus1(row, col, weightMatrix);

            			else if (row < rows-1) 
            				buildEdgesForMiddleRowsBonus1(row, col, weightMatrix);
            			
            			// If bottom row, add top edges
            			else 
            				buildEdgesForLastRowBonus2(row, col, weightMatrix);
            		}
            		//last column
            		else if (col == cols-1) {
               			// If top row, only add horizontal edges
            			if (row == 0) 
            				buildEdgesForFirstRowLastColumnBonus2(row, col, weightMatrix);
            			
            			else if (row < rows-1) 
            				buildEdgesForMiddleRowLastColumnBonus2(row, col, weightMatrix);
            			
            			// If bottom row, add top edges
            			else 
            				buildEdgesForLastRowLastColumnBonus2(row, col, weightMatrix);
            		}
            	}
            }
            int[] dimensions = sourceDimensions(sourceNodeStr);
            List<Integer> sourceWeights = new ArrayList<Integer>();
            sourceWeights.add(weightMatrix[dimensions[0]-1][dimensions[1]-1]);
            graph.setSourceWeights(sourceWeights); 
        }
        
        return graph;
    }

    /**
     * 
     * @param row
     * @param col
     * @return the id made for a node based on its row and column
     */
	private String makeNodeId(int row, int col) {
		return Integer.toString(row+1) + "," + Integer.toString(col+1);
	}
    
    /**
     * 
     * @param header
     * @return true if the header is a standard header
     */
    private boolean isStandard(String header) {
    	return "S".equals(header);
    }
    
    /**
     * 
     * @param header
     * @return true if the header is a Bonus1 header
     */
    private boolean isBonus1(String header) {
    	return "B1".equals(header);
    }
    
    /**
     * 
     * @param header
     * @return true if the header is a Bonus2 header
     */
    private boolean isBonus2(String header) {
    	return "B2".equals(header);
    }
    
    /**
     * 
     * @param sourceStr
     * @return Given the id of a node, it will return the corresponding row and column indices in the matrix
     */
    private int[] sourceDimensions(String sourceStr) {
		int commaIndex = sourceStr.indexOf(",");
		String i = sourceStr.substring(0, commaIndex);
		String j = sourceStr.substring(commaIndex+1, sourceStr.length());
		int[] ij = new int[2];
		ij[0] = Integer.parseInt(i);
		ij[1] = Integer.parseInt(j);
		return ij;
    }
    
    /**
     * 
     * @param rows
     * @param columns
     * Creates nodes for each cell of the input matrix
     */
    private void createAllNodesOfGraph(int rows, int columns) {
    	try {
	    	for (int row=0; row<rows; row++) {
	    		for (int column = 0; column < columns; column++) {
					String nodeId = makeNodeId(row, column); 
	    			Node node = new Node(nodeId);
	    			nodesMap.put(nodeId, node);
	    		}
	    	}
    	}
    	catch (IllegalArgumentException e) {
    		System.out.println(e.getMessage());
    	}
    }
    
    /**
     * 
     * @param row
     * @param col
     * @param weight
     */
    private void buildEdgesFirstRowStandard(int row, int col, int[][] weightMatrix) {
		Edge edge1 = new Edge(nodesMap.get(makeNodeId(row, col-1)), nodesMap.get(makeNodeId(row, col)), weightMatrix[row][col]);
		graph.getEdges().add(edge1);
    }
    
    /**
     * 
     * @param row
     * @param col
     * @param weight
     * @param weight2
     */
    private void buildEdgesForMiddleRowsStandard(int row, int col, int[][] weightMatrix) {
		Edge edge1 = new Edge(nodesMap.get(makeNodeId(row-1, col-1)), nodesMap.get(makeNodeId(row, col)), weightMatrix[row][col]);
		graph.getEdges().add(edge1);
		Edge edge2 = new Edge(nodesMap.get(makeNodeId(row, col-1)), nodesMap.get(makeNodeId(row, col)), weightMatrix[row][col]);
		graph.getEdges().add(edge2);
		Edge edge3 = new Edge(nodesMap.get(makeNodeId(row, col-1)), nodesMap.get(makeNodeId(row-1, col)), weightMatrix[row-1][col]);
		graph.getEdges().add(edge3);
    }
    
    /**
     * 
     * @param row
     * @param col
     * @param weight1
     * @param weight2
     */
    private void buildEdgesForLastRowStandard(int row, int col, int[][] weightMatrix) {
		Edge edge1 = new Edge(nodesMap.get(makeNodeId(row-1, col-1)), nodesMap.get(makeNodeId(row, col)), weightMatrix[row][col]);
		graph.getEdges().add(edge1);
		Edge edge2 = new Edge(nodesMap.get(makeNodeId(row, col-1)), nodesMap.get(makeNodeId(row, col)), weightMatrix[row][col]);
		graph.getEdges().add(edge2);

		// Add a path to the top row
		Edge edge3 = new Edge(nodesMap.get(makeNodeId(row, col-1)), nodesMap.get(makeNodeId(0,  col)), weightMatrix[0][col]);
		graph.getEdges().add(edge3);
		
		// Add a path from the top row
		Edge edge4 = new Edge(nodesMap.get(makeNodeId(0, col-1)), nodesMap.get(makeNodeId(row, col)), weightMatrix[row][col]);
		graph.getEdges().add(edge4);

		Edge edge5 = new Edge(nodesMap.get(makeNodeId(row, col-1)), nodesMap.get(makeNodeId(row-1, col)), weightMatrix[row-1][col]);
		graph.getEdges().add(edge5);

    }
    
    /**
     * 
     * @param row
     * @param col
     * @param weightMatrix
     */
    private void buildEdgesForMiddleRowsFirstColumnBonus1(int row, int col, int[][] weightMatrix) {
		Edge edge1 = new Edge(nodesMap.get(makeNodeId(row, col)), nodesMap.get(makeNodeId(row-1, col)), weightMatrix[row-1][col]);
		graph.getEdges().add(edge1);
		Edge edge2 = new Edge(nodesMap.get(makeNodeId(row-1, col)), nodesMap.get(makeNodeId(row, col)), weightMatrix[row][col]);
		graph.getEdges().add(edge2);  	
    }
    
    /**
     * 
     * @param row
     * @param col
     * @param weightMatrix
     */
    private void buildEdgesForLastRowsFirstColumnBonus1(int row, int col, int[][] weightMatrix) {
		Edge edge1 = new Edge(nodesMap.get(makeNodeId(row, col)), nodesMap.get(makeNodeId(0, 0)), weightMatrix[0][0]);
		graph.getEdges().add(edge1);
		Edge edge2 = new Edge(nodesMap.get(makeNodeId(0, 0)), nodesMap.get(makeNodeId(row, col)), weightMatrix[row][col]);
		graph.getEdges().add(edge2);
		Edge edge3 = new Edge(nodesMap.get(makeNodeId(row, col)), nodesMap.get(makeNodeId(row-1, col)), weightMatrix[row-1][col]);
        graph.getEdges().add(edge3);
        Edge edge4 = new Edge(nodesMap.get(makeNodeId(row-1, col)), nodesMap.get(makeNodeId(row, col)), weightMatrix[row][col]);
        graph.getEdges().add(edge4);
    }
    
    /**
     * 
     * @param row
     * @param col
     * @param weightMatrix
     */
    private void buildEdgesForFirstRowBonus1(int row, int col, int[][] weightMatrix) {
		Edge edge1 = new Edge(nodesMap.get(makeNodeId(row, col-1)), nodesMap.get(makeNodeId(row, col)), weightMatrix[row][col]);
		graph.getEdges().add(edge1);
		Edge edge2 = new Edge(nodesMap.get(makeNodeId(row, col)), nodesMap.get(makeNodeId(row, col-1)), weightMatrix[row][col-1]);
		graph.getEdges().add(edge2);    	
    }
    
    /**
     * 
     * @param row
     * @param col
     * @param weightMatrix
     */
    private void buildEdgesForMiddleRowsBonus1(int row, int col, int[][] weightMatrix) {
		Edge edge1 = new Edge(nodesMap.get(makeNodeId(row-1, col-1)), nodesMap.get(makeNodeId(row, col)), weightMatrix[row][col]);
		graph.getEdges().add(edge1);
		Edge edge2 = new Edge(nodesMap.get(makeNodeId(row, col)), nodesMap.get(makeNodeId(row-1, col-1)), weightMatrix[row-1][col-1]);
		graph.getEdges().add(edge2);
		Edge edge3 = new Edge(nodesMap.get(makeNodeId(row, col-1)), nodesMap.get(makeNodeId(row, col)), weightMatrix[row][col]);
		graph.getEdges().add(edge3);
		Edge edge4 = new Edge(nodesMap.get(makeNodeId(row, col)), nodesMap.get(makeNodeId(row, col-1)), weightMatrix[row][col-1]);
		graph.getEdges().add(edge4);
		Edge edge5 = new Edge(nodesMap.get(makeNodeId(row, col-1)), nodesMap.get(makeNodeId(row-1, col)), weightMatrix[row-1][col]);
		graph.getEdges().add(edge5);
		Edge edge6 = new Edge(nodesMap.get(makeNodeId(row-1, col)), nodesMap.get(makeNodeId(row, col-1)), weightMatrix[row][col-1]);
		graph.getEdges().add(edge6);
		Edge edge7 = new Edge(nodesMap.get(makeNodeId(row-1, col)), nodesMap.get(makeNodeId(row, col)), weightMatrix[row][col]);
		graph.getEdges().add(edge7);
		Edge edge8 = new Edge(nodesMap.get(makeNodeId(row, col)), nodesMap.get(makeNodeId(row-1, col)), weightMatrix[row-1][col]);
		graph.getEdges().add(edge8);

    }
    
    /**
     * 
     * @param row
     * @param col
     * @param weightMatrix
     */
    private void buildEdgesForLastRowBonus1(int row, int col, int[][] weightMatrix) {
		Edge edge1 = new Edge(nodesMap.get(makeNodeId(row, col-1)), nodesMap.get(makeNodeId(row, col)), weightMatrix[row][col]);
		graph.getEdges().add(edge1);
		Edge edge2 = new Edge(nodesMap.get(makeNodeId(row, col)), nodesMap.get(makeNodeId(row, col-1)), weightMatrix[row][col-1]);
		graph.getEdges().add(edge2);
		Edge edge3 = new Edge(nodesMap.get(makeNodeId(row, col-1)), nodesMap.get(makeNodeId(0, col)), weightMatrix[0][col]);
		graph.getEdges().add(edge3);
		Edge edge4 = new Edge(nodesMap.get(makeNodeId(0, col)), nodesMap.get(makeNodeId(row, col-1)), weightMatrix[row][col-1]);
		graph.getEdges().add(edge4);
		Edge edge5 = new Edge(nodesMap.get(makeNodeId(0, col-1)), nodesMap.get(makeNodeId(row, col)), weightMatrix[row][col]);
		graph.getEdges().add(edge5);
		Edge edge6 = new Edge(nodesMap.get(makeNodeId(row, col)), nodesMap.get(makeNodeId(0, col-1)), weightMatrix[0][col-1]);
		graph.getEdges().add(edge6);
		Edge edge7 = new Edge(nodesMap.get(makeNodeId(row-1, col-1)), nodesMap.get(makeNodeId(row, col)), weightMatrix[row][col]);
		graph.getEdges().add(edge7);
		Edge edge8 = new Edge(nodesMap.get(makeNodeId(row, col)), nodesMap.get(makeNodeId(row-1, col-1)), weightMatrix[row-1][col-1]);
		graph.getEdges().add(edge8);
		Edge edge9 = new Edge(nodesMap.get(makeNodeId(row-1, col)), nodesMap.get(makeNodeId(row, col)), weightMatrix[row][col]);
		graph.getEdges().add(edge9);
		Edge edge10 = new Edge(nodesMap.get(makeNodeId(row, col)), nodesMap.get(makeNodeId(row-1, col)), weightMatrix[row-1][col]);
		graph.getEdges().add(edge10);
		Edge edge11 = new Edge(nodesMap.get(makeNodeId(0, col)), nodesMap.get(makeNodeId(row, col)), weightMatrix[row][col]);
		graph.getEdges().add(edge11);
		Edge edge12 = new Edge(nodesMap.get(makeNodeId(row, col)), nodesMap.get(makeNodeId(0, col)), weightMatrix[0][col]);
		graph.getEdges().add(edge12);
		Edge edge13 = new Edge(nodesMap.get(makeNodeId(row, col-1)), nodesMap.get(makeNodeId(row-1, col)), weightMatrix[row-1][col]);
		graph.getEdges().add(edge13);
		Edge edge14 = new Edge(nodesMap.get(makeNodeId(row-1, col)), nodesMap.get(makeNodeId(row, col-1)), weightMatrix[row][col-1]);
		graph.getEdges().add(edge14);
   	
    }
    
    /**
     * 
     * @param row
     * @param col
     * @param weightMatrix
     */
    private void buildEdgesForLastRowBonus2(int row, int col, int[][] weightMatrix) {
		Edge edge1 = new Edge(nodesMap.get(makeNodeId(row, col-1)), nodesMap.get(makeNodeId(row, col)), weightMatrix[row][col]);
		graph.getEdges().add(edge1);
		Edge edge2 = new Edge(nodesMap.get(makeNodeId(row, col)), nodesMap.get(makeNodeId(row, col-1)), weightMatrix[row][col-1]);
		graph.getEdges().add(edge2);
		Edge edge3 = new Edge(nodesMap.get(makeNodeId(row, col-1)), nodesMap.get(makeNodeId(0, col)), weightMatrix[0][col]);
		graph.getEdges().add(edge3);
		Edge edge4 = new Edge(nodesMap.get(makeNodeId(0, col)), nodesMap.get(makeNodeId(row, col-1)), weightMatrix[row][col-1]);
		graph.getEdges().add(edge4);
		Edge edge5 = new Edge(nodesMap.get(makeNodeId(0, col-1)), nodesMap.get(makeNodeId(row, col)), weightMatrix[row][col]);
		graph.getEdges().add(edge5);
		Edge edge6 = new Edge(nodesMap.get(makeNodeId(row, col)), nodesMap.get(makeNodeId(0, col-1)), weightMatrix[0][col-1]);
		graph.getEdges().add(edge6);
		Edge edge7 = new Edge(nodesMap.get(makeNodeId(row-1, col-1)), nodesMap.get(makeNodeId(row, col)), weightMatrix[row][col]);
		graph.getEdges().add(edge7);
		Edge edge8 = new Edge(nodesMap.get(makeNodeId(row, col)), nodesMap.get(makeNodeId(row-1, col-1)), weightMatrix[row-1][col-1]);
		graph.getEdges().add(edge8);
		Edge edge9 = new Edge(nodesMap.get(makeNodeId(row-1, col)), nodesMap.get(makeNodeId(row, col)), weightMatrix[row][col]);
		graph.getEdges().add(edge9);
		Edge edge10 = new Edge(nodesMap.get(makeNodeId(row, col)), nodesMap.get(makeNodeId(row-1, col)), weightMatrix[row-1][col]);
		graph.getEdges().add(edge10);
		Edge edge11 = new Edge(nodesMap.get(makeNodeId(0, col)), nodesMap.get(makeNodeId(row, col)), weightMatrix[row][col]);
		graph.getEdges().add(edge11);
		Edge edge12 = new Edge(nodesMap.get(makeNodeId(row, col)), nodesMap.get(makeNodeId(0, col)), weightMatrix[0][col]);
		graph.getEdges().add(edge12);
		Edge edge13 = new Edge(nodesMap.get(makeNodeId(row, col-1)), nodesMap.get(makeNodeId(row-1, col)), weightMatrix[row-1][col]);
		graph.getEdges().add(edge13);
		Edge edge14 = new Edge(nodesMap.get(makeNodeId(row-1, col)), nodesMap.get(makeNodeId(row, col-1)), weightMatrix[row][col-1]);
		graph.getEdges().add(edge14);
    }
    
    /**
     * 
     * @param row
     * @param col
     * @param weightMatrix
     */
    private void buildEdgesForMiddleRowsFirstColumnBonus2(int row, int col, int[][] weightMatrix) {
		Edge edge1 = new Edge(nodesMap.get(makeNodeId(row, col)), nodesMap.get(makeNodeId(row-1, col)), weightMatrix[row-1][col]);
		graph.getEdges().add(edge1);
		Edge edge2 = new Edge(nodesMap.get(makeNodeId(row-1, col)), nodesMap.get(makeNodeId(row, col)), weightMatrix[row][col]);
		graph.getEdges().add(edge2);  
		Edge edge3 = new Edge(nodesMap.get(makeNodeId(row, col)), nodesMap.get(makeNodeId(row-1, weightMatrix[0].length-1)), weightMatrix[row-1][weightMatrix[0].length-1]);
		graph.getEdges().add(edge3);  
		Edge edge4 = new Edge(nodesMap.get(makeNodeId(row-1,  weightMatrix[0].length-1)), nodesMap.get(makeNodeId(row, col)), weightMatrix[row][col]);
		graph.getEdges().add(edge4);
    }
    
    /**
     * 
     * @param row
     * @param col
     * @param weightMatrix
     */
    private void buildEdgesForLastRowsFirstColumnBonus2(int row, int col, int[][] weightMatrix) {
		Edge edge1 = new Edge(nodesMap.get(makeNodeId(row, col)), nodesMap.get(makeNodeId(0, 0)), weightMatrix[0][0]);
		graph.getEdges().add(edge1);
		Edge edge2 = new Edge(nodesMap.get(makeNodeId(0, 0)), nodesMap.get(makeNodeId(row, col)), weightMatrix[row][col]);
		graph.getEdges().add(edge2); 
		Edge edge3 = new Edge(nodesMap.get(makeNodeId(row-1, col)), nodesMap.get(makeNodeId(row, col)), weightMatrix[row][col]);
		graph.getEdges().add(edge3);
		Edge edge4 = new Edge(nodesMap.get(makeNodeId(row, col)), nodesMap.get(makeNodeId(row-1, col)), weightMatrix[row-1][col]);
		graph.getEdges().add(edge4); 
		Edge edge5 = new Edge(nodesMap.get(makeNodeId(row, col)), nodesMap.get(makeNodeId(0, weightMatrix[0].length-1)), weightMatrix[0][weightMatrix[0].length-1]);
		graph.getEdges().add(edge5);
		Edge edge6 = new Edge(nodesMap.get(makeNodeId(0, weightMatrix[0].length-1)), nodesMap.get(makeNodeId(row, col)), weightMatrix[row][col]);
		graph.getEdges().add(edge6); 
		Edge edge7 = new Edge(nodesMap.get(makeNodeId(row, col)), nodesMap.get(makeNodeId(row-1, weightMatrix[row-1].length-1)), weightMatrix[row-1][weightMatrix[row-1].length-1]);
		graph.getEdges().add(edge7);
		Edge edge8 = new Edge(nodesMap.get(makeNodeId(row-1, weightMatrix[row-1].length-1)), nodesMap.get(makeNodeId(row, col)), weightMatrix[row][col]);
		graph.getEdges().add(edge8);
    }
    
    /**
     * 
     * @param row
     * @param col
     * @param weightMatrix
     */
    private void buildEdgesForFirstRowLastColumnBonus2(int row, int col, int[][] weightMatrix) {
		Edge edge1 = new Edge(nodesMap.get(makeNodeId(row, col)), nodesMap.get(makeNodeId(row, 0)), weightMatrix[row][0]);
		graph.getEdges().add(edge1);
		Edge edge2 = new Edge(nodesMap.get(makeNodeId(row, 0)), nodesMap.get(makeNodeId(row, col)), weightMatrix[row][col]);
		graph.getEdges().add(edge2);
		Edge edge3 = new Edge(nodesMap.get(makeNodeId(row, col-1)), nodesMap.get(makeNodeId(row, col)), weightMatrix[row][col]);
		graph.getEdges().add(edge3);
		Edge edge4 = new Edge(nodesMap.get(makeNodeId(row, col)), nodesMap.get(makeNodeId(row, col-1)), weightMatrix[row][col-1]);
		graph.getEdges().add(edge4);
    }
    
    /**
     * 
     * @param row
     * @param col
     * @param weightMatrix
     */
    private void buildEdgesForMiddleRowLastColumnBonus2(int row, int col, int[][] weightMatrix) {
		Edge edge1 = new Edge(nodesMap.get(makeNodeId(row-1, col)), nodesMap.get(makeNodeId(row, col)), weightMatrix[row][col]);
		graph.getEdges().add(edge1);
		Edge edge2 = new Edge(nodesMap.get(makeNodeId(row, col)), nodesMap.get(makeNodeId(row-1, col)), weightMatrix[row-1][col]);
		graph.getEdges().add(edge2);
		Edge edge3 = new Edge(nodesMap.get(makeNodeId(row, col-1)), nodesMap.get(makeNodeId(row, col)), weightMatrix[row][col]);
		graph.getEdges().add(edge3);
		Edge edge4 = new Edge(nodesMap.get(makeNodeId(row, col)), nodesMap.get(makeNodeId(row, col-1)), weightMatrix[row][col-1]);
		graph.getEdges().add(edge4);
		Edge edge5 = new Edge(nodesMap.get(makeNodeId(row, 0)), nodesMap.get(makeNodeId(row, col)), weightMatrix[row][col]);
		graph.getEdges().add(edge5);
		Edge edge6 = new Edge(nodesMap.get(makeNodeId(row, col)), nodesMap.get(makeNodeId(row, 0)), weightMatrix[row][0]);
		graph.getEdges().add(edge6);
		Edge edge7 = new Edge(nodesMap.get(makeNodeId(row-1, 0)), nodesMap.get(makeNodeId(row, col)), weightMatrix[row][col]);
		graph.getEdges().add(edge7);
		Edge edge8 = new Edge(nodesMap.get(makeNodeId(row, col)), nodesMap.get(makeNodeId(row-1, 0)), weightMatrix[row-1][0]);
		graph.getEdges().add(edge8);
		Edge edge9 = new Edge(nodesMap.get(makeNodeId(row-1, col-1)), nodesMap.get(makeNodeId(row, col)), weightMatrix[row][col]);
		graph.getEdges().add(edge9);
		Edge edge10 = new Edge(nodesMap.get(makeNodeId(row, col)), nodesMap.get(makeNodeId(row-1, col-1)), weightMatrix[row-1][col-1]);
		graph.getEdges().add(edge10);
		Edge edge11 = new Edge(nodesMap.get(makeNodeId(row, col-1)), nodesMap.get(makeNodeId(row-1, weightMatrix[row-1].length-1)), weightMatrix[row-1][weightMatrix[row-1].length-1]);
		graph.getEdges().add(edge11);
		Edge edge12 = new Edge(nodesMap.get(makeNodeId(row-1, weightMatrix[row-1].length-1)), nodesMap.get(makeNodeId(row, col-1)), weightMatrix[row][col-1]);
		graph.getEdges().add(edge12);
    }
    
    /**
     * 
     * @param row
     * @param col
     * @param weightMatrix
     */
    private void buildEdgesForLastRowLastColumnBonus2(int row, int col, int[][] weightMatrix) {
		Edge edge1 = new Edge(nodesMap.get(makeNodeId(row-1, col-1)), nodesMap.get(makeNodeId(row, col)), weightMatrix[row][col]);
		graph.getEdges().add(edge1);
		Edge edge2 = new Edge(nodesMap.get(makeNodeId(row, col)), nodesMap.get(makeNodeId(row-1, col-1)), weightMatrix[row-1][col-1]);
		graph.getEdges().add(edge2);
		Edge edge3 = new Edge(nodesMap.get(makeNodeId(row-1, col)), nodesMap.get(makeNodeId(row, col)), weightMatrix[row][col]);
		graph.getEdges().add(edge3);
		Edge edge4 = new Edge(nodesMap.get(makeNodeId(row, col)), nodesMap.get(makeNodeId(row-1, col)), weightMatrix[row-1][col]);
		graph.getEdges().add(edge4);
		Edge edge5 = new Edge(nodesMap.get(makeNodeId(row, col-1)), nodesMap.get(makeNodeId(row, col)), weightMatrix[row][col]);
		graph.getEdges().add(edge5);
		Edge edge6 = new Edge(nodesMap.get(makeNodeId(row, col)), nodesMap.get(makeNodeId(row, col-1)), weightMatrix[row][col-1]);
		graph.getEdges().add(edge6);
		Edge edge7 = new Edge(nodesMap.get(makeNodeId(row-1, 0)), nodesMap.get(makeNodeId(row, col)), weightMatrix[row][col]);
		graph.getEdges().add(edge7);
		Edge edge8 = new Edge(nodesMap.get(makeNodeId(row, col)), nodesMap.get(makeNodeId(row-1, 0)), weightMatrix[row-1][0]);
		graph.getEdges().add(edge8);
		Edge edge9 = new Edge(nodesMap.get(makeNodeId(row, 0)), nodesMap.get(makeNodeId(row, col)), weightMatrix[row][col]);
		graph.getEdges().add(edge9);
		Edge edge10 = new Edge(nodesMap.get(makeNodeId(row, col)), nodesMap.get(makeNodeId(row, 0)), weightMatrix[row][0]);
		graph.getEdges().add(edge10);
		Edge edge11 = new Edge(nodesMap.get(makeNodeId(0, 0)), nodesMap.get(makeNodeId(row, col)), weightMatrix[row][col]);
		graph.getEdges().add(edge11);
		Edge edge12 = new Edge(nodesMap.get(makeNodeId(row, col)), nodesMap.get(makeNodeId(0, 0)), weightMatrix[0][0]);
		graph.getEdges().add(edge12);
		Edge edge13 = new Edge(nodesMap.get(makeNodeId(0, col)), nodesMap.get(makeNodeId(row, col)), weightMatrix[row][col]);
		graph.getEdges().add(edge13);
		Edge edge14 = new Edge(nodesMap.get(makeNodeId(row, col)), nodesMap.get(makeNodeId(0, col)), weightMatrix[0][col]);
		graph.getEdges().add(edge14);
		Edge edge15 = new Edge(nodesMap.get(makeNodeId(0, col-1)), nodesMap.get(makeNodeId(row, col)), weightMatrix[row][col]);
		graph.getEdges().add(edge15);
		Edge edge16 = new Edge(nodesMap.get(makeNodeId(row, col)), nodesMap.get(makeNodeId(0, col-1)), weightMatrix[0][col-1]);
		graph.getEdges().add(edge16);
		Edge edge17 = new Edge(nodesMap.get(makeNodeId(row, col-1)), nodesMap.get(makeNodeId(row-1, col)), weightMatrix[row-1][col]);
		graph.getEdges().add(edge17);
		Edge edge18 = new Edge(nodesMap.get(makeNodeId(row-1, col)), nodesMap.get(makeNodeId(row, col-1)), weightMatrix[row][col-1]);
		graph.getEdges().add(edge18);
	    Edge edge19 = new Edge(nodesMap.get(makeNodeId(row, col-1)), nodesMap.get(makeNodeId(0, weightMatrix[0].length-1)), weightMatrix[0][weightMatrix[0].length-1]);
	    graph.getEdges().add(edge19);
	    Edge edge20 = new Edge(nodesMap.get(makeNodeId(0, weightMatrix[0].length-1)), nodesMap.get(makeNodeId(row, col-1)), weightMatrix[row][col-1]);
	    graph.getEdges().add(edge20);
    }

}