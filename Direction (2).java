package Pacman;
/*
 * This class is to create a enum for Directions because Directions are used a lot through the program
 */
public enum Direction{
		LEFT, RIGHT, UP, DOWN;
	

public Direction opposite(){//This method allows the opposite of a direction to easily be returned
	Direction result = LEFT;
	switch(this) {
	case LEFT:
		result = RIGHT;
	break;
	case RIGHT:
		result = LEFT;
	break;
	case DOWN:
		result = UP;
		break;
	case UP:
		result = DOWN;
		break;
	}
	return result;
		}
}
