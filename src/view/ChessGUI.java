/**
 * @author Danny Cummings
 * This class is the main front-end component of the game
 * containing all the relevant GUI
 * This class updates all the visuals and is where
 * you can run the main()
 */

package view;

/* GUI */
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.Node;
import javafx.scene.Cursor;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javax.swing.JOptionPane;

/* Data Structures */
import java.util.ArrayList;
import javafx.util.Pair; 

/* Model Classes */
import model.Chessboard;
import model.Piece;
import model.Coordinate;
import model.Turn;

@SuppressWarnings("restriction")
public class ChessGUI extends Application {

	private Chessboard grid;

	private Turn human = new HumanTurn(), 
				 computer = new ComputerTurn(),
				 currTurn = human; // Keeps track of the state of the game

	private Image[] images;
	private ImageView selected; // The current piece selected

	private final double HIGHLIGHT = 0.5, // Indicates which box the piece can move to
						SCENE_DIM = 640.0, BOX_DIM = SCENE_DIM / 8.0; // dimensions of board
	
	private final int DEPTH = 3,  // How far ahead the computer looks
					KING_VAL = 200, QUEEN_VAL = 9, ROOK_VAL = 5,   // Worth of each piece
					BISHOP_VAL = 3, KNIGHT_VAL = 2,  PAWN_VAL = 1;
	
	/**
	 * Creates and displays the chessboard and assigns functionality to
	 * the images and GUI
	 * This method is called first when the main() is run and acts like
	 * a constructor for the class
	 * @param primaryStage 
	 */
	public void start(Stage primaryStage) throws InterruptedException {
		
		/* Initializes the images */
		images = new Image[12];
		images[0] = new Image("view/Pieces/White Pawn.png");
		images[1] = new Image("view/Pieces/White Rook.png");
		images[2] = new Image("view/Pieces/White Knight.png");
		images[3] = new Image("view/Pieces/White Bishop.png");
		images[4] = new Image("view/Pieces/White Queen.png");
		images[5] = new Image("view/Pieces/White King.png");
		images[6] = new Image("view/Pieces/Black Pawn.png");
		images[7] = new Image("view/Pieces/Black Rook.png");
		images[8] = new Image("view/Pieces/Black Knight.png");
		images[9] = new Image("view/Pieces/Black Bishop.png");
		images[10] = new Image("view/Pieces/Black Queen.png");
		images[11] = new Image("view/Pieces/Black King.png");

		/* Initializes the chess board */
		grid = new Chessboard();
		colorGrid();
		initializePieces();
		
		/* Defines what happens when a box or piece is clicked */
		for (Node node : grid.getChildren()) {
			if (node.getClass() == Label.class) { // User clicks on a box
				labelClick(node);
			}
			else if (node.getClass() == ImageView.class) { // User clicks on a piece
				imageClick(node);
			}
		}
		
		/* Adds the chess board to the stage and displays the stage */
		Scene scene = new Scene(grid, SCENE_DIM, SCENE_DIM);
		primaryStage.setTitle("Chess");
		primaryStage.setScene(scene);
		primaryStage.show();
	}
	
	
	/**
	 * Defines functionality for a label on the board, when it is 
	 * pressed and released
	 * @param node
	 */
	private void labelClick(Node node) {
		node.setOnMousePressed(e -> {
			if (node.getOpacity() == HIGHLIGHT) { // Only take action if highlighted
				performMove(grid.getPiece(selected).getLocation(), grid.getLabel((Label) node)); // Performs human move
				restoreOpacity(); // Takes away remaining highlights
			}
		});
		node.setOnMouseReleased(e -> {
			if (currTurn == computer) { // Only executes when correct label was clicked
				/* Performs computer move with smart AI */
				Pair<ImageView, ArrayList<Coordinate>> pair = grid.dfs(DEPTH); 
				selected = pair.getKey(); // Update selected piece to move
				performMove(pair.getValue().get(0), pair.getValue().get(1));  // performs computer move
			}
		});
	}
	
	/**
	 * Defines functionality for an image on the board, when it is
	 * pressed and released
	 * @param node
	 */
	private void imageClick(Node node) {
		node.setOnMousePressed(e -> {
			ImageView view = (ImageView) node;
			Piece piece = grid.getPiece(view); // Finds piece
			restoreOpacity(); // Resets highlights from last selected piece
			
			/* Only highlight possible moves if piece is white */
			if (piece.isWhite()) { 
				selected = view; // Updates selected piece
				highlightBoxes(grid.reduceAndGetMoves(piece)); 
			}
			else { // Piece is black
				/* Makes a move if image is highlighted/has a hand */ 
				if (piece.getImage().getCursor() == Cursor.HAND) {
					performMove(grid.getPiece(selected).getLocation(), piece.getLocation()); // Performs human move
				}
			}
		});
		node.setOnMouseReleased(e -> {
			if (currTurn == computer) { // Only executes when correct piece was clicked
				/* Performs computer move with smart AI */
				Pair<ImageView, ArrayList<Coordinate>> pair = grid.dfs(DEPTH); 
				selected = pair.getKey(); // Update selected piece to move
				performMove(pair.getValue().get(0), pair.getValue().get(1));  // performs computer move
			}
		});
	}

