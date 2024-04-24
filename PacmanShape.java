package Pacman;

import javafx.scene.paint.Color;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;

/*
 * Wrapper Class for the Pacman dot. It handles the location of the dot and sets basic attributes like color and width.
 */
public class PacmanShape {
	Ellipse _dot;
	Rectangle _rect;

	public PacmanShape() {

		_dot = new Ellipse(10, 10);
		_dot.setFill(Color.YELLOW);

	}

	public void setX(double x) {

		_dot.setCenterX(x + 12);
	}

	public void setY(double y) {

		_dot.setCenterY(y + 10);
	}

	public double getX() {
		return _dot.getCenterX();
	}

	public double getY() {
		return _dot.getCenterY();
	}

	public Shape getDot() {
		return _dot;
	}

}