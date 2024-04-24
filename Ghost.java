package Pacman;

import java.util.ArrayList;
import java.util.LinkedList;

import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;

/*
 * This class handles game logic and it is a wrapper for the rectangle that represents a ghost. Its two main
 * methods are BFS which implements the Breadth First Search Algorithm in order to move the 
 * ghost in the best path to chase the Pacman object. The second main method is the collision method
 * which is the implementation through an interface of a generic method on objects of type collidable.
 * It handles any actions needed surrounding collision of a ghost with Pacman.
 */
public class Ghost implements SmartObjects {
	Rectangle _dot;
	Color _c;
	int _origrow;
	int _origcol;
	Boolean _pen;
	Mode _mode;
	Pacman _g;
	Direction _initialdir;
	int _startx;
	int _starty;
	int _ready = 0;

	public Ghost(Pacman g, Color c) {

		_dot = new Rectangle();
		_dot.setWidth(Constants.SQUARE_WIDTH);
		_dot.setHeight(Constants.SQUARE_WIDTH);
		_dot.setFill(c);
		_c = c;// This is an instance variable so that it can be accessed in the Pacman class
		_g = g;
		_initialdir = Direction.UP;

	}

	/*
	 * Calculating the distance between the target and the ghost
	 */
	public double calculateDist(BoardCoordinate target, double x, double y) {
		double xcord = Math.pow((target.getRow() * Constants.SQUARE_WIDTH) - x * Constants.SQUARE_WIDTH, 2);
		double ycord = Math.pow((target.getColumn() * Constants.SQUARE_WIDTH) - y * Constants.SQUARE_WIDTH, 2);
		return Math.sqrt(xcord + ycord);
	}

	/*
	 * This method was code factored out of the BFS method. It checks whether the
	 * square in a given direction is a wall or if it is in the opposite direction
	 */
	public Boolean checkNeighbor(Direction dir, int row, int col, Direction[][] dirArr) {
		boolean result = false;

		switch (dir) {
		case UP:
			if ((row - 1) < 0) {
				result = false;
			} else if (!_g.getBoard()[row - 1][col].isWall() && dirArr[row - 1][col] == null
					&& Direction.UP.opposite() != _initialdir) {
				result = true;
			}
			break;
		case DOWN:
			if ((row + 1) > 22) {
				result = false;
			} else if (!_g.getBoard()[row + 1][col].isWall() && dirArr[row + 1][col] == null
					&& Direction.DOWN.opposite() != _initialdir) {
				result = true;
			}

			break;
		case RIGHT:

			if ((col + 1) > 22) {
				result = false;
			} else if (!_g.getBoard()[row][col + 1].isWall() && dirArr[row][col + 1] == null
					&& Direction.RIGHT.opposite() != _initialdir) {
				result = true;
			}

			break;
		case LEFT:
			if ((col - 1) < 0) {
				result = false;
			} else if (!_g.getBoard()[row][col - 1].isWall() && dirArr[row][col - 1] == null
					&& Direction.LEFT.opposite() != _initialdir) {

				result = true;
			}

			break;

		}
		return result;
	}

