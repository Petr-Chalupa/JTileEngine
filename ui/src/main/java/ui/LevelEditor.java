package ui;

import engine.Engine;
import engine.core.LevelData;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;

import java.io.IOException;
import java.util.List;

public class LevelEditor {

	@FXML
	private Pane canvasParent;
	@FXML
	private VBox selector;
	@FXML
	private TextField filterInput;
	@FXML
	private VBox levelSelect;
	@FXML
	private TabPane editor;

	@FXML
	private void clickMainMenu() throws IOException {
		App.getEngine().shutdown();
		App.setRoot("main_menu");
	}

	@FXML
	private void clickCreateNew() {
		System.out.println("create new level");
		openEditor();
	}

	@FXML
	private void clickImport() {
		System.out.println("import level");
		openEditor();
	}

	@FXML
	private void filterSelect() {
		loadLevelSelect(filterInput.getText().trim().toLowerCase());
	}

	@FXML
	public void initialize() {
		loadLevelSelect("");
	}

	private void openEditor() {
		selector.setVisible(false);
		editor.setVisible(true);
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
		loadButton.getStyleClass().add("load_button");
		loadButton.setOnAction(event -> loadLevel(level.getName()));

		infoBox.getChildren().addAll(nameLabel, region, loadButton);
		levelBox.getChildren().addAll(thumbnailView, infoBox);

		return levelBox;
	}

	public void loadLevel(String name) {
		openEditor();
	}
}
