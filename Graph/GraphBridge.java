/**
 * This is a graph bridge program to be used for testing CSS533's homework assignment. You need to launch this program
 * from Worker.java through ZooKeeper.
 *  java -Xss512M GraphBridge 100
 *  100's # brdiges = 90, time elapsed = 1 msec
 *  java -Xss512M GraphBridge 1000
 *  1000's # brdiges = 2, time elapsed = 6 msec
 *  java -Xss512M GraphBridge 10000
 *  10000's # brdiges = 14, time elapsed = 29 msec
 *  java -Xss512M GraphBridge 100000
 *  100000's # brdiges = 200, time elapsed = 302 msec
 *  java -Xss512M GraphBridge 1000000
 *  1000000's # brdiges = 1980, time elapsed = 3799 msec
 *  java -Xss512M GraphBridge 2000000
 *  2000000's # brdiges = 3973, time elapsed = 8736 msec
 *  java -Xss512M GraphBridge 3000000
 *  3000000's # brdiges = 6096, time elapsed = 13874 msec
 *  java -Xss512M GraphBridge 4000000
 *  4000000's # brdiges = 8063, time elapsed = 22957 msec
 *  java -Xss512M GraphBridge 5000000
 *  5000000's # brdiges = 10108, time elapsed = 32005 msec
 */

import java.io.*;
import java.util.*;

public class GraphBridge {
    public int nBridges = 0;                // #bridges
    private int nHops = 0;                  // hop count
    private int[] low;                      // low[v] = lowest preorder of any vertex connected to v
    private int[] disc;                     // disc[v] = order in which dfs examines v

    public static boolean enbPrint = false; // print messages if it's true

    /**
     * Is the constructor that initializes low[0] - low[nVertices] and disc[0] - disc[nVertices] and thereafter
     * finds out all graph bridges
     *
     * @param V an array of Integer lists, each maintaining all the neighbors of a different vertex
     * @param E an array of Integer lists, each maintinaing all edge weights emanating from a different vertex
     */
    public GraphBridge( ArrayList<Integer>[] V, ArrayList<Integer>[] E ) {
	low = new int[ V.length ];         // low[v]: lowest preorder of any vertex connected to v
	disc = new int[ V.length ];        // disc[v]: order in which dfs examines

	// initialization
	for ( int i = 0; i < low.length; i++ )
	    low[i] = -1;
	for ( int i = 0; i < disc.length; i++ )
	    disc[i] = -1;

	// conduct depth-first traverses
	for ( int i = 0; i < V.length; i++ )
	    if ( disc[i] == -1 ) // not discovered yet
		dfs( V, E, i, i );

	// print out low[] and disc[] of each verte.
	if ( enbPrint ) {
	    for ( int i = 0; i < V.length; i++ ) 
		System.out.println( "low[" + i + "] = " + low[i] + ", disc[" + i + "] = " + disc[i] );
	}
    }

     /**
     * Traverse G( V, E ) in a depth-first manner. Upon encountering a circle, the control tracks back to the origin.
     * 
     * @param V       an array of Integer lists, each maintaining all the neighbors of a different vertex
     * @param E      an array of Integer lists, each maintinaing all edge weights emanating from a different vertex
     * @param curr   the current vertex to start a dfs
     * @param parent the parent of the current vertex
     */   
    private void dfs( ArrayList<Integer>[] V, ArrayList<Integer>[] E, int curr, int parent ) {
	if ( enbPrint ) System.out.println( "dfs( " + curr + ", " + parent + " ) -----------------------" );
	disc[curr] = low[curr] = ++nHops; // hop increment

	if ( enbPrint ) System.out.println( "disc[" + curr + "] = " + disc[curr] + ", low[" + curr + "] = " + low[curr] );

	ArrayList<Integer> vertices = V[curr]; // all neighbors from the current vertex.
	for ( int i = 0; i < vertices.size( ); i++ ) {
	    int next = vertices.get( i ); // get a next neighbor.
	    if ( next == parent )
		continue; // we don't want to go back to the parent.
	    if ( disc[next] == -1 ) {     // next hasn't been discoverred yet
		dfs( V, E, next, curr );  // dig into this neighbor.
		low[curr] = Math.min( low[next], low[curr] ); // upon a back-track, if low[next] is smaller, I'm on a circle.
		if ( disc[curr] < low[next] ) { // if i'm on a circle, disc[curr] >= low[next]
		    if ( enbPrint ) System.out.println( curr + " - " + next + " is a bridge" );
		    nBridges++;
		}
	    }
	    else
		low[curr] = Math.min( low[curr], disc[next] ); // find a circle. in most cases, disc[next] will be smaller.
	}
    }

