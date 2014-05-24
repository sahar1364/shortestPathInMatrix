**I tried to follow solium standards for solving the assignment (similar fileNames and similar structure)**

This is a solution to the CylindricalMatrix problem from the solium programming problems document. 
The input file for the system is located in the input directory and based on the input format mentioned in 
the document.

To build and view the outputs for the provided inputs:

>ant compile create.jar 
>java -jar <jar file> input/input.txt

The solution has been tested and built under OS X 10.9.2 using java version 1.6.0_65

Given a matrix of integers, the program computes the minimum weight path from source to destination.
There are three versions:

+Standard: A path starts anywhere in column 0 and ends anywhere in the last column (It considers all different
			combinations of sources in first column and destinations in last column and returns the minimum)
			***NOTE: Standard is slower than bonus1 and bonus2 because the source and destination are unknown***
			
+Bonus1:Rather than going from west column to the east column, we can go from any arbitrary source coordinate
	to any arbitrary destination coordinate, still via the shortest path.  
	Path may proceed in any of the eight cardinal directions (N, E, W, S, NE, SE, SW, NW) 
	 
+Bonus2:Considers the extreme west and east columns as adjacent as well


The code builds a directional graph to represent the input matrix, and corresponding weights in each cell 
are graph's edges weights and then uses Dijkstra's algorithm to find the minimal weight to get from source
to destination

Assumptions: The matrix will have more than two rows and two columns (to avoid additional checking - it does not blow 
up if we have less than three rows and columns but it might include some duplicate edges, which do not effect the 
result anyways)

**Future work: We can store adjacent nodes in each node upon construction 
so that we improve the performance by not having to iterate through all edges
and find a node's neighbors in Dijkstra algorithm
having to iterate through all edges and find the neighbors 

