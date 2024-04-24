package Pacman;

import java.util.ArrayList;
import java.util.LinkedList;

import javax.swing.JLabel;

import cs015.fnl.PacmanSupport.SquareType;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.TextAlignment;
import javafx.util.Duration;

/*
 * This class handles all the game logic of the game Pacman. It creates the board, moves the Pacman, and moves 
 * the Ghosts and responds to key input. It creates the board through using the support map and placing
 * the information in a 2D Array. It sets up a timeline that moves the pacman in whatever direction is set by
 * key input in the keyhandler. There are instance variables that keep track of rows and columns. I also handles,
 * moving the ghost through calling a Breadth First Search Algorithm defined in the Ghost class. In this class,
 * different targets were called for the four ghosts depending on the current mode of the game.
 */
public class Pacman {
	BorderPane _gamePane;
	Square[][] _myboard;
	PacmanShape _pac;// Wrapper Class for the Pacman Shape
	ArrayList<Ghost> _ghosts = new ArrayList();
	LinkedList<Ghost> _pen = new LinkedList();
	int _row;
	int _col;

	int _startx;
	int _starty;
	int _lives;
	Timeline _ghostTimeline;
	Timeline _pacTimeline;
	Label _scoreLabel;
	Label _livesLabel;
	BoardCoordinate _pcoor;
	Direction _direction;
	int _score;
	int _ghrow;
	int _ghcol;
	Ghost _pinky;
	Ghost _blinky;
	Ghost _inky;
	Ghost _clyde;
	Mode _mode;
	int _count = 0;
	int _chasecounter = 0;
	int _letout = 0;

	public Pacman() {
		_gamePane = new BorderPane();
		_pac = new PacmanShape();
		_direction = null;
		_score = 0;
		this.buildBoard();
		_count = 0;
		_chasecounter = 0;
		_lives = 3;
		_mode = Mode.CHASE;
		_gamePane.addEventHandler(KeyEvent.KEY_PRESSED, new KeyHandler());
		_gamePane.setFocusTraversable(true);
		_gamePane.requestFocus();
		this.setupTimeline();
		this.setupGhostTimeline();

	}

	/*
	 * Create an accessor method to use in the Ghost class to know what mode the
	 * game is currently
	 */
	public Mode getMode() {
		return _mode;
	}

	public void setMode(Mode mode) {
		_mode = mode;
	}

	// This method creates the score label and lives label at the top. It uses an
	// instance variable
	// to represent lives and score so that they can be incremented as the game
	// continues
	public void makeLabel(Label _scoreLabel, Label _livesLabel) {
		_scoreLabel = new Label("Score:" + _score);
		HBox hbox = new HBox();
		hbox.getChildren().add(_scoreLabel);
		hbox.setSpacing(40);
		_gamePane.setCenter(hbox);
		_scoreLabel.setTranslateY(670);
		_scoreLabel.setTranslateX(0);

		_livesLabel = new Label("Lives:" + _lives);

		hbox.getChildren().add(_livesLabel);

		_scoreLabel.toFront();
		_livesLabel.toFront();
		_livesLabel.setTranslateY(670);
		_livesLabel.setTranslateX(70);
		_scoreLabel.setTextFill(Color.YELLOW);
		_livesLabel.setTextFill(Color.YELLOW);
		_scoreLabel.setMinHeight(20);
		_livesLabel.setMinHeight(20);

	}

	// This method just returns the original row of ghost. It is used in the Ghost
	// class to reset a
	// ghosts' position to the pen
	public int getRow() {
		return _ghrow;
	}

	public int getCol() {
		return _ghcol;
	}

