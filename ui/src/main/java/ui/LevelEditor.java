package ui;

import engine.Engine;
import engine.core.LevelData;
import engine.utils.LevelLoader;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.List;

public class LevelEditor {

	@FXML
	private Pane canvasParent;
	@FXML
	private VBox selector;
	@FXML
	private VBox levelSelect;
	@FXML
	private BorderPane editor;

	@FXML
	private void clickMainMenu() throws IOException {
		App.getEngine().shutdown();
		App.setRoot("main_menu");
	}

	@FXML
	private void openEditor() {
		selector.setVisible(false);
		editor.setVisible(true);
	}

	@FXML
	public void initialize() {
		loadLevelSelect();
	}

	private void loadLevelSelect() {
		List<LevelData> levels = Engine.getInstance().getLevelLoader().getImportedLevels();
		levels.sort((level1, level2) -> level1.getName().compareToIgnoreCase(level2.getName()));

		levelSelect.getChildren().clear();
		for (LevelData level : levels) {
			// render todo
			Label label = new Label(level.getName());
			label.getStyleClass().add("no_levels_label");
			levelSelect.getChildren().add(label);
		}

		if (levels.isEmpty()) {
			Label noLevelsLabel = new Label("No levels found");
			noLevelsLabel.getStyleClass().add("no_levels_label");
			levelSelect.getChildren().add(noLevelsLabel);
		}
	}

	public void loadLevel(String path) {
		Engine engine = App.getEngine();
		engine.init(canvasParent, 10);

		LevelLoader levelLoader = LevelLoader.getInstance();
		levelLoader.loadLevel(path);
	}
}
