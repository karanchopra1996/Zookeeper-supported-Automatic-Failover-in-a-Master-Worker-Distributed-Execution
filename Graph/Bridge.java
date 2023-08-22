import java.io.*;
import java.util.*;

public class Bridge {
    public int nBridges = 0; // #bridges
    private int nHops = 0;     // hop count
    private int[] low;        // low[v] = lowest preorder of any vertex connected to v
    private int[] disc;        // pre[v] = order in which dfs examines v


    public Bridge( ArrayList<int[]> V, ArrayList<int[]> E ) {
	low = new int[ V.size( ) ];
	disc = new int[ V.size( ) ];

	for ( int i = 0; i < low.length; i++ )
	    low[i] = -1;
	for ( int i = 0; i < disc.length; i++ )
	    disc[i] = -1;

	for ( int i = 0; i < V.size( ); i++ )
	    if ( disc[i] == -1 ) // not discovered yet
		dfs( V, E, i, i );
	
	//	for ( int i = 0; i < V.size( ); i++ ) 
	//  System.out.println( "low[" + i + "] = " + low[i] + ", disc[" + i + "] = " + disc[i] );
    }

    private void dfs( ArrayList<int[]> V, ArrayList<int[]> E, int curr, int parent ) {
	// System.out.println( "dfs( " + curr + ", " + parent + " ) -----------------------" );
	int children = 0;
	disc[curr] = low[curr] = ++nHops; // hop increment

	// System.out.println( "disc[" + curr + "] = " + disc[curr] + ", low[" + curr + "] = " + low[curr] );
	
	for ( int next : V.get( curr ) ) {
	    if ( next == parent )
		continue; // we don't want to go back to the parent.
	    if ( disc[next] == -1 ) { // next hasn't been discoverred yet
		dfs( V, E, next, curr );
		low[curr] = Math.min( low[next], low[curr] );
		if ( disc[curr] < low[next] ) {
		    System.out.println( curr + " - " + next + " is a bridge" );
		    nBridges++;
		}

	    }
	    else
		low[curr] = Math.min( low[curr], disc[next] );
	}
    }

    public static void main(String[] args) throws Exception {
	ArrayList<int[]> graph_neighbors = new ArrayList<int[]>( );
	ArrayList<int[]> graph_weights = new ArrayList<int[]>( );
	
	FileInputStream fstream = new FileInputStream( args[0] );
	BufferedReader freader = new BufferedReader( new InputStreamReader( fstream ) );
	String line;
	
	while ( ( line = freader.readLine( ) ) != null ) {
	    String[] vertex_edges = line.split( "=" );

	    String[] edges = vertex_edges[1].split( ";" );
	    int[] neighbors = new int[ edges.length ];
	    int[] weights = new int[ edges.length ];
	    
	    for ( int i = 0; i < edges.length; i++ ) {
		String[] neighbor_weight = edges[i].split( "," );
		neighbors[i] = Integer.parseInt( neighbor_weight[0] );
		weights[i] = Integer.parseInt( neighbor_weight[1] );
	    }
	    graph_neighbors.add( neighbors );
	    graph_weights.add( weights );
	}

	/*
	for ( int i = 0; i < graph_neighbors.size( ); i++ ) {
	    System.out.println( "Vertex " + i + ":" );
	    for ( int j = 0; j < graph_neighbors.get(i).length; j++ ) {
		System.out.println( "\t to " + graph_neighbors.get(i)[j] + " with distance " + graph_weights.get(i)[j] );
	    }
	}
	*/
	
        Bridge bridge = new Bridge( graph_neighbors, graph_weights );
	
	System.out.println( args[0] + "'s # brdiges = " + bridge.nBridges );
    }
}