	// Used the stencil code in order to get the supportmap that contained the
	// information about
	// the starting locations
	public void buildBoard() {
		cs015.fnl.PacmanSupport.SquareType[][] stencilmap = cs015.fnl.PacmanSupport.SupportMap.getSupportMap();

		_myboard = new Square[(int) Constants.ROWS][(int) Constants.COLS];
		for (int i = 0; i < Constants.ROWS; i++) {
			for (int j = 0; j < Constants.COLS; j++) {
				Square square = new Square();
				if (stencilmap[i][j] == SquareType.WALL) {

					square.setColor(Color.BLUE);
					square.setX(j * Constants.SQUARE_WIDTH);
					square.setY(i * Constants.SQUARE_WIDTH);
					square.setWall(true);
					_gamePane.getChildren().add(square.getShape());
				} else if (stencilmap[i][j] == SquareType.FREE) {

					square.setColor(Color.BLACK);
					square.setX(j * Constants.SQUARE_WIDTH);
					square.setY(i * Constants.SQUARE_WIDTH);
					square.setWall(false);
					_gamePane.getChildren().add(square.getShape());
				} else if (stencilmap[i][j] == SquareType.DOT) {

					square.setColor(Color.BLACK);
					square.setX(j * Constants.SQUARE_WIDTH);
					square.setY(i * Constants.SQUARE_WIDTH);
					square.setWall(false);
					Dot dot = new Dot(this, _gamePane);
					dot.setX(j * Constants.SQUARE_WIDTH);
					dot.setY(i * Constants.SQUARE_WIDTH);
					square.getArr().add(dot);

					_gamePane.getChildren().add(square.getShape());
					_gamePane.getChildren().add(dot.getShape());

				} else if (stencilmap[i][j] == SquareType.ENERGIZER) {

					square.setColor(Color.BLACK);
					square.setX(j * Constants.SQUARE_WIDTH);
					square.setWall(false);
					square.setY(i * Constants.SQUARE_WIDTH);
					Energizer e = new Energizer(this, _gamePane);
					e.setX(j * Constants.SQUARE_WIDTH);
					e.setY(i * Constants.SQUARE_WIDTH);

					square.getArr().add(e);
					_gamePane.getChildren().add(square.getShape());
					_gamePane.getChildren().add(e.getShape());
				} else if (stencilmap[i][j] == SquareType.PACMAN_START_LOCATION) {
					_row = i;// Begin keeping track of Pacman's location
					_col = j;
					_startx = j * Constants.SQUARE_WIDTH;// This is to be accessed in Ghost to reset Pacman to the start
															// location
					_starty = i * Constants.SQUARE_WIDTH;
					square.setColor(Color.BLACK);
					square.setX(j * Constants.SQUARE_WIDTH);
					square.setWall(false);
					square.setY(i * Constants.SQUARE_WIDTH);

					_pac.setX(_col * Constants.SQUARE_WIDTH);
					_pac.setY(_row * Constants.SQUARE_WIDTH);

					_gamePane.getChildren().add(square.getShape());
					_gamePane.getChildren().add(_pac.getDot());
				} else if (stencilmap[i][j] == SquareType.GHOST_START_LOCATION) {
					_ghrow = i; // Save the start locations
					_ghcol = j;
					square.setColor(Color.BLACK);
					square.setWall(true);
					square.setX(j * Constants.SQUARE_WIDTH);
					square.setY(i * Constants.SQUARE_WIDTH);
					_pinky = new Ghost(this, Color.PINK); // Repeat steps for multiple ghosts
					_blinky = new Ghost(this, Color.RED);
					_inky = new Ghost(this, Color.SKYBLUE);
					_clyde = new Ghost(this, Color.ORANGE);
					_ghosts.add(_blinky);
					_ghosts.add(_pinky);
					_ghosts.add(_inky);
					_ghosts.add(_clyde);

					_pinky.setX(j * Constants.SQUARE_WIDTH);
					_pinky.setY(i * Constants.SQUARE_WIDTH);
					_pinky.setPen(true);// This tells the Timeline whether to call HandlePen

					_blinky.setX(j * Constants.SQUARE_WIDTH);
					_blinky.setY((i - 2) * Constants.SQUARE_WIDTH);
					_blinky.setPen(false);

					_inky.setX((j - 1) * Constants.SQUARE_WIDTH);
					_inky.setY(i * Constants.SQUARE_WIDTH);
					_inky.setPen(true);

					_clyde.setX((j + 1) * Constants.SQUARE_WIDTH);
					_clyde.setY(i * Constants.SQUARE_WIDTH);
					_clyde.setPen(true);

					_pen.add(_pinky);
					_pen.add(_inky);
					_pen.add(_clyde);

					_gamePane.getChildren().add(square.getShape());
					_gamePane.getChildren().add(_pinky.getShape());
					_gamePane.getChildren().add(_blinky.getShape());
					_gamePane.getChildren().add(_inky.getShape());
					_gamePane.getChildren().add(_clyde.getShape());

				}
				_myboard[i][j] = square;
			}
		}

	}

