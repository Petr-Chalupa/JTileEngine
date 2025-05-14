package ui;

import engine.Engine;
import engine.core.LevelData;
import engine.utils.LevelLoader;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

public class LevelEditor {

	private LevelData levelData;
	private Path levelPath;

	@FXML
	private VBox selector;
	@FXML
	private TextField filterInput;
	@FXML
	private VBox levelSelect;
	@FXML
	private TabPane editor;
	@FXML
	private TextField levelNameInput;
	@FXML
	private TextField authorInput;
	@FXML
	private TextField rowsInput;
	@FXML
	private TextField colsInput;
	@FXML
	private ChoiceBox<String> thumbnailBox;

	@FXML
	private void clickMainMenu() throws IOException {
		App.setRoot("main_menu");
	}

	@FXML
	private void clickCreateNew() {
		levelData = new LevelData("New level", false);
		levelPath = App.getEngine().getResourceManager().getUserSavePath().resolve("levels/" + levelData.getId() + "/config.json");
		openEditor();
	}

	@FXML
	private void clickImport() {
		DirectoryChooser directoryChooser = new DirectoryChooser();
		directoryChooser.setTitle("Select Level Folder");
		File selectedDir = directoryChooser.showDialog(null);

		if (selectedDir != null) {
			String levelName = selectedDir.getName();
			boolean imported = App.getEngine().getResourceManager().importLevel(selectedDir, levelName);

			if (imported) {
				LevelLoader levelLoader = App.getEngine().getLevelLoader();
				levelLoader.loadLevel(levelName);
				levelData = levelLoader.getCurrentLevel();
				levelPath = levelLoader.getCurrentLevelPath();
				if (levelData != null) openEditor();
			} else {
				System.err.println("Failed to import level: " + levelName);
			}
		}
	}

	@FXML
	private void clickSaveConfig() {
		try {
			levelData.setName(levelNameInput.getText().trim());
			levelData.setAuthor(authorInput.getText().trim());
			levelData.setRows(Integer.parseInt(rowsInput.getText().trim()));
			levelData.setCols(Integer.parseInt(colsInput.getText().trim()));
			levelData.setThumbnail(thumbnailBox.getValue());

			App.getEngine().getLevelLoader().saveLevel(levelData, levelPath);
		} catch (IOException e) {
			System.err.println("Failed to save level " + levelData.getName() + ": " + e.getMessage());
		}
	}

	@FXML
	private void filterSelect() {
		loadLevelSelect(filterInput.getText().trim().toLowerCase());
	}

	@FXML
	public void initialize() {
		loadLevelSelect("");
	}

	private void loadLevelSelect(String filter) {
		List<LevelData> levels = Engine.getInstance().getLevelLoader().getImportedLevels();
		levels.sort((level1, level2) -> level1.getName().compareToIgnoreCase(level2.getName()));
		levels.removeIf(level -> !level.getName().toLowerCase().contains(filter));

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

		HBox infoBox = new HBox();
		infoBox.getStyleClass().add("info_box");
		HBox.setHgrow(infoBox, Priority.ALWAYS);

		Label nameLabel = new Label(level.getName());
		nameLabel.getStyleClass().add("level_name");

		Region region = new Region();
		HBox.setHgrow(region, Priority.ALWAYS);

		Button loadButton = new Button("Load");
		loadButton.getStyleClass().add("secondary");
		loadButton.setOnAction(event -> loadLevel(level.getId()));

		infoBox.getChildren().addAll(nameLabel, region, loadButton);
		levelBox.getChildren().addAll(thumbnailView, infoBox);

		return levelBox;
	}

	public void loadLevel(String id) {
		LevelLoader levelLoader = App.getEngine().getLevelLoader();
		levelLoader.loadLevel(id);
		levelData = levelLoader.getCurrentLevel();
		levelPath = levelLoader.getCurrentLevelPath();
		openEditor();
	}

	private void openEditor() {
		selector.setVisible(false);
		editor.setVisible(true);

		// Add thumbnail images
		thumbnailBox.getItems().clear();
		thumbnailBox.getItems().addAll(App.getEngine().getResourceManager().getAllBuiltinImageNames());

		// Load level data
		levelNameInput.setText(levelData.getName());
		authorInput.setText(levelData.getAuthor());
		rowsInput.setText(String.valueOf(levelData.getRows()));
		colsInput.setText(String.valueOf(levelData.getCols()));
		thumbnailBox.setValue(levelData.getThumbnail());
	}
}