	/**
	 * Calculates move distance, adjusts images accordingly, moving selected piece
	 * from 'from' coordinate to 'to' coordinate on board
	 * This method also handles the case where a pawn reaches the end of board
	 * Displays state of the game if necessary
	 * @param from
	 * @param to
	 */
	private void performMove(Coordinate from, Coordinate to) {
	    /* Determines the factors for how far a piece must move
		* by subtracting the coordinates of the image from the
		* highlighted label */
		int xFactor = from.getX() - to.getX();
		int yFactor = from.getY() - to.getY();
		
		currTurn.move(xFactor, yFactor); // moves image on board
		
		/* Updates the coordinates of the image that just moved */
		if (grid.movePiece(from.getX(), from.getY(), to.getX(), to.getY())) { // Pawn at the end of the board
			replacePawn(to);
		}
		
		boolean white = currTurn == human ? true : false, 
				check = grid.isCheck(white), 
				oom = grid.outOfMoves(white);

		/* displays messages regarding state of the game */
		if (displayGameState(white, check, oom)) {
			/* GAME OVER */
			System.exit(0);
		}
		
		/* Resets all the black pieces to have default cursors */
		restoreCursors();
	}

	/**
	* Replaces a pawn on the board with a queen by default
	* Called when a pawn reaches the end of the board
	* @param co - coordinate of the pawn to be replaced
	*/
	private void replacePawn(Coordinate co) {
		Piece piece = grid.pieceAt(co.getX(), co.getY()); // Gets pawn
		piece.getImage().setVisible(false);  // removes pawn's image from board
		piece.getImage().setDisable(true);
		int val = piece.isWhite() ? QUEEN_VAL : -QUEEN_VAL, queenIdx = piece.isWhite() ? 4 : 10; // Always a queen by default
		initializePiece(new ImageView(images[queenIdx]), piece.isWhite(), co.getX(), co.getY(), val); // adds new image to board
		imageClick(grid.pieceAt(co.getX(), co.getY()).getImage()); // Give new piece functionality
	}

