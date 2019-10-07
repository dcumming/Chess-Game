/**
 * @author Danny Cummings
 * This class is an extension of a GridPane and acts as the main
 * back-end component of the game, storing and processing
 * relevant information to the board
 */
package model;

/* GUI */
import javafx.scene.layout.GridPane;
import javafx.scene.image.ImageView;
import javafx.scene.control.Label;

/* Data Structures */
import java.util.ArrayList;
import java.util.HashMap;
import javafx.util.Pair; 

/* Utility Classes */
import utils.ChessUtils;

/* Back end class used to store additional information about the board */
@SuppressWarnings("restriction")
public class Chessboard extends GridPane { // Adds to original GridPane GUI
	
	private Piece[][] pieceList = new Piece[8][8]; // Position of each piece on board
	private Label[][] labelList = new Label[8][8]; // Position of each label on board

	/* Static so that all new instances of boards have same information */
	private static HashMap<String, String> moveMap = new HashMap<String, String>(); // Maps board to best move
	private static HashMap<String, String> scoreMap = new HashMap<String, String>(); // Maps board to score

	
	/**
	 * Default constructor initializes board to empty
	 */
	public Chessboard(){
		/* Initializes pieceList and labelList */
		for(int row = 0; row < 8; row++) {
			for(int col = 0; col < 8; col++) {
				pieceList[row][col] = null;
				labelList[row][col] = null;
			}
		}
		/* Loads the AI data to enhance performance */
		//ChessUtils.load("movedata.txt", moveMap);
		//ChessUtils.load("scoredata.txt", scoreMap);
	}
	
	/**
	 * Copy constructor makes a DEEP copy of the provided board
	 * @param grid
	 */
	public Chessboard(Chessboard grid) {
		for (int row = 0; row < 8; row++) {
			for (int col = 0; col < 8; col++) {
				Piece piece = grid.pieceAt(row, col);
				Label label = grid.labelAt(row, col);
				if (piece != null) {
					pieceList[row][col] = new Piece(piece.isWhite(), 
							new ImageView(piece.getImage().getImage()), 
							new Coordinate(piece.getLocation().getX(), piece.getLocation().getY()), 
							piece.getValue());
				}
				else {
					pieceList[row][col] = null;
				}

				labelList[row][col] = label;
			}
		}
	}
	
	/**
	 * Adds a piece to the board
	 * @param white
	 * @param x
	 * @param y
	 * @param image
	 * @param value
	 */
	public void addPiece(boolean white, int x, int y, ImageView image, int value) {
		Coordinate imC = new Coordinate(x, y);
		Piece piece = new Piece(white, image, imC, value);
		pieceList[x][y] = piece;
	}
	
	/**
	 * Adds a label to the board
	 * @param x
	 * @param y
	 * @param label
	 */
	public void addLabel(int x, int y, Label label) {
		labelList[x][y] = label;
	}
	
	/** 
	 * Moves a piece from (fromX, fromY) to (toX, toY) on the board
	 * @param fromX
	 * @param fromY
	 * @param toX
	 * @param toY
	 * @return first element of array is true if opponent piece captured, false otherwise
	 * second element true only when a pawn has moved to the end of the board, false otherwise
	 */
	public boolean[] movePiece(int fromX, int fromY, int toX, int toY) {
		Piece copy = pieceList[fromX][fromY];
		Piece opponent = pieceList[toX][toY];
		pieceList[fromX][fromY] = null;
		pieceList[toX][toY] = copy;
		copy.setLocation(toX, toY);
		
		boolean capture = false, 
				// white or black pawn reached end of board if true
				pawnAtEnd = (copy.getValue() == 1 && toY == 0) || (copy.getValue() == -1 && toY == 7);
		
		if (opponent != null) { // opponent piece captured
			opponent.getImage().setVisible(false);
			opponent.getImage().setDisable(true);
			capture = true;
		}
		
		boolean[] results = {capture, pawnAtEnd};
		
		return results;
	}
	
	/**
	 * 
	 * @param img - unique ImageView ID for the desired piece
	 * @return piece desired
	 */
	public Piece getPiece(ImageView img) {
		for (int x = 0; x < 8; x++) {
			for (int y = 0; y < 8; y++) {
				if (pieceList[x][y] != null && img.equals(pieceList[x][y].getImage())) {
					return pieceList[x][y];
				}
			}
		}
		return null;
	}
	
