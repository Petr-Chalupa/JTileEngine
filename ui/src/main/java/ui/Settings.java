package ui;

import engine.core.GameSettings;
import javafx.fxml.FXML;
import javafx.scene.control.Slider;
import javafx.scene.control.ToggleButton;

import java.io.IOException;

public class Settings {
	private GameSettings settings;

	@FXML
	Slider tileSizeSlider;
	@FXML
	ToggleButton soundsToggle;

	@FXML
	private void buttonMainMenu() throws IOException {
		saveSettings();
		App.setRoot("main_menu");
	}

	@FXML
	public void initialize() {
		settings = App.getEngine().getGameSettings();
		settings.load();
		//
		tileSizeSlider.setValue(settings.getTileSize());
		soundsToggle.setSelected(settings.isSoundEnabled());
	}

	private void saveSettings() {
		settings.setTileSize(tileSizeSlider.getValue());
		settings.setSoundEnabled(soundsToggle.isSelected());
		//
		settings.save();
	}

}
