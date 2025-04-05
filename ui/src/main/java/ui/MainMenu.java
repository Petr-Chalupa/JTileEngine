package ui;

import java.io.IOException;

import javafx.application.Platform;
import javafx.fxml.FXML;

public class MainMenu {

    @FXML
    private void clickPlay() throws IOException {
        GameView controller = (GameView) App.setRoot("game_view");
        controller.loadLvl("levels/1/");
    }

    @FXML
    private void clickLevelEditor() throws IOException {
        LevelEditor controller = (LevelEditor) App.setRoot("level_editor");
        controller.loadLevel("levels/1/");
    }

    @FXML
    private void clickExit() throws IOException {
        Platform.exit();
    }
}
