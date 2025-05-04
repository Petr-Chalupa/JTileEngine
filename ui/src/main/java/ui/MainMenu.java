package ui;

import javafx.application.Platform;
import javafx.fxml.FXML;

import java.io.IOException;

public class MainMenu {

	@FXML
	private void clickPlay() throws IOException {
		App.setRoot("level_menu");
	}

	@FXML
	private void clickLevelEditor() throws IOException {
		LevelEditor controller = (LevelEditor) App.setRoot("level_editor");
		controller.loadLevel("1");
	}

	@FXML
	private void clickExit() throws IOException {
		Platform.exit();
	}
}
