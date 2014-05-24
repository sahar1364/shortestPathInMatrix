package com.solium.main;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import com.solium.cylindricalMatrix.Graph;
import com.solium.cylindricalMatrix.parser.CylindricalMatrixInputParser;
import com.solium.cylindricalMatrix.parser.CylindricalMatrixInputParserException;

/**
 * Class with main method for getting inputs for constructing a matrix.
 * This program finds out the minimum weight path from the source cell to the destination cell in a given matrix 
 */
public class CylindricalMatrix {

	public static void main(String[] args) {
        if (args.length == 0 || args.length > 3) {
            System.err.println();
            System.err.println("CylindricalMatrix requires one argument, the name of the input file.");
            System.err.println();
            System.err.println("Example: java CylindricalMatrix/input/input.txt");
            System.err.println();
            System.exit(1);
        }
        try {
        	File input = new File(args[0]);
        	
        	//construct the graph from the input file matrix
        	Graph graph = new CylindricalMatrixInputParser().buildGraph(new FileReader(input));
        	
        	//Print the list of the shortest path
    		System.out.println(graph.lexicographicallySmallestForDisplay());
    		
    		//Print the minimum weight
        	System.out.println(graph.getMinimumWeight());
        }
        catch (IOException e) {
        	System.out.print(e.getMessage());
        }
        catch (CylindricalMatrixInputParserException e) {
        	System.out.print(e.getMessage());
        }
	}
}