	public double getstartx() {// Have access to start location of Pacman in the Ghost class
		return _startx;
	}

	public double getstarty() {
		return _starty;
	}

	public void setRow(int row) { // Allow the Ghost class to change the _row variable in order to keep track of
									// the row and column of Pacman
		_row = row;
	}

	public void setCol(int col) {
		_col = col;
	}

	public Square[][] getBoard() {
		return _myboard;
	}

	public PacmanShape getPacman() {
		return _pac;
	}

	public Node getpane() {
		return _gamePane;
	}

	// This allows other classes to update the score when there is a collision
	public void updateScore(int score) {
		_score = _score + score;
	}

	public void setupTimeline() {
		KeyFrame kf = new KeyFrame(Duration.seconds(.25), new TimeHandler());
		_ghostTimeline = new Timeline(kf);
		_ghostTimeline.setCycleCount(Animation.INDEFINITE);
		_ghostTimeline.play();
	}

	public void setupGhostTimeline() {
		KeyFrame kf = new KeyFrame(Duration.seconds(.25), new GhostHandler());
		_pacTimeline = new Timeline(kf);
		_pacTimeline.setCycleCount(Animation.INDEFINITE);
		_pacTimeline.play();
	}

	// Look at the square that Pacman currently resides. Look to the neighbor and
	// check for collision

	public void checkCollision() {
		ArrayList<SmartObjects> arr = _myboard[_row][_col].getArr();
		for (int i = 0; i < arr.size(); i++) {
			if (arr.get(i) != null) {
				arr.get(i).collision(arr);
			}

		}
	}

	/*
	 * This method checks the if the next square in the board is a wall or within
	 * the board
	 */
	public Boolean checkValidity(int y, int x) {

		if ((_col + y) >= 0 && (_row + x) >= 0 && (_row + x) < Constants.ROWS && (_col + y) < Constants.COLS
				&& !_myboard[_row + x][_col + y].isWall()) {
			return true;
		} else {
			return false;
		}
	}

	/*
	 * Created this method in order to be able to update the life instance variable
	 * in the ghost class
	 */
	public void updateLives(int x) {
		_lives = _lives + x;
	}

	public PacmanShape getPac() {
		return _pac;
	}

	/*
	 * This method is called in the Timehandler to smoothly mooth Pacman in the
	 * direction inputted in the KeyHandler. It uses a switch statement that
	 * switches on the current direction and moves the Pacman in that direction
	 * until it encounters a wall. It also updates the row and column that the
	 * Pacman is currently at.
	 * 
	 */
	public void movement() {
		Boolean move;

		if (_direction != null) {

			switch (_direction) {

			case LEFT:
				move = Pacman.this.checkValidity(-1, 0);

				if (move) {

					_pac.setX((_col - 1) * Constants.SQUARE_WIDTH);
					_col--;
				}
				break;
			case RIGHT:
				move = Pacman.this.checkValidity(1, 0);

				if (move) {
					_pac.setX((_col + 1) * Constants.SQUARE_WIDTH);
					_col++;
				}
				break;
			case UP:

				move = Pacman.this.checkValidity(0, -1);

				if (move) {
					_pac.setY((_row - 1) * Constants.SQUARE_WIDTH);
					_row--;
				}
				break;
			case DOWN:
				move = Pacman.this.checkValidity(0, 1);

				if (move) {
					_pac.setY((_row + 1) * Constants.SQUARE_WIDTH);
					_row++;
				}
				break;
			default:

			}

		}

	}

