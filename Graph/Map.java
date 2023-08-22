import java.util.*;

public class Map {
    public int[] neighbors = null;
    public int[] distances = null; 
    public Map( int nNodes, int nodeId ) {
	
	// Prepare temporal storages for neighboring information
	ArrayList<Integer> neighborsList = new ArrayList<Integer>( );
	ArrayList<Integer> distancesList = new ArrayList<Integer>( );
	
	// All nodes have the same random number generator
	Random rand = new Random( 0 );
	// All nodes have the same upper limit
	int upperLimit = ( int )( nNodes * 0.001 );
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
		if ( i == nodeId ) {
		    // check if this is a self-directed link
		    if ( neighbor == nodeId )
			continue;
		    // check if this is a duplication
		    if ( neighborsList.indexOf( new Integer( neighbor ) )
			 == -1 ) {
			// store my neighbor and its distance
			neighborsList.add( new Integer( neighbor ) );
			distancesList.add( new Integer( distance ) );
		    }
		}
		else if ( neighbor == nodeId ) {
		    // check if this is a duplication
		    if ( neighborsList.indexOf( new Integer( i ) )
			 == -1 ) {
			// store my neighbor and its distance
			neighborsList.add( new Integer( i ) );
			distancesList.add( new Integer( distance ) );
		    }
		}
	    }
	}
	neighbors = new int[neighborsList.size( )];
	distances = new int[distancesList.size( )];
	for ( int i = 0; i < neighborsList.size( ); i++ ) {
	    neighbors[i] = neighborsList.get( i );
	    distances[i] = distancesList.get( i );
	}
    }
}