	/**
	 * 
	 * @param lab - desired label
	 * @return coordinates of desired label
	 */
	public Coordinate getLabel(Label lab) {
		for (int x = 0; x < 8; x++) {
			for (int y = 0; y < 8; y++) {
				if (lab.equals(labelList[x][y])) {
					return new Coordinate(x, y);
				}
			}
		}
		return null;
	}
	
	/**
	 * 
	 * @param x
	 * @param y
	 * @return the piece at the given coordinates
	 * @throws ArrayIndexOutOfBoundsException
	 */
	public Piece pieceAt(int x, int y) throws ArrayIndexOutOfBoundsException {
		return pieceList[x][y];
	}

	/**
	*
	* @param x
	* @param y
	* @return the label at the given coordinates
	*/
	public Label labelAt(int x, int y) {
		return labelList[x][y];
	}
	
	/**
	 * @param white - true if looking for white pieces, false if looking for 
	 * black pieces
	 * @return a list of pieces of the designated color
	 */
	public ArrayList<Piece> getColorPieces(boolean white) {
		ArrayList<Piece> list = new ArrayList<Piece>();
		for (int x = 0; x < 8; x++) {
			for (int y = 0; y < 8; y++) {
				if (pieceList[x][y] != null && pieceList[x][y].isWhite() == white) {
					list.add(pieceList[x][y]);
				}
			}
		}
		return list;
	}
	
	/**
	 * Finds the king piece on the board
	 * @param white - true if looking for white king, false if looking for 
	 * black king
	 * @return the king piece
	 */
	private Piece getKing(boolean white) {
		int target = (white) ? 200 : -200;
		for (int x = 0; x < 8; x++) {
			for (int y = 0; y < 8; y++) {
				if (pieceList[x][y] != null && pieceList[x][y].getValue() == target) {
					return pieceList[x][y];
				}
			}
		}
		return null;
	}

