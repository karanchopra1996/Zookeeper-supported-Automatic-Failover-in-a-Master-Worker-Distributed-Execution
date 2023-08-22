import java.io.*;
import java.util.*;

public class FileRead {
    
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

	for ( int i = 0; i < graph_neighbors.size( ); i++ ) {
	    System.out.println( "Vertex " + i + ":" );
	    for ( int j = 0; j < graph_neighbors.get(i).length; j++ ) {
		System.out.println( "\t to " + graph_neighbors.get(i)[j] + " with distance " + graph_weights.get(i)[j] );
	    }
	}
    }
}
