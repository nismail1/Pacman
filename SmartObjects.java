package Pacman;

import java.util.ArrayList;

import javafx.scene.shape.Shape;

/*
 * This interface allows there to be a generic object called SmartObjects which represent all the Collidables in the
 * game. The ghost, dot, and ellipse classes all implement this interface so they all have to handle collision. The
 * purpose is to allow the Pacman game class to be able to generically keep track of collidable objects.
 */
public interface SmartObjects {
	public void setX(double x);

	public Shape getShape();

	public void collision(ArrayList<SmartObjects> arr);

}