	/**
	 * Searches the grid to see if the king is vulnerable
	 * @param white - true if checking safety of white king, false if checking safety of black king
	 * @return true if the state of the board is in check, false otherwise
	 */
	public boolean isCheck(boolean white) {
		Piece king = getKing(white);
		for (Piece piece : getColorPieces(!white)) {
			for (Coordinate move : getMoves(piece)) {
				if (king.getLocation().equals(move)) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Checks if the player has run out of moves and the game is over
	 * @param white - true if checking whether white side has moves, false otherwise
	 * @return true if player is out of moves, false otherwise
	 */
	public boolean outOfMoves(boolean white) {
		for (Piece piece : getColorPieces(white)) {
			if (reduceAndGetMoves(piece).size() != 0) { return false; }
		}
		return true;
	}	

	/**
	 * Searches for all possible moves for each piece in the game
	 * @param p - selected piece
	 * @return a list of coordinates of boxes that the given piece p can move to on
	 * the given board grid
	 */
	public ArrayList<Coordinate> getMoves(Piece p) {
		ArrayList<Coordinate> list = new ArrayList<Coordinate>();
		Coordinate imC = p.getLocation();
		Piece piece;
		int value = Math.abs(p.getValue()), start, y1, y2;
		boolean white = p.isWhite();
		
		switch(value) { // Checks which piece was chosen
		case 1:  // Pawn
			start = white ? 6 : 1; // Starting row is 6 for white pawns and 1 for black pawns
			y1 = white ? imC.getY() - 1 : imC.getY() + 1; // Direction pawns move based
			y2 = white ? imC.getY() - 2 : imC.getY() + 2; // on its color
			
			try { // Forward movement
				if (pieceAt(imC.getX(), y1) == null) { // No piece in front
					list.add(new Coordinate(imC.getX(), y1));
					if (imC.getY() == start && pieceAt(imC.getX(), y2) == null) { // No pieces two places in front and pawn in starting position 
						list.add(new Coordinate(imC.getX(), y2));
					}
				}
			} catch (ArrayIndexOutOfBoundsException e) {} // Prevents movement off board
			
			try { // Diagonal movement left
				piece = pieceAt(imC.getX() - 1, y1);
				if (piece != null && piece.isWhite() != white) { // Opponent in range
					list.add(new Coordinate(imC.getX() - 1, y1));
				}
			} catch (ArrayIndexOutOfBoundsException e) {}
			
			try { // diagonal movement right
				piece = pieceAt(imC.getX() + 1, y1);
				if (piece != null && piece.isWhite() != white) { // opponent in range
					list.add(new Coordinate(imC.getX() + 1, y1));
				}
			} catch (ArrayIndexOutOfBoundsException e) {}
			
			break;
		case 2:  // Knight
			/* 1 O'Clock */
			moveOne(imC.getX() + 1, imC.getY() - 2, white, list);
			
			/* 2 O'Clock */
			moveOne(imC.getX() + 2, imC.getY() - 1, white, list);
			
			/* 4 O'Clock */
			moveOne(imC.getX() + 2, imC.getY() + 1, white, list);
			
			/* 5 O'Clock */
			moveOne(imC.getX() + 1, imC.getY() + 2, white, list);
			
			/* 7 O'Clock */
			moveOne(imC.getX() - 1, imC.getY() + 2, white, list);
			
			/* 8 O'Clock */
			moveOne(imC.getX() - 2, imC.getY() + 1, white, list);
			
			/* 10 O'Clock */
			moveOne(imC.getX() - 2, imC.getY() - 1, white, list);
			
			/* 11 O'Clock */
			moveOne(imC.getX() - 1, imC.getY() - 2, white, list);
			
			break;
		case 3:  // Bishop
			diagonalMovement(imC, white, list);
			
			break;
		case 5:  // Rook
			horizontalAndVertical(imC, white, list);
			
			break;
		case 9:  // Queen
			horizontalAndVertical(imC, white, list);
			diagonalMovement(imC, white, list);
			
			break;
		case 200:  // King
			for (int x = imC.getX() - 1; x <= imC.getX() + 1; x++) {  // Checks all 8 squares around king
				for (int y = imC.getY() - 1; y <= imC.getY() + 1; y++) {
					if (x == imC.getX() && y == imC.getY()) { continue; } // Avoids checking current position
					moveOne(x, y, white, list);
				}
			}
			
			break;
		}
		
		return list;
	}
	
	/**
	 * Checks to see if a position on the board is available or if the attacking piece
	 * can capture an opposing piece in that position
	 * @param x - x coordinate to move to
	 * @param y - y coordinate to move to
	 * @param white - true if the moving piece is white, false if black
	 * @param list
	 */
	private void moveOne(int x, int y, boolean white, ArrayList<Coordinate> list) {
		try {
			Piece piece = pieceAt(x, y);
			if (piece == null) { // Space is available if no one occupies
				list.add(new Coordinate(x, y));
			}
			else if (piece.isWhite() != white) { // Space is available if occupied by opposite color
				list.add(new Coordinate(x, y));
			}
		} catch (ArrayIndexOutOfBoundsException e) {}
	}
	
	/**
	 * Same as moveOne() except indicates to the caller that you cannot keep going
	 * @param x - x coordinate to move to
	 * @param y - y coordinate to move to
	 * @param white - true if attacking piece is white, false if black
	 * @param list
	 * @return true if a piece is in the way whether black or white, false if box is
	 * empty
	 */
	private boolean moveTwo(int x, int y, boolean white, ArrayList<Coordinate> list) {
		Piece piece = pieceAt(x, y);
		if (piece == null) {
			list.add(new Coordinate(x, y));
		} else { 
			if (piece.isWhite() != white) {
				list.add(new Coordinate(x, y));
			}
			return true;
		}
		return false;
	}
	
	/**
	 * Tracks all the possible boxes the piece can move into before hitting another
	 * piece in both the vertical and horizontal directions
	 * @param imC - coordinates of attacking piece
	 * @param white - true if attacking piece is white, false if black
	 * @param list
	 */
	private void horizontalAndVertical(Coordinate imC, boolean white, ArrayList<Coordinate> list) {
		/* Horizontal movement right */
		for (int x = imC.getX() + 1; x < 8; x++) {
			if (moveTwo(x, imC.getY(), white, list)) { break; } // Stop checking if we run into a piece
		}
		
		/* Horizontal movement left */
		for (int x = imC.getX() - 1; x >= 0; x--) {
			if (moveTwo(x, imC.getY(), white, list)) { break; }
		}
		
		/* Vertical movement up */
		for (int y = imC.getY() - 1; y >= 0; y--) {
			if (moveTwo(imC.getX(), y, white, list)) { break; }
		}
		
		/* Vertical movement down */
		for (int y = imC.getY() + 1; y < 8; y++) {
			if (moveTwo(imC.getX(), y, white, list)) { break; }
		}
	}

	/**
	 * Tracks all the possible boxes the piece can move into before hitting another
	 * piece all diagonal directions
	 * @param imC - coordinates of the attacking piece
	 * @param white - true if attacking piece is white, false if black
	 * @param list
	 */
	private void diagonalMovement(Coordinate imC, boolean white, ArrayList<Coordinate> list) {
		/* Diagonal movement bottom right */
		for (int x = imC.getX() + 1, y = imC.getY() + 1; x < 8 && y < 8; x++, y++) {
			if (moveTwo(x, y, white, list)) { break; }
		}
		
		/* Diagonal movement top right */
		for (int x = imC.getX() + 1, y = imC.getY() - 1; x < 8 && y >= 0; x++, y--) {
			if (moveTwo(x, y, white, list)) { break; }
		}
		
		/* Diagonal movement bottom left */
		for (int x = imC.getX() - 1, y = imC.getY() + 1; x >= 0 && y < 8; x--, y++) {
			if (moveTwo(x, y, white, list)) { break; }
		}
		
		/* Diagonal movement top left */
		for (int x = imC.getX() - 1, y = imC.getY() - 1; x >= 0 && y >= 0; x--, y--) {
			if (moveTwo(x, y, white, list)) { break; }
		}
	}

	/**
	 * Limits the highlighting of some tiles to prevent the loss of a king
	 * @param moves - list of valid moves for the selected piece
	 * @param location - coordinate of the selected piece
	 * @param white - team color
	 * @return list of coordinates the selected piece is allowed to move to
	 */
	private ArrayList<Coordinate> reduceMoves(ArrayList<Coordinate> moves, Coordinate location, boolean white) {
		ArrayList<Coordinate> list = new ArrayList<Coordinate>();
		for (Coordinate move : moves) {
			Chessboard copy = new Chessboard(this); // DEEP copy of the board
			copy.movePiece(location.getX(), location.getY(), move.getX(), move.getY());
			if (!copy.isCheck(white)) { list.add(move); } // Only add the move if not in check after making the move
		}
		return list;
	}

	/**
	* Calls reduceMoves after getMoves
	* @param piece - selected piece
	* @return list of coordinates the selected piece is allowed to move to
	*/
	public ArrayList<Coordinate> reduceAndGetMoves(Piece piece) {
		return reduceMoves(getMoves(piece), piece.getLocation(), piece.isWhite());
	}

	/**
	 * Performs AI algorithm to make computer think ahead and score all possible
	 * moves up to depth
	 * @param depth
	 * @return tuple of (bestImage, [bestPiece, bestLabel])
	 */
	public Pair<ImageView, ArrayList<Coordinate>> dfs(int depth) {
		ImageView bestImage = null;
		Coordinate bestPiece = null, bestLabel = null;
		Integer min = Integer.MAX_VALUE, curr;  // Impossible value
		for (Piece piece : getColorPieces(false)) { // Get black pieces
			Coordinate imC = piece.getLocation(); // Coordinates of each black piece
			for (Coordinate move : reduceAndGetMoves(piece)) {	// Look at all moves for each black piece
				Chessboard copy = new Chessboard(this); // DEEP copy of the board
				copy.movePiece(imC.getX(), imC.getY(), move.getX(), move.getY()); // Updates the copy according to each move
				curr = alphabeta(copy, depth - 1, true, min); // Searches deeper with new copy, smaller depth, switches color to white
				if (curr < min || curr == Integer.MAX_VALUE) { // If the score found from searching deeper is better than the best score for the computer then update the best score
					min = curr;
					bestPiece = imC;
					bestLabel = move;
					bestImage = piece.getImage();
				} 
			}
		}
		
		//ChessUtils.writeToFile("movedata.txt", grid.toString()); // Key
		//ChessUtils.writeToFile("movedata.txt", bestPiece.toString() + " " + bestLabel.toString()); // Value
		
		ArrayList<Coordinate> cos = new ArrayList<Coordinate>();
		cos.add(bestPiece);
		cos.add(bestLabel);
		return new Pair<ImageView, ArrayList<Coordinate>>(bestImage, cos);
	}
	
	/**
	 * Call when trying to make the AI play itself
	 * @param depth
	 * @param white - initial color making the move
	 * @return tuple of (bestImage, [bestPiece, bestLabel])
	 */
	public Pair<ImageView, ArrayList<Coordinate>> dfs(int depth, boolean white) {
		ImageView bestImage = null;
		Coordinate bestPiece = null, bestLabel = null;
		Integer minmax = (white) ? Integer.MIN_VALUE : Integer.MAX_VALUE, curr;  // Impossible value
		for (Piece piece : getColorPieces(white)) { // Get black pieces
			Coordinate imC = piece.getLocation(); // Coordinates of each black piece
			for (Coordinate move : reduceAndGetMoves(piece)) {	// Look at all moves for each black piece
				Chessboard copy = new Chessboard(this); // DEEP copy of the board
				copy.movePiece(imC.getX(), imC.getY(), move.getX(), move.getY()); // Updates the copy according to each move
				curr = alphabeta(copy, depth - 1, !white, minmax); // Searches deeper with new copy, smaller depth, switches color to white
				if (white) { // MAX node
					if (curr >= minmax) { // Update MAX node
						minmax = curr; 
						bestPiece = imC;
						bestLabel = move;
						bestImage = piece.getImage();
					} 
				}
				else { // MIN node
					if (curr <= minmax) { // Update MIN node
						minmax = curr; 
						bestPiece = imC;
						bestLabel = move;
						bestImage = piece.getImage();
					} 
				}
			}
		}
		
		ArrayList<Coordinate> cos = new ArrayList<Coordinate>();
		cos.add(bestPiece);
		cos.add(bestLabel);
		return new Pair<ImageView, ArrayList<Coordinate>>(bestImage, cos);
	}
	
	/**
	 * Assists in AI search
	 * @param grid
	 * @param depth
	 * @param white - true if piece is white, false if black
	 * @param root - the best score of the parent move used to limit search space
	 * @return score for the move
	 */
	private Integer alphabeta(Chessboard grid, int depth, boolean white, int root) {
		if (depth == 0) { // Base case - don't go any further
			return grid.score(); // Return score of the board
		} else {
			Integer minmax = white ? Integer.MIN_VALUE : Integer.MAX_VALUE, curr;
			for (Piece piece : grid.getColorPieces(white)) { // Iterates through all pieces of a certain color
				Coordinate imC = piece.getLocation();
				for (Coordinate move : grid.reduceAndGetMoves(piece)) { // Looks at all moves for each piece of designated color
					Chessboard copy = new Chessboard(grid); // DEEP copy of the board
					copy.movePiece(imC.getX(), imC.getY(), move.getX(), move.getY()); // Updates the copy based on move
					curr = alphabeta(copy, depth - 1, !white, minmax); // Searches one step further
					if (white) { // MAX node
						if (curr > minmax) { minmax = curr; } // Update MAX node
						if (minmax >= root) { return minmax; } // Beta cutoff - no need to keep searching
					}
					else { // MIN node
						if (curr < minmax) { minmax = curr; } // Update MIN node
						if (minmax <= root) { return minmax; } // Alpha cutoff - no need to keep searching
					}
				}
			}
			return minmax; // Return best score found for either MIN or MAX node
		}
	}
	
	/**
	 * Scores the board by taking the sum of the values of all pieces on the board
	 * @return the material score of the board
	 */
	private Integer materialScore() {
		Integer sum = 0;
		for (int x = 0; x < 8; x++) {
			for (int y = 0; y < 8; y++) {
				if (pieceList[x][y] != null) {
					sum += pieceList[x][y].getValue();
				}
			}
		}
		return sum;
	}
	
	/**
	 * Scores the board by taking the difference between the number of legal moves
	 * of the human player and computer player
	 * @return the mobility score of the board
	 */
	private Integer mobilityScore() {
		int humanMoves = 0, compMoves = 0, mobilityWeight = 10;  // mobilityWeight is 1/10 so score is divided by 10
		
		for (Piece piece : getColorPieces(true)) { // All white pieces
			humanMoves += reduceAndGetMoves(piece).size();
		}
		for (Piece piece : getColorPieces(false)) { // All black pieces
			compMoves += reduceAndGetMoves(piece).size();
		}
		
		return (humanMoves - compMoves) / mobilityWeight;
	}

	/**
	* sum of the material score and mobility score 
	* @return the score of the board
	*/
	public Integer score() {
		return materialScore() + mobilityScore();
	}

	
	
	/**
	 * Used to store a board the computer has previously seen to avoid re-calculating best move
	 * Prints the board in its integer representation
	 */
	@Override
	public String toString() {
		String ans = "";
		for (int x = 0; x < 8; x++) {
			for (int y = 0; y < 8; y++) {
				if (pieceList[x][y] != null) {
					ans += pieceList[x][y].getValue();
				} else {
					ans += "0";
				}
			}
		}
		return ans;
	}
}
