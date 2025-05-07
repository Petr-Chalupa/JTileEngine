package ui;

import javafx.application.Platform;
import javafx.fxml.FXML;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class MainMenu {

	@FXML
	private void buttonPlay() throws IOException {
		App.setRoot("level_menu");
	}

	@FXML
	private void buttonLevelEditor() throws IOException {
		App.setRoot("level_editor");
	}

	@FXML
	private void buttonExit() throws IOException {
		Platform.exit();
	}

	@FXML
	private void buttonReportBug() throws URISyntaxException, IOException {
		Desktop.getDesktop().browse(new URI("https://github.com/Petr-Chalupa/JTileEngine/issues"));
	}
}
