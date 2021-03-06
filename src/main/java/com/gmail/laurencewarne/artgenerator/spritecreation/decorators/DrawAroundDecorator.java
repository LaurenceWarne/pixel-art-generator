package com.gmail.laurencewarne.artgenerator.spritecreation.decorators;

import java.util.List;
import java.util.Set;
import java.util.HashSet;
import java.util.Arrays;

import com.gmail.laurencewarne.artgenerator.spritecreation.ISpriteGenerator;
import com.gmail.laurencewarne.artgenerator.cellgrid.CellCoordinate;
import com.gmail.laurencewarne.artgenerator.cellgrid.ICellGrid;
import com.gmail.laurencewarne.artgenerator.cellgrid.ArrayListCellGrid;

/**
<pre>
This decorator implementation "draws around" the ICellGrid(visualised as a table)
representing a sprite . The "filled"(true) values are set to false and their
"touching" cells are set to true.

The output grid is generated based on the decorated grid as follows:

If a value in the decorated grid is true, then it is set to false in the new grid.
If the value is false, it's adjacent values(excluding diagonals) are searched, and 
if at least one is true, then the false value is set to true in the new grid.
Values which are always "filled"(true) and always "empty"(false) can be specified.
These values are changed retrospecively after the previous step has been completed
for all elements in the grid/array.
</pre>
 */
public class DrawAroundDecorator extends SpriteTransformer<Boolean, Boolean> implements ISpriteGenerator<Boolean> {

    protected final Set<CellCoordinate> alwaysFullCoords, alwaysEmptyCoords;

    /**
       Constructs a draw around decorator by decorating the given spriteGenerator.
       Coordinates of cells set to be always filled and always empty can also be
       specified.

       @param spriteGenerator the ISpriteGenerator to be decorated
       @param alwaysFullCoords a set of coordinates to be always set to true in the
       output grid
       @param alwaysEmptyCoords a set of coordinates to be always set to false in the
       output grid
     */
    public DrawAroundDecorator( final ISpriteGenerator<Boolean> spriteGenerator, final Set<CellCoordinate> alwaysFullCoords, final Set<CellCoordinate> alwaysEmptyCoords ) {

	super(spriteGenerator);
	this.alwaysFullCoords = alwaysFullCoords;
	this.alwaysEmptyCoords = alwaysEmptyCoords;
    }

    /**
       Constructs a draw around decorator by decorating the given 
       spriteGenerator.

       @param spriteGenerator the ISpriteGenerator to be decorated
     */
    public DrawAroundDecorator( final ISpriteGenerator spriteGenerator ) {

	this(spriteGenerator, new HashSet<CellCoordinate>(), new HashSet<CellCoordinate>());
    }

    /**
       Add the coordinate of a cell which will always be set to true on the output
       array.

       @param coord the coordinate of the cell to be set to always true.
       @throws IllegalArgumentException if the input coordinate is deemed to not lie
       in the output array or already a coordinate set to be always empty.
     */
    public void addAlwaysFullCoord( final CellCoordinate coord )
	throws IllegalArgumentException {

	if ( !isValidCoord(coord) ){
	    throw new IllegalArgumentException("Coordinate does not lie in the grid!");
	}
	else if ( alwaysEmptyCoords.contains(coord) ) {
	    throw new IllegalArgumentException("Coordinate is already set to be always set to always empty!");
	}
	else {
	    alwaysFullCoords.add(coord);
	}
    }

    /**
       Add the coordinate of a cell which will always be set to false on the output
       array.

       @param coord the coordinate of the cell to be set to always false.
       @throws IllegalArgumentException if the input coordinate is deemed to not lie
       in the output array all already a coordinate set to be always full.
     */
    public void addAlwaysEmptyCoord( final CellCoordinate coord )
	throws IllegalArgumentException {

	if ( !isValidCoord(coord) ){
	    throw new IllegalArgumentException("Coordinate does not lie in the grid!");
	}
	else if ( alwaysFullCoords.contains(coord) ) {
	    throw new IllegalArgumentException("Coordinate is already set to be always set to always full!");
	}
	else {
	    alwaysEmptyCoords.add(coord);
	}
    }    

    
    /**
       Remove the coordinate of a cell which will always be set to true on the output
       array.

       @param coord the coordinate of the cell which is no longer to be set to always
       true.
       @throws IllegalArgumentException if the input coordinate is deemed to not lie
       in the output array
     */
    public void removeAlwaysFullCoord( final CellCoordinate coord )
	throws IllegalArgumentException {

	if ( !isValidCoord(coord) ){
	    throw new IllegalArgumentException("Coordinate does not lie in the grid!");
	}
	else {
	    alwaysFullCoords.remove(coord);
	}
    }
    
    /**
       Remove the coordinate of a cell which will always be set to false on the output
       array.

       @param coord the coordinate of the cell which is no longer to be set to always
       false.
       @throws IllegalArgumentException if the input coordinate is deemed to not lie
       in the output array
     */
    public void removeAlwaysEmptyCoord( final CellCoordinate coord )
	throws IllegalArgumentException {

	if ( !isValidCoord(coord) ){
	    throw new IllegalArgumentException("Coordinate does not lie in the grid!");
	}
	else {
	    alwaysEmptyCoords.remove(coord);
	}
    }

    @Override
    public ICellGrid<Boolean> genSpriteAsCellGrid() {

	ICellGrid<Boolean> wrappedGrid = super.genSpriteAsCellGridFromDecoratee();
	int xLength = getXLength(), yLength = getYLength();
	ICellGrid<Boolean> decGrid = new ArrayListCellGrid<>(xLength, yLength, false);
	for ( int i = 0; i < yLength; i++ ){
	    for ( int j = 0; j < xLength; j++ ){
		CellCoordinate coord = new CellCoordinate(j, i);
		decGrid.setValueAt(coord, false);
		boolean state = wrappedGrid.getValueAt(coord);
		// Only previously empty cells can be true
		if ( state == false ){
		    List<CellCoordinate> neighbours = wrappedGrid.
			getCoordsOfMooreNeighbours(coord);
		    for ( CellCoordinate adjCoord : neighbours ){
			if ( wrappedGrid.getValueAt(adjCoord) == true ){
			    decGrid.setValueAt(coord, true); break;
			}
		    }
		}
	    }
	}
	for ( CellCoordinate coord : alwaysFullCoords ){
	    decGrid.setValueAt(coord, true);
	}
	for ( CellCoordinate coord : alwaysEmptyCoords ){
	    decGrid.setValueAt(coord, false);
	}
	return decGrid;
    }
    
}