	/**
	* Displays messages regarding the state of the game: check, checkmate, or stalemate
	* @param white - team color
	* @param check - whether provided color is in check
	* @param oom - out of moves
	* @return true if the game is over, false otherwise
	*/
	private boolean displayGameState(boolean white, boolean check, boolean oom) {
		if (white) {
			if (check && oom) {
				JOptionPane.showMessageDialog(null, "Checkmate: Black wins!", "Game Over", JOptionPane.INFORMATION_MESSAGE);
				return true;
			} else if (check) {
				JOptionPane.showMessageDialog(null, "White is in check!", "Check", JOptionPane.INFORMATION_MESSAGE);
			} else if (oom) {
				JOptionPane.showMessageDialog(null, "Stalemate: It's a draw!", "Game Over", JOptionPane.INFORMATION_MESSAGE);
				return true;
			}
		} else {
			if (check && oom) {
				JOptionPane.showMessageDialog(null, "Checkmate: White wins!", "Game Over", JOptionPane.INFORMATION_MESSAGE);
				return true;
			} else if (check) {
				JOptionPane.showMessageDialog(null, "Black is in check!", "Check", JOptionPane.INFORMATION_MESSAGE);
			} else if (oom) {
				JOptionPane.showMessageDialog(null, "Stalemate: It's a draw!", "Game Over", JOptionPane.INFORMATION_MESSAGE);
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Colors the background of the chessboard with labels
	 */
	private void colorGrid() {
		for (int row = 0; row < 8; row++) {
			for (int col = 0; col < 8; col++) {
				Label label = new Label(" ");
				label.setMinSize(BOX_DIM, BOX_DIM);
				if ((row + col) % 2 == 0) { // Alternates colors
					label.setBackground(new Background(new BackgroundFill(
							Color.DARKGRAY, CornerRadii.EMPTY, Insets.EMPTY)));
				} 
				else {
					label.setBackground(new Background(new BackgroundFill(
							Color.BISQUE, CornerRadii.EMPTY, Insets.EMPTY)));
				}
				grid.add(label, col, row); // Adds colored box to board
				grid.addLabel(col, row, label); // Stores label position for back end purposes
			}
		}
	}
	
	/**
	 * Adds all the chess pieces to their starting positions on the 
	 * chessboard as images
	 * Each piece has a unique new ImageView that doubles as its ID
	 * Black pieces are given opposite negative values making the computer the MIN
	 * node and the player the MAX node
	 */
	private void initializePieces() {
		/* Black Pawns */
		for (int i = 0; i < 8; i++) {
			initializePiece(new ImageView(images[6]), false, i, 1, -PAWN_VAL);
		}
		
		/* White Pawns */
		for (int i = 0; i < 8; i++) {
			initializePiece(new ImageView(images[0]), true, i, 6, PAWN_VAL);
		}
		
		/* Black Rooks */
		initializePiece(new ImageView(images[7]), false, 0, 0, -ROOK_VAL);
		initializePiece(new ImageView(images[7]), false, 7, 0, -ROOK_VAL);
		
		/* White Rooks */
		initializePiece(new ImageView(images[1]), true, 0, 7, ROOK_VAL);
		initializePiece(new ImageView(images[1]), true, 7, 7, ROOK_VAL);
		
		/* Black Knights */
		initializePiece(new ImageView(images[8]), false, 1, 0, -KNIGHT_VAL);
		initializePiece(new ImageView(images[8]), false, 6, 0, -KNIGHT_VAL);
		
		/* White Knights */
		initializePiece(new ImageView(images[2]), true, 1, 7, KNIGHT_VAL);
		initializePiece(new ImageView(images[2]), true, 6, 7, KNIGHT_VAL);
		
		/* Black Bishops */
		initializePiece(new ImageView(images[9]), false, 2, 0, -BISHOP_VAL);
		initializePiece(new ImageView(images[9]), false, 5, 0, -BISHOP_VAL);
		
		/* White Bishops */
		initializePiece(new ImageView(images[3]), true, 2, 7, BISHOP_VAL);
		initializePiece(new ImageView(images[3]), true, 5, 7, BISHOP_VAL);
		
		/* Black Queen */
		initializePiece(new ImageView(images[10]), false, 3, 0, -QUEEN_VAL);
		
		/* White Queen */
		initializePiece(new ImageView(images[4]), true, 3, 7, QUEEN_VAL);
		
		/* Black King */
		initializePiece(new ImageView(images[11]), false, 4, 0, -KING_VAL);
		
		/* White King */
		initializePiece(new ImageView(images[5]), true, 4, 7, KING_VAL);
	}
	
	/**
	 * Helps add each piece to the board
	 * @param image
	 * @param white - true if piece is white, false if black
	 * @param x - x coordinate of image
	 * @param y - y coordinate of image
	 * @param value - worth of piece
	 */
	private void initializePiece(ImageView image, boolean white, int x, int y, int value) {
		image.setFitHeight(BOX_DIM);
		image.setFitWidth(BOX_DIM);
		if (white) { image.setCursor(Cursor.HAND); } // Indicates you can click on white pieces
		grid.add(image, x, y); // Adds piece to the board
		grid.addPiece(white, x, y, image, value); // Stores piece info for back end purposes
	}
	
	/**
	 * Makes all the boxes on the chessboard full opacity indicating a player
	 * has moved or a reset was needed for another piece
	 */
	private void restoreOpacity() {
		final double FULL = 1.0;
		for (Node node : grid.getChildren()) {
			node.setOpacity(FULL);
		}
	}

	/** 
	 * Sets all the black pieces to have default cursors
	 */
	private void restoreCursors() {
		for (int x = 0; x < 8; x++) {
			for (int y = 0; y < 8; y++) {
				Piece piece = grid.pieceAt(x, y);
				if (piece != null && !piece.isWhite()) {
					piece.getImage().setCursor(Cursor.DEFAULT);
				}
			}
		}
	}
	
	/**
	 * Iterates through a list of coordinates and highlights each box at the given
	 * coordinate, also setting the cursor of an opponent to hand if piece is
	 * highlighted
	 * @param list - list of coordinates
	 */
	private void highlightBoxes(ArrayList<Coordinate> list) {
		restoreCursors(); // Resets all the black pieces to have default cursors
		for (Coordinate co : list) {
			grid.labelAt(co.getX(), co.getY()).setOpacity(HIGHLIGHT);
			Piece piece = grid.pieceAt(co.getX(), co.getY());
			if (piece != null && !piece.isWhite()) { piece.getImage().setCursor(Cursor.HAND); }
		}
	}
	
	
	/* Private inner classes */
	private class HumanTurn implements Turn {
		public void move(int xFactor, int yFactor) {
			double xPos = selected.getTranslateX(), yPos = selected.getTranslateY();
			selected.setTranslateX(xPos - (xFactor * BOX_DIM));
			selected.setTranslateY(yPos - (yFactor * BOX_DIM));
			currTurn = computer; // Switches turn
		}
	}
	
	private class ComputerTurn implements Turn {
		public void move(int xFactor, int yFactor) {
			double xPos = selected.getTranslateX(), yPos = selected.getTranslateY();
			selected.setTranslateX(xPos - (xFactor * BOX_DIM));
			selected.setTranslateY(yPos - (yFactor * BOX_DIM));
			currTurn = human; // Switches turn
		}
	}
	
	
	/**
	 * Runs the program
	 * @param args
	 */
	public static void main(String[] args) {
		Application.launch(args);
	}
}
