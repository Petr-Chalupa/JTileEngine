package ui;

import engine.Engine;
import engine.core.LevelData;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

import java.io.IOException;
import java.util.List;

public class LevelMenu {
	private boolean viewingBuiltinLevels = true;

	@FXML
	private FlowPane levelSelect;

	@FXML
	private void buttonMainMenu() throws IOException {
		App.getEngine().shutdown();
		App.setRoot("main_menu");
	}

	@FXML
	private void buttonChangeLevelView() {
		viewingBuiltinLevels = !viewingBuiltinLevels;
		loadLevelSelect();
	}

	@FXML
	public void initialize() {
		loadLevelSelect();
	}

	private void loadLevelSelect() {
		List<LevelData> levels;
		if (viewingBuiltinLevels) levels = Engine.getInstance().getLevelLoader().getBuiltinLevels();
		else levels = Engine.getInstance().getLevelLoader().getImportedLevels();
		levels.sort((level1, level2) -> level1.getName().compareToIgnoreCase(level2.getName()));

		levelSelect.getChildren().clear();
		for (LevelData level : levels) {
			HBox levelBox = renderLevel(level);
			levelSelect.getChildren().add(levelBox);
		}

		if (levels.isEmpty()) {
			Label noLevelsLabel = new Label("No levels found");
			noLevelsLabel.getStyleClass().add("no_levels_label");
			levelSelect.getChildren().add(noLevelsLabel);
		}
	}

	private HBox renderLevel(LevelData level) {
		HBox levelBox = new HBox();
		levelBox.getStyleClass().add("level_box");

		ImageView thumbnailView = new ImageView();
		thumbnailView.getStyleClass().add("thumbnail");
		thumbnailView.setImage(Engine.getInstance().getResourceManager().getImg(level.getThumbnail()));

		VBox infoBox = new VBox();
		infoBox.getStyleClass().add("info_box");

		HBox statusBox = new HBox();
		statusBox.getStyleClass().add("status_box");

		Label nameLabel = new Label(level.getName());
		nameLabel.getStyleClass().add("level_name");

		Circle statusCircle = new Circle(6);
		statusCircle.setFill(level.isCompleted() ? Color.GREEN : Color.GRAY);

		Label statusLabel = new Label(level.isCompleted() ? "Completed" : "Not completed");
		statusLabel.getStyleClass().add("status_label");

		Button playButton = new Button("Play");
		playButton.getStyleClass().add("play_button");
		playButton.setOnAction(event -> playLevel(level));

		statusBox.getChildren().addAll(statusCircle, statusLabel);
		infoBox.getChildren().addAll(nameLabel, statusBox, playButton);
		levelBox.getChildren().addAll(thumbnailView, infoBox);

		return levelBox;
	}

	private static void playLevel(LevelData level) {
		try {
			GameView controller = (GameView) App.setRoot("game_view");
			controller.loadLevel(level.getName());
		} catch (IOException e) {
			Engine.LOGGER.severe("Failed to load level: " + e.getMessage());
		}
	}

}
