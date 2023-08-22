import java.util.*;
import java.io.PrintWriter;
import java.io.IOException;

public class GraphGen {
    public static void main( String[] args ) {
	if ( args.length > 3 ) {
	    System.out.println( "usage: java GraphGen #nodes filename [y/n]" );
	    System.exit( -1 );
	}
	GraphGen g = new GraphGen( args );
    }

    public GraphGen( String[] args ) {
	int nNodes = Integer.parseInt( args[0] );

	try {
	    PrintWriter file = new PrintWriter( args[1] );

	    System.out.println( "nNodes = " + nNodes +"\n" );
	    Map[] node = new Map[nNodes];
	    for ( int i = 0; i < nNodes; i++ ) {
		if ( args.length == 3 ) System.out.println( "Node " + i + ":" );
		node[i] = new Map( nNodes, i );

		if ( file != null )
		    file.print( i + "=" ); 
		for ( int j = 0; j < node[i].neighbors.length; j++ ) {
		    if ( args.length == 3 ) System.out.println( "to " + node[i].neighbors[j]
							       + " with distance " 
							       + node[i].distances[j] );
		    if ( file != null ) {
			if ( j != 0 )
			    file.print( ";" );
			file.print( node[i].neighbors[j] + "," + node[i].distances[j] );
		    }
		}
		if ( args.length == 3 ) System.out.println( );
		if ( file != null )
		    file.println( );
	    }
	    
	    if ( file != null ) {
		file.close( );
		System.out.println( "file: " + args[1] + " was created" );
	    }
	} catch( IOException e ) {
	    e.printStackTrace( );
	}
    }
}
