package Pacman;

import java.util.ArrayList;

import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
/*
 * This class is a wrapper class of an Ellipse that represent an Energizer. Its main purpose is to
 * define the collision method for an Energizer.
 */
public class Energizer implements SmartObjects{
	Ellipse _dot;
	Rectangle _rect;
	Pacman _pac;
	BorderPane _pane;
	public Energizer(Pacman pacman, BorderPane pane) {
		_pane = pane;
		_pac = pacman;
		_dot = new Ellipse(10,10);
		_dot.setFill(Color.WHITE);
	
	}
	
	public void setX(double x) {

		_dot.setCenterX(x+12);
	}
	public void setY(double y) {

		_dot.setCenterY(y+10);
	}
	public Shape getShape() {
		return _dot;
	}
	//Set the mode to be frightened and remove it from the array and the screen and update the score of the game
	public void collision(ArrayList<SmartObjects> arr) {
		
		_pac.setMode(Mode.FRIGHTENED);
		if(!arr.isEmpty()) {//it contains a dot/peg
			_pac.getPacman().getDot().toFront();
				_pane.getChildren().remove(this.getShape());
				arr.remove(this);
				
				_pac.updateScore(10);
			
		}
	}
}