	/*
	 * This method handles ghost movement. It takes in a target and finds the best
	 * direction to move in to get to that target. It first checks valid neighbors
	 * and adds them to a queue. Then, it pops directions off and calculates the
	 * minimum distance between the squares and the target. Then it returns the
	 * direction associated with the minimum distance.
	 */
	public Direction BFS(BoardCoordinate cord) {
		int row = (int) (_dot.getY() / Constants.SQUARE_WIDTH);

		int col = (int) (_dot.getX() / Constants.SQUARE_WIDTH);

		double minDistance = 100000.0;
		Direction closestSquare = null;
		Direction[][] dirArr = new Direction[23][23];// Keeps track of what has been visited and what direction taken to
														// reach the quare
		LinkedList<BoardCoordinate> queue = new LinkedList<BoardCoordinate>();

		if (checkNeighbor(Direction.UP, row, col, dirArr)) {

			BoardCoordinate up = new BoardCoordinate(row - 1, col, false);
			queue.add(up);
			dirArr[row - 1][col] = Direction.UP;
		}
		if (checkNeighbor(Direction.DOWN, row, col, dirArr)) {

			BoardCoordinate down = new BoardCoordinate(row + 1, col, false);
			queue.add(down);
			dirArr[row + 1][col] = Direction.DOWN;
		}
		if (checkNeighbor(Direction.LEFT, row, col, dirArr)) {

			BoardCoordinate left = new BoardCoordinate(row, col - 1, false);
			queue.add(left);
			dirArr[row][col - 1] = Direction.LEFT;
		}
		if (checkNeighbor(Direction.RIGHT, row, col, dirArr)) {

			BoardCoordinate right = new BoardCoordinate(row, col + 1, false);
			queue.add(right);
			dirArr[row][col + 1] = Direction.RIGHT;
		}

		while (!queue.isEmpty()) {
			BoardCoordinate current = queue.removeFirst();

			int currRow = current.getRow();
			int currCol = current.getColumn();

			Direction currentDir = dirArr[currRow][currCol];

			double dist = this.calculateDist(cord, currRow, currCol);

			if (dist < minDistance) {

				minDistance = dist;
				closestSquare = currentDir;
			}

			if (currRow < 0 || currRow > 22 || currCol < 0 || currCol > 22) {// Check first if still within the board

			}

			else {
				if (checkNeighbor(Direction.UP, currRow, currCol, dirArr)) {// Check the neighbors of the current square
																			// and enque them if valid

					BoardCoordinate up = new BoardCoordinate(currRow - 1, currCol, false);
					queue.add(up);
					dirArr[currRow - 1][currCol] = currentDir;
				}
				if (checkNeighbor(Direction.DOWN, currRow, currCol, dirArr)) {
					BoardCoordinate down = new BoardCoordinate(currRow + 1, currCol, false);
					queue.add(down);
					dirArr[currRow + 1][currCol] = currentDir;
				}
				if (checkNeighbor(Direction.LEFT, currRow, currCol, dirArr)) {
					BoardCoordinate left = new BoardCoordinate(currRow, currCol - 1, false);
					queue.add(left);
					dirArr[currRow][currCol - 1] = currentDir;
				}
				if (checkNeighbor(Direction.RIGHT, currRow, currCol, dirArr)) {
					BoardCoordinate right = new BoardCoordinate(currRow, currCol + 1, false);
					queue.add(right);
					dirArr[currRow][currCol + 1] = currentDir;
				}
			}
		}
		_initialdir = closestSquare;// This is the direction associated with the minimum distance

		return _initialdir;
	}

	@Override
	public void setX(double x) {

		_dot.setX(x);
	}

	public void setY(double y) {// All of these accessor and getter methods are used in the Pacman class for
								// different methods

		_dot.setY(y);
	}

	public double getX() {

		return _dot.getX();
	}

	public double getY() {

		return _dot.getY();
	}

	public Direction getDirection() {
		return _initialdir;
	}

	public void setDirection(Direction dir) {
		_initialdir = dir;
	}

	public Shape getShape() {
		return _dot;
	}

	public Boolean getPen() {
		return _pen;
	}

	public void setPen(Boolean pen) {
		_pen = pen;
	}

	public Paint getFill() {
		return _c;
	}

	@Override
	public void collision(ArrayList<SmartObjects> arr) {
		// Handle collision for Ghost
		_mode = _g.getMode();
		if (_mode != Mode.FRIGHTENED) {// If not frightened, then Pacman loses a life
			_g.updateLives(-1);
			_g.getPac().setY(_g.getstarty());// Pacman is reset to start location
			_g.getPac().setX(_g.getstartx());
			_g.setCol((int) _g.getstartx() / Constants.SQUARE_WIDTH);// Update the row and column variables for Pacman
			_g.setRow((int) _g.getstarty() / Constants.SQUARE_WIDTH);
			_dot.toFront();
			arr.remove(this);// Remove from the current arraylist
			_pen = false;

		} else {
			_dot.setX(_g.getCol() * Constants.SQUARE_WIDTH);// If Frightened mode, then reset the ghosts location
			_dot.setY(_g.getRow() * Constants.SQUARE_WIDTH);
			_g.updateScore(200);// Increase the score
			_g.handlePen(this);// Remove ghost from Pen
			_pen = true;
		}
	}
}