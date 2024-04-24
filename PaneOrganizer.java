package Pacman;

/*
 * This class sets up and organizes the different panes for the game and also adds a quit button to the screen
 */

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

public class PaneOrganizer {
	BorderPane _root;

	public PaneOrganizer() {
		_root = new BorderPane();
		Pacman pacman = new Pacman();
		_root.setCenter(pacman.getpane());
		this.setupButtons();

	}

	public BorderPane getRoot() {
		return _root;
	}

	private void setupButtons() {
		VBox buttonPane = new VBox();
		buttonPane.setAlignment(Pos.BASELINE_RIGHT);
		_root.setBottom(buttonPane);
		Button _button = new Button("Quit");
		buttonPane.getChildren().addAll(_button);
		buttonPane.setSpacing(30);
		_button.setOnAction(new ActionHandler());

	}

	private class ActionHandler implements EventHandler<ActionEvent> {
		public void handle(ActionEvent event) {
			Platform.exit();
		}
	}
}