	public void ghostWrapping() {
		for (int i = 0; i < _ghosts.size(); i++) {
			Ghost ghost = _ghosts.get(i);

			int row = (int) ghost.getY() / Constants.SQUARE_WIDTH;
			int col = (int) ghost.getX() / Constants.SQUARE_WIDTH;
			if ((col == 0 && row == 11)) {

				col = 21;

				ghost.setY(col * Constants.SQUARE_WIDTH);

			}
			if (col == 22 && row == 11) {

				col = 0;
				ghost.setY(col * Constants.SQUARE_WIDTH);
			}
		}
	}/*
		 * Allows the Pacman to through the tunnel and appear on the other side of the
		 * screen.
		 */

	public void pacmanWrapping() {
		if ((_col == 0 && _row == 11)) {

			_col = 21;
			_pac.setX(_col * Constants.SQUARE_WIDTH);

		} else if (_col == 21 && _row == 11) {

			_col = 0;
			_pac.setX(_col * Constants.SQUARE_WIDTH);
		}
	}

	// This method just resets all the ghosts to their starting position when the
	// game ends
	public void endGame() {
		_pinky.setX(_ghrow * Constants.SQUARE_WIDTH);
		_pinky.setY(_ghcol * Constants.SQUARE_WIDTH);
		_blinky.setX((_ghcol - 1) * Constants.SQUARE_WIDTH);
		_blinky.setY(_ghrow * Constants.SQUARE_WIDTH);
		_inky.setX((_ghcol + 1) * Constants.SQUARE_WIDTH);
		_inky.setY(_ghrow * Constants.SQUARE_WIDTH);
		_clyde.setX(_ghcol * Constants.SQUARE_WIDTH);
		_clyde.setY((_ghrow) * Constants.SQUARE_WIDTH);
	}

	// This method removes a ghost from the pen
	public void handlePen(Ghost pen) {

		pen.setX(_ghcol * Constants.SQUARE_WIDTH);
		pen.setY((_ghrow - 2) * Constants.SQUARE_WIDTH);
		pen.setPen(true);

	}

	/*
	 * This is my Ghost Time Handler. It is where I handle mode by utilizing
	 * counters which keep track of when modes should switch. I chose to put a lot
	 * in the TimeHanlder because the TimeHandler was where the counters were and
	 * different actions had to be taken based on teh counter.
	 */
	private class GhostHandler implements EventHandler<ActionEvent> {
		public void handle(ActionEvent event) {

			if (_lives == 0) {// This checks to see if the game has ended
				Pacman.this.makeLabel(new Label("Score:" + _score), new Label("Lives:" + _lives));
				Pacman.this.endGame();
				_ghostTimeline.stop();
			} else {

				if (_mode == Mode.FRIGHTENED) {
					_count++;// This counter keeps track of how long frightened mode should last
				} else {
					_chasecounter++;
				}
				_letout = _letout + 1;// This counter checks how long ghost should remain in the pen in the beginning
				if (_letout > 10 && !_pen.isEmpty()) {
					Pacman.this.handlePen(_pen.removeFirst());
					_letout = 0;
				}
				Pacman.this.checkCollision();
				for (int i = 0; i < _ghosts.size(); i++) {
					Ghost ghost = _ghosts.get(i);
					if (_ghosts.get(i).getPen() == false) { // Checking to see if the pen needs ghost removed
						Pacman.this.handlePen(ghost);
					}
				}
				if (_mode == Mode.FRIGHTENED) {
					if (_count < 28) {// This is how long the ghosts should stay in frightened mode
						Pacman.this.ghostModes(_pinky);// Passing in the instance of ghost to use
						Pacman.this.ghostModes(_blinky);
						Pacman.this.ghostModes(_inky);
						Pacman.this.ghostModes(_clyde);
					} else {
						_count = 0;// Reset count
						_mode = Mode.CHASE;
					}
				} else if (_chasecounter < 80) {// Chase Mode
					_pinky.getShape().setFill(_pinky.getFill());// Reset colors
					_inky.getShape().setFill(_inky.getFill());
					_blinky.getShape().setFill(_blinky.getFill());
					_clyde.getShape().setFill(_clyde.getFill());

					_mode = Mode.CHASE;
					Pacman.this.ghostModes(_pinky);
					Pacman.this.ghostModes(_blinky);
					Pacman.this.ghostModes(_inky);
					Pacman.this.ghostModes(_clyde);

				} else if (_chasecounter < 96) {
					_mode = Mode.SCATTER;
					Pacman.this.ghostModes(_pinky);
					Pacman.this.ghostModes(_blinky);
					Pacman.this.ghostModes(_inky);
					Pacman.this.ghostModes(_clyde);

				} else {
					_chasecounter = 0;// Reset counter
				}
				_pinky.getShape().toFront(); // Bring the shapes to front
				_blinky.getShape().toFront();
				_inky.getShape().toFront();
				_clyde.getShape().toFront();

				Pacman.this.checkCollision();// Check Collision
				Pacman.this.ghostWrapping();
			}
		}
	}

