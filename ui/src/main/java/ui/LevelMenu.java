package ui;

import engine.Engine;
import engine.core.LevelData;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
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
	}

	private HBox renderLevel(LevelData level) {
		HBox levelBox = new HBox(15);
		levelBox.setPadding(new Insets(10));
		levelBox.setAlignment(Pos.CENTER_LEFT);
		levelBox.setMinWidth(250);
		levelBox.setStyle("-fx-background-color: #f4f4f4; -fx-border-color: #cccccc; -fx-border-radius: 5;");

		ImageView thumbnailView = new ImageView();
		Image thumbnail = Engine.getInstance().getResourceManager().getImg(level.getThumbnailPath());
		thumbnailView.setImage(thumbnail);
		thumbnailView.setFitWidth(100);
		thumbnailView.setFitHeight(100);

		VBox infoBox = new VBox(10);
		infoBox.setAlignment(Pos.CENTER_LEFT);
		HBox statusBox = new HBox(5);
		statusBox.setAlignment(Pos.CENTER_LEFT);

		Label nameLabel = new Label(level.getName());
		nameLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

		Circle statusCircle = new Circle(6);
		statusCircle.setFill(level.isCompleted() ? Color.GREEN : Color.GRAY);

		Label statusLabel = new Label(level.isCompleted() ? "Completed" : "Not completed");
		statusLabel.setStyle("-fx-font-size: 12px;");

		Button playButton = new Button("Play");
		playButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-weight: bold;");
		playButton.setPrefWidth(80);
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
			throw new RuntimeException(e);
		}
	}

}
