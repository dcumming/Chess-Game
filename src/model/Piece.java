/**
 * @author Danny Cummings
 */
package model;

import javafx.scene.image.ImageView;

@SuppressWarnings("restriction")
public class Piece {
	
	private boolean white;
	private ImageView image;
	private Coordinate location;
	private int value;   // How important the piece is
	/*
	 * King - 200
	 * Queen - 9
	 * Rook - 5
	 * Bishop - 3
	 * Knight - 2
	 * Pawn - 1
	 */
	
	public Piece(boolean white, ImageView image, Coordinate location, int value) {
		this.white = white;
		this.image = image;
		this.location = location;
		this.value = value;
	}
	
	public boolean isWhite() {
		return white;
	}
	
	public ImageView getImage() {
		return image;
	}
	
	public void setLocation(int x, int y) {
		location = new Coordinate(x, y);
	}
	
	public Coordinate getLocation() {
		return location;
	}
	
	public int getValue() {
		return value;
	}
}