	/*
	 * This TimeHandler is simpler and just calls the methods associated with moving
	 * the Pacman, checking collision and wrapping the Pacman.
	 */
	private class TimeHandler implements EventHandler<ActionEvent> {
		public void handle(ActionEvent event) {
			if (_lives == 0) {// Checks to see if the game is done
				Pacman.this.endGame();
				Pacman.this.makeLabel(new Label("Score:" + _score), new Label("Lives:" + _lives));
				_pacTimeline.stop();
			} else {
				Pacman.this.movement();
				Pacman.this.checkCollision();
				Pacman.this.makeLabel(new Label("Score:" + _score), new Label("Lives:" + _lives));
				Pacman.this.pacmanWrapping();
			}
		}
	}

	/*
	 * This method takes in the direction and moves the instance of ghost in that
	 * direction
	 */
	public void moveGhost(int x, int y, Ghost ghost) {
		int ghrow = (int) ghost.getY() / Constants.SQUARE_WIDTH;
		int ghcol = (int) ghost.getX() / Constants.SQUARE_WIDTH;
		Square currSquare = _myboard[ghrow][ghcol];
		currSquare.getArr().remove(ghost);// Remove it from the array of the current square
		ghost.setX(y * Constants.SQUARE_WIDTH);
		ghost.setY(x * Constants.SQUARE_WIDTH);
		_myboard[x][y].getArr().add(ghost);// Add it to the array of the new square
	}

	/*
	 * Takes in a direction and moves the ghost accordingly using a switch statement
	 */
	public Boolean ghostBFS(Direction dir, Ghost ghost) {

		if (dir == null) {
			return false;
		} else {
			int ghrow = (int) ghost.getY() / Constants.SQUARE_WIDTH;
			int ghcol = (int) ghost.getX() / Constants.SQUARE_WIDTH;
			switch (dir) {
			case UP:
				this.moveGhost(ghrow - 1, ghcol, ghost);

				break;
			case DOWN:
				this.moveGhost(ghrow + 1, ghcol, ghost);

				break;
			case LEFT:
				this.moveGhost(ghrow, ghcol - 1, ghost);

				break;
			case RIGHT:
				this.moveGhost(ghrow, ghcol + 1, ghost);

				break;
			}
			return true;
		}
	}

