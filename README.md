# Chess-Game
A fun Java project I wrote that was inspired by a class I took on artificial inteligence. 

The project involves a variety of ideas such as the MVC software design pattern, the alphabeta search algorithm, and `javafx` to create a GUI.

Project Files
-------------
* model package
  * __Piece.java__: This file describes a piece on a standard chess board
  * __Coordinate.java__: This file is used to help identify the position of pieces on a chess board
  * __Chessboard.java__: This file is an extension of a GridPane - storing and processing the data on the board
* utils package
  * __ChessUtils.java__: This file contains helpful methods for reading and writing from files - this is important for storing information to help the AI make better, quicker decisions
* view package
  * __ChessGUI.java__: This file contains all the GUI components of the project and assembles them to make the board - this class receives player input, requests an action from the Chessboard class, and updates the view of the board based on the response