    /**
     * Generates a random graph with arg[0] vertices and computes the number of bridges of this graph.
     *
     * @param args arg[0] is the number of vertices. arg[1] is optional to allow messages to be printed.
     */
    public static void main( String[] args ) {
	long startTime = System.currentTimeMillis( ); // start a timer

	int nNodes = Integer.parseInt( args[0] );       // # vertices
	enbPrint = ( args.length > 1 ) ? true : false;  // allow debugging messages to be printed
		    
	if ( enbPrint ) System.out.println( "nNodes = " + nNodes +"\n" );
	// create an array of Integer lists, each manitaining all the neighbors of a different vertex
	// create an array of Integer lists, each manitaining all the edge weights from a different vertex
	ArrayList<Integer>[] graph_neighbors = (ArrayList<Integer>[]) new ArrayList[nNodes];
	ArrayList<Integer>[] graph_distances = (ArrayList<Integer>[]) new ArrayList[nNodes];

	for ( int i = 0; i < nNodes; i++ ) {
	    graph_neighbors[i] = new ArrayList<Integer>( );
	    graph_distances[i] = new ArrayList<Integer>( );
	}

	// All nodes have the same upper limit
	// if nNodes is small below 1K, #neighbors will be 10%: 1-10
	// otherwise, #neighbors is always 10
	Random rand = new Random( 0 );
	int upperLimit = ( nNodes < 1000 ) ? ( int )( nNodes * 0.01 ) : 10;
	upperLimit = ( upperLimit == 0 ) ? 1 : upperLimit;

	for ( int i = 0; i < nNodes; i++ ) {
	    // Generate # neighbors from node i
	    int nNeighbors = rand.nextInt( upperLimit );
	    if ( nNeighbors == 0 ) nNeighbors = 1;

	    // Generate neighboring nodes from node i
	    for ( int j = 0; j < nNeighbors; j++ ) {
		int neighbor = rand.nextInt( nNodes );
		int distance = rand.nextInt( nNodes );
		if ( distance == 0 ) distance = 1;

		// check if this is a self-directed link
		if ( neighbor == i )
		    continue;
		// check if this is a duplication
		if ( graph_neighbors[i].indexOf( new Integer( neighbor ) ) == -1 ) {
		    // store my neighbor and its distance
		    graph_neighbors[i].add( new Integer( neighbor ) );
		    graph_distances[i].add( new Integer( distance ) );

		    if ( graph_neighbors[neighbor].indexOf( new Integer( i ) ) == -1 ) {
			// make sure that a dual edge is created from my neighbor back to me.
			graph_neighbors[neighbor].add( new Integer( i ) );
			graph_distances[neighbor].add( new Integer( distance ) );
		    }
		}
	    }
	}

	// print out all the neighbors and edge weights from each vertex
	if ( enbPrint ) {
	    for ( int i = 0; i < graph_neighbors.length; i++ ) {
		System.out.println( "Vertex " + i + ":" );
		for ( int j = 0; j < graph_neighbors[i].size( ); j++ ) {
		    System.out.println( "\t to " + graph_neighbors[i].get(j) + " with distance " + graph_distances[i].get(j) );
		}
	    }
	}

	// compute # bridges of a given graph.
        GraphBridge bridge = new GraphBridge( graph_neighbors, graph_distances );

	long endTime = System.currentTimeMillis( ); // finish the timer
	System.out.println( args[0] + "'s # brdiges = " + bridge.nBridges + ", time elapsed = " + ( endTime - startTime ) + " msec" );
    }
}
