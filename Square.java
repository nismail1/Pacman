package Pacman;

import java.util.ArrayList;

import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

/*
 * This class is a wrapper for the squares of the board. It defines their length and keeps tracks whether or not
 * it is a wall.
 */
public class Square {
	Rectangle _square;
	Boolean _isWall;
	ArrayList<SmartObjects> _contains;

	public Square() {
		_square = new Rectangle();
		_square.setWidth(Constants.SQUARE_WIDTH);
		_square.setHeight(Constants.SQUARE_WIDTH);
		_isWall = false;
		_contains = new ArrayList<SmartObjects>();

	}

	public ArrayList<SmartObjects> getArr() {
		return _contains;
	}

	public void setColor(Color c) {
		_square.setFill(c);
	}

	public void setX(double x) {
		_square.setX(x);
	}

	public void setY(double y) {
		_square.setY(y);
	}

	public Rectangle getShape() {
		return _square;
	}

	public void setWall(Boolean t) {
		_isWall = t;
	}

	public Boolean isWall() {
		return _isWall;
	}

}