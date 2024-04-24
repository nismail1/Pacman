package Pacman;

import java.util.ArrayList;

import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;

/*
 * Wrapper class for the Dots in the game that Pacman eats as it goes along the board. This class
 * implements collision between Pacman and one of these dots.
 */
public class Dot implements SmartObjects {
	Ellipse _dot;
	Rectangle _rect;
	Pacman _pac;
	BorderPane _pane;

	public Dot(Pacman pac, BorderPane pane) {

		_pane = pane;
		_pac = pac;
		_dot = new Ellipse(5, 5);
		_dot.setFill(Color.WHITE);

	}

	@Override
	public void setX(double x) {
		_dot.setCenterX(x + 12);// Offset to appear in middle of square
	}

	public void setY(double y) {
		_dot.setCenterY(y + 10);
	}

	public Shape getShape() {
		return _dot;
	}

	@Override
	public void collision(ArrayList<SmartObjects> arr) {

		if (!arr.isEmpty()) {// it contains a dot/peg
			_pac.getPacman().getDot().toFront();
			_pane.getChildren().remove(this.getShape());// Remove graphically
			arr.remove(this);// Remove from array
			_pac.updateScore(10);// Update Score

		}
	}
}