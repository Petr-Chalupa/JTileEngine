package ui;

import engine.Engine;
import engine.core.GameSettings;
import engine.utils.DebugManager;
import engine.utils.DebugManager.Features;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Slider;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.logging.Level;

public class Settings {
	private GameSettings settings;

	@FXML
	private Slider tileSizeSlider;
	@FXML
	private ToggleButton soundsToggle;
	@FXML
	private VBox debugInfoBox;
	@FXML
	private ChoiceBox<Level> loggingChoiceBox;

	@FXML
	private void buttonMainMenu() throws IOException {
		saveSettings();
		App.setRoot("main_menu");
	}

	@FXML
	public void initialize() {
		settings = App.getEngine().getGameSettings();
		settings.load();
		// Tile size
		tileSizeSlider.setValue(settings.getTileSize());
		// Sounds
		soundsToggle.setSelected(settings.isSoundEnabled());
		soundsToggle.setText(settings.isSoundEnabled() ? "ENABLED" : "DISABLED");
		soundsToggle.setOnAction(event -> soundsToggle.setText(soundsToggle.isSelected() ? "ENABLED" : "DISABLED"));
		// Debug info
		DebugManager debugManager = App.getEngine().getDebugManager();
		for (Features feature : debugManager.getFeatures()) {
			HBox featureBox = new HBox();
			featureBox.getStyleClass().add("debug_feature");
			RadioButton featureButton = new RadioButton();
			featureButton.setSelected(debugManager.isFeatureEnabled(feature));
			Label featureLabel = new Label(feature.name());
			featureBox.getChildren().addAll(featureButton, featureLabel);
			debugInfoBox.getChildren().add(featureBox);
		}
		// Logging
		loggingChoiceBox.getItems()
				.addAll(Level.ALL, Level.SEVERE, Level.WARNING, Level.INFO, Level.FINE, Level.CONFIG, Level.OFF);
		loggingChoiceBox.setValue(Engine.LOGGER.getLevel());
	}

	private void saveSettings() {
		// Tile size
		settings.setTileSize(tileSizeSlider.getValue());
		// Sounds
		settings.setSoundEnabled(soundsToggle.isSelected());
		// Debug info
		DebugManager debugManager = App.getEngine().getDebugManager();
		for (int i = 0; i < debugInfoBox.getChildren().size(); i++) {
			HBox featureBox = (HBox) debugInfoBox.getChildren().get(i);
			RadioButton featureButton = (RadioButton) featureBox.getChildren().getFirst();
			debugManager.setFeature(Features.values()[i], featureButton.isSelected());
		}
		// Logging
		App.getEngine().setLogLevel(loggingChoiceBox.getValue());
		//
		settings.save();
	}

}