	/*
	 * Based on the instance variable that keeps track of the current mode, this
	 * method calls the BFS method defined in the Ghost class on different targets
	 * depending on the mode
	 */
	public void ghostModes(Ghost ghost) {
		BoardCoordinate cord;

		if (_mode == Mode.FRIGHTENED) {
			ghost.getShape().setFill(Color.ALICEBLUE);
			int validn = 0;// This variable will keep track of how many valid neighbors there are
			ArrayList<Direction> onedir = new ArrayList<Direction>();// This list keeps track of valid directions
			if (this.checkValidNeighbors(ghost, Direction.UP)) {
				validn++;
				onedir.add(Direction.UP);
			}
			if (this.checkValidNeighbors(ghost, Direction.DOWN)) {
				validn++;
				onedir.add(Direction.DOWN);
			}
			if (this.checkValidNeighbors(ghost, Direction.RIGHT)) {
				validn++;
				onedir.add(Direction.RIGHT);
			}
			if (this.checkValidNeighbors(ghost, Direction.LEFT)) {
				validn++;
				onedir.add(Direction.LEFT);
			}

			int rand = (int) ((Math.random() * ((0 - (onedir.size() - 1)) + 1)) + 0);/// Select a random index of the
																						/// direction array
			this.ghostBFS(onedir.get(rand), ghost);// Move the ghost in that random direction
			ghost.setDirection(onedir.get(rand));// Set the initial direction in the ghost class to be this new random
													// direction

		} else {
			if (_mode == Mode.CHASE) {
				if (ghost == _blinky) {// Check what instance of the class it is
					cord = new BoardCoordinate(_row, _col, true); // Change the target accordingly based on the location
																	// of Pacman
					this.ghostBFS(ghost.BFS(cord), ghost);
				}
				if (ghost == _inky) {
					cord = new BoardCoordinate(_row, _col + 2, true);
					this.ghostBFS(ghost.BFS(cord), ghost);
				}
				if (ghost == _clyde) {
					cord = new BoardCoordinate(_row - 4, _col, true);
					this.ghostBFS(ghost.BFS(cord), ghost);
				}
				if (ghost == _pinky) {
					cord = new BoardCoordinate(_row + 1, _col - 3, true);
					this.ghostBFS(ghost.BFS(cord), ghost);
				}

			} else if (_mode == Mode.SCATTER) {// Target should be corner of the board

				if (ghost == _blinky) {// Check what instance of the class it is
					cord = new BoardCoordinate(Constants.SQUARE_WIDTH - 1, Constants.SQUARE_WIDTH - 1, true); // Change
																												// the
																												// target
																												// accordingly
																												// based
																												// on
																												// the
																												// location
																												// of
																												// Pacman
					this.ghostBFS(ghost.BFS(cord), ghost);
				}
				if (ghost == _inky) {
					cord = new BoardCoordinate(Constants.SQUARE_WIDTH - 1, 0, true);
					this.ghostBFS(ghost.BFS(cord), ghost);
				}
				if (ghost == _clyde) {
					cord = new BoardCoordinate(0, 0, true);
					this.ghostBFS(ghost.BFS(cord), ghost);
				}
				if (ghost == _pinky) {
					cord = new BoardCoordinate(0, Constants.SQUARE_WIDTH - 1, true);
					this.ghostBFS(ghost.BFS(cord), ghost);
				}
			}
		}
	}

	/*
	 * This method is to check valid neighbors of the ghost in order to move the
	 * ghost randomly in frightened mode. It checks whether the square in that
	 * direction is a wall or if it is in the opposite direction.
	 */
	public boolean checkValidNeighbors(Ghost ghost, Direction dir) {
		int row = (int) (ghost.getY() / Constants.SQUARE_WIDTH);
		int col = (int) (ghost.getX() / Constants.SQUARE_WIDTH);
		boolean result = false;
		switch (dir) {
		case UP:
			if (!_myboard[row - 1][col].isWall() && ghost.getDirection().opposite() != dir) {
				result = true;
			}
			break;
		case DOWN:
			if (!_myboard[row + 1][col].isWall() && ghost.getDirection().opposite() != dir) {
				result = true;
			}
			break;
		case RIGHT:
			if (!_myboard[row][col + 1].isWall() && ghost.getDirection().opposite() != dir) {
				result = true;
			}
			break;
		case LEFT:
			if (!_myboard[row][col - 1].isWall() && ghost.getDirection().opposite() != dir) {
				result = true;
			}
			break;
		}
		return result;
	}

	private class KeyHandler implements EventHandler<KeyEvent> {

		@Override
		public void handle(KeyEvent e) {
			KeyCode keyPressed = e.getCode();

			switch (keyPressed) {

			case LEFT:
				Boolean move = Pacman.this.checkValidity(-1, 0);// Check Move valididity

				if (move) {

					_direction = Direction.LEFT;// Set current direction of the Pacman

				}
				break;
			case RIGHT:
				move = Pacman.this.checkValidity(1, 0);

				if (move) {
					_direction = Direction.RIGHT;
				}
				break;
			case UP:
				move = Pacman.this.checkValidity(0, -1);

				if (move) {
					_direction = Direction.UP;
				}
				break;
			case DOWN:
				move = Pacman.this.checkValidity(0, 1);

				if (move) {
					_direction = Direction.DOWN;
				}
				break;
			default:
			}

			e.consume();
		}
	}

}